package servidor;

import controlador.ControladorRaspberry;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.concurrent.atomic.AtomicBoolean;

public class Server {

    private Socket socket;
    private ServerSocket serverSocket;
    private String adress;
    private int porta;
    //public Hashtable<Socket, ObjectOutputStream> outputStreams;
    //public Hashtable<String, ObjectOutputStream> clients;
    private ObjectOutputStream outputCliente;
    private AtomicBoolean running;
    private ControladorRaspberry cr;
    private Integer id = 0;

    public Server(String adress, int porta) {
        this.adress = adress;
        this.porta = porta;
    }

    //Waiting for clients to connect
    public void ligarServidor() {

        try {
            serverSocket = new ServerSocket();
            if (!serverSocket.isBound()) {
                serverSocket.bind(new InetSocketAddress(adress, porta), 50);
            }
            System.out.println("Servido Raspberry ligado!");
            running = new AtomicBoolean(true);
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Erro ao criar servidor \"Class servidor.Server\"");
            System.exit(0);
        }

        while (running.get()) {
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
     * #a -> parar alarme
     * #f -> falha detectada
     * #x -> Sair da aplicação
     */

    public void tratarComando(String mensagem) {
        System.out.println("Comando recebido: " + mensagem);

        if (mensagem.startsWith("#s")) {
            cr = new ControladorRaspberry(this);
            cr.start();
        }
        if (mensagem.startsWith("#p")) {
            cr.pararMotor(false);
            cr.alarmRunning.set(false);
        }
        if (mensagem.startsWith("#a")) {
            cr.alarmeStarted.set(false);
            cr.alarmRunning.set(false);
        }
        if (mensagem.startsWith("#x")) {
            cr.pararMotor(false);
            cr.cleanup();
            desligarServer();
            System.exit(0);
        }
    }


    /*public void sendToAll(String data) {
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
    }*/

    /*//To get Output Stream of the available clients from the hash table
    private Enumeration<ObjectOutputStream> getOutputStreams() {
        return outputStreams.elements();
    }*/


    public synchronized void send(String message) {
        new Thread(() -> {
            try {
                outputCliente.writeObject(message);
                outputCliente.flush();

                System.out.println(message);
            } catch (IOException e) {
                System.out.println("Erro ao enviar msg metodo Send");
            }
        }).start();
    }

    public boolean desligarServer() {
        try {
            if (serverSocket != null) {
                running.set(false);
                serverSocket.close();
            }
            return true;
        } catch (IOException e) {
            return false;
        }
    }

    public boolean isRunning() {
        return this.running.get();
    }

    public Integer getId() {
        return id;
    }

    public void addId() {
        this.id++;
    }

    public ObjectOutputStream getOutputCliente() {
        return outputCliente;
    }

    public void setOutputCliente(ObjectOutputStream outputCliente) {
        this.outputCliente = outputCliente;
    }
}
