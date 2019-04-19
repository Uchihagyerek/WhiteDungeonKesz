package game;


import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.util.Arrays;
import java.util.Random;
import java.util.Vector;



public class Map extends Canvas {
    private JFrame frame = null; //ez a JFrame, összeegyeztetés érdekében
    private int roomCount; //szobák száma
    private int maxRooms; //szobák számának felső határa
    private int tries; //próbálkozások a véletlenszerű szoba sorsolásakor
    private int x; //jelenlegi rajz X pozíció
    private int y; //jelenlegi rajz Y pozíció
    private int startX; //kezdő X pozíció
    private int startY; //kezdő Y pozíció
    public static int myX; //játékos X pozíciója
    public static int myY; //játékos Y pozíciója
    public static boolean started = false; //eldönti, hogy a harc az adott szobában elkezdődött-e
    public static int[][] map; //mátrix, ez tartalmazza a térképet
    private int mapsize = 15; //a térkép mérete
    private int monsters = 0; //jelenlegi élő szörnyek száma
    public static boolean boss; //eldönti, hogy a főellenség szobába be lehet-e lépni
    boolean playerDead; //eldönti, hogy a játékos halott-e
    private static Player player; //a játékos
    private int levelCount = 0; //jelenlegi pálya száma
    private Random random = new Random ();
    static boolean defeated = false; //eldönti, hogy a játékos győzött, vagy elmenekült a csatából
    private game.Point[] deltas; //pozíció pont

    /**
     *
     * megnyitja a pályát, itt folyik a játékmenet,
     * tárol minden adatot a jelenlegi folyamatról, illetve a játékosról,
     * elindítja a pálya zenét,
     * kezeli a billentyűzetet / mozgást.
     * @param name a játékos neve;
     */
    Map (String name) {
        setSize (new Dimension (900, 900));
        tries = 0;

        addKeyListener (new KeyAdapter () {
            @Override
            public void keyPressed (KeyEvent evt) {
                moveIt (evt);
            }

        });



        Sounds.playMusic ("bgmusic.wav");


        if (player == null)
            player = new Player (name, this);


        if (! Arrays.asList (DataBase.getPlayers ()).contains (name)) {
            DataBase.newPlayer (name);
        }

        Timer timer = new Timer (1000, new ActionListener () {
            public void actionPerformed (ActionEvent e) {
                player.points -= 50;
                System.out.println (player.points);
            }
        });
        timer.start ();

        deltas = new game.Point[]{
                new game.Point (0, 1),
                new game.Point (0, - 1),
                new game.Point (1, 0),
                new game.Point (- 1, 0)
        };
    }

    //kezeli a mozgást
    public void moveIt (KeyEvent evt) {
        try {
            switch (evt.getKeyCode ()) {
                case KeyEvent.VK_DOWN:
                    if (map[myY + 1][myX] > 0)
                        myY += 1;
                    break;
                case KeyEvent.VK_UP:
                    if (map[myY - 1][myX] > 0)
                        myY -= 1;
                    break;
                case KeyEvent.VK_LEFT:
                    if (map[myY][myX - 1] > 0)
                        myX -= 1;
                    break;
                case KeyEvent.VK_RIGHT:
                    if (map[myY][myX + 1] > 0)
                        myX += 1;
                    break;
                case KeyEvent.VK_NUMPAD0:
                    player.die ();
                    break;
                case KeyEvent.VK_I:         //megjeleníti a játékos jelenlegi statisztikáit
                    player.getStats ();
                    break;
                case KeyEvent.VK_C:         //ki- és bekapcsolja a csalást, meghússzorozza a játékos erejét
                    if(!player.cheat){
                        player.damage*=20;
                        player.cheat=true;
                        System.out.println ("Cheat mode activated");
                    }else{
                        player.damage/=20;
                        player.cheat=false;
                        System.out.println ("Cheat mode deactivated");
                    }
                        break;



            }
        } catch (ArrayIndexOutOfBoundsException ex) {

        }
        repaint ();

    }

    //elkészíti a pályát
    void generateMap () {
        if (playerDead) {
            player = new Player (player.name, this);
            playerDead = false;
        }
        map = new int[mapsize][mapsize];
        clearMap ();
        fillMap ();
        levelCount++;
        System.out.println ("Level " + levelCount);


    }

