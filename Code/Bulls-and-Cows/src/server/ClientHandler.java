package server;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.Scanner;
import java.util.StringTokenizer;

public class ClientHandler implements Runnable {
    private final DataInputStream dataInputStream;
    private final DataOutputStream dataOutputStream;
    private Scanner scanner = new Scanner(System.in);
    private String name;
    private Socket socket;
    private boolean isLoggedIn;

    public ClientHandler(Socket socket, String name,
                         DataInputStream dataInputStream, DataOutputStream dataOutputStream) {
        this.dataInputStream = dataInputStream;
        this.dataOutputStream = dataOutputStream;
        this.name = name;
        this.socket = socket;
        this.isLoggedIn = true;
    }

    @Override
    public void run() {

        String received;
        while (true) {
            try {
                received = dataInputStream.readUTF();
                System.out.println(received);
                if (received.equals("logout")) {
                    this.isLoggedIn = false;
                    this.socket.close();
                    break;
                }

                StringTokenizer st = new StringTokenizer(received, "#");
                String MsgToSend = st.nextToken();
                String recipient = st.nextToken();
                for (ClientHandler clientHandler : Server.ar) {
                    if (clientHandler.name.equals(recipient) && clientHandler.isLoggedIn) {
                        clientHandler.dataOutputStream.writeUTF(this.name + " : " + MsgToSend);
                        break;
                    }
                }
            } catch (IOException e) {

                e.printStackTrace();
            }

        }
        try {
            dataInputStream.close();
            dataOutputStream.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}