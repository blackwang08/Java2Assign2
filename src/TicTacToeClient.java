import java.io.*;
import java.net.*;
import java.util.*;

public class TicTacToeClient {

    static final String MARK_1 = "X";
    static final String MARK_2 = "O";
    static final String MARK_NONE = " ";
    static final String JOUEZ = "JOUEZ";
    static final String POSITION = "POSITION";
    static final String INVALIDE = "INVALIDE";
    static final String GAGNE = "GAGNE";
    static final String FINI = "FINI";
    static final String PERDU = "PERDU";

    static String cases[] = {MARK_NONE,MARK_NONE,MARK_NONE,MARK_NONE,MARK_NONE,MARK_NONE,MARK_NONE,MARK_NONE,MARK_NONE};



    public static void main(String [] argv) throws IOException, SecurityException {
//          记得设置客户端名字比如 argv[0] = "李狗蛋";
        InetAddress IPaddress = null;
        Socket client = null;
        DataOutputStream dataoutput = null;
        DataInputStream datainput = null;
        String mark;
        String markAdv;
        int position = -1;
        boolean fini = false;

        //与服务器连接
        try {
            System.out.println("Connection with the serveur");
            IPaddress = InetAddress.getLocalHost();
            client = new Socket(IPaddress,8888);

        } catch (IOException e) {
            System.err.println ("Error for connection");
            System.exit(1);
        }

        //打开与服务器的通讯通道
        try {
            InputStream input = client.getInputStream();
            datainput = new DataInputStream(input);
            OutputStream output = client.getOutputStream();
            dataoutput = new DataOutputStream(output);
        } catch (IOException e) {
            System.err.println("Erreur ouverture des flux joueur");
            System.exit(1);
        }

        // 向服务器发送玩家名字
        dataoutput.writeUTF(argv[0]);
        //接受服务器分配的标志（“X”;“O”）
        mark = datainput.readUTF();
        if (mark.equals(MARK_NONE)) {
            System.out.println("Nom joueur incorrect");
            client.close();
            System.exit(1);
        }
        if (mark.equals(MARK_1)) {
            markAdv = new String(MARK_2);
        } else {
            markAdv = new String(MARK_1);
        }

        Scanner sc = new Scanner(System.in);
        while (!fini) {
//客户端接收部分
            mark = datainput.readUTF();
            System.out.println(mark);
            datainput.readInt();
//客户端发送部分
            dataoutput.writeUTF(argv[0]+" coup ");
            System.out.println("Position est "+ position);
            position = sc.nextInt();
            dataoutput.writeInt(position);
            System.out.println("Position est change a "+ position);
        }
        sc.close();
        client.close();
    }
}
