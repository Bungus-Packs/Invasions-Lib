package bunguspacks.invasionslib.util;

import bunguspacks.invasionslib.InvasionsLib;
import bunguspacks.invasionslib.StateSaverAndLoader;
import net.minecraft.server.world.ServerWorld;

public class InvasionDirectorUpdater {
    private InvasionDirector dir;
    private ServerWorld world;

    public InvasionDirectorUpdater() {
    }

    public void addDirector(InvasionDirector dir) {
        //create a new director and save it to persistent data
        InvasionsLib.LOGGER.info("Invasion started with profile \"" + dir.getProfile().name() + "\" and mob data \"" + dir.getMobData().name() + "\".");
        this.dir = dir;
        StateSaverAndLoader.getServerState(world.getServer()).loadFromDirector(dir);
    }

    public void updateDirectors(ServerWorld world) {
        this.world = world;
        //if the saved director is marked active, load it
        if (StateSaverAndLoader.getServerState(world.getServer()).active) {
            dir = StateSaverAndLoader.getServerState(world.getServer()).generateDirector();
        }
        //have directors check their mobs every tick
        if (dir != null) {
            dir.checkMobs();
            //the second null check is needed because the director can yeet itself after checking mobs if it determines itself to be finished
            if (dir != null) {
                dir.trySpawn();
                dir.updateCredits();
                StateSaverAndLoader.getServerState(world.getServer()).loadFromDirector(dir);
            }
        }
    }

    //mark persistent director as inactive and clear director
    public void removeDirector() {
        StateSaverAndLoader.getServerState(world.getServer()).active = false;
        StateSaverAndLoader.getServerState(world.getServer()).markDirty();
        dir = null;
    }
}
