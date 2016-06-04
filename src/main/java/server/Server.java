package server;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import database.Database;
import database.InMemoryDB;
import database.TransactionManager;
import spark.Request;
import spark.Spark;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;

import static spark.Spark.*;

public class Server {

    private Database database;
    private Gson jsonParser;
    private int port = 4567;
    private Replicator replicator;
    private ConcurrentHashMap<String, Function<Database, String>> transactions;

    private List<Integer> nodes;

    public Server(int port, List<Integer> neighborPorts) {
        this.port = port;
        database = new InMemoryDB();
        jsonParser = new Gson();
        nodes = neighborPorts;
        replicator = new Replicator(neighborPorts, port);
        transactions = new TransactionManager();
        Spark.port(port);
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        run();
    }

    public void run() {
        System.out.println("LISTENING PORT " + port);
        get("/:key", (req, res) -> {
            System.out.println("get");
            String key = req.params("key");
            return database.get(key);
        });

        /**
         * Query to commit from coordinator/master.
         * Let's prepare the database transaction
         * and the ack.
         */
        put("/"+ URLContract.QUERY+"/:id", (req, res) -> {
            String id = req.params("id");
            String key = getFromBody(req, "key");
            String value = getFromBody(req, "value");
            Function<Database, String> transaction = (database) -> {
                return database.put(key, value);
            };
            transactions.put(id, transaction);
            return true;
        });

        /**
         * Initial request from clients
         */
        put("/:key", (req, res) -> {
            System.out.println("PUT");
            String key = req.params("key");
            String value = getFromBody(req, "value");
            Function<Database, String> transaction = (database) -> {
                return database.put(key, value);
            };
            String id = ""+System.currentTimeMillis();
            transactions.put(id, transaction);
            int numberOfResponses = replicator.queryToCommit(key, value, id);
            System.out.println("NUMBER OF RESPONSES: " + numberOfResponses);
            if (numberOfResponses != nodes.size()) {
                replicator.abortTransaction(id);
                transactions.remove(id);
                return false;
            } else {
                replicator.commitTransaction(id);
                return commitTransaction(id);
            }
        });

        /**
         * Coordinator/master decided to commit the
         * transaction after the vote. Now it should
         * be committed also in cohorts/slaves.
         */
        post("/" + URLContract.COMMIT + "/:id", (req, res)-> {
            String id = req.params("id");
            return commitTransaction(id);
        });

        /**
         * Coordinator/master decided to abort the
         * transaction after the vote. Now it should
         * be aborted also in cohorts/slaves.
         */
        post("/"+URLContract.ABORT +"/:id", (req, res) -> {
            String id = req.params("id");
            transactions.remove(id);
            return true;
        });

        /**
         * TODO
         */
        delete("/:key", (req, res) -> {

            return false;
        });
    }

    private String commitTransaction(String id) {
        Function<Database, String> trans = transactions.get(id);
        String returnValue = trans.apply(database);
        transactions.remove(id);
        return returnValue;
    }

    private String getFromBody(Request req, String value) {
        String body = req.body();
        JsonObject bodyAsJson = jsonParser.fromJson(body, JsonObject.class);
        return bodyAsJson.get(value).getAsString();
    }

    public static class URLContract {
        public static String QUERY = "query";
        public static String COMMIT = "commit";
        public static String ABORT = "abort";
        public static String BASE_URL = "http://localhost:";
    }
}
