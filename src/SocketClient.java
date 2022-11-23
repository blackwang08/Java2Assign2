import java.io.*;
import java.net.Socket;
import java.util.Scanner;

public class SocketClient {
    public static void main(String[] args) throws InterruptedException {
        try {
            // 和服务器创建连接
            while (true){
                Socket socket = new Socket("localhost",8088);
                // 要发送给服务器的信息
                OutputStream os = socket.getOutputStream();
                PrintWriter pw = new PrintWriter(os);
                Scanner sc = new Scanner(System.in);
                String s = "";
                s = sc.nextLine();
                if (s.equals("-1")){
                    break;
                }
                pw.write(s);
                pw.flush();

                socket.shutdownOutput();

                // 从服务器接收的信息
                InputStream is = socket.getInputStream();
                BufferedReader br = new BufferedReader(new InputStreamReader(is));
                String info = null;
                while((info = br.readLine())!=null){
                    System.out.println("我是客户端，服务器返回信息："+info);
                }
                socket.close();

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
