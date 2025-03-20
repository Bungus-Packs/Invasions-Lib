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
        this.dir = dir;
        StateSaverAndLoader.getServerState(world.getServer()).loadFromDirector(dir);
    }

    public void updateDirectors(ServerWorld world) {
        this.world = world;
        if (StateSaverAndLoader.getServerState(world.getServer()).active) {
            dir = StateSaverAndLoader.getServerState(world.getServer()).generateDirector();
        }
        //have directors check their mobs every tick
        if (dir != null) {
            dir.checkMobs();
            dir.trySpawn();
            dir.updateCredits();
            InvasionsLib.LOGGER.info(dir.getCreditRate() + "    " + dir.getLivingCredits());
            StateSaverAndLoader.getServerState(world.getServer()).loadFromDirector(dir);
        }
    }

    public void removeDirector() {
        StateSaverAndLoader.getServerState(world.getServer()).active = false;
        StateSaverAndLoader.getServerState(world.getServer()).markDirty();
        dir = null;
    }
}
