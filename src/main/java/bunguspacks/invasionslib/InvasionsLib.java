package bunguspacks.invasionslib;

import bunguspacks.invasionslib.command.SpawnCommand;
import bunguspacks.invasionslib.config.InvasionDirectorConfig;
import bunguspacks.invasionslib.config.InvasionMobConfig;
import bunguspacks.invasionslib.config.MobGroupConfig;
import bunguspacks.invasionslib.event.ModWorldTickEvents;
import bunguspacks.invasionslib.util.InvasionDirectorUpdater;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class InvasionsLib implements ModInitializer {
    public static final String MOD_ID = "invasionslib";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);
    public static final InvasionDirectorUpdater invasionDirectorUpdater = new InvasionDirectorUpdater();

    @Override
    public void onInitialize() {
        LOGGER.info("Initializing " + MOD_ID);

        LOGGER.info("Initializing commands for " + MOD_ID);
        CommandRegistrationCallback.EVENT.register(SpawnCommand::register);

        LOGGER.info("Initializing mod configs for " + MOD_ID);
        MobGroupConfig.loadConfig();
        InvasionDirectorConfig.loadConfig();
        InvasionMobConfig.loadConfig();

        LOGGER.info("Initializing mod events for " + MOD_ID);
        new ModWorldTickEvents(invasionDirectorUpdater);


    }
}
