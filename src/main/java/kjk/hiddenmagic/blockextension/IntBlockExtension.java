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
        WorldExtension<Integer> we = getWorldExtension(world);
        int newAmount = Math.max(we.get(pos) + amount, min());
        we.set(pos, newAmount);
    }

    @Override
    protected Integer defaultValue() {
        return 0;
    }
}