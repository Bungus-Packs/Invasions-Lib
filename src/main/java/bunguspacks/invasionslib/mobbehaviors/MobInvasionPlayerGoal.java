package bunguspacks.invasionslib.mobbehaviors;

import bunguspacks.invasionslib.mixin.MobEntityAccessor;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.TargetPredicate;
import net.minecraft.entity.ai.goal.AttackGoal;
import net.minecraft.entity.ai.goal.GoalSelector;
import net.minecraft.entity.ai.goal.MoveToTargetPosGoal;
import net.minecraft.entity.mob.PathAwareEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.PositionImpl;
import net.minecraft.world.WorldView;

public class MobInvasionPlayerGoal extends AttackGoal {
    PathAwareEntity entity;
    int dementiaTolerance;
    int dementiaTimer;
    int targetRange;
    GoalSelector goalSelector;

    //Overloaded Constructors
    public MobInvasionPlayerGoal(PathAwareEntity entity){
        super(entity);
        if (entity!=null){
            this.entity = entity;
            dementiaTimer = 0;
            dementiaTolerance = 50;
            targetRange = 6;
        }
    }

    public MobInvasionPlayerGoal(PathAwareEntity entity, int targetRange){
        super(entity);
        if (entity != null){
            this.entity = entity;
            this.targetRange = targetRange;
            dementiaTimer = 0;
            dementiaTolerance = 50;
        }
    }

    public MobInvasionPlayerGoal(PathAwareEntity entity, int targetRange, int dementiaTolerance){
        super(entity);
        if (entity != null){
            this.entity = entity;
            this.targetRange = targetRange;
            dementiaTimer = 0;
            this.dementiaTolerance = dementiaTolerance;
        }
    }

    //Trigger for mob to follow this goal
    @Override
    public boolean canStart() {
        //If there is a player in range, return true
        if (entity!=null){
            LivingEntity closestPlayer = this.entity.getWorld().getClosestEntity(PlayerEntity.class, TargetPredicate.DEFAULT, null, entity.getX(), entity.getY(), entity.getZ(), new Box(entity.getBlockPos()).expand(targetRange));
            return (closestPlayer != null)||(dementiaTimer<dementiaTolerance);
        }
        return false;
    }

    @Override
    public void start(){
        super.start();
    }

    @Override
    public void stop(){
        super.stop();
        dementiaTimer = 0;
    }

    @Override
    public void tick(){
        if (entity!=null){
            LivingEntity closestPlayer = this.entity.getWorld().getClosestEntity(PlayerEntity.class, TargetPredicate.DEFAULT, null, entity.getX(), entity.getY(), entity.getZ(), new Box(entity.getBlockPos()).expand(targetRange));
            entity.setTarget(closestPlayer);
            dementiaTimer++;
        }
        super.tick();
    }

    @Override
    public boolean shouldRunEveryTick() {
        return true;
    }

}
