package liedge.limacore.data.generation;

import com.google.common.base.Preconditions;
import liedge.limacore.lib.ModResources;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.client.model.generators.*;
import net.neoforged.neoforge.common.data.ExistingFileHelper;

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

    protected void simpleBlockWithItem(Holder<Block> holder)
    {
        simpleBlockWithItem(holder, existingModel(blockFolderLocation(holder)));
    }

    public BlockModelBuilder cubeAll(Block block, ResourceLocation texture)
    {
        BlockModelBuilder builder = models().cubeAll(getBlockName(block), texture);
        simpleBlockWithItem(block, builder);
        return builder;
    }

    public BlockModelBuilder cubeAll(Holder<Block> holder, ResourceLocation texture)
    {
        return cubeAll(holder.value(), texture);
    }

    public BlockModelBuilder cubeAll(Holder<Block> holder, String texture)
    {
        return cubeAll(holder, blockFolderLocation(texture));
    }

    @Override
    public BlockModelBuilder cubeAll(Block block)
    {
        return cubeAll(block, blockFolderLocation(block));
    }

    public BlockModelBuilder cubeAll(Holder<Block> holder)
    {
        return cubeAll(holder.value());
    }

    protected void liquidBlock(Holder<Block> holder)
    {
        String name = getBlockName(holder);
        ModelFile model = getBlockBuilder(holder).texture("particle", blockFolderLocation(name + "_still")).renderType("translucent");
        simpleBlock(holder, model);
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