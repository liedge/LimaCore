package liedge.limacore.client.model.geometry;

import com.google.common.base.Preconditions;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;

import java.util.List;

@ApiStatus.Internal
public record GeometryLayerDefinition(@Nullable String name, List<String> groups, int emissivity, @Nullable
                                      ResourceLocation renderTypeName)
{
    public GeometryLayerDefinition
    {
        Preconditions.checkArgument(!groups.isEmpty(), "Layer groups cannot be empty.");
        Preconditions.checkArgument(emissivity >= 0 && emissivity < 16, "Layer emissivity must be between 0 and 15.");
    }

    public boolean hasEmissivity()
    {
        return emissivity > 0;
    }
}