package liedge.limacore.inventory.menu;

import liedge.limacore.lib.ModResources;
import liedge.limacore.lib.Translatable;
import liedge.limacore.util.LimaCoreUtil;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.inventory.MenuType;

public abstract class LimaMenuType<CTX, M extends LimaMenu<CTX>> extends MenuType<M> implements Translatable
{
    protected final ResourceLocation registryId;
    private final Class<CTX> contextClass;
    private final MenuFactory<CTX, M> factory;
    private final String descriptionId;

    public LimaMenuType(ResourceLocation registryId, Class<CTX> contextClass, MenuFactory<CTX, M> factory)
    {
        super((containerId, inv) -> {
            throw new UnsupportedOperationException("Parameterless menu creation not supported. Use createMenu or tryCreateMenu");
        }, FeatureFlags.DEFAULT_FLAGS);

        this.registryId = registryId;
        this.contextClass = contextClass;
        this.factory = factory;
        this.descriptionId = ModResources.prefixedIdLangKey("container", registryId);
    }

    public Class<CTX> getContextClass()
    {
        return contextClass;
    }

    public void tryEncodeContext(Object uncheckedContext, RegistryFriendlyByteBuf net)
    {
        CTX menuContext = checkContext(uncheckedContext);
        encodeContext(menuContext, net);
    }

    public abstract void encodeContext(CTX menuContext, RegistryFriendlyByteBuf net);

    protected abstract CTX decodeContext(RegistryFriendlyByteBuf net, Inventory inventory);

    public abstract boolean canPlayerKeepUsing(CTX menuContext, Player player);

    public MutableComponent getMenuTitle(Object uncheckedContext)
    {
        return translate();
    }

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
        return LimaCoreUtil.castOrThrow(contextClass, uncheckedContext, () -> new IllegalArgumentException("Invalid context class type '" + uncheckedContext.getClass().getSimpleName() + "' for menu type " + registryId.toString()));
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

    @Override
    public String descriptionId()
    {
        return descriptionId;
    }

    @FunctionalInterface
    public interface MenuFactory<CTX, M extends LimaMenu<CTX>>
    {
        M createMenu(LimaMenuType<CTX, ?> type, int containerId, Inventory inventory, CTX menuContext);
    }
}