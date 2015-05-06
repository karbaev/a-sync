/**
 * Created by Danila on 17.04.2015.
 */
import java.nio.*;
import java.nio.channels.*;
import java.net.*;
import java.util.*;
import java.io.IOException;

public class EchoServer2
{
    public static int DEFAULT_PORT=7000;

    public static void main(String [] args)
    {

        ServerSocketChannel serverChannel;
        Selector selector;
        try
        {
            serverChannel = ServerSocketChannel.open();
            ServerSocket ss = serverChannel.socket();
            InetSocketAddress address = new InetSocketAddress(DEFAULT_PORT);
            ss.bind(address);
            serverChannel.configureBlocking(false);
            selector=Selector.open();
            serverChannel.register(selector,SelectionKey.OP_ACCEPT);
        } catch(IOException ex) {ex.printStackTrace(); return;}


        while(true)
        {
            int selectednum=0;
            try{
                selectednum=selector.select();  //blocks
            }catch (IOException ex) {ex.printStackTrace(); break;}
            if (selectednum>0) {
                Set<SelectionKey> readyKeys = selector.selectedKeys();
                Iterator<SelectionKey> iterator = readyKeys.iterator();
                while (iterator.hasNext()) {
                    SelectionKey key=iterator.next();
                    iterator.remove();
                    try{

                        if (key.isValid()==false) {key.cancel(); key.channel().close(); continue; }

                        if (key.isAcceptable()){
                            ServerSocketChannel server = (ServerSocketChannel) key.channel();
                            SocketChannel client = server.accept();
                            System.out.println("Accepted from "+client);
                            client.configureBlocking(false);
                            SelectionKey clientKey=client.register(
                                    selector, SelectionKey.OP_WRITE | SelectionKey.OP_READ);
                            ByteBuffer buffer = ByteBuffer.allocate(100);
                            clientKey.attach(buffer);
                        }
                        if (key.isReadable()){
                            SocketChannel client = (SocketChannel) key.channel();
                            ByteBuffer output = (ByteBuffer) key.attachment();
                            System.out.println("Reading.."+key.channel());
                            client.read(output);
                        }
                        if (key.isWritable()){
                            SocketChannel client = (SocketChannel) key.channel();
                            ByteBuffer output = (ByteBuffer) key.attachment();
                            output.flip();
                            System.out.println("Writing..");
                            client.write(output);
                            output.compact();
                        }
                    } catch (IOException ex) {
                        key.cancel();
                        try { key.channel().close();}
                        catch (IOException cex) {};
                    }
                }
            }
        }
    }
}