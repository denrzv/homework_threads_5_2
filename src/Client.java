import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;

public class Client {
    private final String host;
    private final int port;
    private final long DELAY = 1000;

    public Client(String host, int port) {
        this.host = host;
        this.port = port;
    }

    public void start() {
        InetSocketAddress socketAddress = new InetSocketAddress(host, port);

        try (SocketChannel socketChannel = SocketChannel.open();
             Scanner scanner = new Scanner(System.in)) {

            socketChannel.connect(socketAddress);
            final ByteBuffer inputBuffer = ByteBuffer.allocate(2 << 10);
            String msg;
            System.out.println("Введите строчки с лишними пробелами. Для выхода наберите 'end'.");

            while (scanner.hasNext()) {
                msg = scanner.nextLine();

                if (msg.equals("end")) {
                    System.out.println("Клиент завершает работу");
                    break;
                }
                socketChannel.write(ByteBuffer.wrap(msg.getBytes(StandardCharsets.UTF_8)));

                Thread.sleep(DELAY);
                int bytesCount = socketChannel.read(inputBuffer);
                System.out.println(new String(inputBuffer.array(), 0, bytesCount, StandardCharsets.UTF_8));
                inputBuffer.clear();
            }

        } catch (UnknownHostException e) {
            System.err.println("Неверно задан адрес сервера!");
        } catch (IOException e) {
            System.err.println("Ошибка в работе клиента!");
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
