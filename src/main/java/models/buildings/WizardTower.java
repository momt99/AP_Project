package models.buildings;

import models.Attack;
import utils.Point;

public class WizardTower extends DefensiveTower
{
    public WizardTower(Point location)
    {
        super(location);
    }

    @Override
    public void attack(Attack attack)
    {

    }

    @Override
    public int getType()
    {
        return 11;
    }
}