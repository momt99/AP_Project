package models.buildings;

import utils.Point;

public class WizardTower extends DefensiveTower
{
    public static final int DEFENSIVE_TOWER_TYPE = 11;
    public static final int SECOND_RANGE = 2;
    public WizardTower(Point location, int buildingNum)
    {
        super(location, buildingNum);
    }

    @Override
    public int getType()
    {
        return 11;
    }
}
