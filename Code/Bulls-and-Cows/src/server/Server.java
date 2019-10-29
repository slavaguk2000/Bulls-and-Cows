package server;

import java.io.*;
import java.util.*;
import java.net.*;

public class Server
{
    private static Vector<ClientHandler> array = new Vector<>();

    private static int i = 0;

    public static void main(String[] args) throws IOException
    {
        ServerSocket ss = new ServerSocket(1234);

        Socket socket;
        while (true)
        {
            socket = ss.accept();

            System.out.println("New client request received : " + socket);
            DataInputStream dis = new DataInputStream(socket.getInputStream());
            DataOutputStream dos = new DataOutputStream(socket.getOutputStream());

            System.out.println("Creating a new handler for this client...");
            ClientHandler clientHandler = new ClientHandler(socket,"client " + i, dis, dos);
            Thread t = new Thread(clientHandler);
            System.out.println("Adding this client to active client list");
            array.add(clientHandler);
            t.start();
            i++;

        }
    }
}