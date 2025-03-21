package bunguspacks.invasionslib;

import bunguspacks.invasionslib.util.InvasionDirector;
import bunguspacks.invasionslib.util.InvasionMobObserver;
import net.fabricmc.loader.impl.lib.tinyremapper.extension.mixin.common.data.Pair;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.world.PersistentState;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;


public class StateSaverAndLoader extends PersistentState {

    public boolean active;
    public float waveCredits;
    public float totalPassiveCredits;
    public float currentPassiveCredits;
    public float creditRate;
    public float intensity;
    public float livingCredits;
    public float passiveCreditsKilled;
    public float totalCreditsKilled;
    public float direction;
    public String invasionProfile;
    public String invasionMobData;
    public int[] originPos;
    public List<UUID> trackedMobs;
    public List<Float> trackedMobCosts;
    public List<Boolean> trackedMobsIsWaveSpawn;
    public ServerWorld world;
    public List<Boolean> wavesFinished;
    public float passiveCredits;


    //generate this object from a director object
    public void loadFromDirector(InvasionDirector dir) {
        waveCredits = dir.getWaveCredits();
        totalPassiveCredits = dir.getTotalPassiveCredits();
        currentPassiveCredits = dir.getCurrentPassiveCredits();
        creditRate = dir.getCreditRate();
        intensity = dir.getIntensity();
        livingCredits = dir.getLivingCredits();
        passiveCreditsKilled = dir.getPassiveCreditsKilled();
        totalCreditsKilled = dir.getCreditsKilled();
        direction = dir.getDirection();
        invasionProfile = dir.getProfile().name();
        invasionMobData = dir.getMobData().name();
        originPos = new int[]{dir.getOrigin().getX(), dir.getOrigin().getY(), dir.getOrigin().getZ()};
        InvasionMobObserver observer = dir.getObserver();
        List<Pair<MobEntity, Float>> passives = observer.getActivePassiveMobs();
        List<Pair<MobEntity, Float>> waves = observer.getActiveWaveMobs();
        trackedMobs = new ArrayList<>();
        trackedMobCosts = new ArrayList<>();
        trackedMobsIsWaveSpawn = new ArrayList<>();
        for (Pair<MobEntity, Float> p : passives) {
            trackedMobs.add(p.first().getUuid());
            trackedMobCosts.add(p.second());
            trackedMobsIsWaveSpawn.add(false);
        }
        for (Pair<MobEntity, Float> p : waves) {
            trackedMobs.add(p.first().getUuid());
            trackedMobCosts.add(p.second());
            trackedMobsIsWaveSpawn.add(true);
        }
        active = true;
        passiveCredits = dir.getPassiveCredits();
        wavesFinished = dir.getWavesFinished();
        markDirty();
    }

    //generate a director object from this object
    public InvasionDirector generateDirector() {
        return new InvasionDirector(this);
    }

    //load this object from saved nbt
    public static StateSaverAndLoader createFromNbt(NbtCompound nbtData) {
        NbtCompound nbt = nbtData.getCompound("directorData");
        StateSaverAndLoader state = new StateSaverAndLoader();
        state.waveCredits = nbt.getFloat("waveCredits");
        state.totalPassiveCredits = nbt.getFloat("totalPassiveCredits");
        state.currentPassiveCredits = nbt.getFloat("currentPassiveCredits");
        state.creditRate = nbt.getFloat("creditRate");
        state.intensity = nbt.getFloat("intensity");
        state.livingCredits = nbt.getFloat("livingCredits");
        state.passiveCreditsKilled = nbt.getFloat("passiveCreditsKilled");
        state.totalCreditsKilled = nbt.getFloat("totalCreditsKilled");
        state.direction = nbt.getFloat("direction");
        state.invasionProfile = nbt.getString("invasionProfile");
        state.invasionMobData = nbt.getString("invasionMobData");
        state.originPos = nbt.getIntArray("originPos");
        state.passiveCredits = nbt.getFloat("passiveCredits");
        int trackedMobCount = nbt.getCompound("trackedMobs").getKeys().size();
        state.trackedMobs = new ArrayList<>();
        state.trackedMobCosts = new ArrayList<>();
        state.trackedMobsIsWaveSpawn = new ArrayList<>();
        for (int i = 0; i < trackedMobCount; i++) {
            NbtCompound mob = nbt.getCompound("trackedMobs").getCompound("" + i);
            state.trackedMobs.add(mob.getUuid("UUID"));
            state.trackedMobCosts.add(mob.getFloat("cost"));
            state.trackedMobsIsWaveSpawn.add(mob.getBoolean("isWaveSpawn"));
        }
        state.active = nbt.getBoolean("active");
        int trackedWaveCount = nbt.getCompound("waves").getKeys().size();
        state.wavesFinished = new ArrayList<>();
        for (int i = 0; i < trackedWaveCount; i++) {
            NbtCompound wave = nbt.getCompound("waves").getCompound("" + i);
            state.wavesFinished.add(wave.getBoolean("finished"));
        }
        return state;
    }

