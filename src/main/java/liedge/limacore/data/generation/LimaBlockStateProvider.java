package liedge.limacore.data.generation;

import com.google.common.base.Preconditions;
import liedge.limacore.block.LimaFluidType;
import liedge.limacore.lib.ModResources;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.data.PackOutput;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.LiquidBlock;
import net.neoforged.neoforge.client.model.generators.*;
import net.neoforged.neoforge.common.data.ExistingFileHelper;

import java.util.function.Supplier;

import static liedge.limacore.util.LimaRegistryUtil.getBlockName;
import static liedge.limacore.util.LimaRegistryUtil.getItemName;

public abstract class LimaBlockStateProvider extends BlockStateProvider implements ModelProviderExtensions
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

    public VariantBlockStateBuilder getVariantBuilder(Holder<Block> holder)
    {
        return getVariantBuilder(holder.value());
    }

    protected void simpleBlock(Holder<Block> holder, ModelFile model)
    {
        this.simpleBlock(holder.value(), model);
    }

    protected void simpleBlockItem(Holder<Block> holder, ModelFile model)
    {
        simpleBlockItem(holder.value(), model);
    }

    protected void simpleBlockWithItem(Holder<Block> holder, ModelFile model)
    {
        simpleBlockWithItem(holder.value(), model);
    }

    protected void cubeAll(Holder<Block> holder, String texture)
    {
        simpleBlockWithItem(holder, models().cubeAll(getBlockName(holder), blockFolderLocation(texture)));
    }

    protected void cubeAll(Holder<Block> holder)
    {
        cubeAll(holder, getBlockName(holder));
    }

    protected void liquidBlock(Supplier<? extends LiquidBlock> block, Supplier<? extends LimaFluidType> fluid)
    {
        String name = getBlockName(block.get());
        ModelFile model = models().getBuilder(name).texture("particle", fluid.get().getStillTexture()).renderType("translucent");
        simpleBlock(block.get(), model);
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

    protected ItemModelBuilder getItemBuilder(Holder<Block> holder)
    {
        return getItemBuilder(holder.value());
    }

    protected BlockModelBuilder getBlockBuilder(Block block)
    {
        return models().getBuilder(getBlockName(block));
    }

    protected BlockModelBuilder getBlockBuilder(Holder<Block> holder)
    {
        return models().getBuilder(getBlockName(holder));
    }
}