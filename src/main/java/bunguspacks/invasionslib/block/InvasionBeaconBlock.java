package bunguspacks.invasionslib.block;

import bunguspacks.invasionslib.block.entity.InvasionBeaconBlockEntity;
import bunguspacks.invasionslib.block.entity.ModBlockEntities;
import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.util.function.BooleanBiFunction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class InvasionBeaconBlock extends BlockWithEntity implements BlockEntityProvider {
    private static final VoxelShape SHAPE = VoxelShapes.combineAndSimplify(Block.createCuboidShape(0, 0, 0, 16, 2, 16),
            VoxelShapes.combineAndSimplify(Block.createCuboidShape(1, 2, 1, 15, 22, 15),
                    VoxelShapes.combineAndSimplify(Block.createCuboidShape(2, 22, 2, 14, 24, 14),
                            Block.createCuboidShape(6, 24, 6, 10, 32, 10), BooleanBiFunction.OR), BooleanBiFunction.OR), BooleanBiFunction.OR);


    protected InvasionBeaconBlock(Settings settings) {
        super(settings);
    }

    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return SHAPE;
    }

    @Override
    public BlockRenderType getRenderType(BlockState state) {
        return BlockRenderType.MODEL;
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
}
