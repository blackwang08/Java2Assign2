import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.*;

class DrawLinePanel extends JPanel{
    public void paint(Graphics g) {
        //几个参数表示起点的坐标与终点的坐标
        g.drawLine(0, 100, 0, 100);
        g.drawLine(0, 200, 0, 200);
        g.drawLine(100, 0, 100, 0);
        g.drawLine(200, 0, 200, 0);
    }
}
@SuppressWarnings("serial")
public class PositionOfMouse extends JFrame{
    private JLabel label;
    public PositionOfMouse(){
        super("测试程序");//建立新窗体
        this.setSize(300,300);//设置窗体的宽和高
        this.setVisible(true);//设置窗体可见
        this.setLayout(new FlowLayout(FlowLayout.CENTER));//框架流布局且居中对齐
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);//设置点击关闭按钮时的默认操作
        JLabel label = new JLabel("此处显示鼠标左键点击的坐标");
        this.add(label); // 将标签放入窗体

        this.addMouseListener(new MouseAdapter(){  //匿名内部类，鼠标事件
            public void mouseClicked(MouseEvent e){   //鼠标完成点击事件
                if(e.getButton() == MouseEvent.BUTTON1){ //e.getButton就会返回点鼠标的那个键，左键还是右健，3代表右键
                    int x = e.getX();  //得到鼠标x坐标
                    int y = e.getY();  //得到鼠标y坐标
                    String banner = "鼠标当前点击位置的坐标是" + x + "," + y;
                    label.setText(banner);  //修改标签内容
                }
            }
        });
    }
    public static void main(String[] args) {
        // TODO 自动生成的方法存根
        new PositionOfMouse();
    }

}
