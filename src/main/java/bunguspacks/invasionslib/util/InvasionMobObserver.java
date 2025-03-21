package bunguspacks.invasionslib.util;

import bunguspacks.invasionslib.StateSaverAndLoader;
import net.fabricmc.loader.impl.lib.tinyremapper.extension.mixin.common.data.Pair;
import net.minecraft.entity.mob.MobEntity;

import java.util.ArrayList;
import java.util.List;

public class InvasionMobObserver {
    private List<Pair<MobEntity, Float>> activePassiveMobs = new ArrayList<>();
    private List<Pair<MobEntity, Float>> activeWaveMobs = new ArrayList<>();

    public InvasionMobObserver() {
    }

    public InvasionMobObserver(StateSaverAndLoader save) {
        for (int i = 0; i < save.trackedMobs.size(); i++) {
            if (save.trackedMobsIsWaveSpawn.get(i)) {
                activeWaveMobs.add(Pair.of((MobEntity) save.world.getEntity(save.trackedMobs.get(i)), save.trackedMobCosts.get(i)));
            } else {
                activePassiveMobs.add(Pair.of((MobEntity) save.world.getEntity(save.trackedMobs.get(i)), save.trackedMobCosts.get(i)));
            }
        }
    }

    public void addMob(MobEntity m, float cost, boolean waveMob) {
        if (waveMob) {
            activeWaveMobs.add(Pair.of(m, cost));
        } else {
            activePassiveMobs.add(Pair.of(m, cost));
        }

    }

    public float checkPassiveMobs() {
        //iterate over all mobs, return the number found dead or missing and remove from checking
        float out = 0;
        for (int i = 0; i < activePassiveMobs.size(); i++) {
            MobEntity m = activePassiveMobs.get(i).first();
            if (m == null || !m.isAlive()) {
                out += activePassiveMobs.get(i).second();
                activePassiveMobs.remove(i);
                i--;
            }
        }
        return out;
    }

    public float checkWaveMobs() {
        //iterate over all mobs, return the number found dead or missing and remove from checking
        float out = 0;
        for (int i = 0; i < activeWaveMobs.size(); i++) {
            MobEntity m = activeWaveMobs.get(i).first();
            if (m == null || !m.isAlive()) {
                out += activeWaveMobs.get(i).second();
                activeWaveMobs.remove(i);
                i--;
            }
        }
        return out;
    }

    public List<Pair<MobEntity, Float>> getActivePassiveMobs() {
        return activePassiveMobs;
    }

    public List<Pair<MobEntity, Float>> getActiveWaveMobs() {
        return activeWaveMobs;
    }
}
