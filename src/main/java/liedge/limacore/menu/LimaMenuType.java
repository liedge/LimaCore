package liedge.limacore.menu;

import liedge.limacore.lib.ModResources;
import liedge.limacore.lib.Translatable;
import liedge.limacore.util.LimaCoreUtil;
import liedge.limacore.util.LimaRegistryUtil;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.inventory.MenuType;
import org.jetbrains.annotations.Nullable;

public abstract class LimaMenuType<CTX, M extends LimaMenu<CTX>> extends MenuType<M>
{
    public static Translatable defaultMenuTitle(ResourceLocation id)
    {
        return Translatable.standalone(ModResources.prefixedIdLangKey("container", id));
    }

    private final Class<CTX> contextClass;
    private final MenuFactory<CTX, M> factory;
    private final @Nullable Translatable defaultTitle;

    protected LimaMenuType(Class<CTX> contextClass, MenuFactory<CTX, M> factory, @Nullable Translatable defaultTitle)
    {
        super((containerId, inv) -> {
            throw new UnsupportedOperationException("Parameterless menu creation not supported. Use createMenu or tryCreateMenu");
        }, FeatureFlags.DEFAULT_FLAGS);

        this.contextClass = contextClass;
        this.factory = factory;
        this.defaultTitle = defaultTitle;
    }

    public Class<CTX> getContextClass()
    {
        return contextClass;
    }

    public @Nullable Translatable getDefaultTitle()
    {
        return defaultTitle;
    }

    public void tryEncodeContext(Object uncheckedContext, RegistryFriendlyByteBuf net)
    {
        CTX menuContext = checkContext(uncheckedContext);
        encodeContext(menuContext, net);
    }

    public abstract void encodeContext(CTX menuContext, RegistryFriendlyByteBuf net);

    protected abstract CTX decodeContext(RegistryFriendlyByteBuf net, Inventory inventory);

    public abstract boolean canPlayerKeepUsing(CTX menuContext, Player player);

    public M createMenu(int containerId, Inventory inventory, CTX menuContext)
    {
        return factory.createMenu(this, containerId, inventory, menuContext);
    }

    public M tryCreateMenu(int containerId, Inventory inventory, Object uncheckedContext)
    {
        return createMenu(containerId, inventory, checkContext(uncheckedContext));
    }

    protected CTX checkContext(Object uncheckedContext)
    {
        return LimaCoreUtil.castOrThrow(contextClass, uncheckedContext, () -> new IllegalArgumentException(String.format("Invalid context type '%s' for menu type '%s'", uncheckedContext.getClass().getSimpleName(), LimaRegistryUtil.getNonNullRegistryId(this, BuiltInRegistries.MENU))));
    }

    @Override
    public final M create(int containerId, Inventory inventory)
    {
        throw new UnsupportedOperationException("Parameterless menu creation not supported. Use createMenu or tryCreateMenu");
    }

    @Override
    public final M create(int containerId, Inventory inventory, RegistryFriendlyByteBuf net)
    {
        CTX menuContext = decodeContext(net, inventory);
        return factory.createMenu(this, containerId, inventory, menuContext);
    }

    @FunctionalInterface
    public interface MenuFactory<CTX, M extends LimaMenu<CTX>>
    {
        M createMenu(LimaMenuType<CTX, ?> type, int containerId, Inventory inventory, CTX menuContext);
    }
}