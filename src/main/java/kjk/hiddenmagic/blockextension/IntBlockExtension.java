package kjk.hiddenmagic.blockextension;

import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagInt;

public class IntBlockExtension extends BlockExtension<Integer> {

    public IntBlockExtension(String name) { super(name); }

    @Override
    protected NBTBase toNBT(Integer value) {
        return new NBTTagInt(value);
    }

    @Override
    protected Integer fromNBT(NBTBase nbt) {
        return ((NBTTagInt)nbt).getInt();
    }

    @Override
    protected Integer defaultValue() {
        return 0;
    }
}