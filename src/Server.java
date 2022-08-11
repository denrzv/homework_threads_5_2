import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.charset.StandardCharsets;

public class Server extends Thread {
    private final int port;

    public Server(int port) {
        this.port = port;
    }

    private String removeExtraSpaces(String line) {
        return line.trim().replaceAll("\\s+", " ");
    }

    @Override
    public void run() {
        try (ServerSocketChannel serverChannel = ServerSocketChannel.open()){
            serverChannel.socket().bind(new InetSocketAddress(port));
            serverChannel.configureBlocking(false);

            System.out.println("Сервер запущен " + serverChannel.getLocalAddress());

            while (!Thread.interrupted()) {
                try (SocketChannel socketChannel = serverChannel.accept()) {
                    final ByteBuffer inputBuffer = ByteBuffer.allocate(2 << 10);
                    if (socketChannel != null) {
                        System.out.println("Новое подключение от клиента " + socketChannel.getLocalAddress());
                    }
                    while (socketChannel != null) {

                        int bytesCount = socketChannel.read(inputBuffer);
                        if (bytesCount == -1) throw new InterruptedException();

                        String msg = new String(inputBuffer.array(), 0, bytesCount, StandardCharsets.UTF_8);
                        inputBuffer.clear();
                        if (msg.equals("end")) throw new InterruptedException();
                        socketChannel.write(ByteBuffer.wrap((this.getClass().getName() + ": " + removeExtraSpaces(msg))
                                .getBytes(StandardCharsets.UTF_8)));
                    }
                } catch (IOException e) {
                    System.err.println("Ошибка в работе сервера");
                } catch (InterruptedException e) {
                    System.out.println("Сервер завершает работу");
                    Thread.currentThread().interrupt();
                }
            }
        } catch (IOException e) {
            System.err.println("Ошибка открытия сокета сервера");
        }
    }
}
