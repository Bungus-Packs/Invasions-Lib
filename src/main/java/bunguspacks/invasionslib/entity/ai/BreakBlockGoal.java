package bunguspacks.invasionslib.entity.ai;

import net.fabricmc.fabric.api.mininglevel.v1.MiningLevelManager;
import net.minecraft.block.Block;
import net.minecraft.entity.ai.NavigationConditions;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.ai.pathing.MobNavigation;
import net.minecraft.entity.ai.pathing.Path;
import net.minecraft.entity.ai.pathing.PathNode;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.Difficulty;
import net.minecraft.world.GameRules;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;

import java.util.function.Predicate;
import java.util.function.Supplier;

public class BreakBlockGoal extends Goal {
    protected MobEntity mob;
    protected BlockPos pos;
    protected boolean blockValid;

    private static final int MIN_MAX_PROGRESS = 240;
    private final Predicate<Difficulty> difficultySufficientPredicate;
    private final Supplier<Integer> miningLevelSupplier;
    protected int breakProgress;
    protected int prevBreakProgress;
    protected int maxProgress;

    // TODO: use pickaxe that entity is holding to determine max block hardness level
    public BreakBlockGoal(MobEntity mob, Supplier<Integer> miningLevelSupplier, Predicate<Difficulty> difficultySufficientPredicate) {
        this.pos = BlockPos.ORIGIN;
        this.mob = mob;
        if (!NavigationConditions.hasMobNavigation(mob)) {
            throw new IllegalArgumentException("Unsupported mob type for BreakBlockGoal");
        }

        this.prevBreakProgress = -1;
        this.maxProgress = -1;
        this.difficultySufficientPredicate = difficultySufficientPredicate;
        this.miningLevelSupplier = miningLevelSupplier;
    }

    public BreakBlockGoal(MobEntity mob, int maxProgress, Supplier<Integer> miningLevelSupplier, Predicate<Difficulty> difficultySufficientPredicate) {
        this(mob, miningLevelSupplier, difficultySufficientPredicate);
        this.maxProgress = maxProgress;
    }

    protected int getMaxProgress() {
        return Math.max(MIN_MAX_PROGRESS, this.maxProgress);
    }

    @Override
    public boolean canStart() {
        if (!NavigationConditions.hasMobNavigation(mob)) {
            return false;
        } else if (!this.mob.getWorld().getGameRules().getBoolean(GameRules.DO_MOB_GRIEFING)) {
            return false;
        } else if (!this.isDifficultySufficient(this.mob.getWorld().getDifficulty())) {
            return false;
        } else {
            MobNavigation mobNavigation = (MobNavigation)this.mob.getNavigation();
            Path path = mobNavigation.getCurrentPath();
            if (path != null && !path.isFinished()) {
                for (int i = 0; i < Math.min(path.getCurrentNodeIndex() + 2, path.getLength()); ++i) {
                    PathNode pathNode = path.getNode(i);
                    this.pos = new BlockPos(pathNode.x, pathNode.y + 1, pathNode.z);
                    if (!(this.mob.squaredDistanceTo(this.pos.getX(), this.mob.getY(), this.pos.getZ()) > 2.25)) {
                        this.blockValid = this.canMineBlock(this.mob.getWorld(), this.pos);
                        if (this.blockValid) {
                            return true;
                        }
                    }
                }

                this.pos = this.mob.getBlockPos().up();
                this.blockValid = this.canMineBlock(this.mob.getWorld(), this.pos);
                return this.blockValid;
            } else {
                return false;
            }
        }
    }

    public void start() {
        this.breakProgress = 0;
    }

    public boolean shouldContinue() {
        return this.breakProgress <= this.getMaxProgress() && this.pos.isWithinDistance(this.mob.getPos(), 2.0) && this.isDifficultySufficient(this.mob.getWorld().getDifficulty());
    }

    public void stop() {
        this.mob.getWorld().setBlockBreakingInfo(this.mob.getId(), this.pos, -1);
    }

    public void tick() {
        if (this.mob.getRandom().nextInt(20) == 0) {
            if (!this.mob.handSwinging) {
                this.mob.swingHand(this.mob.getActiveHand());
            }
        }

        ++this.breakProgress;
        int i = (int)((float)this.breakProgress / (float)this.getMaxProgress() * 10.0F);
        if (i != this.prevBreakProgress) {
            this.mob.getWorld().setBlockBreakingInfo(this.mob.getId(), this.pos, i);
            this.prevBreakProgress = i;
        }

        if (this.breakProgress == this.getMaxProgress() && this.isDifficultySufficient(this.mob.getWorld().getDifficulty())) {
            this.mob.getWorld().removeBlock(this.pos, false);
            this.mob.getWorld().syncWorldEvent(2001, this.pos, Block.getRawIdFromState(this.mob.getWorld().getBlockState(this.pos)));
        }
    }

    private boolean isDifficultySufficient(Difficulty difficulty) {
        return this.difficultySufficientPredicate.test(difficulty);
    }

    private boolean canMineBlock(@NotNull World world, BlockPos pos) {
        return MiningLevelManager.getRequiredMiningLevel(world.getBlockState(pos)) <= this.miningLevelSupplier.get();
    }
}
