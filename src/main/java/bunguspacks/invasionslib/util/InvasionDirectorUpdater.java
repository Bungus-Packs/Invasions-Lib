package bunguspacks.invasionslib.util;

import bunguspacks.invasionslib.InvasionsLib;
import bunguspacks.invasionslib.StateSaverAndLoader;
import net.minecraft.entity.boss.BossBar;
import net.minecraft.entity.boss.ServerBossBar;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;

public class InvasionDirectorUpdater {
    private InvasionDirector dir;
    private ServerWorld world;
    private ServerBossBar progressBar = null;
    private final int PROGRESS_BAR_VICTORY_DISPLAY_TICKS = 1000;
    private int progressBarDisplayTime;


    public InvasionDirectorUpdater() {
    }

    public void addDirector(InvasionDirector dir) {
        //create a new director and save it to persistent data
        InvasionsLib.LOGGER.info("Invasion started with profile \"" + dir.getProfile().name() + "\" and mob data \"" + dir.getMobData().name() + "\".");
        this.dir = dir;
        if (progressBar != null) {
            progressBar.clearPlayers();
        }
        progressBar = new ServerBossBar(Text.translatable("progressbar.invasion"), BossBar.Color.BLUE, BossBar.Style.NOTCHED_20);
        StateSaverAndLoader.getServerState(world.getServer()).loadFromDirector(dir);
    }

    public void updateDirectors(ServerWorld world) {
        //if the progress bar victory time is playing, finish it then clear the bar
        if (dir == null && progressBar != null) {
            progressBarDisplayTime--;
            if (progressBarDisplayTime <= 0) {
                progressBar.clearPlayers();
                progressBar = null;
            }
        }
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
                updateProgressBar();
                StateSaverAndLoader.getServerState(world.getServer()).loadFromDirector(dir);
            }
        }
    }

    //if a progress bar exists, broadcast it to all players and set the content
    public void updateProgressBar() {
        if(progressBar==null) {
            progressBar = new ServerBossBar(Text.translatable("progressbar.invasion"), BossBar.Color.BLUE, BossBar.Style.NOTCHED_20);
        }else{
            for (ServerPlayerEntity player : world.getPlayers()) {
                if (!progressBar.getPlayers().contains(player)) {
                    progressBar.addPlayer(player);
                }
            }
            progressBar.setPercent(dir.getCreditsKilled() / (dir.getPassiveCredits() + dir.getWaveCredits()));
        }
    }

    public void playProgressBarCompletion() {
        progressBar.setPercent(1.0f);
        progressBar.setName(Text.translatable("progressbar.victory"));
        progressBarDisplayTime = PROGRESS_BAR_VICTORY_DISPLAY_TICKS;
    }

    //mark persistent director as inactive and clear director
    public void removeDirector() {
        StateSaverAndLoader.getServerState(world.getServer()).active = false;
        StateSaverAndLoader.getServerState(world.getServer()).markDirty();
        dir = null;
        playProgressBarCompletion();
    }
}
