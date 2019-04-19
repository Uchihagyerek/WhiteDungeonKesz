package game;

import javax.swing.*;
import java.awt.*;
import java.util.List;

//Ez az osztály kezeli a ranglista ablakot, lekérdezi és megjeleníti az adatokat pontok alapján csökkenő sorrendben.
public class HighScores extends Canvas {

    static JFrame thisFrame;

    public HighScores(){

        setSize(500,500);
    }

    public void ui(JFrame frame){

        thisFrame=frame;

        List<String[]> scores=DataBase.getScores();
        String[] score;

        for (int i = 0; i < scores.size()&&i< 10; i++) {
            score=scores.get(i);
            JLabel placement=new JLabel((i+1)+".");
            JLabel name=new JLabel(score[0]);
            JLabel points =new JLabel(score[1]);

            placement.setBounds(0,i*50,20,50);
            name.setBounds(250,i*50,150,50);
            points.setBounds(400,i*50,100,50);

            frame.add(placement);
            frame.add(name);
            frame.add(points);

        }
    }
}
