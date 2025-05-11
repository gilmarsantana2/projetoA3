import servidor.Server;

public class Verificador {
    public static void main(String[] args) {
        Server server = new Server(args[0], 3333);
        server.ligarServidor();
    }
}
