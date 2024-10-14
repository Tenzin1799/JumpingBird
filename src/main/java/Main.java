import javax.swing.*;
public class Main {
    public static void main(String [] args){
        JFrame frame = new JFrame();
        FlappyBird2 flappyBird2 = new FlappyBird2();

        frame.setVisible(true);
        frame.setResizable(false);
        frame.add(flappyBird2);
       // flappyBird2.requestFocus();
        flappyBird2.requestFocusInWindow();
        frame.pack();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }

}
