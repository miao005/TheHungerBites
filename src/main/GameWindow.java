package main;

import javax.swing.JFrame;

public class GameWindow {
    public JFrame jframe;

    public GameWindow(GamePanel gamePanel){
        jframe = new JFrame();
        jframe.setTitle("The Hunger Bites");
        jframe.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        jframe.setResizable(false); // Important for pixel art
        jframe.add(gamePanel);
        jframe.pack();
        jframe.setLocationRelativeTo(null);
        jframe.setVisible(true);

        System.out.println("Game Window: " + GamePanel.WIDTH + "x" + GamePanel.HEIGHT);
    }
}