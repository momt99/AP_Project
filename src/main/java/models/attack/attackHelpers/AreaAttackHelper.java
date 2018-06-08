package models.attack.attackHelpers;

import exceptions.SoldierNotFoundException;
import models.attack.Attack;
import models.attack.attackHelpers.DefensiveTowerAttackHelper;
import models.buildings.Building;
import models.buildings.DefensiveTower;
import models.soldiers.Soldier;
import utils.Point;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class AreaAttackHelper extends DefensiveTowerAttackHelper
{
    private ArrayList<Soldier> wholeTargets;

    public AreaAttackHelper(DefensiveTower building, Attack attack)
    {
        super(building, attack);
    }

    @Override
    public void setTarget() throws SoldierNotFoundException
    {
        DefensiveTower defensiveTower = (DefensiveTower)building;
        mainTargets = new ArrayList<>();
        wholeTargets = new ArrayList<>();
        List<Soldier> soldiersInRange = null;
        Point soldier = attack.getNearestSoldier(defensiveTower.getLocation(), defensiveTower.getRange(), defensiveTower.getDefenseType().convertToMoveType());
        Stream<Soldier> soldiers = attack.getSoldiersOnLocations().getSoldiers(soldier, defensiveTower.getDefenseType().convertToMoveType());
        mainTargets.addAll(soldiers.collect(Collectors.toList()));
        soldiersInRange = attack.getSoldiersInRange(defensiveTower.getLocation(), SECOND_RANGE, defensiveTower.getDefenseType().convertToMoveType());
        // TODO: 6/6/18 Change Second range.
        if (soldiersInRange != null)
            wholeTargets.addAll(soldiersInRange);
    }

    @Override
    public void attack()
    {
        DefensiveTower tower = (DefensiveTower)building;
        if (mainTargets != null)
            mainTargets.forEach(soldier -> soldier.getAttackHelper().decreaseHealth(tower.getDamagePower()));
        if (wholeTargets != null)
            wholeTargets.forEach(soldier -> soldier.getAttackHelper().decreaseHealth(tower.getDamagePower() - 1));
        mainTargets = null;
        wholeTargets = null;
    }
}