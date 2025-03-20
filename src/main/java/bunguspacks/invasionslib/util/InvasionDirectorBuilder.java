package bunguspacks.invasionslib.util;

import bunguspacks.invasionslib.config.InvasionMobConfig;
import bunguspacks.invasionslib.config.InvasionProfileConfig;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;

import java.util.Map;
import java.util.Set;

//helper class to build invasion directors in a more modular way
public class InvasionDirectorBuilder {
    private float creditTotal;
    private float intensity;
    private ServerWorld world;
    private BlockPos origin;
    private InvasionProfileConfig.DirectorProfileData profile;
    private InvasionMobConfig.InvasionMobData mobData;

    private InvasionDirectorBuilder(ServerWorld w, BlockPos pos) {
        world = w;
        creditTotal = 0;
        intensity = 0;
        profile = null;
        mobData = null;
        origin = pos;
    }

    public static InvasionDirectorBuilder create(ServerWorld w, BlockPos pos) {
        return new InvasionDirectorBuilder(w, pos);
    }

    public InvasionDirectorBuilder withCreditTotal(int total) {
        creditTotal = total;
        return this;
    }

    public InvasionDirectorBuilder withIntensity(float intensity) {
        this.intensity = intensity;
        return this;
    }

    public InvasionDirectorBuilder withIntensityFraction(float intensityMult) {
        this.intensity = creditTotal * intensityMult;
        return this;
    }

    public InvasionDirectorBuilder withProfile(InvasionProfileConfig.DirectorProfileData data) {
        profile = data;
        return this;
    }

    public InvasionDirectorBuilder withMobData(InvasionMobConfig.InvasionMobData data) {
        mobData = data;
        return this;
    }

    public InvasionDirector build() {
        final Random random = world.random;
        //if no profile is supplied, choose a weighted random one
        if (profile == null) {
            float profileRandom = random.nextFloat();
            float chanceCumSum = 0f;
            InvasionProfileConfig.DirectorProfileData out = null;
            Set<Map.Entry<String, InvasionProfileConfig.DirectorProfileData>> set = InvasionProfileConfig.profiles.entrySet();
            int i = 0;
            for (Map.Entry<String, InvasionProfileConfig.DirectorProfileData> entry : set) {
                out = entry.getValue();
                chanceCumSum += out.chance();
                if (chanceCumSum > profileRandom) {
                    break;
                }
            }
            profile = out;
        }
        //if no mob data is supplied, choose a weighted random one
        if (mobData == null) {
            float mobDataRandom = random.nextFloat();
            float chanceCumSum = 0f;
            InvasionMobConfig.InvasionMobData out = null;
            Set<Map.Entry<String, InvasionMobConfig.InvasionMobData>> set = InvasionMobConfig.invasionMobs.entrySet();
            int i = 0;
            for (Map.Entry<String, InvasionMobConfig.InvasionMobData> entry : set) {
                out = entry.getValue();
                chanceCumSum += out.chance();
                if (chanceCumSum > mobDataRandom) {
                    break;
                }
            }
            mobData = out;
        }
        return new InvasionDirector(creditTotal, intensity, world, origin, profile, mobData);
    }
}
