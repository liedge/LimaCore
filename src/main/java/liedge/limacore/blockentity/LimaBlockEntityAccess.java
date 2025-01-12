package liedge.limacore.blockentity;

/**
 * To be used by other interfaces and classes that are also instances of {@link LimaBlockEntity}.
 */
public interface LimaBlockEntityAccess
{
    LimaBlockEntity getAsLimaBlockEntity();

    void setChanged();
}