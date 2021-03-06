package models.attack.attackHelpers;

import exceptions.SoldierNotFoundException;
import graphics.helpers.HealerGraphicHelper;
import graphics.helpers.SoldierGraphicHelper;
import models.attack.Attack;
import models.soldiers.Healer;
import models.soldiers.MoveType;
import models.soldiers.Soldier;
import models.soldiers.SoldiersHealReport;
import utils.Point;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class HealerAttackHelper extends SoldierAttackHelper
{
    private ArrayList<Soldier> targets;
    private Point destination;

    public void ageOneDeltaT()
    {
        decreaseHealth(1, false);
    }

    public HealerAttackHelper(Attack attack, Healer healer)
    {
        super(attack, healer);
    }

    public Point getDestination()
    {
        return destination;
    }

    @Override
    public void passTurn()
    {
        ageOneDeltaT();
        super.passTurn();
    }

    @Override
    protected void removeSoldierIfDead()
    {
        super.removeSoldierIfDead();
    }

    @Override
    public void move()
    {
        if (soldier != null && isSoldierDeployed() && !soldier.getAttackHelper().isDead())
        {
            if (destination != null)
            {
                Point pointToGo = getPointToGo(destination);
                attack.moveOnLocation(soldier, getSoldierLocation(), pointToGo);
                soldier.setLocation(pointToGo);
            }
        }
    }

    @Override
    public void fire()
    {
        if (!isReal)
            return;
        ArrayList<SoldiersHealReport> reports = new ArrayList<>();
        if (soldier != null && !soldier.getAttackHelper().isDead())
        {
            if (targets != null && targets.size() != 0)
            {
                for (Soldier target : targets)
                {
                    System.out.println("healer healing soldier type:" + target.getType() + "in amount of" + getDamage());
                    target.getAttackHelper().increaseHealth(getDamage(), false);
                }
            }
        }
    }

    @Override
    public void setTarget()
    {
        if (!isReal)
            return;
        targets = getSoldiersInRange();
        try
        {
            destination = attack.getNearestSoldier(getSoldierLocation(), 35, MoveType.GROUND);// 35 represents a high range to cover all the map
        }
        catch (SoldierNotFoundException e) {}
    }

    @Override
    public Point getTargetLocation()
    {
        if (targets.size() == 0)
            return new Point(-1, -1);
        return targets.get(0).getLocation();
    }

    private ArrayList<Soldier> getSoldiersInRange()
    {
        ArrayList<Soldier> soldiersInRange = new ArrayList<>();
        List<Soldier> soldiers = attack.getDeployedAliveUnits().collect(Collectors.toList());
        if (soldiers != null && soldiers.size() != 0)
        {
            for (Soldier soldier : soldiers)
            {
                if (soldier != null && !soldier.getAttackHelper().isDead() && isSoldierDeployed() && soldier.getMoveType() == MoveType.GROUND)
                {
                    if (Point.euclideanDistance(soldier.getLocation(), getSoldierLocation()) - getRange() < 0.01)
                    {
                        soldiersInRange.add(soldier);
                    }
                }
            }
        }
        return soldiersInRange;
    }

    @Override
    public void setGraphicHelper(SoldierGraphicHelper graphicHelper)
    {
        if (!(graphicHelper instanceof HealerGraphicHelper))
            throw new IllegalArgumentException("Graphic helper should be a HealerGraphicHelper.");
        super.setGraphicHelper(graphicHelper);
    }

    @Override
    public void onReload()
    {
        super.onReload();
        if (isSoldierDeployed() && (soldier == null || isDead() || getHealth() <= 0))
        {
            callOnSoldierDie();
            return;
        }
        setTarget();
        ageOneDeltaT();
        if (!readyToFireTarget)
            return;
        if (soldier != null && isSoldierDeployed() && !isDead() && getHealth() > 0)
        {
            fire();
            if (destination == null)
            {
                callOnDecamp();
                return;
            }
            if (Point.euclideanDistance(getSoldierLocation(), destination) > getRange())
                callOnDecamp();
        }
    }
}
