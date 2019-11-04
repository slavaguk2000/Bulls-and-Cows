package server;

import java.io.*;
import java.net.*;
import java.util.Deque;
import java.util.LinkedList;


public class Server {

    private static ServerSocket server;
    private static Deque<ClientHandler> clientHandlerList = new LinkedList<>();

    public static void main(String[] args) {
        System.out.println("Wait clients for 60 second");
        begin();
        handle();
    }

    private static void begin() {   //create server socket

        try {
            server = new ServerSocket(4444, 0, InetAddress.getLocalHost());//192.168.31.245
            server.setSoTimeout(60000);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    private static void handle() {
        try {
            while (true) {

                if (clientHandlerList.size() == 2) {
                    if (!server.isClosed()) {
                        server.close();
                    }

                    if (isServerNeedReset()) {
                        serverReset();
                    }
                    if (isFirstServerChange()) {
                        firstServerChange();
                    } else if (clientHandlerList.getFirst().yourTurn && !clientHandlerList.getLast().yourTurn && clientHandlerList.getFirst().wait) {
                        changeTurn(true);

                    } else if (clientHandlerList.getLast().yourTurn && !clientHandlerList.getFirst().yourTurn && clientHandlerList.getLast().wait) {
                        changeTurn(false);

                    }
                    for (ClientHandler client : clientHandlerList) {
                        if (client.stop) {

                            deleteClient(client);
                            clientHandlerList.getFirst().reset = true;
                            begin();
                            break;
                        }
                    }
                } else clientHandlerList.add(new ClientHandler(server.accept()));
            }
        } catch (SocketTimeoutException ex) {
            System.out.println("Time out is 60 seconds,server close");
            end();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private static boolean isFirstServerChange() {
        return (!clientHandlerList.getFirst().yourTurn && !clientHandlerList.getLast().yourTurn);
    }

    private static void firstServerChange() {
        clientHandlerList.getFirst().myNumber = null;
        clientHandlerList.getLast().myNumber = null;
        clientHandlerList.getFirst().yourTurn = true;
        clientHandlerList.getFirst().sent = true;
    }

    private static void serverReset() {
        clientHandlerList.getFirst().myNumber = "null";
        clientHandlerList.getLast().myNumber = "null";
        clientHandlerList.getFirst().wait = false;
        clientHandlerList.getLast().wait = false;
        clientHandlerList.getFirst().yourTurn = true;
        clientHandlerList.getLast().yourTurn = true;
    }

    private static boolean isServerNeedReset() {
        if (clientHandlerList.getFirst().myNumber != null && clientHandlerList.getLast().myNumber != null) {
            return (clientHandlerList.getFirst().myNumber.equals("null") && clientHandlerList.getFirst().wait
                    || clientHandlerList.getLast().myNumber.equals("null") && clientHandlerList.getLast().wait);
        } else return false;
    }

    private static void changeTurn(boolean changeZeroElementToFirst) {
        if (changeZeroElementToFirst) {
            clientHandlerList.getFirst().yourTurn = false;
            clientHandlerList.getFirst().wait = false;
            clientHandlerList.getLast().myNumber = clientHandlerList.getFirst().myNumber;
            clientHandlerList.getLast().yourTurn = true;
            clientHandlerList.getLast().sent = true;
        } else {
            clientHandlerList.getLast().yourTurn = false;
            clientHandlerList.getLast().wait = false;
            clientHandlerList.getFirst().myNumber = clientHandlerList.getLast().myNumber;
            clientHandlerList.getFirst().yourTurn = true;
            clientHandlerList.getFirst().sent = true;
        }
    }

    private static void deleteClient(ClientHandler client) {
        clientHandlerList.remove(client);
    }

    private static void end() {
        try {
            for (ClientHandler client : clientHandlerList) {
                client.stop = true;
            }
            try {
                Thread.sleep(500);//wait half of second until ClientHandlers close
            } catch (InterruptedException ex) {
            }

            clientHandlerList.clear();
            server.close();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
}