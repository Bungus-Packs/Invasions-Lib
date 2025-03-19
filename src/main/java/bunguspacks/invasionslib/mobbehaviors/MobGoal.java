package bunguspacks.invasionslib.mobbehaviors;

import bunguspacks.invasionslib.mixin.MobEntityAccessor;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.TargetPredicate;
import net.minecraft.entity.ai.goal.*;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.mob.PathAwareEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.world.WorldView;

public class MobGoal<T extends PathAwareEntity> extends Goal {

    PathAwareEntity entity;
    int dementiaTolerance = 20;
    int dementiaTimer = 0;
    int targetRange = 0;
    GoalSelector goalSelector;
    ActiveTargetGoal<PlayerEntity> fightPlayer;
    //Ideally range is infinite, but I am going to set a "reasonable number" in its place
    GoToWalkTargetGoal attackBase = new GoToWalkTargetGoal((PathAwareEntity)this.entity, this.entity.speed);
    BlockPos beaconLocation;

    public MobGoal(PathAwareEntity entity){
        this.entity = entity;
    }

    public MobGoal(PathAwareEntity entity, int targetRange){
        this.entity = entity;
        this.targetRange = targetRange;
    }

    @Override
    public boolean canStart() {
        return false;
    }

    @Override
    public void start(){
        super.start();
        this.goalSelector = ((MobEntityAccessor)entity).getGoalSelector();
        //Mob should always have attacking base as priority 1
        goalSelector.add(1, attackBase);
    }

    @Override
    public void stop(){
        super.stop();

    }

    @Override
    public void tick(){
        super.tick();
        //The Following code is entirely to handle the mob's targetting of the player
        //If closest player is in targetting range of mob and not null, set target and start dementia timer
        LivingEntity closestPlayer = this.entity.getWorld().getClosestEntity(PlayerEntity.class, TargetPredicate.DEFAULT, null, entity.getX(), entity.getY(), entity.getZ(), new Box(entity.getBlockPos()).expand(targetRange));
        if (!(closestPlayer == null)){
            dementiaTimer = dementiaTolerance;
            entity.setTarget(closestPlayer);
            if (!(goalSelector.getGoals().contains(fightPlayer))) {
                fightPlayer = new ActiveTargetGoal<PlayerEntity>(this.entity, PlayerEntity.class, true, true);
                goalSelector.add(1, fightPlayer);
            }
        }
        //If not forgor, target should still be set to the previous player
        else {
            dementiaTimer = Math.max(dementiaTimer-1, 0);
            //If forgor
            if (!(dementiaTimer == 0)) {
                //target beacon thing
                if (goalSelector.getGoals().contains(fightPlayer)) {
                    goalSelector.remove(fightPlayer);
                }
                goalSelector.remove(fightPlayer);
                entity.setPositionTarget(new BlockPos(0,77,0), 1000);
            }
        }
    }

    @Override
    public boolean shouldRunEveryTick() {
        return true;
    }

}
