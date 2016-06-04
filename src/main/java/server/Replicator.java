package server;

import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import org.json.JSONObject;

import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

public class Replicator {

    private List<Integer> nodes;
    private int currentPort;

    public Replicator(List<Integer> nodes, int port) {
        this.nodes = nodes;
        this.currentPort = port;
    }

    public int queryToCommit(String key, String value, String transactionID) {
        System.out.println("START QUERY TO COMMIT");
        return forEachNode((port) -> {
            String url = Server.URLContract.BASE_URL
                    + port + "/"+ Server.URLContract.QUERY +"/" + transactionID;
            JSONObject json = new JSONObject();
            json.put("value",value);
            json.put("key", key);
            try {
                return Unirest.put(url)
                        .header("Content-Type", "application/json")
                        .body(json)
                        .asString();
            } catch (UnirestException e) {
                return null;
            }
        });
    }

    public int commitTransaction(String id) {
        return forEachNode((port) -> {
            String url = Server.URLContract.BASE_URL + port + "/"+Server.URLContract.COMMIT+"/" + id;
            try {
                return Unirest.post(url)
                        .header("Content-Type", "application/json")
                        .asString();
            } catch (UnirestException e) {
                return null;
            }
        });
    }

    public int abortTransaction(String id) {
        return forEachNode((port) -> {
            String url = Server.URLContract.BASE_URL + port + "/" +Server.URLContract.ABORT +"/" + id;
            try {
                return Unirest.post(url)
                        .header("Content-Type", "application/json")
                        .asString();
            } catch (UnirestException e) {
                return null;
            }
        });
    }

    /**
     * Executes given function for each node and counts, how many gave 200 OK response.
     * @param function that creates the request.
     */
    private int forEachNode(Function<Integer, HttpResponse<String>> function) {
        List<Integer> results = nodes.stream().map((port) -> {
            HttpResponse<String> response = function.apply(port);
            if (response != null && response.getStatus() == 200) {
                return 1;
            }
            return 0;
        }).collect(Collectors.toList());
        Integer numberOfSucceccedNodes = count(results);
        return numberOfSucceccedNodes.intValue();
    }

    private Integer count(List<Integer> results) {
        return results.stream().reduce(0, (a, b) -> {
                return a + b;
            });
    }

    /**
     * TODO
     */
    public boolean replicateDelete(String key) {
        return false;
    }


}
