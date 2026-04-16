package liedge.limacore.client.model;

import com.google.gson.JsonObject;
import net.neoforged.neoforge.client.model.generators.template.CustomLoaderBuilder;

public class ExtendedCuboidBuilder extends CustomLoaderBuilder
{
    private boolean forceEmissive = false;
    private ItemModelPipeline modelPipeline = ItemModelPipeline.ITEM_PIPELINE;

    public ExtendedCuboidBuilder()
    {
        super(ExtendedCuboidModel.LOADER_ID, true);
    }

    public ExtendedCuboidBuilder forceEmissiveQuads()
    {
        this.forceEmissive = true;
        return this;
    }

    public ExtendedCuboidBuilder setModelPipeline(ItemModelPipeline modelPipeline)
    {
        this.modelPipeline = modelPipeline;
        return this;
    }

    @Override
    protected CustomLoaderBuilder copyInternal()
    {
        ExtendedCuboidBuilder builder = new ExtendedCuboidBuilder();
        builder.forceEmissive = this.forceEmissive;
        builder.modelPipeline = this.modelPipeline;
        return builder;
    }

    @Override
    public JsonObject toJson(JsonObject json)
    {
        json = super.toJson(json);

        if (forceEmissive)
        {
            json.addProperty(ExtendedCuboidModel.KEY_FORCE_EMISSIVE, true);
        }

        if (modelPipeline != ItemModelPipeline.ITEM_PIPELINE)
        {
            json.addProperty(ExtendedCuboidModel.KEY_PIPELINE, modelPipeline.getSerializedName());
        }

        return json;
    }
}