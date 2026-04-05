package liedge.limacore.client.model;

import com.google.gson.JsonObject;
import net.neoforged.neoforge.client.model.generators.template.CustomLoaderBuilder;

public class EmissiveModelBuilder extends CustomLoaderBuilder
{
    private int emissionTarget = 0;

    public EmissiveModelBuilder()
    {
        super(EmissiveItemCuboidModel.LOADER_ID, true);
    }

    public EmissiveModelBuilder emissionTarget(int emissionTarget)
    {
        this.emissionTarget = emissionTarget;
        return this;
    }

    public EmissiveModelBuilder maxEmission()
    {
        return emissionTarget(15);
    }

    @Override
    protected CustomLoaderBuilder copyInternal()
    {
        EmissiveModelBuilder builder = new EmissiveModelBuilder();
        builder.emissionTarget = this.emissionTarget;
        return builder;
    }

    @Override
    public JsonObject toJson(JsonObject json)
    {
        json = super.toJson(json);

        if (emissionTarget > 0) json.addProperty("emission_target", emissionTarget);

        return json;
    }
}