package bunguspacks.invasionslib.world.spawner;
import bunguspacks.invasionslib.config.InvasionMobConfig;
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

import java.util.List;

public class MobSpawner implements Spawner{
    @Override
    public int spawn(ServerWorld world, boolean spawnMonsters, boolean spawnAnimals) {
        return 0;
    }

    public static void spawnMob(ServerWorld world, BlockPos pos){
        EntityType<?> mobType= Registries.ENTITY_TYPE.get(new Identifier("minecraft:zombie"));
        MobEntity mob=(MobEntity)mobType.create(world);
        if(mob!=null) {
            BlockPos spawnPos=getBlockPosWithDistance(pos,world,5,10);
            mob.refreshPositionAndAngles(spawnPos, 0, 0);
            world.spawnEntity(mob);
        }
    }

    public static void spawnMobGroup(InvasionMobConfig.MobGroupData data, ServerWorld world, BlockPos pos){
        List<InvasionMobConfig.MobUnitData> unitData=data.mobs();
        for(int i=0;i<unitData.size();i++){
            Random random=world.random;

        }
    }

    private static BlockPos getBlockPosWithDistance(BlockPos pos, World world, int distanceMin, int distanceMax) {
        final Random random = world.random;
        double d=0;
        double x=0;
        double z=0;
        d=random.nextBetween(distanceMin, distanceMax);
        x=random.nextBetween(0, (int)d);
        if (x==0){
            z=d;
        }else{
            z=Math.sqrt((d*d)-(x*x));
            if(random.nextBoolean()) {
                x=x*-1;
            }
        }
        if(random.nextBoolean()) {
            z=z*-1;
        }
        return new BlockPos(pos.getX()+(int)x, world.getTopY(Heightmap.Type.WORLD_SURFACE, pos.getX()+(int)x, pos.getZ()+(int)z), pos.getZ()+(int)z);
    }
}
