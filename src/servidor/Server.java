package servidor;

import controlador.ControladorRaspberry;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Enumeration;
import java.util.Hashtable;

public class Server {

    private Socket socket;
    private ServerSocket serverSocket;
    private String adress;
    private int porta;
    public Hashtable<Socket, ObjectOutputStream> outputStreams;
    public Hashtable<String, ObjectOutputStream> clients;
    private volatile boolean running;
    private ControladorRaspberry cr;
    private Integer id = 0;

    public Server(String adress, int porta) {
        this.adress = adress;
        this.porta = porta;
    }

    //Waiting for clients to connect
    public void ligarServidor(ControladorRaspberry cr) {
        try {
            serverSocket = new ServerSocket();
            if (!serverSocket.isBound()) {
                serverSocket.bind(new InetSocketAddress(adress, porta), 50);
            }
            System.out.println("Servido Raspberry ligado!");
            this.cr = cr;
            running = true;
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Erro ao criar servidor \"Class servidor.Server\"");
            System.exit(0);
        }

        while (running) {
            try {
                socket = serverSocket.accept();
                new ServerThread(this, socket).start();
            } catch (IOException e) {
                e.printStackTrace();
                break;
            }
        }
    }

    /**
     * #s -> iniciar escaneamento
     * #p -> parar escaneamento
     * #r -> resultado do escaneamento
     * */

    public void tratarComando(String mensagem){


    }


    public void sendToAll(String data) {
        for (Enumeration<ObjectOutputStream> e = getOutputStreams(); e.hasMoreElements(); ) {
            //since we don't want server to remove one client and at the same time sending message to it
            synchronized (outputStreams) {
                ObjectOutputStream tempOutput = e.nextElement();
                new Thread(() -> {
                    try {
                        tempOutput.writeObject(data);
                        tempOutput.flush();
                    } catch (IOException ex) {
                        ex.printStackTrace();
                    }
                }).start();
            }
        }
    }

    //To get Output Stream of the available clients from the hash table
    private Enumeration<ObjectOutputStream> getOutputStreams() {
        return outputStreams.elements();
    }

    public boolean desligarServer() {
        try {
            if (serverSocket != null) {
                running = false;
                serverSocket.close();
            }
            return true;
        } catch (IOException e) {
            return false;
        }
    }

    public boolean isRunning() {
        return this.running;
    }

    public Integer getId() {
        return id;
    }

    public void addId() {
        this.id++;
    }
}