    //create a blank instance
    public static StateSaverAndLoader createNew() {
        StateSaverAndLoader state = new StateSaverAndLoader();
        state.waveCredits = 0;
        state.totalPassiveCredits = 0;
        state.currentPassiveCredits = 0;
        state.creditRate = 0;
        state.intensity = 0;
        state.livingCredits = 0;
        state.passiveCreditsKilled = 0;
        state.passiveCredits = 0;
        state.totalCreditsKilled = 0;
        state.direction = 0;
        state.invasionProfile = "";
        state.invasionMobData = "";
        state.originPos = new int[3];
        state.trackedMobs = new ArrayList<>();
        state.trackedMobCosts = new ArrayList<>();
        state.trackedMobsIsWaveSpawn = new ArrayList<>();
        state.active = false;
        state.wavesFinished = new ArrayList<>();
        return state;
    }

    //save this object into nbt
    @Override
    public NbtCompound writeNbt(NbtCompound nbt) {
        NbtCompound data = new NbtCompound();
        data.putFloat("waveCredits", waveCredits);
        data.putFloat("totalPassiveCredits", totalPassiveCredits);
        data.putFloat("currentPassiveCredits", currentPassiveCredits);
        data.putFloat("creditRate", creditRate);
        data.putFloat("intensity", intensity);
        data.putFloat("livingCredits", livingCredits);
        data.putFloat("passiveCreditsKilled", passiveCreditsKilled);
        data.putFloat("totalCreditsKilled", totalCreditsKilled);
        data.putFloat("direction", direction);
        data.putString("invasionProfile", invasionProfile);
        data.putString("invasionMobData", invasionMobData);
        data.putIntArray("originPos", originPos);
        data.putFloat("passiveCredits", passiveCredits);
        NbtCompound trackedMobData = new NbtCompound();
        for (int i = 0; i < trackedMobs.size(); i++) {
            NbtCompound mob = new NbtCompound();
            mob.putUuid("UUID", trackedMobs.get(i));
            mob.putFloat("cost", trackedMobCosts.get(i));
            mob.putBoolean("isWaveSpawn", trackedMobsIsWaveSpawn.get(i));
            trackedMobData.put("" + i, mob);
        }
        NbtCompound waves = new NbtCompound();
        for (int i = 0; i < wavesFinished.size(); i++) {
            NbtCompound wave = new NbtCompound();
            wave.putBoolean("finished", wavesFinished.get(i));
            waves.put("" + i, wave);
        }
        data.put("waves", waves);
        data.put("trackedMobs", trackedMobData);
        data.putBoolean("active", active);
        nbt.put("directorData", data);
        return nbt;
    }

    public static StateSaverAndLoader getServerState(MinecraftServer server) {
        ServerWorld world = server.getWorld(ServerWorld.OVERWORLD);
        StateSaverAndLoader state = world.getPersistentStateManager().getOrCreate(StateSaverAndLoader::createFromNbt, StateSaverAndLoader::createNew, InvasionsLib.MOD_ID);
        state.world = world;
        return state;
    }
}
