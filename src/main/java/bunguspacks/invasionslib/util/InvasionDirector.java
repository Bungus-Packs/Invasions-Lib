package bunguspacks.invasionslib.util;

import bunguspacks.invasionslib.InvasionsLib;
import bunguspacks.invasionslib.config.InvasionDirectorConfig;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;


public class InvasionDirector {
    private final InvasionMobObserver observer = new InvasionMobObserver();
    private float credits;
    private float creditsKilled;
    private float livingCredits;
    private float creditRate;
    private final InvasionDirectorConfig.DirectorProfileData profile;

    //this constructor chooses a weighted random profile
    public InvasionDirector(float c, World world) {
        creditRate = c;
        credits = 0;
        creditsKilled = 0;
        livingCredits = 0;
        final Random random = world.random;
        float profileRandom = random.nextFloat();
        float chanceCumSum = 0f;
        InvasionDirectorConfig.DirectorProfileData out = null;
        int i = 0;
        while (chanceCumSum < profileRandom) {
            out = InvasionDirectorConfig.profiles.get(i);
            chanceCumSum += out.chance();
            i++;
        }
        profile = out;
    }

    //this constructor chooses a set profile
    public InvasionDirector(float c, World world, InvasionDirectorConfig.DirectorProfileData profile) {
        creditRate = c;
        credits = 0;
        creditsKilled = 0;
        livingCredits = 0;
        this.profile = profile;
    }

    //add a mob to the list of "invasion mobs"
    public void startTracking(MobEntity m, float cost) {
        observer.addMob(m, cost);
        livingCredits += cost;
    }

    //check over all invasion mobs, modifying living and killed credit totals appropriately
    public void checkMobs() {
        float thisKill = observer.checkMobs();
        creditsKilled += thisKill;
        livingCredits -= thisKill;
    }

    public void updateRate() {

    }
}
