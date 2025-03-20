package bunguspacks.invasionslib.command;

import bunguspacks.invasionslib.InvasionsLib;
import bunguspacks.invasionslib.config.InvasionMobConfig;
import bunguspacks.invasionslib.config.InvasionProfileConfig;
import bunguspacks.invasionslib.util.InvasionDirectorBuilder;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;

public class SpawnCommand {
    //CLASS IS FOR TESTING, REMOVE LATER

    private static int run(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        InvasionsLib.invasionDirectorUpdater.addDirector(InvasionDirectorBuilder
                .create(context.getSource().getWorld(), context.getSource().getPlayer().getBlockPos())
                .withCreditTotal(1000)
                .withIntensityFraction(0.02f)
                .withMobData(InvasionMobConfig.invasionMobs.get("basicInvasion"))
                .withProfile(InvasionProfileConfig.profiles.get("classic"))
                .build());
        //MobSpawner.spawnMobGroup(MobGroupConfig.mobGroups.get("zombieGroup"), context.getSource().getPlayer().getServerWorld(), context.getSource().getPlayer().getBlockPos(), InvasionsLib.invasionDirectorUpdater.getDirectors().get(0));
        return 1;
    }

    public static void register(CommandDispatcher<ServerCommandSource> dispatcher, CommandRegistryAccess commandRegistryAccess, CommandManager.RegistrationEnvironment registrationEnvironment) {
        dispatcher.register(CommandManager.literal("invasion_spawn").executes(SpawnCommand::run));
    }
}
