package bunguspacks.invasionslib.block.entity.renderer;

import bunguspacks.invasionslib.block.entity.InvasionBeaconBlockEntity;
import bunguspacks.invasionslib.block.entity.model.InvasionBeaconBlockEntityModel;
import bunguspacks.invasionslib.block.entity.renderer.layers.InvasionBeaconBlockEntityBaseRenderLayer;
import bunguspacks.invasionslib.block.entity.renderer.layers.InvasionBeaconBlockEntityCrystalRenderLayer;
import bunguspacks.invasionslib.block.entity.renderer.layers.InvasionBeaconBlockEntityWaterRenderLayer;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.renderer.GeoBlockRenderer;
import software.bernie.geckolib.renderer.layer.AutoGlowingGeoLayer;

public class InvasionBeaconBlockEntityRenderer extends GeoBlockRenderer<InvasionBeaconBlockEntity> {
    public InvasionBeaconBlockEntityRenderer(BlockEntityRendererFactory.Context context) {
        super(new InvasionBeaconBlockEntityModel());

        addRenderLayer(new AutoGlowingGeoLayer<>(this));
        addRenderLayer(new InvasionBeaconBlockEntityWaterRenderLayer(this));
        addRenderLayer(new InvasionBeaconBlockEntityCrystalRenderLayer(this));
        addRenderLayer(new InvasionBeaconBlockEntityBaseRenderLayer(this));





    }


    @Override
    public RenderLayer getRenderType(InvasionBeaconBlockEntity animatable, Identifier texture, VertexConsumerProvider bufferSource, float partialTick) {
        return super.getRenderType(animatable, texture, bufferSource, partialTick);

    }
}
