import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Arrays;

class Player {
    Socket socket = null;
    DataOutputStream dataoutput = null;
    DataInputStream datainput = null;
    String name;
    String mark;

    public Player(Socket s) {
        this.socket = s;
        try {
            InputStream input = s.getInputStream();
            this.datainput = new DataInputStream(input);
            OutputStream output = s.getOutputStream();
            this.dataoutput = new DataOutputStream(output);
        } catch (IOException e) {
            System.err.println("Server connect Error");
            System.exit(1);
        }
    }

    public void init(String name, String mark) {
        this.name = name;
        this.mark = mark;
    }
}

public class myServer {
    static final String MARK_1 = "X";
    static final String MARK_2 = "O";
    static final String MARK_NONE = " ";
    static final String YOUR_TURN = "YOUR_TURN";
    static final String WIN = "WIN";
    static final String DRAW = "DRAW";
    static final String FAIL = "FAIL";
    static final String ENDGAME = "ENDGAME";
    static String[][] chessBoard = {
            {MARK_NONE, MARK_NONE, MARK_NONE},
            {MARK_NONE, MARK_NONE, MARK_NONE},
            {MARK_NONE, MARK_NONE, MARK_NONE}
    };

    public static void main(String[] args) throws IOException {
        ServerSocket serverSocket = null;
        Socket client1 = null;
        Socket client2 = null;
        Player playerNow;
        Player playerNext;
        boolean fin = false;

        try {
            serverSocket = new ServerSocket(8888);
            System.out.println("server construct on port 8888");
        } catch (IOException e) {
            System.err.println("server initial failure, port occupied?");
            System.exit(1);
        }

        try {
            System.out.println("Waiting for the connection of player1");
            client1 = serverSocket.accept();
        } catch(IOException | SecurityException e) {
            System.err.println ("Error for connection");
            System.exit(1);
        }
        playerNow = new Player(client1);
        String name1 = playerNow.datainput.readUTF();
        System.out.println("Player 1 : " + name1);
        playerNow.dataoutput.writeUTF(MARK_1 + "\nWaiting");
        playerNow.init(name1, MARK_1);
        try {
            System.out.println("Waiting for the connection of player2");
            client2 = serverSocket.accept();
        } catch(IOException | SecurityException e) {
            System.err.println ("Error for connection");
            System.exit(1);
        }
        playerNext = new Player(client2);
        String name2 = playerNext.datainput.readUTF();
        System.out.println("Player 2 : " + name2);
        playerNext.dataoutput.writeUTF(MARK_2 + "\nStart");
        playerNext.init(name2, MARK_2);
        while (!fin) {
            try {
                playerNow.dataoutput.writeUTF(YOUR_TURN);

                playerNow.dataoutput.writeUTF(Arrays.deepToString(chessBoard));

                String index = playerNow.datainput.readUTF();
                int x = Integer.parseInt(index.split(",")[0]);
                int y = Integer.parseInt(index.split(",")[1]);
                System.out.println(Arrays.deepToString(chessBoard));

                if ((x > -1) && (y > -1) &&
                        (x < 3)  && (y < 3)  &&
                        (chessBoard[x][y].equals(MARK_NONE))) {
                    chessBoard[x][y] = playerNow.mark;
                    String state = state();

                    if (state.equals(ENDGAME)) {
                        playerNow.dataoutput.writeUTF(WIN);
                        playerNext.dataoutput.writeUTF(FAIL);
                        System.out.println(playerNow.datainput.readUTF());
                        System.out.println(playerNext.datainput.readUTF());
                        fin = true;
                    } else if (state.equals(DRAW)) {
                        playerNow.dataoutput.writeUTF(DRAW);
                        playerNext.dataoutput.writeUTF(DRAW);
                        System.out.println(playerNow.datainput.readUTF());
                        System.out.println(playerNext.datainput.readUTF());
                        fin = true;
                    } else {
                        Player temp = playerNext;
                        playerNext = playerNow;
                        playerNow = temp;

                    }
                }
            }catch (Exception e) {
                System.err.println("error client exit");
                System.exit(1);
            }
        }
    }

    private static String state() {
        for (int i = 0; i < 3; i++) {
            if ((!chessBoard[i][0].equals(MARK_NONE))
                    && chessBoard[i][0].equals(chessBoard[i][1])
                    && chessBoard[i][1].equals(chessBoard[i][2])) {
                return ENDGAME;
            }
            if ((!chessBoard[0][i].equals(MARK_NONE))
                    && chessBoard[0][i].equals(chessBoard[1][i])
                    && chessBoard[1][i].equals(chessBoard[2][i])) {
                return ENDGAME;
            }
        }
        if ((!chessBoard[0][0].equals(MARK_NONE))
                && chessBoard[0][0].equals(chessBoard[1][1])
                && chessBoard[1][1].equals(chessBoard[2][2])) {
            return ENDGAME;
        }
        if ((!chessBoard[2][0].equals(MARK_NONE))
                && chessBoard[2][0].equals(chessBoard[1][1])
                && chessBoard[1][1].equals(chessBoard[0][2])) {
            return ENDGAME;
        }
        if (isdraw()) {
            return DRAW;
        }
        return "Continue";
    }

    private static boolean isdraw() {
        for(int i = 0; i < 3; i++){
            for (int j = 0; j < 3; j++) {
                if(chessBoard[i][j].equals(MARK_NONE)){
                    return false;
                }
            }
        }
        return true;
    }
}
