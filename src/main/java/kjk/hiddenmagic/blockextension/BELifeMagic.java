package kjk.hiddenmagic.blockextension;

import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagInt;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class BELifeMagic extends BlockExtension<Integer> {

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

    public void add(World world, BlockPos pos, int amount) {
        WorldExtension<Integer> we = getWorldExtension(world);
        we.set(pos, we.get(pos) + amount);
    }

    @Override
    protected Integer defaultValue() {
        return 0;
    }
}
