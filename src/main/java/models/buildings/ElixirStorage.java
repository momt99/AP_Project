package models.buildings;

import utils.Point;

public class ElixirStorage extends Storage
{
    public ElixirStorage(Point location, int buildingNum)
    {
        super(location, buildingNum);
        ensureLevel();
    }

    public static final int BUILDING_TYPE = 4;

    @Override
    public int getType()
    {
        return BUILDING_TYPE;
    }
}
