package client;

import java.io.*;
import java.net.*;

public class Model extends Thread {
    private static Socket socket;
    private boolean beginServerStop;
    private boolean continueServerStop = true;

    private boolean myTurn;
    private String myGuess;
    private String opponentGuess;
    private String myNumber;
    private String opponentNumber;
    private boolean sendServer;
    private boolean reset;

    Model() {
        start();
    }

    public void reset() {
        myTurn = false;
        myGuess = null;
        opponentGuess = null;
        myNumber = null;
        opponentNumber = null;
    }

    @Override
    public void run() {
        connect();
        handle();
    }

    private void connect() {
        try {
            socket = new Socket(InetAddress.getLocalHost(), 4444);
        } catch (ConnectException ex) {
            beginServerStop = true;
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.exit(0);

        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    private void handle() {
        try {
            socket.setSoTimeout(500);
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
            String buffer;
            while (true) {
                try {
                    if (!myTurn) {
                        if (sendServer) {
                            if (myGuess == null) {
                                out.println(myNumber);
                            } else {
                                out.println(myGuess);
                                if (myGuess.equals("yourTurn")) {
                                    myGuess = null;
                                }
                            }
                            sendServer = false;
                        }
                        if ((buffer = in.readLine()).equals("yourTurn")) {//activate clientTurn
                            myTurn = true;
                            if (opponentNumber == null) {
                                opponentNumber = in.readLine();
                            } else {
                                opponentGuess = in.readLine();
                            }
                        } else if (buffer.equals("reset")) {
                            reset = true;
                        }
                    }

                    if (socket.getInputStream().read() == -1) throw new SocketException("");
                } catch (SocketException ex) {
                    beginServerStop = true;
                    out.close();
                    in.close();
                    if (continueServerStop) {
                        try {
                            Thread.sleep(2000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                    System.exit(0);
                } catch (IOException ex) {
                }
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    static void end() {
        try {
            socket.close();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
}

