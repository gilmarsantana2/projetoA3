package servidor;

import java.io.*;
import java.net.Socket;

public class ServerThread extends Thread {

    private Server server;
    private Socket socket;
    private ObjectInputStream input;
    private ObjectOutputStream output;


    public ServerThread(Server server, Socket socket) {

        this.server = server;
        this.socket = socket;
        System.out.println("Cliente conectado!");
        try {
            output = new ObjectOutputStream(this.socket.getOutputStream());
            output.flush();
            input = new ObjectInputStream(this.socket.getInputStream());
            server.setOutputCliente(output);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        String message = "";
        while (server.isRunning()) {
            try {
                message = (String) input.readObject();
                if (message != null) server.tratarComando(message);
            } catch (Exception e) {
                break;
            }
        }
    }

}
