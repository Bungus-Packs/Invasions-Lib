package bunguspacks.invasionslib.mobbehaviors;

import bunguspacks.invasionslib.mixin.MobEntityAccessor;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.TargetPredicate;
import net.minecraft.entity.ai.goal.*;
import net.minecraft.entity.mob.PathAwareEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.PositionImpl;
import net.minecraft.world.WorldView;

public class MobInvasionBeaconGoal extends MoveToTargetPosGoal {

    private PathAwareEntity entity;
    private int dementiaTolerance = 50;
    private int dementiaTimer = 0;
    private int targetRange = 0;
    private PositionImpl basePosition = new PositionImpl(0,77,0);
    private GoalSelector goalSelector;
    public AttackGoal fightPlayer;
    //Ideally range is infinite, but I am going to set a "reasonable number" in its place
    public MoveToTargetPosGoal attackBase = new MoveToTargetPosGoal(entity, entity.speed, 10000) {
        @Override
        protected boolean isTargetPos(WorldView world, BlockPos pos) {
            if (world == null){
                return false;
            }
            else return entity.getPos().isInRange(basePosition, 3);
        }
    };

    public MobInvasionBeaconGoal(PathAwareEntity entity){
        super(entity, entity.getMovementSpeed(), -1);
        this.entity = entity;
    }

    public MobInvasionBeaconGoal(PathAwareEntity entity, int targetRange){
        super(entity, entity.getMovementSpeed(), targetRange);
        this.entity = entity;
        this.targetRange = targetRange;
    }

    @Override
    public boolean canStart() {
        return true;
    }

    @Override
    public void start(){
        super.start();
        this.goalSelector = ((MobEntityAccessor)entity).getGoalSelector();
        //Mob should always have attacking base as priority 1
        goalSelector.add(0, attackBase);
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
                fightPlayer = new AttackGoal(this.entity);
                goalSelector.add(1, fightPlayer);
            }
        }
        //If not forgor, target should still be set to the previous player
        else {
            dementiaTimer--;
            //If forgor
            if (!(dementiaTimer == 0)) {
                //target beacon thing
                if (goalSelector.getGoals().contains(fightPlayer)) {
                    goalSelector.remove(fightPlayer);
                }
                entity.setPositionTarget(new BlockPos(0,77,0), 1000);
                if (!(goalSelector.getGoals().contains(attackBase))) {
                    goalSelector.add(0, attackBase);
                }
            }
        }
    }

    @Override
    protected boolean isTargetPos(WorldView world, BlockPos pos) {
        if (goalSelector.getGoals().contains(attackBase)){
            return entity.getPos().isInRange(basePosition, 2);
        }
        return false;
    }

    @Override
    public boolean shouldRunEveryTick() {
        return true;
    }

}
