package bunguspacks.invasionslib.world.spawner;

import bunguspacks.invasionslib.InvasionsLib;
import bunguspacks.invasionslib.config.InvasionMobConfig;
import bunguspacks.invasionslib.util.InvasionDirector;
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

    public static void spawnMob(String mobid, ServerWorld world, BlockPos pos) {
        EntityType<?> mobType = Registries.ENTITY_TYPE.get(new Identifier(mobid));
        MobEntity mob = (MobEntity) mobType.create(world);
        if (mob != null) {
            BlockPos spawnPos = getBlockPosWithDistance(pos, world, 5, 10);
            mob.refreshPositionAndAngles(spawnPos, 0, 0);
            world.spawnEntity(mob);
        }
    }

    public static void spawnMob(String mobid, ServerWorld world, BlockPos pos, InvasionDirector director, float cost) {
        EntityType<?> mobType = Registries.ENTITY_TYPE.get(new Identifier(mobid));
        MobEntity mob = (MobEntity) mobType.create(world);
        if (mob != null) {
            BlockPos spawnPos = getBlockPosWithDistance(pos, world, 5, 10);
            mob.refreshPositionAndAngles(spawnPos, 0, 0);
            world.spawnEntity(mob);
            director.startTracking(mob,cost);
        }
    }

    public static void spawnMobGroup(InvasionMobConfig.MobGroupData data, ServerWorld world, BlockPos pos, @Nullable InvasionDirector director) {

        List<InvasionMobConfig.MobUnitData> unitData = data.mobs();

        int totalCredits = data.cost();
        Random random = world.random;
        List<Integer> unitCounts = new ArrayList<>();
        int unitCreditWeightSum = 0;

        for (int i = 0; i < unitData.size(); i++) {
            int unitCount = random.nextBetween(unitData.get(i).minCount(), unitData.get(i).maxCount());
            unitCreditWeightSum += unitCount * unitData.get(i).creditWeight();
            unitCounts.add(unitCount);
        }

        if (director == null) {
            for (int i = 0; i < unitData.size(); i++) {
                for (int j = 0; j < unitCounts.get(i); j++)
                    spawnMob(unitData.get(i).mobid(), world, pos);
            }
        }else{
            for (int i = 0; i < unitData.size(); i++) {
                for (int j = 0; j < unitCounts.get(i); j++)
                    spawnMob(unitData.get(i).mobid(), world, pos, director, ((float)unitData.get(i).creditWeight()*totalCredits/unitCreditWeightSum));
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
