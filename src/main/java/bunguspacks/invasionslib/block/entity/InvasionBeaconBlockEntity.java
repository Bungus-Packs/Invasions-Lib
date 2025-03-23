package bunguspacks.invasionslib.block.entity;

import bunguspacks.invasionslib.InvasionsLib;
import bunguspacks.invasionslib.network.TestNetworking;
import net.fabricmc.fabric.api.networking.v1.PlayerLookup;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.Entity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;
import net.minecraft.screen.PropertyDelegate;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.animatable.GeoBlockEntity;
import software.bernie.geckolib.constant.DefaultAnimations;
import software.bernie.geckolib.core.animatable.GeoAnimatable;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.core.animation.AnimationController;
import software.bernie.geckolib.core.animation.AnimationState;
import software.bernie.geckolib.core.animation.RawAnimation;
import software.bernie.geckolib.core.object.PlayState;
import software.bernie.geckolib.util.GeckoLibUtil;

public class InvasionBeaconBlockEntity extends BlockEntity implements GeoBlockEntity {
    private final AnimatableInstanceCache cache= GeckoLibUtil.createInstanceCache(this);
    protected static final RawAnimation FLOAT_ANIM= RawAnimation.begin().thenLoop("crystalfloat");
    protected static final RawAnimation FILL_ANIM=RawAnimation.begin().thenPlay("crystalfill");
    protected static final RawAnimation EMPTY_ANIM=RawAnimation.begin().thenPlay("crystalempty");
    protected static final RawAnimation STILL_ANIM=RawAnimation.begin().thenLoop("crystalstill");

    public boolean active;


    public InvasionBeaconBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.INVASION_BEACON_BLOCK_ENTITY, pos, state);
    }

    public void tick(World world, BlockPos pos, BlockState state) {
        if (world.isClient()) {
            return;
        }
    }



    @Override
    protected void writeNbt(NbtCompound nbt) {
        nbt.putBoolean("active",active);
        super.writeNbt(nbt);
    }

    @Override
    public void readNbt(NbtCompound nbt) {
        super.readNbt(nbt);
        active=nbt.getBoolean("active");
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllerRegistrar) {
        controllerRegistrar.add(
                new AnimationController<>(this,"invasionBeaconAnimController", 10,
                        state->state.setAndContinue(active?FLOAT_ANIM:STILL_ANIM))
                        .triggerableAnim("crystalfill",FILL_ANIM)
                        .triggerableAnim("crystalempty",EMPTY_ANIM));

    }


    public void toggleState(){
        if(active){
            triggerAnim("invasionBeaconAnimController","crystalempty");
        }else{
            triggerAnim("invasionBeaconAnimController","crystalfill");
        }
        active=!active;
        markDirty();
    }

    @Nullable
    @Override
    public Packet<ClientPlayPacketListener> toUpdatePacket() {
        return BlockEntityUpdateS2CPacket.create(this);
    }

    @Override
    public NbtCompound toInitialChunkDataNbt() {
        return createNbt();
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return cache;
    }
}
