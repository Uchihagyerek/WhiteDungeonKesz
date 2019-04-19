package game;

import org.junit.Test;

import static org.junit.Assert.*;

public class BattleTest {
    Map map=new Map ("asd");
    Player player=new Player ("asd", map);
    Battle battle=new Battle(map,player,false);

    @Test
    public void TestPlayerAttack(){

        assertEquals (battle.enemy.health, battle.enemy.maxHealth);
        battle.attack (true);
        assertTrue(battle.enemy.health!=battle.enemy.maxHealth);
    }

    @Test
    public void testKill(){
        battle.enemy.health-=2000;
        assertTrue (battle.isDead(battle.enemy));
        battle.enemy.health+=2000;
    }

    @Test
    public void testPotions(){
        player.health-=100;
        battle.potion();
        assertEquals (player.health,player.maxHealth);
        player.health-=100;
        player.potionsCount=0;
        assertTrue (player.health!=player.maxHealth);
    }
}
