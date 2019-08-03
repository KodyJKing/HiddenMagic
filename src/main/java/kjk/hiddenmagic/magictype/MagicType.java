package kjk.hiddenmagic.magictype;

import com.google.common.collect.Sets;
import kjk.hiddenmagic.blockextension.IntBlockExtension;
import kjk.hiddenmagic.common.DefaultMap;
import net.minecraft.block.Block;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.World;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

public abstract class MagicType {

    private static int idCounter = 0;

    public final int id;
    public final String name;

    private final IntBlockExtension field;

    public final HashMap<Block, Integer> capacities = new HashMap<Block, Integer>();

    public DefaultMap<Integer, Set<BlockPos>> dirty = new DefaultMap<>(HashSet::new);

    public MagicType(String name) {
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
        return result == null ? 0 : result;
    }

    public int remainingCapacity(World world, BlockPos pos) {
        return capacity(world, pos) - field.get(world, pos);
    }

    public double pressure(World world, BlockPos pos) {
        return field.get(world, pos) / (double) capacity(world, pos);
    }

    public double vacuum(World world, BlockPos pos) { return 1 - pressure(world, pos); }

    public Set<BlockPos> getActive(World world, ChunkPos pos) {
        return field.getActive(world, pos);
    }

    public Integer get(World world, BlockPos pos) {
        return field.get(world, pos);
    }

    public void set(World world, BlockPos pos, Integer value) {
        int oldValue = field.get(world, pos);
        value = Math.min(value, capacity(world, pos));
        field.set(world, pos, value);
        if (oldValue != value)
            markDirty(world, pos);
    }

    public void add(World world, BlockPos pos, int amount) {
        amount += field.get(world, pos);
        field.set(world, pos, amount);
    }

    private void markDirty(World world, BlockPos pos) {
        int dimension = world.provider.getDimension();
        Set<BlockPos> positions = dirty.get(dimension);

    }

}
