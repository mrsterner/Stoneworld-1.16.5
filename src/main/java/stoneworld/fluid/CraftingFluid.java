package stoneworld.fluid;


import net.minecraft.block.BlockState;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.FluidState;
import net.minecraft.item.BucketItem;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.WorldView;
import stoneworld.Stoneworld;

/**
 * A fluid that can only be used for crafting, i.e. not be placed in the world.
 */
public class CraftingFluid extends Fluid {
    public final Item bucketItem;
    public final String name;
    public final int color;
    private final CraftingFluidBlock block;

    public CraftingFluid(String name, int color) {
        bucketItem = new BucketItem(this, new Item.Settings().recipeRemainder(Items.BUCKET).maxCount(1).group(Stoneworld.STONEWORLD_ITEM_GROUP));
        this.name = name;
        this.block = new CraftingFluidBlock(name, color);
        this.color = color;
    }

    @Override
    public Item getBucketItem() {
        return bucketItem;
    }

    @Override
    protected boolean canBeReplacedWith(FluidState state, BlockView world, BlockPos pos, Fluid fluid, Direction direction) {
        return true;
    }

    @Override
    protected Vec3d getVelocity(BlockView world, BlockPos pos, FluidState state) {
        return Vec3d.ZERO;
    }

    @Override
    public int getTickRate(WorldView world) {
        return 0;
    }

    @Override
    protected float getBlastResistance() {
        return 0;
    }

    @Override
    public float getHeight(FluidState state, BlockView world, BlockPos pos) {
        return 0;
    }

    @Override
    public float getHeight(FluidState state) {
        return 0;
    }

    @Override
    protected BlockState toBlockState(FluidState state) {
        return block.getDefaultState();
    }

    @Override
    public boolean isStill(FluidState state) {
        return true;
    }

    @Override
    public int getLevel(FluidState state) {
        return 0;
    }

    @Override
    public VoxelShape getShape(FluidState state, BlockView world, BlockPos pos) {
        return null;
    }
}