/**
 * Created by Danila on 17.04.2015.
 */
import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Set;

public class Client3 {

    String myIdentity;

    public Client3(String pIdentity) {
        myIdentity = pIdentity;
    }

    void talkToServer() throws InterruptedException {

        try {

            SocketChannel mySocket = SocketChannel.open();

            // non blocking 1
            mySocket.configureBlocking(false);

            // connect to a running server
            mySocket.connect(new InetSocketAddress(InetAddress.getLocalHost(), 9999));

            // get a selector
            Selector selector = Selector.open();

            // register the client socket with "connect operation" to the selector
            mySocket.register(selector, SelectionKey.OP_CONNECT);

            // select() blocks until something happens on the underlying socket
            while (selector.select() > 0) {

                Set keys = selector.selectedKeys();
                Iterator it = keys.iterator();

                while (it.hasNext()) {

                    SelectionKey key = (SelectionKey)it.next();

                    SocketChannel myChannel = (SocketChannel) key.channel();

                    it.remove();

                    if (key.isConnectable()) {
                        if (myChannel.isConnectionPending()) {
                            myChannel.finishConnect();
                            System.out.println("Connection was pending but now is finished connecting.");
                        }

                        ByteBuffer bb = null;

                        while (true) {
                            bb = ByteBuffer.wrap(new String("I am Client : " + myIdentity).getBytes());
                            myChannel.write(bb);
                            bb.clear();
                            synchronized (this) {
                                wait(3000);
                            }
                        }
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) throws InterruptedException {

        Client3 client = new Client3("127.0.0.1");//args[0]);
        client.talkToServer();
    }

}
