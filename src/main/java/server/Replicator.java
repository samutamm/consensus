package server;

import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.Unirest;
import org.json.JSONObject;

import java.util.List;
import java.util.stream.Collectors;

public class Replicator {

    private List<Integer> nodes;
    private int currentPort;
    private String baseUrl = "http://localhost:";

    public Replicator(List<Integer> nodes, int port) {
        this.nodes = nodes;
        this.currentPort = port;
    }

    public int queryToCommit(String key, String value, long transactionID) {
        List<Integer> results = nodes.stream().map((port) -> {
            try {
                String url = baseUrl + port + "/query/" + transactionID;
                JSONObject json = new JSONObject();
                json.put("value",value);
                json.put("key", key);
                HttpResponse<String> response = Unirest.put(url)
                        .header("Content-Type", "application/json")
                        .body(json)
                        .asString();
                if (response.getStatus() == 200) {
                    return 1;
                }
                return 0;
            } catch (Exception e) {
                e.printStackTrace();
                return 0;
            }
        }).collect(Collectors.toList());
        Integer numberOfSucceccedNodes = count(results);
        return numberOfSucceccedNodes.intValue();
    }

    private Integer count(List<Integer> results) {
        return results.stream().reduce(0, (a, b) -> {
                return a + b;
            });
    }

    public int abortTransaction(long id) {
        List<Integer> results = nodes.stream().map((port) -> {
            try {
                String url = baseUrl + port + "/abort/" + id;
                HttpResponse<String> response = Unirest.post(url)
                        .header("Content-Type", "application/json")
                        .asString();
                if (response.getStatus() == 200) {
                    return 1;
                }
                return 0;
            } catch (Exception e) {
                e.printStackTrace();
                return 0;
            }
        }).collect(Collectors.toList());
        Integer numberOfSucceccedNodes = count(results);
        return numberOfSucceccedNodes.intValue();
    }

    public boolean replicateDelete(String key) {
        return false;
    }

}
