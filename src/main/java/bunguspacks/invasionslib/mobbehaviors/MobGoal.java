package bunguspacks.invasionslib.mobbehaviors;

import net.minecraft.MinecraftVersion;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.TargetPredicate;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.ai.goal.GoalSelector;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import java.util.ArrayList;

public class MobGoal<T extends MobEntity> extends Goal {

    MobEntity entity;
    int dementiaTolerance = 20;
    int dementiaTimer = 0;
    int targetRange = 0;

    public MobGoal(MobEntity entity){
        this.entity = entity;
    }

    public MobGoal(MobEntity entity, int targetRange){
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

    }

    @Override
    public void stop(){
        super.stop();

    }

    @Override
    public void tick(){
        super.tick();
        //If closest player is in targetting range of mob and not null, set target and start dementia timer
        LivingEntity closestPlayer = this.entity.getWorld().getClosestEntity(PlayerEntity.class, TargetPredicate.DEFAULT, null, entity.getX(), entity.getY(), entity.getZ(), new Box(entity.getBlockPos()).expand(targetRange));
        if (!(closestPlayer == null)){
            this.entity.setTarget(closestPlayer);
            dementiaTimer = dementiaTolerance;
            entity.clearPositionTarget();
        }
        //If not forgor, target should still be set to the previous player
        else {
            dementiaTimer = Math.max(dementiaTimer-1, 0);
            //If forgor
            if (!(dementiaTimer == 0)) {
                //target beacon thing
                entity.setPositionTarget(new BlockPos(0,0,0), 1);
            }
        }

    }

    @Override
    public boolean shouldRunEveryTick() {
        return true;
    }

}
