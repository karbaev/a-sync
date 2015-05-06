/**
 * Created by Danila on 17.04.2015.
 */
import java.net.*;
import java.nio.*;
import java.io.*;
import java.util.*;


public class EchoClient2
{

    public static void main(String [] args)
    {
        byte ch='a';
        try{
            Socket socket = new Socket("localhost",7000);
            OutputStream out = socket.getOutputStream();
            InputStream in = socket.getInputStream();
            int closein=5;

            while(true){
                Thread.sleep(1000);
                out.write((byte) ch++);
                System.out.println((char) in.read());
                if (--closein<=0) socket.close();
            }
        }
        catch (InterruptedException ex) {}
        catch (IOException ex) {}
        catch (RuntimeException ex) {}
    }

}