    //letisztítja a pályát, lenullázza a mátrixot
    private void clearMap () {
        for (int i = 0; i < mapsize; i++) {
            for (int j = 0; j < mapsize; j++) {
                map[i][j] = 0;
            }

        }
    }

    //megtölti a mátrixot
    private void fillMap () {
        getStart ();
        int maxTreasure = 0; //kincses szobák száma
        map[startX][startY] = 1;
        roomCount = 0;
        maxRooms = 20;
        x = startX;
        y = startY;
        map[x][y] = 1;
        while (roomCount < maxRooms - 1) {
            rollRoom (x, y);
            if (map[x][y] == 0) {
                roomCount++;
                if (maxTreasure < 5)
                    map[x][y] = random.nextInt (3) + 1; //2: monster, 3:treasure
                else
                    map[x][y] = random.nextInt (2) + 1;
                if (map[x][y] == 3) {
                    maxTreasure++;
                }
                if (map[x][y] == 2) {
                    monsters++;
                }
            }
        }

        rollRoom (x, y);
        map[x][y] = 1;   //boss room as last one
        for (int i = 0; i < mapsize; i++) {
            for (int j = 0; j < mapsize; j++) {
                System.out.print (map[j][i]);
            }
            System.out.println ();
        }
        boss = false;
        repaint ();
    }

    @Override
    public void update (Graphics g) {
        checkCurrentRoom ();
        paint (g);
    }


    @Override
    public void paint (Graphics ig) {
        BufferedImage image = new BufferedImage (1200, 900, BufferedImage.TYPE_INT_RGB);
        Graphics2D g = image.createGraphics ();
        g.setColor (Color.DARK_GRAY);
        g.fillRect (0, 0, 900, 900);



        int roomSize = 50;
        int roomSize2 = 35;
        for (int oszlop = 0; oszlop < mapsize; oszlop++) {
            for (int sor = 0; sor < mapsize; sor++) {
                if (map[oszlop][sor] > 0) {
                    g.setColor (Color.BLACK);
                    g.fillRect (sor * roomSize, oszlop * roomSize, roomSize, roomSize);
                    g.setColor (Color.GRAY);
                    g.fillRect ((sor * roomSize) + 7, ((oszlop) * roomSize) + 7, roomSize2, roomSize2);
                    if (checkWalls (sor - 1, oszlop)) {
                        g.fillRect (sor * roomSize, ((oszlop) * roomSize) + (roomSize / 3), 10, roomSize / 3);  //left door
                    }

                    if (checkWalls (sor, oszlop - 1)) {
                        g.fillRect ((sor * roomSize) + (roomSize / 3), oszlop * roomSize, roomSize / 3, 10); //top door
                    }

                    if (checkWalls (sor + 1, oszlop)) {
                        g.fillRect (((sor + 1) * roomSize) - 10, ((oszlop) * roomSize) + (roomSize / 3), 10, roomSize / 3); //right door
                    }

                    if (checkWalls (sor, oszlop + 1)) {
                        g.fillRect ((sor * roomSize) + (roomSize / 3), ((oszlop + 1) * roomSize) - 10, roomSize / 3, 10);//bottom door
                    }
                }
            }
        }
        if (map[myY][myX] == 3) {
            g.setColor (Color.YELLOW);
            g.fillRect (750, 500, 100, 50);
            map[myY][myX] = 1;
            System.out.println ("You found treasure!");
            Sounds.playSound ("loot.wav");
            treasureRoom ();
        }
        boss = bossCheck ();
        if (boss) {

            g.setColor (Color.RED);
            g.fillRect ((y * roomSize) + 7, ((x) * roomSize) + 7, roomSize2, roomSize2);
        }
        g.setColor (Color.CYAN);
        g.fillRect ((startY * roomSize) + 7, ((startX) * roomSize) + 7, roomSize2, roomSize2);
        g.setColor (Color.GREEN);
        g.fillOval ((myX * roomSize) + 15, (myY * roomSize) + 15, 20, 20);


        ig.drawImage (image, 0, 0, this);

    }


    //megnézi, hogy a játékos jelenleg milyen szobán áll, folyamatosan fut
    private void checkCurrentRoom(){
        switch(map[myY][myX]){
            case 2:
                setBattleStatus ();
                break;

            case 4:
                setBossStatus ();
                break;
            default:
                break;

        }
    }

