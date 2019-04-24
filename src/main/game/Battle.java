package game;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class Battle extends Canvas {


    private int cooldown;  //hátralévő körök száma, mielőtt a varázslat újra használható
    private static JFrame thisFrame; //azért kell, hogy összeegyeztethető legyen a map és ez a JFrame
    private Map map; //a jelenlegi Map osztály, hogy hozzáférhessünk az adataihoz
    Monster enemy; //a jelenlegi ellenfél
    private JProgressBar enemyHealth; //ellenfél életerejét jelzi
    private JProgressBar playerHealth; //játékos életerejét jelzi
    private JProgressBar playerMana; //játékos manáját jelzi
    private JButton attack; //támadás gomb
    private JButton spell; //varázslat gomb
    private JButton potion; //varázsital gomb
    private JButton escape; //menekülés gomb
    private Player player; //jelenlegi játékos
    private boolean boss; //eldöntendő: Főellenség-e, vagy nem


    /**
     * @author Fehér Dávid
     * @param map
     * @param player
     * @param boss
     *
     * létrehozza az ablakot, leállítja a pálya zenét és elindítja a harci zenét,
     * beállítja a felhasználói felületen megjelenő adatokat
     */
    public Battle(Map map, Player player, boolean boss) {

        Sounds.stopMusic ();
        String[] monster;
        setSize(900, 900);
        String music;
        this.boss=boss;
        if (!boss) {
            monster = DataBase.getMonster (0);
            music="battlemusic.wav";
        }
        else {
            monster = DataBase.getMonster (1);
            music="bossmusic.wav";
        }
        this.map = map;
        this.player = player;

        for (int i = 0; i < monster.length; i++) {
            System.out.println(monster[i] + ", ");
        }
        enemy = new Monster(monster[1], Integer.parseInt(monster[2]), Integer.parseInt(monster[3]),monster[4], player);
        Sounds.playMusic (music);


    }


    @Override
    public void update(Graphics g) {
        paint(g);

    }
    @Override
    public void paint(Graphics ig) {
        BufferedImage image = new BufferedImage(900, 900, BufferedImage.TYPE_INT_RGB);
        Graphics2D g = image.createGraphics();
        g.setColor(Color.LIGHT_GRAY);
        g.fillRect (0,0,900,900);
        BufferedImage sprite;
        try {
            sprite=Sprite.resize(ImageIO.read(new File("src/resources/sprites/"+enemy.img)),450,500);
        } catch (IOException e) {
            System.out.println(e.getMessage());
            sprite=null;
        }
        ig.drawImage(image, 0,0,this);

        ig.drawImage(sprite,sprite.getWidth()/2,20,null);

    }

    //beállít minden gombot, progress bar-t
    public void ui(JFrame frame){
        thisFrame=frame;
        attack=new JButton("Támadás");
        attack.setBounds(50,750,200, 50);
        attack.setBackground(Color.WHITE);
        attack.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                attackBtn(false);
            }
        });

        spell=new JButton("Varázslat");
        spell.setBounds(250,750,200, 50);
        spell.setBackground(Color.WHITE);
        spell.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                spell();
            }
        });

        potion =new JButton("Varázsital");
        potion.setBounds(50, 800,200,50);
        potion.setBackground(Color.WHITE);
        potion.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                potionBtn ();
            }
        });

        escape=new JButton("Menekülés");
        escape.setBounds(250, 800, 200,50);
        escape.setBackground(Color.WHITE);
        escape.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                escape();
            }
        });

        enemyHealth=new JProgressBar();

        enemyHealth.setMaximum(enemy.maxHealth);
        enemyHealth.setValue(enemy.maxHealth);
        enemyHealth.setBounds(0,0,900,30);
        enemyHealth.setString(enemy.name+": "+enemy.health+"/"+enemy.maxHealth);
        enemyHealth.setStringPainted(true);
        enemyHealth.setForeground(Color.RED);

        playerHealth=new JProgressBar();
        playerHealth.setMaximum(player.maxHealth);
        playerHealth.setValue(player.health);
        playerHealth.setBounds(500,750,350,50);
        playerHealth.setString("Életerő: "+player.health+"/"+player.maxHealth);
        playerHealth.setStringPainted(true);
        playerHealth.setForeground(Color.GREEN);

        playerMana=new JProgressBar ();
        playerMana.setMaximum (player.maxMana);
        playerMana.setValue (player.mana);
        playerMana.setBounds (500,800,350,50);
        playerMana.setString ("Varázserő: "+player.mana+"/"+player.maxMana);
        playerMana.setStringPainted (true);
        playerMana.setForeground (Color.BLUE);

        JProgressBar playerExp = new JProgressBar();
        playerExp.setMaximum(player.remainingExp);
        playerExp.setValue(player.exp);
        playerExp.setBounds(0,880,900,20);
        playerExp.setString("Tp: "+player.exp+"/"+player.remainingExp);
        playerExp.setStringPainted(true);
        playerExp.setForeground(Color.magenta);

        JLabel level = new JLabel();
        level.setText("Lvl: "+player.level);
        level.setBounds(430,870,50,10);
        level.setBackground(Color.lightGray);
        level.setVisible(true);

        frame.add(attack);
        frame.add(spell);
        frame.add(potion);
        frame.add(escape);
        frame.add(enemyHealth);
        frame.add(playerHealth);
        frame.add(playerMana);
        frame.add(playerExp);
        frame.add(level);

    }

    /**
     *
     * @param spell eldönti, hogy a játékos varázslatot használ-e
     * elindítja a játékos körét,
     * ki- és bekapcsolja a gombokat,
     * beállítja az új statisztikákat,
     * elindítja az ellenfél körét,
     * lejátssza az ellenfél támadási hangját, ha még él.
     */
    private void attackBtn(boolean spell) {
        attack(spell);
        manageButtons();
        setStats ();
        Thread myThread=new Thread() {
            public void run() {
                try {

                    sleep(1000);
                    if(!isDead (enemy))
                    Sounds.playSound ("monster.wav");
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                enemyAttack();
                manageButtons();
            }
        };
        myThread.start();
    }

    //eldönti, hogy hogyan támadjon, lejátssza a megfelelő hangot, megsebzi az ellenfelet
     void attack(boolean spell){

        if(spell){
            enemy.health-=(int) (player.attack() * 1.8);
            Sounds.playSound ("fireball.wav");
        }else {
            enemy.health -= player.attack ();
            Sounds.playSound ("sword.wav");
            System.out.println ("TÁMADÁS!");
        }

        checkKill();
    }

    //kezeli a varázslat gombot és a manát
    private void spell() {
        if(player.mana>20) {

            attackBtn (true);
            cooldown = 3;
            player.mana-=20;
            System.out.println ("Tűzgolyót használtál!");
        }else{

            System.out.println ("Nincs elég varázserőd!");
        }

    }

    //megnézi, hogy a játékos legyőzte-e az ellenfelet
    private void checkKill(){
        if (isDead(enemy)){
            Sounds.stopMusic ();
            enemy.die();
            map.defeated=true;
            if(boss)
                player.points+=500;
            else
                player.points+=50;

            endBattle ();

        }
    }

    //megnézi, hogy a célpont halott-e
    boolean isDead (Entity target){

        return target.health<=0;

    }

    //ellenfél támadása, megsebzi a játékost
    void enemyAttack (){

        player.health-=enemy.attack();
        setStats ();


        if(isDead(player)){

            player.die();
            player=new Player(player.name,map);
        }


    }

    //beállítja és megjeleníti a statisztikákat
    private void setStats() {
        playerHealth.setValue(player.health);
        playerHealth.setString("Életerő: "+player.health+"/"+player.maxHealth);
        playerMana.setValue (player.mana);
        playerMana.setString("Varázserő: "+player.mana+"/"+player.maxMana);
        enemyHealth.setValue(enemy.health);
        enemyHealth.setString(enemy.name+": "+enemy.health+"/"+enemy.maxHealth);
    }

    private void potionBtn(){
        potion();
        setStats ();
    }
    //kezeli a varázsitalokat
    void potion () {
        if (player.potionsCount>0){
            if(player.health+100<=player.maxHealth)
                player.health+=100;
            else
                player.health=player.maxHealth;
            if(player.mana+100<=player.maxMana)
                player.mana+=100;
            else
                player.mana=player.maxMana;

            player.potionsCount--;
        }else{
            System.out.println ("Nincs több varázsitalod!");
        }
    }

    //kezeli a menekülést
    private void escape() {

        map.defeated=false;
        endBattle ();
    }


    //kikapcsolja a gombokat, míg az ellenfél köre tart, ellenőrzi, hogy melyik lehetőségek használhatók.
    private void manageButtons(){
        if (attack.isEnabled()){
            attack.setEnabled(false);
            spell.setEnabled(false);
            potion.setEnabled(false);
            escape.setEnabled(false);
        }else{
            attack.setEnabled(true);
            if(cooldown==0) {
                spell.setEnabled (true);
                spell.setText ("Varázslat");
            }
            else{
                cooldown--;
                spell.setText ("Varázslat ("+(cooldown+1)+")");
            }
            potion.setEnabled(true);
            escape.setEnabled(true);
        }

    }

    /**
     * harc vége,
     * beállítja, hogy a játékos elkezdhet egy új harcot,
     * megnézi, hogy a játékos győzött-e, vagy elmenekült,
     * leállítja a harci zenét, újraindítja a pálya zenét
     *
     */
    private void endBattle(){

        map.setFocusable(true);
        map.requestFocus();
        thisFrame.dispose();
        map.started=false;
        if(map.defeated){
            map.repaint ();
        }
        Sounds.playMusic ("bgmusic.wav");

    }






}
