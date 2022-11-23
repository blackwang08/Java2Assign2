import java.io.*;
import java.net.InetAddress;
import java.net.Socket;
import java.util.Scanner;

public class myClient {
    static final String MARK_NONE = " ";
    static String[][] chessBoard = {
            {MARK_NONE, MARK_NONE, MARK_NONE},
            {MARK_NONE, MARK_NONE, MARK_NONE},
            {MARK_NONE, MARK_NONE, MARK_NONE}
    };

    public static void main(String[] args) throws IOException {
        InetAddress IPaddress = null;
        Socket client = null;
        DataOutputStream dataoutput = null;
        DataInputStream datainput = null;
        String mark;
        String index;
        try {
            System.out.println("Connection with the server");
            IPaddress = InetAddress.getLocalHost();
            client = new Socket(IPaddress,8888);
        } catch (IOException e) {
            System.err.println ("Error for connection");
            System.exit(1);
        }

        try {
            InputStream input = client.getInputStream();
            datainput = new DataInputStream(input);
            OutputStream output = client.getOutputStream();
            dataoutput = new DataOutputStream(output);
        } catch (IOException e) {
            System.err.println("Error for connection Stream");
            System.exit(1);
        }
        Scanner in = new Scanner(System.in);
        String name = in.nextLine();
        dataoutput.writeUTF(name);
        mark = datainput.readUTF();
        System.out.println(mark);
        while (true) {
            mark = datainput.readUTF();
            if (!mark.equals("YOUR_TURN")){
                dataoutput.writeUTF("End");
                System.out.println(mark);
                break;
            }
            System.out.println(mark);
            String chessboardToString = datainput.readUTF();
            System.out.println(chessboardToString);
            index = in.nextLine();
            dataoutput.writeUTF(index);
        }
    }
}
