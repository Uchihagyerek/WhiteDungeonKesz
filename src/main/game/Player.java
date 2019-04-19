package game;

import javax.swing.*;
import java.util.Random;

public class Player extends Entity{
    public int exp; //játékos jelenlegi tapasztalatpontja
    public int points;   //játékos jelenlegi pontszáma
    public int remainingExp; //játékos hátralévő tapasztalatpontja a szintlépéshez
    public int potionsCount; //játékos jelenlegi varázsital száma
    public int damage; //játékos ereje
    public boolean cheat=false; //csalás mód be van-e kapcsolva
    Map map; //a pálya
    public Player(String name, Map map){
        this.name=name;
        damage=20;
        level=1;
        maxHealth=500;
        maxMana=500;
        health=maxHealth;
        mana=maxMana;
        exp=0;
        remainingExp=1000;
        this.map=map;
        potionsCount=3;
        points = 5000;
    }

    //ellenőrzi, hogy a játékos szintet tud-e lépni, ezt kezeli
    public void levelUp(){
        if(exp>=remainingExp) {
            if (remainingExp < exp)
                exp = exp - remainingExp;
            else
                exp = 0;
            level++;
            damage*=1.2;
            maxHealth += level * 100;
            maxMana += level * 100;
            remainingExp *= 1.2;
            health = maxHealth;
            mana = maxMana;
            System.out.println ("LEVEL UP! Your stats have been increased!");
            getStats();
            levelUp();
        }
    }

    //kiírja a játékos jelenlegi statisztikáit
    public void getStats() {
        System.out.println ("Életerő: "+maxHealth);
        System.out.println ("Mana: "+maxMana);
        System.out.println ("Erő: "+damage);
    }

    //megsebzi az ellenfelet
    int attack() {
        Random rand=new Random();
        int minDmg= (int) Math.floor(damage-damage*0.3);
        int maxDmg=(int) Math.ceil(damage+damage*0.3);
        int dmgDealt=rand.nextInt(maxDmg-minDmg)+minDmg;
        System.out.println(dmgDealt);
        return dmgDealt;
    }
    //halálakor levon 3000 pontot, felveszi azt az adatbázisba, megkérdezi, hogy szeretnénk-e újrakezdeni
    void die(){
        points-=3000;
        DataBase.setScore(points, name);
        int restart= JOptionPane.YES_NO_OPTION;
        restart=JOptionPane.showConfirmDialog(null,"Would you like to restart?","You died!",restart);
        if(restart==JOptionPane.YES_OPTION){
            map.playerDead=true;
        map.generateMap();
        }else if (restart==JOptionPane.NO_OPTION){
            System.exit(1);

        }
    }
}