    //kezeli a főellenség szoba állapotát
    private void setBossStatus(){
        if (! defeated) {
            System.out.println ("Boss battle starts");
            startBattle ();
        } else {
            map[myY][myX] = 1;
            defeated = false;
            if (levelCount < 8)
                generateMap ();
            else {
                JOptionPane.showMessageDialog (frame, "You Win!");

                DataBase.setScore (player.points, player.name);
                frame.dispose ();
                MainMenu menu = new MainMenu ();
                menu.open (menu);
            }
        }
    }



    //kezeli a szörny szobák állapotát
    private void setBattleStatus (){
        if (! defeated && ! started) {


            System.out.println ("You encountered a monster!");
            startBattle ();


            started = true;


        } else if (defeated) {
            map[myY][myX] = 1;
            monsters--;
            defeated = false;


        }
    }

    //elkezdi a harcot
    private void startBattle () {
        this.setFocusable (false);
        Battle battle = new Battle (this, player, boss);
        JFrame btl = new JFrame ("Battle");
        battle.ui (btl);
        btl.setDefaultCloseOperation (WindowConstants.DISPOSE_ON_CLOSE);
        btl.getContentPane ().add (battle);

        btl.pack ();
        btl.setVisible (true);
        btl.setResizable (false);
        btl.requestFocus ();
    }

    //beállítja a kezdő szobát
    private void getStart () {
        startX = map[0].length / 2;
        startY = map[0].length / 2;
        myX = startX;
        myY = startY;
    }

    //kisorsolja, hogy merre lehet új szobát létrehozni
    private void rollRoom (int x, int y) {
        game.Point actual = new game.Point (x, y);
        game.Point nextPoint = checkStep (actual);
        if (nextPoint != null) {
            this.x = nextPoint.x;
            this.y = nextPoint.y;
        } else if (tries > 4) {
            roomCount = maxRooms;
        } else {
            tries++;
            rollRoom (startX, startY);
        }
    }

    //megnézi, hogy egy bizonyos irányba lehet-e szobát létrehozni
    private game.Point checkStep (game.Point point) {
        Vector<game.Point> vector = new Vector<game.Point> ();
        for (game.Point p : deltas) {
            if (checkPoint (point.add (p))) vector.add (point.add (p));
        }
        if (vector.size () == 0) {
            return null;
        }
        return vector.get (random.nextInt (vector.size ()));
    }


    private boolean checkPoint (Point p) {
        if (p.x < 0) return false;
        if (p.y < 0) return false;
        if (p.x >= mapsize) return false;
        if (p.y >= mapsize) return false;
        return map[p.x][p.y] == 0;
    }


    //megnézi, hogy a szobára hova rajzoljon falat
    private boolean checkWalls (int checkx, int checky) {

        try {
            return ((map[checky][checkx] > 0));
        } catch (IndexOutOfBoundsException ex) {
            return false;
        }

        //returns if it does need a wall;
    }


    //megnézi, hogy a főellenség szobába be lehet-e lépni
    private boolean bossCheck () {

        boolean boss=monsters <= 0;
        if(boss){
            map[x][y]=4;
        }
        return boss;
    }

    //elindítja a játékot, megnyitja az ablakot
    public void start () {
        this.generateMap ();
        if (frame != null) {
            frame.dispose ();

        }

        playerDead = false;
        frame = new JFrame ("Basic Game");
        frame.setDefaultCloseOperation (JFrame.EXIT_ON_CLOSE);
        frame.getContentPane ().add (this);
        frame.pack ();
        frame.setResizable (false);
        frame.setVisible (true);
        this.requestFocus ();
    }

    //kezeli a kincses szobákat
    private void treasureRoom () {
        Treasure treasure = new Treasure ();
        int type = treasure.getType ();
        int value = treasure.getValue ();

        switch (type) {
            case 1:
                player.maxHealth += value;
                player.health += value;
                System.out.println ("Your health has been increased by " + value);
                break;
            case 2:
                player.maxMana += value;
                player.mana += value;
                System.out.println ("Your mana has been increased by " + value);
                break;
            case 3:
                player.damage += value;
                System.out.println ("Your damage has been increased by " + value);
                break;

        }
    }




}
