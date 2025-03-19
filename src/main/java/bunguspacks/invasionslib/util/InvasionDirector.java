package bunguspacks.invasionslib.util;

import bunguspacks.invasionslib.InvasionsLib;
import net.minecraft.entity.mob.MobEntity;

public class InvasionDirector {
    private final InvasionMobObserver observer=new InvasionMobObserver();
    private float credits;
    private float creditsKilled;
    private float livingCredits;
    private float creditRate;

    public InvasionDirector(float c){
        creditRate=c;
        credits=0;
        creditsKilled=0;
        livingCredits=0;
    }

    public void startTracking(MobEntity m, float cost){
        observer.addMob(m,cost);
        livingCredits+=cost;
    }
    public void checkMobs(){
        float thisKill=observer.checkMobs();
        creditsKilled+=thisKill;
        livingCredits-=thisKill;
    }
    public void updateRate(){

    }
}
