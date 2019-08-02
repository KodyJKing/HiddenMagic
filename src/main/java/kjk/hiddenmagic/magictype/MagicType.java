package kjk.hiddenmagic.magictype;

import com.google.common.collect.Sets;
import kjk.hiddenmagic.blockextension.IntBlockExtension;
import kjk.hiddenmagic.blockextension.WorldExtension;
import net.minecraft.block.Block;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.HashMap;
import java.util.Set;

public abstract class MagicType {

    private static int idCounter = 0;

    public final int id;
    public final String name;
    public final IntBlockExtension field;

    public final HashMap<Block, Integer> capacities = new HashMap<Block, Integer>();

    public MagicType(String name){
        this.name = name;
        id = idCounter++;
        this.field = new IntBlockExtension(name);
    }

    public static Set<Block> set(Block... blocks) {
        return Sets.newHashSet(blocks);
    }

    public abstract int flowInto(World world, BlockPos pos, int amount);

    public int capacity(World world, BlockPos pos) {
        Block block = world.getBlockState(pos).getBlock();
        Integer result = capacities.get(block);
        return result == null ?  0 : result;
    }

    public int remainingCapacity(World world, BlockPos pos) {
        return capacity(world, pos) - field.get(world, pos);
    }

    public double pressure(World world, BlockPos pos) {
        return field.get(world, pos) / (double) capacity(world, pos);
    }

    public void add(World world, BlockPos pos, int amount) {
        amount = Math.min(remainingCapacity(world, pos), amount);
        field.add(world, pos, amount);
    }

}
