package bunguspacks.invasionslib.util;

import bunguspacks.invasionslib.StateSaverAndLoader;
import bunguspacks.invasionslib.config.InvasionMobConfig;
import bunguspacks.invasionslib.config.InvasionProfileConfig;
import bunguspacks.invasionslib.world.spawner.MobSpawner;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;

import java.util.ArrayList;
import java.util.List;


public class InvasionDirector {
    private InvasionMobObserver observer = new InvasionMobObserver();
    private float creditsKilled;
    private float passiveCreditsKilled;
    private float livingCredits;
    private float creditRate;
    private float intensity;
    private float totalPassiveCredits;
    private float waveCredits;
    private float currentPassiveCredits;
    private final InvasionProfileConfig.DirectorProfileData profile;
    private final InvasionMobConfig.InvasionMobData mobData;
    private InvasionMobConfig.InvasionMobGroupData passiveTopdeck;
    private InvasionMobConfig.InvasionMobGroupData waveTopdeck;
    private final ServerWorld world;
    private BlockPos origin;


    public InvasionDirector(float creditTotal, float intensity, ServerWorld world, BlockPos pos, InvasionProfileConfig.DirectorProfileData profile, InvasionMobConfig.InvasionMobData mobData) {
        this.intensity = intensity;
        creditsKilled = 0;
        passiveCreditsKilled = 0;
        livingCredits = 0;
        this.profile = profile;
        this.mobData = mobData;
        this.world = world;
        creditRate = 1f;
        origin = pos;
        waveCredits = creditTotal * profile.waveFraction();
        totalPassiveCredits = creditTotal - waveCredits;
        currentPassiveCredits = 0;
        passiveTopdeck = getRandomGroup(false);
        waveTopdeck = getRandomGroup(true);
    }

    //add a mob to the list of "invasion mobs"
    public void startTracking(MobEntity m, float cost, boolean waveMob) {
        observer.addMob(m, cost, waveMob);
        livingCredits += cost;
    }

    //check over all invasion mobs, modifying living and killed credit totals appropriately
    public void checkMobs() {
        float thisPassiveKill = observer.checkPassiveMobs();
        float thisWaveKill = observer.checkWaveMobs();
        creditsKilled += thisPassiveKill + thisWaveKill;
        livingCredits -= thisPassiveKill + thisWaveKill;
        List<Boolean> waveThresholds = new ArrayList<>();
        for (int i = 0; i < profile.waves().size(); i++) {
            InvasionProfileConfig.DirectorWaveData wave = profile.waves().get(i);
            waveThresholds.add(passiveCreditsKilled / totalPassiveCredits >= wave.progressPoint());
        }
        passiveCreditsKilled += thisPassiveKill;
        //if the passive kills have passed a threshold
        for (int i = 0; i < waveThresholds.size(); i++) {
            InvasionProfileConfig.DirectorWaveData wave = profile.waves().get(i);
            if (waveThresholds.get(i) ^ passiveCreditsKilled / totalPassiveCredits >= wave.progressPoint()) {
                spawnWave(waveCredits * wave.sizeFraction());
            }
        }
    }

    public void updateCredits() {
        if (livingCredits < intensity) {
            creditRate *= 1.1f;
        } else {
            if (creditRate > 0.005)
                creditRate /= 1.1f;
        }
        currentPassiveCredits += creditRate;
    }

    public void trySpawn() {
        if (currentPassiveCredits >= passiveTopdeck.cost()) {
            MobSpawner.spawnMobGroup(passiveTopdeck.data(), world, origin, this, false);
            currentPassiveCredits -= passiveTopdeck.cost();
            passiveTopdeck = getRandomGroup(false);
        }
    }

    public void spawnWave(float credits) {
        for (int i = 0; i < 100; i++) {
            if (waveTopdeck.cost() <= credits) {
                MobSpawner.spawnMobGroup(waveTopdeck.data(), world, origin, this, true);
                credits -= waveTopdeck.cost();
            }
            waveTopdeck = getRandomGroup(true);
        }
    }

    //generate a random mob group to spawn given weightings
    private InvasionMobConfig.InvasionMobGroupData getRandomGroup(boolean isWave) {
        final Random random = world.random;
        float mobRandom = random.nextFloat();
        float chanceCumSum = 0f;
        InvasionMobConfig.InvasionMobGroupData out = null;
        int i = 0;
        while (chanceCumSum < mobRandom) {
            List<InvasionMobConfig.InvasionMobGroupData> data = isWave ? mobData.waveMobs() : mobData.passiveMobs();
            out = data.get(i);
            chanceCumSum += data.get(i).chance();
            i++;
        }
        return out;
    }

    public InvasionProfileConfig.DirectorProfileData getProfile() {
        return profile;
    }

    public BlockPos getOrigin() {
        return origin;
    }

    public float getCreditRate() {
        return creditRate;
    }

    public float getCreditsKilled() {
        return creditsKilled;
    }

    public float getCurrentPassiveCredits() {
        return currentPassiveCredits;
    }

    public float getIntensity() {
        return intensity;
    }

    public float getLivingCredits() {
        return livingCredits;
    }

    public float getPassiveCreditsKilled() {
        return passiveCreditsKilled;
    }

    public float getTotalPassiveCredits() {
        return totalPassiveCredits;
    }

    public float getWaveCredits() {
        return waveCredits;
    }

    public InvasionMobConfig.InvasionMobData getMobData() {
        return mobData;
    }

    public InvasionMobObserver getObserver() {
        return observer;
    }

    public InvasionDirector(StateSaverAndLoader save) {
        waveCredits = save.waveCredits;
        totalPassiveCredits = save.totalPassiveCredits;
        currentPassiveCredits = save.currentPassiveCredits;
        creditRate = save.creditRate;
        intensity = save.intensity;
        livingCredits = save.livingCredits;
        passiveCreditsKilled = save.passiveCreditsKilled;
        creditsKilled = save.totalCreditsKilled;
        origin = new BlockPos(save.originPos[0], save.originPos[1], save.originPos[2]);
        profile = InvasionProfileConfig.profiles.getOrDefault(save.invasionProfile, null);
        mobData = InvasionMobConfig.invasionMobs.getOrDefault(save.invasionMobData, null);
        world = save.world;
        observer = new InvasionMobObserver(save);

        passiveTopdeck = getRandomGroup(false);
        waveTopdeck = getRandomGroup(true);

    }
}
