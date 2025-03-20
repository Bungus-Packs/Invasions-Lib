package bunguspacks.invasionslib.event;


import bunguspacks.invasionslib.util.InvasionDirector;
import bunguspacks.invasionslib.util.InvasionDirectorUpdater;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;

import java.util.ArrayList;
import java.util.List;

public class ModWorldTickEvents {
    public ModWorldTickEvents(InvasionDirectorUpdater d) {
        //register world tick event for director updater
        ServerTickEvents.END_WORLD_TICK.register((world) -> d.updateDirectors());
    }

}
