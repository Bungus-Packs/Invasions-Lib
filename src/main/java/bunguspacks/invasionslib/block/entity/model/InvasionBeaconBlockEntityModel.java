package bunguspacks.invasionslib.block.entity.model;

import bunguspacks.invasionslib.InvasionsLib;
import bunguspacks.invasionslib.block.entity.InvasionBeaconBlockEntity;
import net.minecraft.util.Identifier;
import software.bernie.geckolib.model.GeoModel;

public class InvasionBeaconBlockEntityModel extends GeoModel<InvasionBeaconBlockEntity> {

    public static final Identifier INVASION_BEACON_TEX=new Identifier(InvasionsLib.MOD_ID,"textures/block/invasion_beacon.png");
    @Override
    public Identifier getModelResource(InvasionBeaconBlockEntity invasionBeaconBlockEntity) {
        return new Identifier(InvasionsLib.MOD_ID,"geo/block/invasion_beacon.geo.json");
    }

    @Override
    public Identifier getTextureResource(InvasionBeaconBlockEntity invasionBeaconBlockEntity) {
        return INVASION_BEACON_TEX;
    }

    @Override
    public Identifier getAnimationResource(InvasionBeaconBlockEntity invasionBeaconBlockEntity) {
        return new Identifier(InvasionsLib.MOD_ID,"animations/block/invasion_beacon.animation.json");
    }


}
