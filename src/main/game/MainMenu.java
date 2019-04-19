package game;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;

/**
 * Kezeli a főmenüt,
 * megjeleníti a lehetőségeket.
 */

public class MainMenu extends Canvas {
    private static JFrame thisFrame;

    public MainMenu(){
        setSize(900,900);
    }



    @Override
    public void update(Graphics g) {paint(g);}
    @Override
    public void paint(Graphics ig) {
        BufferedImage image = new BufferedImage(900, 900, BufferedImage.TYPE_INT_RGB);
        Graphics2D g = image.createGraphics();
        g.setColor(Color.LIGHT_GRAY);
        g.fillRect (0,0,900,900);

        ig.drawImage(image, 0,0,this);


    }

    //kezeli, megjeleníti a gombokat
    public void ui(JFrame frame){
        thisFrame=frame;


        JButton start = new JButton("Játék");
        start.setBounds(250,450,400,150);
        start.setBackground(Color.WHITE);
        start.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                startGame();
            }
        });

        JButton highScores = new JButton("Ranglista");
        highScores.setBounds(250,600,400,150);
        highScores.setBackground(Color.WHITE);
        highScores.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                highScores();
            }
        });


        JButton exit = new JButton("Kilépés");
        exit.setBounds(250,750,400, 150);
        exit.setBackground(Color.WHITE);
        exit.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                exitButton();
            }
        });

        frame.add(start);
        frame.add(highScores);
        frame.add(exit);


    }

    //a ranglista ablakot nyitja meg
    private void highScores() {
        HighScores hs=new HighScores();
        JFrame hiscr= new JFrame("Battle");
        hs.ui(hiscr);
        hiscr.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        hiscr.getContentPane().add(hs);

        hiscr.pack();
        hiscr.setVisible(true);
        hiscr.setResizable(false);
        hiscr.requestFocus();

    }

    //elindítja a játékot
    private void startGame(){

        String name = JOptionPane.showInputDialog("Add meg a neved");
        Map map=new Map(name);
        map.start();


        thisFrame.dispose();
    }

    //bezárja a programot
    private void exitButton(){
        thisFrame.dispose();

    }

    //ez nyitja meg a menü ablakot
    public void open(MainMenu menu){
        final JFrame frame=new JFrame();
        menu.ui(frame);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.getContentPane().add(menu);
        frame.pack();
        frame.setResizable(false);
        frame.setVisible(true);
        menu.requestFocus();
    }

}
