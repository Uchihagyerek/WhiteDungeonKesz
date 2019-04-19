package game;


// ebből származik le a Player és a Monster osztály
abstract class Entity {
    String name; //név
    int level; //szint
    int health; //életerő
    int mana;
    int maxHealth;
    int maxMana;
    abstract int attack();
    abstract void die();

}
























































































