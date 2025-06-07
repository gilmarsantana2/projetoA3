package servidor;

import controlador.ControladorRaspberry;
import data.MessageData;

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

    private int countRed = 0;
    private int countBlue = 0;
    private int countGreen = 0;


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

    public void tratarComando(MessageData data) {
        System.out.println("Comando recebido: " + data.getComando().name());

        var comando = data.getComando();

        switch (comando) {
            case INICIAR: {
                System.out.println("Cor Selecionada: " + data.getCorSelecionada().name());
                cr = new ControladorRaspberry(this, data.getCorSelecionada());
                cr.start();
                break;
            }
            case PARAR_MOTOR: {
                cr.pararMotor(false);
                cr.alarmRunning.set(false);
                break;
            }
            case PARAR_ALARME: {
                cr.alarmeStarted.set(false);
                cr.alarmRunning.set(false);
                break;
            }
            case SAIR: {
                cr.pararMotor(false);
                cr.cleanup();
                desligarServer();
                System.exit(0);
                break;
            }
            case RESET: {
                countBlue = 0;
                countGreen = 0;
                countRed = 0;
            }
            default: {
                break;
            }
        }
    }

    public synchronized void send(MessageData message) {
        new Thread(() -> {
            try {
                outputCliente.writeUnshared(message);
                outputCliente.flush();
            } catch (IOException e) {
                System.err.println("Erro ao enviar msg \"metodo Send\" " + e.getMessage());
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

    public int getCountRed() {
        return countRed;
    }

    public void addRed() {
        this.countRed++;
    }

    public int getCountBlue() {
        return countBlue;
    }

    public void addBlue() {
        this.countBlue++;
    }

    public int getCountGreen() {
        return countGreen;
    }

    public void addGreen() {
        this.countGreen++;
    }
}
