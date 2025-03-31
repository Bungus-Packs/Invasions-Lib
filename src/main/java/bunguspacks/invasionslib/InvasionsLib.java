package bunguspacks.invasionslib;

import bunguspacks.invasionslib.block.ModBlocks;
import bunguspacks.invasionslib.block.entity.ModBlockEntities;
import bunguspacks.invasionslib.command.SpawnCommand;
import bunguspacks.invasionslib.config.InvasionMobConfig;
import bunguspacks.invasionslib.config.InvasionProfileConfig;
import bunguspacks.invasionslib.config.MobGroupConfig;
import bunguspacks.invasionslib.event.ModWorldTickEvents;
import bunguspacks.invasionslib.item.ModItemGroups;
import bunguspacks.invasionslib.item.ModItems;
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

        LOGGER.info("Loading configs for " + MOD_ID);
        MobGroupConfig.loadConfig();
        InvasionProfileConfig.loadConfig();
        InvasionMobConfig.loadConfig();

        LOGGER.info("Initializing events for " + MOD_ID);
        new ModWorldTickEvents(invasionDirectorUpdater);


        LOGGER.info("Registering items for " + MOD_ID);
        ModItems.registerModItems();

        LOGGER.info("Registering blocks for " + MOD_ID);
        ModBlocks.registerModBlocks();

        LOGGER.info("Registering item groups for " + MOD_ID);
        ModItemGroups.registerItemGroups();

        LOGGER.info("Registering block entities for " + MOD_ID);
        ModBlockEntities.registerBlockEntities();
    }
}
