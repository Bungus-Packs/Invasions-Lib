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

    LivingEntity target;
    int targetRange;

    //Overloaded Constructors
    public MobInvasionPlayerGoal(PathAwareEntity entity){
        super(entity);
        if (entity!=null){
            this.entity = entity;
            targetRange = 6;
        }
    }

    public MobInvasionPlayerGoal(PathAwareEntity entity, int targetRange){
        super(entity);
        if (entity != null){
            this.entity = entity;
            this.targetRange = targetRange;
        }
    }

    //Trigger for mob to follow this goal
    @Override
    public boolean canStart() {
        //If there is a player in range, return true
        if (entity!=null){
            target = this.entity.getWorld().getClosestEntity(PlayerEntity.class, TargetPredicate.DEFAULT, null, entity.getX(), entity.getY(), entity.getZ(), new Box(entity.getBlockPos()).expand(targetRange));
            return (target != null);
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
            LivingEntity closestPlayer = this.entity.getWorld().getClosestEntity(PlayerEntity.class, TargetPredicate.DEFAULT, null, entity.getX(), entity.getY(), entity.getZ(), new Box(entity.getBlockPos()).expand(targetRange));
            if (closestPlayer != null){
                entity.setTarget(closestPlayer);
            }
        }
    }

    @Override
    public boolean shouldRunEveryTick() {
        return true;
    }

}
