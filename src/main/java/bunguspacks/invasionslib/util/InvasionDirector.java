package bunguspacks.invasionslib.util;

import bunguspacks.invasionslib.InvasionsLib;
import bunguspacks.invasionslib.StateSaverAndLoader;
import bunguspacks.invasionslib.config.InvasionMobConfig;
import bunguspacks.invasionslib.config.InvasionProfileConfig;
import bunguspacks.invasionslib.world.spawner.InvasionMobSpawner;
import bunguspacks.invasionslib.world.spawner.SpawnLocationFinder;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;

import java.util.ArrayList;
import java.util.List;


public class InvasionDirector {
    //Observer handles tracking mobs spawned by invasion
    private InvasionMobObserver observer = new InvasionMobObserver();
    //total credits killed from both directors
    private float creditsKilled;
    //credits killed from passive director only; used for wave spawning
    private float passiveCreditsKilled;
    //credits of mobs currently alive
    private float livingCredits;
    //current rate of credit income by the passive director
    private float creditRate;
    //fraction of the invasion the passive director attempts to keep alive
    private float intensity;
    //passive credits the director has remaining in its 'savings account'
    private float totalPassiveCredits;
    //total amount of credits allocated to passive spawning
    private float passiveCredits;
    //total amount of credits allocated to waves
    private float waveCredits;
    //passive credits the director currently has access to in its 'checking account'
    private float currentPassiveCredits;
    //invasion direction of approach
    private float direction;
    //invasion profile format described in InvasionProfileConfig
    private final InvasionProfileConfig.DirectorProfileData profile;
    //invasion mob data format described in InvasionMobConfig
    private final InvasionMobConfig.InvasionMobData mobData;
    //current top of deck for the passive director; not persistent
    private InvasionMobConfig.InvasionMobGroupData passiveTopdeck;
    //current top of deck for the wave director; not persistent
    private InvasionMobConfig.InvasionMobGroupData waveTopdeck;
    //world the director acts on, currently a tad jank
    private final ServerWorld world;
    //origin of invasion spawn attempts
    private BlockPos origin;
    //list of which waves described by the profile have been spawned yet
    private List<Boolean> wavesFinished;
    private final Random random;
    private final List<BlockPos> allLocations;


    //create director from all info; usable with builder
    public InvasionDirector(float creditTotal, float intensity, ServerWorld world, BlockPos pos, InvasionProfileConfig.DirectorProfileData profile, InvasionMobConfig.InvasionMobData mobData, float direction) {
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
        passiveCredits = totalPassiveCredits;
        currentPassiveCredits = 0;
        random = world.random;
        passiveTopdeck = getRandomGroup(false);
        waveTopdeck = getRandomGroup(true);
        wavesFinished = new ArrayList<>();
        this.direction = direction;
        allLocations = new ArrayList<>();
        allLocations.addAll(SpawnLocationFinder.findAllLocations(this.mobData, this.world, origin, direction));
        System.out.println("d");
    }

    //add a mob to the list of "invasion mobs"
    public void startTracking(MobEntity m, float cost, boolean waveMob) {
        observer.addMob(m, cost, waveMob);
        livingCredits += cost;
    }

    //check over all invasion mobs, modifying living and killed credit totals appropriately
    public void checkMobs() {
        //grab the amount of mobs killed since last check
        float thisPassiveKill = observer.checkPassiveMobs();
        float thisWaveKill = observer.checkWaveMobs();
        creditsKilled += thisPassiveKill + thisWaveKill;
        livingCredits -= thisPassiveKill + thisWaveKill;
        //redo wavesFinished by checking for each wave if the new passive kill percentage exceeds its threshold
        wavesFinished.clear();
        for (int i = 0; i < profile.waves().size(); i++) {
            InvasionProfileConfig.DirectorWaveData wave = profile.waves().get(i);
            wavesFinished.add(passiveCreditsKilled / passiveCredits >= wave.progressPoint());
        }
        passiveCreditsKilled += thisPassiveKill;
        //for each wave, if the passive kill threshold has just passed the wave threshold, spawn the wave
        boolean allWavesFinished = true;
        for (int i = 0; i < wavesFinished.size(); i++) {
            InvasionProfileConfig.DirectorWaveData wave = profile.waves().get(i);
            if (wavesFinished.get(i) ^ passiveCreditsKilled / passiveCredits >= wave.progressPoint()) {
                spawnWave(waveCredits * wave.sizeFraction());
            }
            if (!wavesFinished.get(i)) {
                allWavesFinished = false;
            }
        }
        //if all waves are finished and the passive director has finished spawning, end the invasion
        if (allWavesFinished && creditsKilled >= (waveCredits + passiveCredits)) {
            InvasionsLib.LOGGER.info("Finished invasion.");
            InvasionsLib.invasionDirectorUpdater.removeDirector();
        }
    }

