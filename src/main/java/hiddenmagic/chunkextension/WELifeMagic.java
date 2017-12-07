package hiddenmagic.chunkextension;

import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagInt;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.event.world.ChunkDataEvent;

public class WELifeMagic extends WorldExtension<Integer> {

    @Override
    public String name() {
        return "Life Magic";
    }

    @Override
    protected NBTBase toNBT(Integer value) {
        return new NBTTagInt(value);
    }

    @Override
    protected Integer fromNBT(NBTBase nbt) {
        return ((NBTTagInt)nbt).getInt();
    }

    public void add(BlockPos pos, int amount) {
        set(pos, get(pos) + amount);
    }

    @Override
    protected Integer defaultValue() {
        return 0;
    }
}
