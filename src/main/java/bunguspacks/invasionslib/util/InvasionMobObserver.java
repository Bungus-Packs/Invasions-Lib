package bunguspacks.invasionslib.util;

import bunguspacks.invasionslib.InvasionsLib;
import net.fabricmc.loader.impl.lib.tinyremapper.extension.mixin.common.data.Pair;
import net.minecraft.entity.mob.MobEntity;

import java.util.ArrayList;
import java.util.List;

public class InvasionMobObserver {
    private List<Pair<MobEntity,Float>> activeMobs=new ArrayList<>();

    public InvasionMobObserver(){

    }
    public void addMob(MobEntity m, float cost){
        activeMobs.add(Pair.of(m,cost));
    }

    public float checkMobs(){

        float out=0;
        for(int i=0;i<activeMobs.size();i++){
            MobEntity m=activeMobs.get(i).first();

            if(m==null||!m.isAlive()){
                out+=activeMobs.get(i).second();
                activeMobs.remove(i);
                i--;
            }
        }return out;
    }


}
