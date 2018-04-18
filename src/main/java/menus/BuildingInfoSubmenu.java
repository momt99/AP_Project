package menus;

import models.buildings.Building;

public class BuildingInfoSubmenu extends Submenu implements IBuildingMenu
{
    public BuildingInfoSubmenu(ParentMenu parent)
    {
        super(Id.BUILDING_INFO, "Info", parent);
        insertItem(Id.OVERALL_INFO, "Overall info");
        insertItem(Id.UPGRADE_INFO, "Upgrade info");
    }

    public Building getBuilding()
    {
        return ((BuildingSubmenu)parent).getBuilding();
    }
}
