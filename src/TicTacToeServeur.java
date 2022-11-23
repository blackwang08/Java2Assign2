import java.io.*;
import java.net.*;

//定义玩家类
class Joueur {
    Socket joueur = null;
    DataOutputStream dataoutput = null;
    DataInputStream datainput = null;
    String name;
    String mark;
    //构造函数，初始化soket对象
    Joueur (Socket j) {
        this.joueur = j;
        try {
            InputStream input = j.getInputStream();
            this.datainput = new DataInputStream(input);
            OutputStream output = j.getOutputStream();
            this.dataoutput = new DataOutputStream(output);
        } catch (IOException e) {
            System.err.println("Erreur ouverture des flux serveur");
            System.exit(1);
        }
    }
    void initJoueur (String name, String mark) {
        this.name = name;
        this.mark = mark;
    }
}

// 创建服务器类
class TicTacToeServeur {

    static final String MARK_1 = "X";
    static final String MARK_2 = "O";
    static final String MARK_NONE = " ";
    static final String JOUEZ = "JOUEZ";
    static final String POSITION = "POSITION";
    static final String ADV_KO = "ABANDON";
    static final String INVALIDE = "INVALIDE";
    static final String GAGNE = "GAGNE";        //胜利
    static final String FINI = "FINI";          //平局结束
    static final String PERDU = "PERDU";        //失败

    static String[] cases = {MARK_NONE,MARK_NONE,MARK_NONE,MARK_NONE,MARK_NONE,MARK_NONE,MARK_NONE,MARK_NONE,
            MARK_NONE};                        //创建棋盘


    public static void main(String [] argv) throws IOException, SecurityException {

        ServerSocket connexion = null;
        Socket client1 = null;
        Socket client2 = null;
        Joueur joueurActuel = null;
        Joueur joueurProchain = null;
        int position = -1;
        boolean fini = false;

        //创建serversoket，并初始化端口
        try {
            connexion = new ServerSocket(8888);
            System.out.println("Attachement serveur sur port 8888");
        } catch (IOException e) {
            System.err.println("Erreur initialisation serveur");
            System.exit(1);
        }

        // 第一位玩家（客户端1）连接
        try {
            System.out.println("Waitting for the connection of joueur1");
            client1 = connexion.accept();
        } catch (IOException | SecurityException e) {
            System.err.println ("Error for connection");
            System.exit(1);
        }
        joueurActuel = new Joueur(client1);      //设置客户端1为当前回合玩家

        String nom1 = joueurActuel.datainput.readUTF();    //接收客户端1发送的玩家名称
        System.out.println("Joueur 1 : "+ nom1);

//为客户端1分配“X”标志
        joueurActuel.dataoutput.writeUTF(MARK_1);
        joueurActuel.initJoueur(nom1, MARK_1);

        // 第二位玩家（客户端2）连接
        try {
            System.out.println("Waitting for the connection of joueur2");
            client2 = connexion.accept();
        } catch (IOException e) {
            System.err.println ("Error for connection");
            System.exit(1);
        } catch (SecurityException e) {
            System.err.println("Error for connection");
            System.exit(1);
        }

        // 将客户端2设置为第二个玩家
        joueurProchain = new Joueur(client2);
        String nom2 = joueurProchain.datainput.readUTF();
        if (!nom2.equals(nom1)) {                       //判断玩家2是否与玩家1重名
            System.out.println("Joueur 2 : "+ nom2);
            joueurProchain.dataoutput.writeUTF(MARK_2);
            joueurProchain.initJoueur(nom2, MARK_2);
        } else {
            joueurProchain.dataoutput.writeUTF(MARK_NONE);
            joueurActuel.dataoutput.writeUTF(FINI);
            client1.close();
            client2.close();
            System.exit(1);
        }

        while (!fini) {                                         //开始游戏
//客户端发送部分
            joueurActuel.dataoutput.writeUTF(JOUEZ);                //向当前玩家发送回合标志“JOUEZ”
            joueurActuel.dataoutput.writeInt(position);             //发送“位置”
//客户端接收部分
            String message = joueurActuel.datainput.readUTF();
            position = joueurActuel.datainput.readInt();
            System.out.println(message + " " + position);
//落子与判断结果部分
            if((position >= 0)&&(position <=8)&&(cases[position].equals(MARK_NONE))) {
                cases[position] = new String(joueurActuel.mark);
                String etatPlateau = etat();            //状态

                if(etatPlateau.equals("GAGNE")) {
                    joueurActuel.dataoutput.writeUTF(GAGNE);
                    joueurProchain.dataoutput.writeUTF(PERDU);
                    fini = true;
                }

                else if(etatPlateau.equals("PLEIN")) {
                    joueurActuel.dataoutput.writeUTF(FINI);
                    joueurProchain.dataoutput.writeUTF(FINI);
                    fini = true;
                }
                //交换当前玩家与下一名玩家，意思就是玩家交换回合~
                else {
                    Joueur temp = joueurProchain;
                    joueurProchain = joueurActuel;
                    joueurActuel = temp;
                }
            }
            else {
                Joueur temp = joueurProchain;
                joueurProchain = joueurActuel;
                joueurActuel = temp;
            }
        }
        joueurActuel.joueur.close();
        joueurProchain.joueur.close();
        connexion.close();
    }

    //判断状态部分
    public static String etat() {
        if(estPlein()){
            return "PLEIN";
        }
        for(int i = 0; i <= 6; i+=3) {
            if((!cases[i].equals(MARK_NONE)) && cases[i].equals(cases[i+1]) && cases[i+1].equals(cases[i+2])) {
                return "GAGNE";
            }
        }
        for(int i = 0; i <= 2; i+=1) {
            if((!cases[i].equals(MARK_NONE)) && cases[i].equals(cases[i+3]) && cases[i+3].equals(cases[i+6])){
                return "GAGNE";
            }
        }
        if((!cases[0].equals(MARK_NONE)) && cases[0].equals(cases[4]) && cases[4].equals(cases[8])){
            return "GAGNE";
        }
        if((!cases[2].equals(MARK_NONE)) && cases[2].equals(cases[4]) && cases[4].equals(cases[6])){
            return "GAGNE";
        }
        return "CONTINUE";
    }

    public static boolean estPlein(){
        for(int i = 0; i < cases.length; i++){
            if(cases[i].equals(MARK_NONE)){
                return false;
            }
        }
        return true;
    }
}
