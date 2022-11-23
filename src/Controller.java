import java.io.*;
import java.net.InetAddress;
import java.net.Socket;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.Scanner;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;


class Index {
    int X;
    int Y;
    @Override
    public String toString() {
        return X + "," + Y;
    }

    public int getX() {
        return X;
    }

    public int getY() {
        return Y;
    }

    public void setX(int x) {
        X = x;
    }

    public void setY(int y) {
        Y = y;
    }
}
public class Controller implements Initializable {
    private static final int PLAY_1 = 1;
    private static final int PLAY_2 = 2;
    private static final int EMPTY = 0;
    private static final int BOUND = 90;
    private static final int OFFSET = 15;

    @FXML
    private Pane base_square;

    @FXML
    private Rectangle game_panel;

    private static boolean TURN = false;

    private static int[][] chessBoard = new int[3][3];
    private static boolean[][] flag = new boolean[3][3];

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        InetAddress IPaddress = null;
        Socket client = null;
        DataOutputStream dataoutput = null;
        DataInputStream datainput = null;
        final String[] mark = new String[1];
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
        try {
            dataoutput.writeUTF(name);
            mark[0] = datainput.readUTF();
        } catch (IOException e) {
            System.err.println("Error for server");
            System.exit(1);
        }
        System.out.println(mark[0]);
        if (mark[0].split("\n")[0].equals("X")){
            TURN = false;
        }
        else {
            TURN = true;
        }

        DataInputStream finalDatainput = datainput;
        DataOutputStream finalDataoutput = dataoutput;
        game_panel.setOnMousePressed(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                try {
                    mark[0] = finalDatainput.readUTF();
                    if (!mark[0].equals("YOUR_TURN")){
                        System.out.println(mark[0]);
                        finalDataoutput.writeUTF("player" + mark[0] + " end");
                        System.exit(0);
                    }
                    System.out.println(mark[0]);
                    String chessboardToString = finalDatainput.readUTF();
                    chessBoard = readChessboard(chessboardToString);
                    flag = new boolean[3][3];
                    drawChess();
                    System.out.println(chessboardToString);

                } catch (IOException e) {
                    System.err.println("error server exit");
                    System.exit(1);
                }
            }
        });

        game_panel.setOnMouseReleased(event -> {
            int x = (int) (event.getX() / BOUND);
            int y = (int) (event.getY() / BOUND);
            String index = x + "," + y;
            try {
                finalDataoutput.writeUTF(index);
            } catch (IOException e) {
                System.err.println("error server exit");
                System.exit(1);
            }
            if (refreshBoard(x, y)) {
            }
        });

    }

    private int[][] readChessboard(String chessboardToString) {
        int[][] res = new int[3][3];
        String[] read = chessboardToString.split("],");
        for (int i = 0; i < 3; i++) {
            if (read[i].charAt(2) == 'X'){
                res[i][0] = 2;
            }
            else if (read[i].charAt(2) == 'O'){
                res[i][0] = 1;
            }
            if (read[i].charAt(5) == 'X'){
                res[i][1] = 2;
            }
            else if (read[i].charAt(5) == 'O'){
                res[i][1] = 1;
            }
            if (read[i].charAt(8) == 'X'){
                res[i][2] = 2;
            }
            else if (read[i].charAt(8) == 'O'){
                res[i][2] = 1;
            }
        }
        return res;
    }

    private boolean refreshBoard (int x, int y) {
        if (chessBoard[x][y] == EMPTY) {
            chessBoard[x][y] = TURN ? PLAY_1 : PLAY_2;
            drawChess();
            return true;
        }
        return false;
    }

    private void drawChess () {
        for (int i = 0; i < chessBoard.length; i++) {
            for (int j = 0; j < chessBoard[0].length; j++) {
                if (flag[i][j]) {
                    // This square has been drawing, ignore.
                    continue;
                }
                switch (chessBoard[i][j]) {
                    case PLAY_1:
                        drawCircle(i, j);
                        break;
                    case PLAY_2:
                        drawLine(i, j);
                        break;
                    case EMPTY:
                        // do nothing
                        break;
                    default:
                        System.err.println("Invalid value!");
                }
            }
        }
    }

    private void drawCircle (int i, int j) {
        Circle circle = new Circle();
        base_square.getChildren().add(circle);
        circle.setCenterX(i * BOUND + BOUND / 2.0 + OFFSET);
        circle.setCenterY(j * BOUND + BOUND / 2.0 + OFFSET);
        circle.setRadius(BOUND / 2.0 - OFFSET / 2.0);
        circle.setStroke(Color.RED);
        circle.setFill(Color.TRANSPARENT);
        flag[i][j] = true;
    }

    private void drawLine (int i, int j) {
        Line line_a = new Line();
        Line line_b = new Line();
        base_square.getChildren().add(line_a);
        base_square.getChildren().add(line_b);
        line_a.setStartX(i * BOUND + OFFSET * 1.5);
        line_a.setStartY(j * BOUND + OFFSET * 1.5);
        line_a.setEndX((i + 1) * BOUND + OFFSET * 0.5);
        line_a.setEndY((j + 1) * BOUND + OFFSET * 0.5);
        line_a.setStroke(Color.BLUE);

        line_b.setStartX((i + 1) * BOUND + OFFSET * 0.5);
        line_b.setStartY(j * BOUND + OFFSET * 1.5);
        line_b.setEndX(i * BOUND + OFFSET * 1.5);
        line_b.setEndY((j + 1) * BOUND + OFFSET * 0.5);
        line_b.setStroke(Color.BLUE);
        flag[i][j] = true;
    }
}
