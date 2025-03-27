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
    float dementiaTimer;
    float ram = 50;
    LivingEntity target;
    int targetRange;

    //Overloaded Constructors
    public MobInvasionPlayerGoal(PathAwareEntity entity){
        super(entity);
        if (entity!=null){
            this.entity = entity;
            dementiaTimer = 0;
            targetRange = 6;
        }
    }

    public MobInvasionPlayerGoal(PathAwareEntity entity, int targetRange){
        super(entity);
        if (entity != null){
            this.entity = entity;
            dementiaTimer = 0;
            this.targetRange = targetRange;
        }
    }

    //Trigger for mob to follow this goal
    @Override
    public boolean canStart() {
        //If there is a player in range, return true
        if (entity!=null){
            target = this.entity.getWorld().getClosestEntity(PlayerEntity.class, TargetPredicate.DEFAULT, null, entity.getX(), entity.getY(), entity.getZ(), new Box(entity.getBlockPos()).expand(targetRange));
            entity.setTarget(target);
            return (super.canStart());
        }
        return false;
    }

    @Override
    public void start(){
        super.start();
        dementiaTimer = 0;
    }

    @Override
    public void stop(){
        entity.setAttacking(false);
        super.stop();
    }

    @Override
    public boolean shouldContinue() {
        if (!this.target.isAlive()) {
            return false;
        } else if (this.entity.squaredDistanceTo(this.target) > targetRange*3) {
            return false;
        } else {
            return !this.entity.getNavigation().isIdle() || this.canStart();
        }
    }

    @Override
    public void tick(){
        if (entity!=null){
            super.tick();
            LivingEntity closestPlayer = this.entity.getWorld().getClosestEntity(PlayerEntity.class, TargetPredicate.DEFAULT, null, entity.getX(), entity.getY(), entity.getZ(), new Box(entity.getBlockPos()).expand(targetRange));
            if (closestPlayer != null){
                entity.setTarget(closestPlayer);
                entity.setAttacking(true);
                dementiaTimer = 0;
            }
            else {
                dementiaTimer++;
            }
            if (dementiaTimer >= ram) {
                entity.setAttacking(false);
            }
        }
    }

    @Override
    public boolean shouldRunEveryTick() {
        return true;
    }

}
