package bunguspacks.invasionslib.world.spawner;

import bunguspacks.invasionslib.config.MobGroupConfig;
import bunguspacks.invasionslib.mixin.MobEntityAccessor;
import bunguspacks.invasionslib.mobbehaviors.MobInvasionPlayerGoal;
import bunguspacks.invasionslib.util.InvasionDirector;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ai.goal.*;
import net.minecraft.entity.ai.goal.GoalSelector;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.mob.PathAwareEntity;
import net.minecraft.registry.Registries;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.spawner.Spawner;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class InvasionMobSpawner implements Spawner {
    @Override
    public int spawn(ServerWorld world, boolean spawnMonsters, boolean spawnAnimals) {
        return 0;
    }

    //spawn mob around specified position and notify director to start tracking it
    public static void spawnMob(String mobId, ServerWorld world, BlockPos pos, InvasionDirector director, float cost, boolean waveMob) {
        EntityType<?> mobType = Registries.ENTITY_TYPE.get(new Identifier(mobId));
        MobEntity mob = (MobEntity) mobType.create(world);
        if (mob != null) {
            if (mob instanceof PathAwareEntity){
                GoalSelector goalSel = ((MobEntityAccessor)mob).getGoalSelector();
                Set<PrioritizedGoal> goalsList = goalSel.getGoals();
                Set<PrioritizedGoal> whitelistedGoals = new HashSet<PrioritizedGoal>();
                for (PrioritizedGoal goal:goalsList){
                    if (goal.getGoal() instanceof AttackGoal || goal.getGoal() instanceof ProjectileAttackGoal || goal.getGoal() instanceof MeleeAttackGoal){
                        whitelistedGoals.add(goal);
                    }
                }
                mob.clearGoalsAndTasks();
                mob.clearPositionTarget();
                goalSel.add(1, new MobInvasionPlayerGoal((PathAwareEntity)mob, 2, 100));
                for (PrioritizedGoal goal: whitelistedGoals){
                    goalSel.add(goal.getPriority(), goal.getGoal());
                }
            }
            mob.refreshPositionAndAngles(pos, 0, 0);
            world.spawnEntity(mob);
            director.startTracking(mob, cost, waveMob);
        }
    }

    public static void spawnMobGroup(MobGroupConfig.MobGroupData data, ServerWorld world, BlockPos pos, @Nullable InvasionDirector director, boolean waveMob) {
        List<MobGroupConfig.MobUnitData> unitData = data.mobs();
        int totalCredits = data.cost();
        Random random = world.random;
        List<Integer> unitCounts = new ArrayList<>();
        List<BlockPos> unitSpawnLocations = new ArrayList<>();

        //predetermine random unit counts so that credits can be appropriately distributed
        int unitCreditWeightSum = 0;
        for (int i = 0; i < unitData.size(); i++) {
            int unitCount = random.nextBetween(unitData.get(i).minCount(), unitData.get(i).maxCount());
            unitCreditWeightSum += unitCount * unitData.get(i).creditWeight();
            unitCounts.add(unitCount);
        }

        //if function was passed a director, spawn mobs with associated cost
        for (int i = 0; i < unitData.size(); i++) {
            unitSpawnLocations.addAll(SpawnLocationFinder.getNearbyLocations(EntityType.get(unitData.get(i).mobId()).get(), pos, unitCounts.get(i)));
            for (int j = 0; j < unitCounts.get(i); j++) {
                spawnMob(unitData.get(i).mobId(), world, unitSpawnLocations.get(random.nextInt(unitCounts.get(i))), director, ((float) unitData.get(i).creditWeight() * totalCredits / unitCreditWeightSum), waveMob);
            }
            unitSpawnLocations.clear();
        }
    }
}
