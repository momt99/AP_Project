package models;

import exceptions.NoAvailableBuilderException;
import models.buildings.*;
import utils.Point;

import java.util.ArrayList;

import static models.buildings.ConstructMode.*;

public class ConstructionManager
{
    public ConstructionManager(Village village)
    {
        this.village = village;
    }

    private Village village;
    private ArrayList<Construction> constructions = new ArrayList<>();

    public ArrayList<Construction> getConstructions()
    {
        return constructions;
    }

    public void construct(int buildingType, Point location) throws NoAvailableBuilderException
    {
        Building building = BuildingFactory.createBuildingByTypeId(buildingType,location);
        int constructTime = building.getBuildingInfo().getBuildDuration();
        ConstructMode constructMode;
        if (village.getMap().isValid(location))
            constructMode = CONSTRUCT;
        else
            constructMode = UPGRADE;

        Builder builder = village.getMap().getTownHall().getAvailableBuilder();
        int builderNum = builder.getBuilderNum();
        builder.setBuilderStatus(BuilderStatus.WORKING);
        Construction construction = new Construction(buildingType, constructTime, constructMode, location, builderNum, building);
        constructions.add(construction);


    }

    public void checkConstructions()
    {
        Construction construction;
        for (int i = 0; i < constructions.size(); i++)
        {
            construction = constructions.get(i);
            if (construction.isFinished())
            {
                construction.finishConstruction();
                Builder builder = village.getMap().getTownHall().getBuilderByNum(construction.getBuilderNum());
                builder.setBuilderStatus(BuilderStatus.FREE);
                constructions.remove(i);
                i--;
            }

        }
    }
}
