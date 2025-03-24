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

    public MobInvasionBeaconGoal(PathAwareEntity entity) {
        super(entity, entity.getMovementSpeed(), -1);
        this.entity = entity;
    }

    public MobInvasionBeaconGoal(PathAwareEntity entity, int targetRange) {
        super(entity, entity.getMovementSpeed(), targetRange);
        this.entity = entity;
        this.targetRange = targetRange;
    }

    @Override
    public boolean canStart() {
        return true;
    }

    @Override
    public void start() {
        super.start();
        this.goalSelector = ((MobEntityAccessor)entity).getGoalSelector();
        //Mob should always have attacking base as priority 1
        goalSelector.add(0, attackBase);
    }

    @Override
    public void stop() {
        super.stop();
    }

    @Override
    public void tick() {
        super.tick();
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
