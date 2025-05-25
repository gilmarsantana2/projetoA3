package servidor;

import controlador.ControladorRaspberry;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.atomic.AtomicBoolean;

public class Server {

    private Socket socket;
    private ServerSocket serverSocket;
    private String adress;
    private int porta;
    private ObjectOutputStream outputCliente;
    private AtomicBoolean running;
    private ControladorRaspberry cr;


    public Server(String adress, int porta) {
        this.adress = adress;
        this.porta = porta;
    }

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
            var color = mensagem.substring(4);
            cr = new ControladorRaspberry(this, getCor(color));
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

    private int getCor(String color) {
        switch (color) {
            case "vermelho":
                return 1;
            case "verde":
                return 2;
            case "azul":
                return 3;
            default:
                return 0;
        }
    }

    public synchronized void send(String message) {
        new Thread(() -> {
            try {
                outputCliente.writeObject(message);
                outputCliente.flush();

                System.out.println(message);
            } catch (IOException e) {
                System.err.println("Erro ao enviar msg \"metodo Send\"");
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


    public void setOutputCliente(ObjectOutputStream outputCliente) {
        this.outputCliente = outputCliente;
    }
}
