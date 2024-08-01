package liedge.limacore.data.generation;

import com.google.common.base.Preconditions;
import liedge.limacore.block.LimaFluidType;
import liedge.limacore.lib.ModResources;
import net.minecraft.core.Direction;
import net.minecraft.data.PackOutput;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.client.model.generators.*;
import net.neoforged.neoforge.common.data.ExistingFileHelper;

import java.util.function.Predicate;
import java.util.function.Supplier;

import static liedge.limacore.util.LimaRegistryUtil.getBlockName;
import static liedge.limacore.util.LimaRegistryUtil.getItemName;
import static net.minecraft.world.level.block.state.properties.BlockStateProperties.HORIZONTAL_FACING;

public abstract class LimaBlockStateProvider extends BlockStateProvider implements ModelProviderHelper
{
    private final ModResources resources;
    private final ExistingFileHelper helper;

    // Vanilla models
    protected final ModelFile mcBlockBlock = new ModelFile.UncheckedModelFile("block/block");
    protected final ModelFile mcBlockCube = new ModelFile.UncheckedModelFile("block/cube");
    protected final ModelFile mcBlockCubeAll = new ModelFile.UncheckedModelFile("block/cube_all");

    public LimaBlockStateProvider(PackOutput output, ModResources resources, ExistingFileHelper helper)
    {
        super(output, resources.modid(), helper);
        this.resources = resources;
        this.helper = helper;
    }

    @Override
    public ModResources modResources()
    {
        return resources;
    }

    @Override
    public ExistingFileHelper fileHelper()
    {
        return helper;
    }

    public VariantBlockStateBuilder getVariantBuilder(Supplier<? extends Block> supplier)
    {
        return getVariantBuilder(supplier.get());
    }

    protected void simpleBlock(Supplier<? extends Block> supplier, ModelFile model)
    {
        this.simpleBlock(supplier.get(), model);
    }

    protected void simpleBlockItem(Supplier<? extends Block> supplier, ModelFile model)
    {
        simpleBlockItem(supplier.get(), model);
    }

    protected void simpleBlockWithItem(Supplier<? extends Block> supplier, ModelFile model)
    {
        simpleBlockWithItem(supplier.get(), model);
    }

    protected void cubeAll(Supplier<? extends Block> supplier, String texture)
    {
        simpleBlockWithItem(supplier, models().cubeAll(getBlockName(supplier.get()), blockFolderLocation(texture)));
    }

    protected void cubeAll(Supplier<? extends Block> supplier)
    {
        cubeAll(supplier, getBlockName(supplier.get()));
    }

    protected void liquidBlock(Supplier<? extends LiquidBlock> block, Supplier<? extends LimaFluidType> fluid)
    {
        String name = getBlockName(block.get());
        ModelFile model = models().getBuilder(name).texture("particle", fluid.get().getStillTexture()).renderType("translucent");
        simpleBlock(block.get(), model);
    }

    protected void conditionalHorizontalState(Supplier<? extends Block> supplier, int angleOffset, ModelFile staticModel, ModelFile rotatedModel, Predicate<BlockState> statePredicate)
    {
        getVariantBuilder(supplier.get()).forAllStates(state ->
        {
            if (statePredicate.test(state))
            {
                return ConfiguredModel.builder().modelFile(rotatedModel).rotationY(getRotationY(state.getValue(HORIZONTAL_FACING), angleOffset)).build();
            }
            else
            {
                return ConfiguredModel.builder().modelFile(staticModel).build();
            }
        });
    }

    protected int getRotationX(Direction side)
    {
        Preconditions.checkArgument(side.getAxis().isVertical(), "Direction must be vertical for x rotation calculation");
        return (side == Direction.DOWN) ? 90 : 270;
    }

    protected int getRotationY(Direction side, int angleOffset)
    {
        Preconditions.checkArgument(side.getAxis().isHorizontal(), "Direction must be horizontal for y rotation calculation");
        return ((int)side.toYRot() + angleOffset) % 360;
    }

    protected ItemModelBuilder getItemBuilder(Block block)
    {
        return itemModels().getBuilder(getItemName(block.asItem()));
    }

    protected ItemModelBuilder getItemBuilder(Supplier<? extends Block> supplier)
    {
        return getItemBuilder(supplier.get());
    }

    protected BlockModelBuilder getBlockBuilder(Block block)
    {
        return models().getBuilder(getBlockName(block));
    }

    protected BlockModelBuilder getBlockBuilder(Supplier<? extends Block> supplier)
    {
        return getBlockBuilder(supplier.get());
    }
}