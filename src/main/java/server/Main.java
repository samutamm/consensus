package server;

import java.util.ArrayList;

public class Main {

    public static void main(String[] args) {
        ArrayList<Integer> neighborPorts = new ArrayList<>();
        if (args.length > 0) {
            int portOfCurrentInstance = Integer.parseInt(args[0]);
            for (int i = 1; i < args.length; i++) {
                int port = Integer.parseInt(args[i]);
                neighborPorts.add(port);
            }
            new Server(portOfCurrentInstance, neighborPorts);
        } else {
            neighborPorts.add(9998);
            new Server(9999, neighborPorts);
        }
    }
}
