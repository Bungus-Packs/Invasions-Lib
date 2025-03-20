package bunguspacks.invasionslib.util;

import bunguspacks.invasionslib.InvasionsLib;

import java.util.ArrayList;
import java.util.List;

public class InvasionDirectorUpdater {
    private List<InvasionDirector> directors = new ArrayList<>();

    public InvasionDirectorUpdater() {
    }

    public void updateDirectors() {
        //have directors check their mobs every tick
        for (InvasionDirector d : directors) {
            d.checkMobs();
            d.trySpawn();
            d.updateCredits();
        }
    }

    public void addDirector(InvasionDirector d) {
        directors.add(d);
    }

    public List<InvasionDirector> getDirectors() {
        return directors;
    }
}
