package liedge.limacore.lib;

import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;

import java.util.Objects;

public interface Translatable
{
    static Translatable standalone(String descriptionId)
    {
        return new TranslationHolder(descriptionId);
    }

    String descriptionId();

    default MutableComponent translate()
    {
        return Component.translatable(descriptionId());
    }

    default MutableComponent translateArgs(Object... args)
    {
        return Component.translatable(descriptionId(), args);
    }

    record TranslationHolder(String descriptionId) implements Translatable
    {
        @Override
        public String toString()
        {
            return descriptionId;
        }

        @Override
        public boolean equals(Object obj)
        {
            if (obj == this)
            {
                return true;
            }
            else if (obj instanceof Translatable translatable)
            {
                return Objects.equals(this.descriptionId, translatable.descriptionId());
            }
            else
            {
                return false;
            }
        }

        @Override
        public int hashCode()
        {
            return descriptionId.hashCode();
        }
    }
}