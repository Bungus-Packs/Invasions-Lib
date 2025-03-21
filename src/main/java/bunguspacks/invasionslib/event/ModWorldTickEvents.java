package bunguspacks.invasionslib.event;


import bunguspacks.invasionslib.util.InvasionDirectorUpdater;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;

public class ModWorldTickEvents {
    public ModWorldTickEvents(InvasionDirectorUpdater d) {
        //register world tick event for director updater
        ServerTickEvents.END_WORLD_TICK.register((world) -> d.updateDirectors(world));
    }

}
