package views;

import menus.IMenuContainer;
import menus.Menu;
import models.attack.Attack;
import models.attack.AttackMap;
import models.Resource;
import models.buildings.*;
import models.soldiers.Soldier;
import views.dialogs.DialogResult;
import views.dialogs.TextInputDialog;

import java.util.Scanner;

public class AttackView extends ConsoleMenuContainerView implements IMenuContainer
{
    private Attack theAttack;

    public AttackView(Scanner scanner)
    {
        super(scanner);
    }

    private void initialize()
    {

    }

    public void setAttack(Attack theAttack)
    {
        this.theAttack = theAttack;
    }

    @Override
    public void onItemClicked(Menu menu)
    {
        switch (menu.getId())
        {
            case Menu.Id.ATTACK_MAP_INFO:
            {
                showMapInfo(theAttack.getMap());
            }
            break;
            default:
                super.onItemClicked(menu);
        }
    }

    public DialogResult showOpenMapDialog()
    {
        return new TextInputDialog(scanner, "Enter map path:").showDialog();
    }

    public void showMapInfo(AttackMap map)
    {
        System.out.println("Gold: " + map.getResources().getElixir());
        System.out.println("Elixir: " + map.getResources().getGold());

        int lastType = -1;
        if (map.getAllDefensiveTowers().size() > 0)
            lastType = map.getAllDefensiveTowers().get(0).getType();
        int counter = 0;
        for (DefensiveTower tower : map.getAllDefensiveTowers())
            if (lastType != tower.getType())
            {
                System.out.printf("%s: %d\n", BuildingValues.getBuildingInfo(lastType).getName(), counter);
                lastType = tower.getType();
                counter = 1;
            }
            else
                counter++;

        if (lastType != -1)
            System.out.printf("%s: %d\n", BuildingValues.getBuildingInfo(lastType).getName(), counter);
    }

    public void showResourcesStatus()
    {
        Resource claimed = theAttack.getClaimedResource();
        System.out.printf("Gold achieved: %d\nElixir achieved: %d\nGold remained in map: %d\nElixir remained in map: %d\n",
                claimed.getGold(),
                claimed.getElixir(),
                theAttack.getTotalResource().getGold() - claimed.getGold(),
                theAttack.getTotalResource().getElixir() - claimed.getElixir());
    }

    private void showTowerStatus(DefensiveTower tower)
    {
        System.out.printf("%s%s: level = %d in (%d, %d) health = %d\n",
                tower.getAttackHelper().isDestroyed() ? "$$$" : "",
                tower.getBuildingInfo().getName(),
                tower.getLevel(),
                tower.getLocation().getX(),
                tower.getLocation().getY(),
                tower.getStrength());
    }

    private void showNonTowerStatus(Building building)
    {
        if (building instanceof DefensiveTower)
            return;
        System.err.printf("%s%s: level = %d in (%d, %d) health = %d\n",
                building.getAttackHelper().isDestroyed() ? "$$$" : "",
                building.getBuildingInfo().getName(),
                building.getLevel(),
                building.getLocation().getX(),
                building.getLocation().getY(),
                building.getStrength());
    }

    public void showTowersStatus(int towerType)
    {
        theAttack.getTowers(towerType).forEach(this::showTowerStatus);
    }

    public void showAllTowersStatus()
    {
        theAttack.getAllTowers().forEach(this::showTowerStatus);
        theAttack.getMap().getBuildings().forEach(this::showNonTowerStatus);
    }

    private void showSoldierStatus(Soldier soldier)
    {
        System.out.printf("%s%s: level = %d in (%d, %d) with health = %d\n",
                soldier.getAttackHelper().isDead() ? "💀💀💀" : "",
                soldier.getSoldierInfo().getName(),
                soldier.getLevel(),
                soldier.getLocation().getX(),
                soldier.getLocation().getY(),
                soldier.getAttackHelper().getHealth());
    }

    public void showSoldiersStatus(int soldierType)
    {
        theAttack.getAllUnits(soldierType).forEach(this::showSoldierStatus);
    }

    public void showAllSoldiersStatus()
    {
        theAttack.getAllUnits().forEach(this::showSoldierStatus);
    }

    public void showAllAll()
    {
        showResourcesStatus();
        showAllSoldiersStatus();
        showAllTowersStatus();
    }

    public void showAttackEndMessage(Attack.QuitReason quitReason)
    {
        System.out.printf("The wat ended with %d golds, %d elixir and %d scores achieved!\n",
                theAttack.getClaimedResource().getGold(),
                theAttack.getClaimedResource().getElixir(),
                theAttack.getClaimedScore());

        System.err.println(quitReason.toString());
    }

    public void viewMapStatus()
    {
        for (int j = 0; j < theAttack.getMap().getWidth(); j++)
            System.out.print(" ―");
        System.out.print('\n');
        for (int j = 0; j < theAttack.getMap().getHeight(); j++)
        {
            System.out.print('|');
            for (int i = 0; i < theAttack.getMap().getWidth(); i++)
            {
                int soldierCount = theAttack.numberOfSoldiersIn(i, j);
                if (!theAttack.getMap().isEmpty(i, j))
                {
                    Building building = theAttack.getMap().getBuildingAt(i, j);
                    if (building.getAttackHelper().isDestroyed())
                        System.out.print('$');
                    else if (building instanceof Storage || building instanceof Mine)
                        System.out.print('*');
                    else if (building instanceof GuardianGiant)
                        System.out.print('G');
                    else if (building instanceof DefensiveTower)
                        System.out.print('#');
                    else if (building instanceof TownHall)
                        System.out.print('@');
                    else
                        System.out.print('&');
                }
                else
                    System.out.print(' ');
                System.out.print(soldierCount == 0 ? " " : Integer.toString(soldierCount));
            }
            System.out.print('|');
            System.out.print('\n');
        }
        for (int j = 0; j < theAttack.getMap().getWidth(); j++)
            System.out.print(" ―");
        System.out.print('\n');
    }
}