    //currently VERY jank. very aggressively adjusts credit income to keep a certain credit value of enemies alive
    public void updateCredits() {
        if (totalPassiveCredits > 0 || !wavesFinished.contains(Boolean.FALSE)) {
            if (livingCredits < intensity) {
                creditRate *= 1.1f;
            } else {
                if (creditRate > 0.005)
                    creditRate /= 1.1f;
            }
            currentPassiveCredits += creditRate;
            totalPassiveCredits -= creditRate;
        }

    }


    //attempt to spawn the top of deck with the passive director; TODO: implement real fail behaviour instead of just waiting for credits
    public void trySpawn() {
        if (currentPassiveCredits >= passiveTopdeck.cost()) {
            InvasionMobSpawner.spawnMobGroup(passiveTopdeck.data(), world, allLocations.get(random.nextInt(allLocations.size())), this, false);
            currentPassiveCredits -= passiveTopdeck.cost();
            passiveTopdeck = getRandomGroup(false);
        }
    }

    //spawns a wave with a certain credit total by repeating spawn attempts until the credits are exhausted
    public void spawnWave(float credits) {
        InvasionsLib.LOGGER.info("Invasion wave spawned with " + credits + " credits.");
        for (int i = 0; i < 100; i++) {
            if (waveTopdeck.cost() <= credits) {
                InvasionMobSpawner.spawnMobGroup(waveTopdeck.data(), world, allLocations.get(random.nextInt(allLocations.size())), this, true);
                credits -= waveTopdeck.cost();
            }
            waveTopdeck = getRandomGroup(true);
        }
    }

    //generate a random mob group to spawn given weightings
    private InvasionMobConfig.InvasionMobGroupData getRandomGroup(boolean isWave) {
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

    //getters for state loading
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

    public List<Boolean> getWavesFinished() {
        return wavesFinished;
    }

    public float getPassiveCredits() {
        return passiveCredits;
    }

    public float getDirection() {
        return direction;
    }

    //rebuild the director from a savestate
    public InvasionDirector(StateSaverAndLoader save) {
        waveCredits = save.waveCredits;
        totalPassiveCredits = save.totalPassiveCredits;
        currentPassiveCredits = save.currentPassiveCredits;
        creditRate = save.creditRate;
        intensity = save.intensity;
        livingCredits = save.livingCredits;
        passiveCreditsKilled = save.passiveCreditsKilled;
        creditsKilled = save.totalCreditsKilled;
        direction = save.direction;
        origin = new BlockPos(save.originPos[0], save.originPos[1], save.originPos[2]);
        profile = InvasionProfileConfig.profiles.getOrDefault(save.invasionProfile, null);
        mobData = InvasionMobConfig.invasionMobs.getOrDefault(save.invasionMobData, null);
        world = save.world;
        random = world.random;
        observer = new InvasionMobObserver(save);
        passiveCredits = save.passiveCredits;
        passiveTopdeck = getRandomGroup(false);
        waveTopdeck = getRandomGroup(true);
        wavesFinished = save.wavesFinished;
        allLocations = new ArrayList<>();
        allLocations.addAll(SpawnLocationFinder.findAllLocations(this.mobData, this.world, origin, direction));
    }
}
