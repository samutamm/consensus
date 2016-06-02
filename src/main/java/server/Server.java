package server;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import database.Database;
import database.InMemoryDB;
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
    private ConcurrentHashMap<String, Function> transactions;

    private List<Integer> nodes;

    public Server(int port, List<Integer> neighborPorts) {
        this.port = port;
        database = new InMemoryDB();
        jsonParser = new Gson();
        nodes = neighborPorts;
        replicator = new Replicator(neighborPorts, port);
        transactions = new ConcurrentHashMap<>();
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

        put("/:key", (req, res) -> {
            System.out.println("PUT");
            String key = req.params("key");
            String value = getFromBody(req, "value");
            String status = getFromBody(req, "status");
            System.out.println("STATUS: " + status);
            switch (status) {
                case "client":
                    //initial request from client, starts the replication algorithm
                    long id = System.currentTimeMillis();
                    int numberOfResponses = replicator.queryToCommit(key, value, id);
                    System.out.println("NUMBER OF RESPONSES: " + numberOfResponses);
                    break;
                case "query":
                    Function<Database, String> transaction = (database) -> {
                        return database.put(key, value);
                    };
                    String transactionID = getFromBody(req, "id");
                    transactions.put(transactionID, transaction);
                    int responsePort = Integer.parseInt(getFromBody(req, "coordinator"));
                    replicator.sendAckToPort(responsePort, key, value, transactionID);
                    break;
                case "commit":
                    //find transaction by id and commit
                    break;
                case "rollback":
                    //find transaction by id and delete
                    break;
            }
            return false;
        });

        delete("/:key", (req, res) -> {
            System.out.println("delete");
            String key = req.params("key");
            String isClient = getFromBody(req, "client");
            if (isClient.equals("true")) {
                if (replicator.replicateDelete(key)) {
                    return database.delete(key);
                }
            } else if(isClient.equals("false")) {
                return database.delete(key);
            }
            return false;
        });
    }

    private String getFromBody(Request req, String value) {
        String body = req.body();
        JsonObject bodyAsJson = jsonParser.fromJson(body, JsonObject.class);
        return bodyAsJson.get(value).getAsString();
    }
}
