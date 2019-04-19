package game;

import java.util.Random;

public class Monster extends Entity {
    private int damage; //szörny ereje
    private Player player; //szörny ellenfele (játékos)
    public Monster(String name, int health, int damage, Player player){
    this.name=name;
    this.health=health;
    this.damage=damage;
    this.player=player;
    this.maxHealth=health;
    }

    //megsebzi a játékost
    synchronized int attack() {

        Random rand=new Random();
        int minDmg= (int) Math.floor(damage-damage*0.3);
        int maxDmg=(int) Math.ceil(damage+damage*0.3);
        int dmgDealt=rand.nextInt(maxDmg-minDmg)+minDmg;
        System.out.println(dmgDealt);
        return dmgDealt;
    }
    //halálakor tapasztalatpontot ad a játékosnak
    void die(){
        if(Map.boss){
            player.exp+=player.level*65;
        }else {
            player.exp += player.level * 30;
        }
        player.levelUp();
    }
}
