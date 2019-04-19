package game;

import java.util.Random;

//kezeli a kincseket
public class Treasure {
    private int type;     //kincs típusa, 1:Életerő, 2:MANA, 3:Erő
    private int value;    //mennyivel növeli meg az adott statot

    public Treasure(){
        Random rand=new Random();
        type=rand.nextInt (3)+1;
        if(type<3)
            value=rand.nextInt (150)+100;
        else
            value=rand.nextInt (10)+10;
    }

    public int getType(){
        return type;
    }

    public int getValue() {
        return value;
    }
}
