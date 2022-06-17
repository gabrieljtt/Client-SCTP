import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.*;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CharsetEncoder;

import com.sun.nio.sctp.SctpChannel;
import com.sun.nio.sctp.MessageInfo;

public class Client {

    private InetSocketAddress portServer;
    private InetSocketAddress addressServer;
    private ByteBuffer buffer;
    private SctpChannel sc;
    private CharBuffer cBuffer;
    private Charset charset;
    private CharsetEncoder encoder;
    private CharsetDecoder decoder;

    public Client(String address, String port){
        int intPort = Integer.parseInt(port);
        this.portServer = new InetSocketAddress(intPort);

        this.buffer = ByteBuffer.allocate(1024);
        this.cBuffer = CharBuffer.allocate(1024);

        this.charset = Charset.forName("ISO-8859-1");
        this.encoder = charset.newEncoder();
        this.decoder = charset.newDecoder();

        this.addressServer = new InetSocketAddress(address, intPort);
    }

    public void connection() throws IOException {
        sc = SctpChannel.open(addressServer, 1, 1);
    }

    public void send(String message) throws IOException {
        cBuffer.put(message);
        cBuffer.flip();
        encoder.encode(cBuffer, buffer, true);
        buffer.flip();

        MessageInfo messageInfo = MessageInfo.createOutgoing(addressServer, 0);

        sc.send(buffer, messageInfo);

        cBuffer.clear();
        buffer.clear();

        try {
            receive();
        } catch (IOException e){
            System.out.println("Erro no recebimento de dados!");
            e.printStackTrace();
        }
    }

    public void receive() throws IOException {
        MessageInfo messageInfo = sc.receive(buffer, null, null);
        buffer.flip();

        String message = decoder.decode(buffer).toString();

        System.out.print(messageInfo.address() + " -> ");
        System.out.println(execCommand(message));

        buffer.clear();
    }

    public String execCommand(String message){
        String print = null;

        try {
            Process exec;
            exec = Runtime.getRuntime().exec(message.trim());

            BufferedReader reader = new BufferedReader(new InputStreamReader(exec.getInputStream()));
            StringBuilder output = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                output.append(line).append("\n");
            }
            print = output.toString();

        } catch (IOException e) {
            System.out.println("Comando inexistente!");
            e.printStackTrace();
        }
        return print;
    }

    public SctpChannel getSc(){
        return sc;
    }

}