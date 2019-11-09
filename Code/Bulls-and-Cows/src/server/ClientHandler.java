package server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.SocketException;
import java.net.SocketTimeoutException;


public class ClientHandler extends Thread {
    private boolean serverStop;
    private boolean yourTurn;
    private boolean sent;
    private boolean wait;
    private boolean resetServer;
    private String myNumber;
    private Socket clientSocket;

    ClientHandler(Socket client) {
        this.clientSocket = client;
        start();
    }

    public boolean isServerStop() {
        return serverStop;
    }

    public void setServerStop(boolean serverStop) {
        this.serverStop = serverStop;
    }

    public boolean isYourTurn() {
        return yourTurn;
    }

    public void setYourTurn(boolean yourTurn) {
        this.yourTurn = yourTurn;
    }

    public void setSent(boolean sent) {
        this.sent = sent;
    }

    public boolean isWait() {
        return wait;
    }

    public void setWait(boolean wait) {
        this.wait = wait;
    }

    public void setResetServer(boolean resetServer) {
        this.resetServer = resetServer;
    }

    public String getMyNumber() {
        return myNumber;
    }

    public void setMyNumber(String myNumber) {
        this.myNumber = myNumber;
    }

    @Override
    public void run() {

        try {
            clientSocket.setSoTimeout(300);

            BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);
            System.out.println("Clint connect");
            while (true)

                try {
                    if (resetServer) {
                        out.println("reset");
                        yourTurn = false;
                        sent = false;
                        wait = false;
                        myNumber = null;
                        resetServer = false;
                    }
                    if (yourTurn)//activate client-turn
                    {
                        try {
                            if (myNumber != null && myNumber.equals("null")) {//if smb disconnect
                                if ((myNumber = in.readLine()).equals("yourTurn"))
                                    yourTurn = false;
                            }
                        } catch (NullPointerException ex) {
                            throw new SocketException("Client disconnect");
                        }
                        if (sent) {
                            out.println("yourTurn");
                            if (myNumber != null) out.println(myNumber);
                            sent = false;
                        } else if ((myNumber = in.readLine()) != null) {
                            wait = true;
                        }
                    }

                    if (clientSocket.getInputStream().read() == -1) throw new SocketException("Client disconnect");
                } catch (SocketTimeoutException ex) {
                    if (serverStop) break;
                } catch (SocketException ex) {
                    System.out.println("Client disconnect");
                    serverStop = true;
                    break;
                }

            out.close();
            in.close();
            end();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void end() {
        try {
            clientSocket.close();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
}
