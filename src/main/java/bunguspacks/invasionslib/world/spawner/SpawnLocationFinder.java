package bunguspacks.invasionslib.world.spawner;

import bunguspacks.invasionslib.config.InvasionMobConfig;
import net.minecraft.block.Block;
import net.minecraft.entity.EntityType;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.Heightmap;
import net.minecraft.world.World;

import java.util.HashMap;
import java.util.HashSet;
import java.util.function.Predicate;

public class SpawnLocationFinder {

    private static HashMap<EntityType<?>, HashSet<BlockPos>> entityTypeLocationsMap = new HashMap<>();
    private float invasionDirection;
    private World world;
    private BlockPos origin;

    //finds all valid spawn locations for every entity type in an invasion, and returns a set of all locations found
    public static HashSet<BlockPos> findAllLocations(InvasionMobConfig.InvasionMobData invasionMobData, World world, BlockPos origin, float invasionDirection) {
        // Maps all valid spawn locations to entity types
        for (String s : InvasionMobConfig.getInvasionMobIds(invasionMobData)) {
            EntityType<?> entityType = EntityType.get(s).get();
            entityTypeLocationsMap.put(entityType, findLocations(entityType, world, origin, invasionDirection));
        }
        return getAllLocations();
    }

    //returns a set of all valid locations in scope for a given entity type
    private static HashSet<BlockPos> findLocations(EntityType<?> entityType, World world, BlockPos origin, float invasionDirection) {
        HashSet<BlockPos> validLocations = new HashSet<>();
        int maxRad = 40;
        int minRad = 20;
        // Finds each pair of coordinates in the band outside the minimum spawn radius and inside the maximum spawn radius
        for (int x = -maxRad; x <= maxRad; x++) {
            for (int z = -maxRad; z <= maxRad; z++) {
                if (x * x + z * z <= maxRad * maxRad && x * x + z * z > minRad * minRad) {
                    // Filters down to those within tolerance of the direction of approach
                    double angle = Math.atan2(x, z);
                    if ((MathHelper.wrapDegrees(angle - (invasionDirection - Math.PI / 6)) + 2 * Math.PI) % (2 * Math.PI) <= Math.PI / 3) {
                        // Adjusts coordinates to relate to the origin, then checks if the space is empty. If it is, it adds that position to the map
                        int xReal = x + origin.getX();
                        int zReal = z + origin.getZ();
                        boolean isValid;
                        BlockPos pos = new BlockPos(xReal, world.getTopY(Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, xReal, zReal), zReal);
                        isValid = world.isSpaceEmpty(entityType.createSimpleBoundingBox((double)pos.getX() + (double)0.5F, (double)pos.getY(), (double)pos.getZ() + (double)0.5F));
                        if (isValid) {
                            validLocations.add(pos);
                        }
                    }
                }
            }
        }
        return validLocations;
    }

    //returns a set of all locations from all entities in the map
    public static HashSet<BlockPos> getAllLocations() {
        HashSet<BlockPos> allLocations = new HashSet<>();
        for (HashSet<BlockPos> posSet : entityTypeLocationsMap.values()) {
            allLocations.addAll(posSet.stream().toList());
        }
        return allLocations;
    }

    //returns a set of all locations near the center of a mob group on which a given entity can spawn
    public static HashSet<BlockPos> getNearbyLocations(EntityType<?> entityType, BlockPos pos, int targetCount) {
        HashSet<BlockPos> nearbyLocations = new HashSet<>();
        int range = 5;
        while(nearbyLocations.size() < 2 * targetCount) {
            range++;
            for (BlockPos testPos : entityTypeLocationsMap.get(entityType)) {
                if (Math.abs(testPos.getX() - pos.getX()) <= range && Math.abs(testPos.getZ() - pos.getZ()) <= range) {
                    nearbyLocations.add(testPos);
                }
            }
        }
        return nearbyLocations;
    }


}
