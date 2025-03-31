package bunguspacks.invasionslib.block.entity.renderer.layers;

import bunguspacks.invasionslib.InvasionsLib;
import bunguspacks.invasionslib.block.entity.InvasionBeaconBlockEntity;
import bunguspacks.invasionslib.block.entity.model.InvasionBeaconBlockEntityModel;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.renderer.GeoRenderer;
import software.bernie.geckolib.renderer.layer.GeoRenderLayer;

public class InvasionBeaconBlockEntityCrystalRenderLayer extends GeoRenderLayer<InvasionBeaconBlockEntity> {

    public InvasionBeaconBlockEntityCrystalRenderLayer(GeoRenderer<InvasionBeaconBlockEntity> renderer) {
        super(renderer);
    }
    public void render(MatrixStack poseStack, InvasionBeaconBlockEntity animatable, BakedGeoModel bakedModel, RenderLayer renderType, VertexConsumerProvider bufferSource, VertexConsumer buffer, float partialTick, int packedLight, int packedOverlay) {
        RenderLayer layer = RenderLayer.getEntityTranslucent(InvasionBeaconBlockEntityModel.INVASION_BEACON_TEX);
        bakedModel.getBone("rock").get().setHidden(false);
        bakedModel.getBone("binding").get().setHidden(true);
        bakedModel.getBone("beads").get().setHidden(true);
        bakedModel.getBone("base").get().setHidden(true);
        bakedModel.getBone("water").get().setHidden(true);
        getRenderer().reRender(bakedModel,poseStack,bufferSource,animatable,layer,bufferSource.getBuffer(layer),partialTick,packedLight,packedOverlay,1f,0.5f,0f,0.7f);
    }
}
