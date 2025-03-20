package bunguspacks.invasionslib.block.entity;

import bunguspacks.invasionslib.InvasionsLib;
import bunguspacks.invasionslib.block.ModBlocks;
import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public class ModBlockEntities {
    public static final BlockEntityType<InvasionBeaconBlockEntity> INVASION_BEACON_BLOCK_ENTITY=
            Registry.register(Registries.BLOCK_ENTITY_TYPE,new Identifier(InvasionsLib.MOD_ID,"invasion_beacon"),
                    FabricBlockEntityTypeBuilder.create(InvasionBeaconBlockEntity::new,
                            ModBlocks.INVASION_BEACON).build());

    public static void registerBlockEntities(){

    }
}
