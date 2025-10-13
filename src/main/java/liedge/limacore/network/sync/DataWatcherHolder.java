package liedge.limacore.network.sync;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.unimi.dsi.fastutil.objects.ObjectList;
import it.unimi.dsi.fastutil.objects.ObjectLists;
import liedge.limacore.network.NetworkSerializer;
import liedge.limacore.util.LimaCollectionsUtil;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import org.jetbrains.annotations.ApiStatus;

import java.util.List;

public interface DataWatcherHolder
{
    List<LimaDataWatcher<?>> getDataWatchers();

    @ApiStatus.OverrideOnly
    void defineDataWatchers(DataWatcherCollector collector);

    @ApiStatus.OverrideOnly
    void sendDataWatcherPacket(List<DataEntry<?>> entries);

    @ApiStatus.Internal
    default void receiveDataWatcherPacket(List<DataEntry<?>> entries)
    {
        List<LimaDataWatcher<?>> dataWatchers = getDataWatchers();

        for (var entry : entries)
        {
            dataWatchers.get(entry.index()).readDataEntry(entry);
        }
    }

    default void forceSyncDataWatchers()
    {
        List<LimaDataWatcher<?>> dataWatchers = getDataWatchers();
        if (dataWatchers.isEmpty()) return;

        List<DataEntry<?>> entries = new ObjectArrayList<>();
        for (int index = 0; index < dataWatchers.size(); index++)
        {
            entries.add(dataWatchers.get(index).writeDataEntry(index));
        }
        sendDataWatcherPacket(entries);
    }

    default void tickDataWatchers()
    {
        List<LimaDataWatcher<?>> dataWatchers = getDataWatchers();
        List<DataEntry<?>> entries = null;

        for (int index = 0; index < dataWatchers.size(); index++)
        {
            // Tick watchers
            LimaDataWatcher<?> watcher = dataWatchers.get(index);

            if (watcher.tick())
            {
                if (entries == null) entries = new ObjectArrayList<>();

                entries.add(watcher.writeDataEntry(index));
            }
        }

        if (entries != null) sendDataWatcherPacket(entries);
    }

    default List<LimaDataWatcher<?>> createDataWatchers()
    {
        ObjectList<LimaDataWatcher<?>> list = new ObjectArrayList<>();
        defineDataWatchers(watcher -> LimaCollectionsUtil.addAndGetIndex(list, watcher));
        return ObjectLists.unmodifiable(list);
    }

    @FunctionalInterface
    interface DataWatcherCollector
    {
        int register(LimaDataWatcher<?> watcher);
    }

    record DataEntry<T>(int index, NetworkSerializer<T> serializer, T data)
    {
        private static final StreamCodec<RegistryFriendlyByteBuf, DataEntry<?>> ELEMENT_CODEC = StreamCodec.of((net, entry) -> entry.encode(net), DataEntry::decode);
        public static final StreamCodec<RegistryFriendlyByteBuf, List<DataEntry<?>>> STREAM_CODEC = ELEMENT_CODEC.apply(ByteBufCodecs.list());

        @SuppressWarnings("unchecked")
        private static <T> DataEntry<T> decode(RegistryFriendlyByteBuf net)
        {
            int index = net.readVarInt();
            NetworkSerializer<T> serializer = (NetworkSerializer<T>) NetworkSerializer.REGISTRY_STREAM_CODEC.decode(net);
            T data = serializer.decode(net);

            return new DataEntry<>(index, serializer, data);
        }

        private void encode(RegistryFriendlyByteBuf net)
        {
            net.writeVarInt(index);
            NetworkSerializer.REGISTRY_STREAM_CODEC.encode(net, serializer);
            serializer.encode(net, data);
        }
    }
}