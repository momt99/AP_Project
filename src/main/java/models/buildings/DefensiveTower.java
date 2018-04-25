package models.buildings;

import models.Attack;
import utils.Point;

public abstract class DefensiveTower extends Building
{
    private int attackPower;
    private int range;

    public DefensiveTower(Point location, int buildingNum)
    {
        super(location, buildingNum);
    }

    public int getRange()
    {
        return range;
    }

    public int getAttackPower()
    {
        return attackPower;
    }

    public abstract void attack(Attack attack);

}
