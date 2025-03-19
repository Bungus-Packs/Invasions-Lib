package bunguspacks.invasionslib.util;

import bunguspacks.invasionslib.InvasionsLib;

import java.util.ArrayList;
import java.util.List;

public class InvasionDirectorUpdater {
    private List<InvasionDirector> directors=new ArrayList<>();
    public InvasionDirectorUpdater(){}
    public void updateDirectors(){
        for(InvasionDirector d:directors){
            d.checkMobs();
            d.updateRate();
        }
    }
    public void addDirector(InvasionDirector d){
        directors.add(d);
    }
    public List<InvasionDirector> getDirectors(){
        return directors;
    }
}
