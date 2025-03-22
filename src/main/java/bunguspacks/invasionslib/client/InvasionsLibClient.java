package bunguspacks.invasionslib.client;

import bunguspacks.invasionslib.block.entity.ModBlockEntities;
import bunguspacks.invasionslib.block.entity.renderer.InvasionBeaconBlockEntityRenderer;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.rendering.v1.BlockEntityRendererRegistry;
import net.fabricmc.fabric.impl.client.rendering.BlockEntityRendererRegistryImpl;

public class InvasionsLibClient implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        BlockEntityRendererRegistry.register(ModBlockEntities.INVASION_BEACON_BLOCK_ENTITY, InvasionBeaconBlockEntityRenderer::new);
    }
}
