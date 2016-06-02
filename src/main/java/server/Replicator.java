package server;

import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
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
                String url = baseUrl + port + "/" +  key;
                String jsonString =
                        "{\"value\":\"" + value + "\"," +
                        "\"status\":\"query\"," +
                        "\"id\":\"" + transactionID + "\","+
                        "\"coordinator\":\"" + currentPort + "\"}";
                JSONObject json = new JSONObject(jsonString);
                HttpResponse<JsonNode> response = Unirest.put(url)
                        .header("Content-Type", "application/json")
                        .body(json)
                        .asJson();
                if (response.getStatus() == 200) {
                    return 1;
                }
                return 0;
            } catch (Exception e) {
                e.printStackTrace();
                return 0;
            }
        }).collect(Collectors.toList());
        Integer numberOfSucceccedNodes = results.stream().reduce(0, (a, b) -> {
            return a + b;
        });
        return numberOfSucceccedNodes.intValue();
    }

    public boolean replicateDelete(String key) {
        return false;
    }

    public void sendAckToPort(int responsePort, String key, String value, String transactionID) {
        try {
            String url = baseUrl + responsePort + "/" +  key;
            JSONObject json = new JSONObject();
            json.put("value", value);
            json.put("status", "ack");
            json.put("id", transactionID);
            json.put("coordinator", currentPort);
            Unirest.put(url)
                    .header("Content-Type", "application/json")
                    .body(json)
                    .asJson();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
