public class Main {
    private static final int PORT = 11235;
    private static final String HOST = "localhost";

    public static void main(String[] args) {
        Server server = new Server(PORT);
        server.start();
        Client client = new Client(HOST, PORT);
        client.start();
    }
}