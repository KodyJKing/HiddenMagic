package kjk.hiddenmagic.blockextension;

import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagInt;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class IntBlockExtension extends BlockExtension<Integer> {

    public IntBlockExtension(String name) { super(name); }

    public int min() { return 0; }

    @Override
    protected NBTBase toNBT(Integer value) {
        return new NBTTagInt(value);
    }

    @Override
    protected Integer fromNBT(NBTBase nbt) {
        return ((NBTTagInt)nbt).getInt();
    }

    public void add(World world, BlockPos pos, int amount) {
        set(world, pos, amount + get(world, pos));
    }

    @Override
    public void set(World world, BlockPos pos, Integer value) {
        value = Math.max(value, min());
        super.set(world, pos, value);
    }

    @Override
    protected Integer defaultValue() {
        return 0;
    }
}