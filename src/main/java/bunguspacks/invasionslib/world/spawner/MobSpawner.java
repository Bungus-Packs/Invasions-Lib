package bunguspacks.invasionslib.world.spawner;

import bunguspacks.invasionslib.InvasionsLib;
import bunguspacks.invasionslib.config.InvasionMobConfig;
import bunguspacks.invasionslib.mixin.MobEntityAccessor;
import bunguspacks.invasionslib.mobbehaviors.MobGoal;
import net.minecraft.block.Blocks;
import net.minecraft.block.TargetBlock;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ai.goal.*;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.mob.PathAwareEntity;
import net.minecraft.registry.Registries;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.Heightmap;
import net.minecraft.world.World;
import net.minecraft.world.spawner.Spawner;

import java.nio.file.Path;
import java.util.List;
import java.util.logging.Logger;

public class MobSpawner implements Spawner {
    @Override
    public int spawn(ServerWorld world, boolean spawnMonsters, boolean spawnAnimals) {
        return 0;
    }

    public static void spawnMob(String mobid, ServerWorld world, BlockPos pos) {
        EntityType<?> mobType = Registries.ENTITY_TYPE.get(new Identifier(mobid));
        MobEntity mob = (MobEntity) mobType.create(world);
        if (mob != null) {
            BlockPos spawnPos = getBlockPosWithDistance(pos, world, 5, 10);
            mob.refreshPositionAndAngles(spawnPos, 0, 0);
            if (mob instanceof PathAwareEntity){
                GoalSelector goalSelector = ((MobEntityAccessor)mob).getGoalSelector();
                //lobotomy
                mob.clearGoalsAndTasks();
                mob.clearPositionTarget();
                //Adds the important attack player/attack base goal
//                goalSelector.add(0, new MobGoal((PathAwareEntity) mob, 5));
                WanderAroundGoal mobgoaled = new WanderAroundGoal((PathAwareEntity)mob, mob.speed, 120);
                mob.setPositionTarget(new BlockPos(0, 77, 0), 3);
                mobgoaled.ignoreChanceOnce();
                goalSelector.add(1, mobgoaled);
                InvasionsLib.LOGGER.info(((MobEntityAccessor) mob).getGoalSelector().getRunningGoals().toArray().toString());
                if (mobgoaled.canStart()) {
                    InvasionsLib.LOGGER.info("can start");
                    mobgoaled.start();
                    InvasionsLib.LOGGER.info("start method called");
                } else {
                    InvasionsLib.LOGGER.info("uh oh");
                }
                    InvasionsLib.LOGGER.info(mobgoaled.toString());
            }
            world.spawnEntity(mob);
        }
    }

    public static void spawnMobGroup(InvasionMobConfig.MobGroupData data, ServerWorld world, BlockPos pos) {
        List<InvasionMobConfig.MobUnitData> unitData = data.mobs();
        for (InvasionMobConfig.MobUnitData currentUnitData : unitData) {
            Random random = world.random;
            int unitCount = random.nextBetween(currentUnitData.minCount(), currentUnitData.maxCount());
            for (int j = 0; j < unitCount; j++) {
                spawnMob(currentUnitData.mobid(), world, pos);
            }
        }
    }

    private static BlockPos getBlockPosWithDistance(BlockPos pos, World world, int distanceMin, int distanceMax) {
        final Random random = world.random;
        double d = 0;
        double x = 0;
        double z = 0;
        d = random.nextBetween(distanceMin, distanceMax);
        x = random.nextBetween(0, (int) d);
        if (x == 0) {
            z = d;
        } else {
            z = Math.sqrt((d * d) - (x * x));
            if (random.nextBoolean()) {
                x = x * -1;
            }
        }
        if (random.nextBoolean()) {
            z = z * -1;
        }
        return new BlockPos(pos.getX() + (int) x, world.getTopY(Heightmap.Type.WORLD_SURFACE, pos.getX() + (int) x, pos.getZ() + (int) z), pos.getZ() + (int) z);
    }
}
