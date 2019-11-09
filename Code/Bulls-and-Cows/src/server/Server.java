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
                    } else if (clientHandlerList.getFirst().isYourTurn() && !clientHandlerList.getLast().isYourTurn()
                            && clientHandlerList.getFirst().isWait()) {
                        changeTurn(true);

                    } else if (clientHandlerList.getLast().isYourTurn() && !clientHandlerList.getFirst().isYourTurn()
                            && clientHandlerList.getLast().isWait()) {
                        changeTurn(false);

                    }
                    for (ClientHandler client : clientHandlerList) {
                        if (client.isServerStop()) {

                            deleteClient(client);
                            clientHandlerList.getFirst().setResetServer(true);
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
        return (!clientHandlerList.getFirst().isYourTurn() && !clientHandlerList.getLast().isYourTurn());
    }

    private static void firstServerChange() {
        clientHandlerList.getFirst().setMyNumber(null);
        clientHandlerList.getLast().setMyNumber(null);
        clientHandlerList.getFirst().setYourTurn(true);
        clientHandlerList.getFirst().setSent(true);
    }

    private static void serverReset() {
        clientHandlerList.getFirst().setMyNumber(null);
        clientHandlerList.getLast().setMyNumber(null);
        clientHandlerList.getFirst().setWait(false);
        clientHandlerList.getLast().setWait(false);
        clientHandlerList.getFirst().setYourTurn(true);
        clientHandlerList.getLast().setYourTurn(true);
    }

    private static boolean isServerNeedReset() {
        if (clientHandlerList.getFirst().getMyNumber() != null && clientHandlerList.getLast().getMyNumber() != null) {
            return (clientHandlerList.getFirst().getMyNumber().equals("null") && clientHandlerList.getFirst().isWait()
                    || clientHandlerList.getLast().getMyNumber().equals("null") && clientHandlerList.getLast().isWait());
        } else
            return false;
    }

    private static void changeTurn(boolean changeZeroElementToFirst) {
        if (changeZeroElementToFirst) {
            clientHandlerList.getFirst().setYourTurn(false);
            clientHandlerList.getFirst().setWait(false);
            clientHandlerList.getLast().setMyNumber(clientHandlerList.getFirst().getMyNumber());
            clientHandlerList.getLast().setYourTurn(true);
            clientHandlerList.getLast().setSent(true);
        } else {
            clientHandlerList.getLast().setYourTurn(false);
            clientHandlerList.getLast().setWait(false);
            clientHandlerList.getFirst().setMyNumber(clientHandlerList.getLast().getMyNumber());
            clientHandlerList.getFirst().setYourTurn(true);
            clientHandlerList.getFirst().setSent(true);
        }
    }

    private static void deleteClient(ClientHandler client) {
        clientHandlerList.remove(client);
    }

    private static void end() {
        try {
            for (ClientHandler client : clientHandlerList) {
                client.setServerStop(true);
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