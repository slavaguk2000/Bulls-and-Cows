package server;

import java.io.*;
import java.net.*;
import java.lang.String;


public class ClientHandler extends Thread {
    private Socket client;
    boolean stop = false;

    boolean yourTurn = false ;
    boolean sent = false ;
    boolean wait = false ;
    boolean reset = false;
    String myNumber;

    ClientHandler(Socket client) {
        this.client = client;
        start();
    }

    @Override
    public void run() {

        try {
            client.setSoTimeout(300);

            BufferedReader in = new BufferedReader(new InputStreamReader(client.getInputStream()));
            PrintWriter out = new PrintWriter(client.getOutputStream(), true);
            System.out.println("Clint connect");
            while (true)

                try {
                    if(reset) {
                        out.println("reset");
                        yourTurn = false ;
                        sent = false ;
                        wait = false ;
                        myNumber = null;
                        reset = false;
                    }
                    if(yourTurn)//activate client-turn
                    {
                        try {
                            if (myNumber != null && myNumber.equals("null")) {//if smb disconnect
                                if ((myNumber = in.readLine()).equals("yourTurn"))
                                    yourTurn = false;
                            }
                        }catch (NullPointerException ex){
                            throw new SocketException("Client disconnect");
                        }
                        if(sent) {
                            out.println("yourTurn");
                            if (myNumber != null) out.println(myNumber);
                            sent = false;
                        }
                        else if((myNumber = in.readLine()) != null) {
                            wait = true;
                        }
                    }

                    if(client.getInputStream().read() ==-1) throw new SocketException("Client disconnect");
                } catch (SocketTimeoutException ex) {
                    if (stop) break;
                } catch (SocketException ex) {
                    System.out.println("Client disconnect");
                    stop = true;
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
            client.close();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
}
