package bunguspacks.invasionslib.block;

import bunguspacks.invasionslib.InvasionsLib;
import bunguspacks.invasionslib.block.entity.InvasionBeaconBlockEntity;
import bunguspacks.invasionslib.block.entity.ModBlockEntities;
import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.function.BooleanBiFunction;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class InvasionBeaconBlock extends BlockWithEntity implements BlockEntityProvider {
    private static final VoxelShape SHAPE = VoxelShapes.combineAndSimplify(
            Block.createCuboidShape(-12,0,-12,28,1,28),VoxelShapes.combineAndSimplify(
                    Block.createCuboidShape(-10,1,-10,26,2,26),VoxelShapes.combineAndSimplify(
                            Block.createCuboidShape(-6,2,-6,22,3,22),VoxelShapes.combineAndSimplify(
                                    Block.createCuboidShape(-3,3,-3,19,13,19),VoxelShapes.combineAndSimplify(
                                            Block.createCuboidShape(-1,31,-1,17,37,17),
                                            Block.createCuboidShape(2,20,2,14,48,14),BooleanBiFunction.OR
                                    ),BooleanBiFunction.OR
                            ),BooleanBiFunction.OR
                    ),BooleanBiFunction.OR
            ),BooleanBiFunction.OR);


    protected InvasionBeaconBlock(Settings settings) {
        super(settings);
    }



    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return SHAPE;
    }


    @Override
    public BlockRenderType getRenderType(BlockState state) {
        return BlockRenderType.ENTITYBLOCK_ANIMATED;
    }

    @Nullable
    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new InvasionBeaconBlockEntity(pos, state);
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(World world, BlockState state, BlockEntityType<T> type) {
        return checkType(type, ModBlockEntities.INVASION_BEACON_BLOCK_ENTITY,
                (world1, pos, state1, blockEntity) -> blockEntity.tick(world1, pos, state1));
    }

    @Override
    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit){
        if (!world.isClient) {
            ((InvasionBeaconBlockEntity)world.getBlockEntity(pos)).toggleState();
        }
        return super.onUse(state, world, pos, player, hand, hit);
    }
}
