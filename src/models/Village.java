package models;

import models.soldiers.Soldier;

import java.util.ArrayList;

public class Village
{
    private Map map;
    private String owner;
    private ConstructionManager constructionManager;
    private VillageStatus villageStatus;
    private int turn;
    private ArrayList<Soldier> soldiers;


    public String getOwner()
    {
        return owner;
    }

    public void setOwner(String owner)
    {
        this.owner = owner;
    }

    public VillageStatus getVillageStatus()
    {
        return villageStatus;
    }

    public void setVillageStatus(VillageStatus villageStatus)
    {
        this.villageStatus = villageStatus;
    }

    public Map getMap()
    {
        return map;
    }

    public ConstructionManager getConstructionManager()
    {
        return constructionManager;
    }

    public int getTurn()
    {
        return turn;
    }

    public ArrayList<Soldier> getSoldiers()
    {
        return soldiers;
    }

    public Resource getResources()
    {
        // TODO: 4/13/18 : gets all storages in map and returns resources
    }
}
