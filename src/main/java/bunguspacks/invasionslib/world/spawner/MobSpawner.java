package bunguspacks.invasionslib.world.spawner;

import bunguspacks.invasionslib.config.MobGroupConfig;
import bunguspacks.invasionslib.util.InvasionDirector;
import net.minecraft.block.AirBlock;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.registry.Registries;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.Heightmap;
import net.minecraft.world.World;
import net.minecraft.world.spawner.Spawner;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class MobSpawner implements Spawner {
    @Override
    public int spawn(ServerWorld world, boolean spawnMonsters, boolean spawnAnimals) {
        return 0;
    }

    //spawn mob in donut around specified position with no associated director
    public static void spawnMob(String mobid, ServerWorld world, BlockPos pos) {
        EntityType<?> mobType = Registries.ENTITY_TYPE.get(new Identifier(mobid));
        MobEntity mob = (MobEntity) mobType.create(world);
        if (mob != null) {
            BlockPos spawnPos = getBlockPosWithDistance(pos, world, 5, 10);
            mob.refreshPositionAndAngles(spawnPos, 0, 0);
            world.spawnEntity(mob);
        }
    }

    //spawn mob in donut around specified position and notify director to start tracking it
    public static void spawnMob(String mobid, ServerWorld world, BlockPos pos, InvasionDirector director, float cost, boolean waveMob) {
        EntityType<?> mobType = Registries.ENTITY_TYPE.get(new Identifier(mobid));
        MobEntity mob = (MobEntity) mobType.create(world);
        if (mob != null) {
            BlockPos spawnPos = getBlockPosWithDistance(pos, world, 5, 10);
            mob.refreshPositionAndAngles(spawnPos, 0, 0);
            world.spawnEntity(mob);
            director.startTracking(mob, cost, waveMob);
        }
    }

    public static void spawnMobGroup(MobGroupConfig.MobGroupData data, ServerWorld world, BlockPos pos, @Nullable InvasionDirector director, boolean waveMob) {
        List<MobGroupConfig.MobUnitData> unitData = data.mobs();
        int totalCredits = data.cost();
        Random random = world.random;
        List<Integer> unitCounts = new ArrayList<>();

        //predetermine random unit counts so that credits can be appropriately distributed
        int unitCreditWeightSum = 0;
        for (int i = 0; i < unitData.size(); i++) {
            int unitCount = random.nextBetween(unitData.get(i).minCount(), unitData.get(i).maxCount());
            unitCreditWeightSum += unitCount * unitData.get(i).creditWeight();
            unitCounts.add(unitCount);
        }

        //if function was not passed a director to spawn with, spawn mobs without an associated cost.
        if (director == null) {
            for (int i = 0; i < unitData.size(); i++) {
                for (int j = 0; j < unitCounts.get(i); j++)
                    spawnMob(unitData.get(i).mobid(), world, pos);
            }
        }

        //if function was passed a director, spawn mobs with associated cost
        else {
            for (int i = 0; i < unitData.size(); i++) {
                for (int j = 0; j < unitCounts.get(i); j++)
                    spawnMob(unitData.get(i).mobid(), world, pos, director, ((float) unitData.get(i).creditWeight() * totalCredits / unitCreditWeightSum), waveMob);
            }
        }

    }

    //black box function to generate a random position within a donut-shaped region around a center position
    private static BlockPos getBlockPosWithDistance(BlockPos pos, World world, int distanceMin, int distanceMax) {
        final Random random = world.random;
        double d = 0;
        double x = 0;
        double z = 0;
        BlockPos spawnPos = pos;
        // tries to find a location with 2 blocks of air using the world heightmap excluding leaves
        for (int i = 0; i < 15; i ++) {
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
            spawnPos = new BlockPos(pos.getX() + (int) x,
                    world.getTopY(Heightmap.Type.MOTION_BLOCKING_NO_LEAVES,
                            pos.getX() + (int) x,
                            pos.getZ() + (int) z),
                    pos.getZ() + (int) z);
            if (world.getBlockState(spawnPos).isAir() && world.getBlockState(spawnPos.up()).isAir())
                return spawnPos;
        }
        // if no valid locations can be found, it will begin searching on just the normal heightmap
        for (int i = 0; i < 15; i ++) {
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
            spawnPos = new BlockPos(pos.getX() + (int) x,
                    world.getTopY(Heightmap.Type.WORLD_SURFACE,
                            pos.getX() + (int) x,
                            pos.getZ() + (int) z),
                    pos.getZ() + (int) z);
            if (world.getBlockState(spawnPos).isAir() && world.getBlockState(spawnPos.up()).isAir())
                return spawnPos;
        }
        // if all that fails, it just returns the input position
        return spawnPos;
    }
}
