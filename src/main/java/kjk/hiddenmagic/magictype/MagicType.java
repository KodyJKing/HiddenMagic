package kjk.hiddenmagic.magictype;

import kjk.hiddenmagic.blockextension.IntBlockExtension;
import kjk.hiddenmagic.common.Common;
import kjk.hiddenmagic.common.DefaultMap;
import kjk.hiddenmagic.flow.ManaFlow;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
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

    // Access

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
        set(world, pos, amount);
    }

    public void clear() {
        field.clear();
    }

    public int capacity(World world, BlockPos pos) {
        Block block = world.getBlockState(pos).getBlock();
        Integer result = capacities.get(block);
        return result == null ? 0 : result;
    }

    public int conductance(World world, BlockPos pos, EnumFacing dir, boolean in) {
        return conductance(world.getBlockState(pos), dir, in);
    }

    public int conductance(IBlockState blockState, EnumFacing dir, boolean in) {
        return 1;
    }

    public Set<BlockPos> getKeys(World world, ChunkPos pos) {
        return field.getKeys(world, pos);
    }

    // Dirty tracking

    public void notifyChange(World world, BlockPos pos) {
        if (capacity(world, pos) > 0)
            markDirty(world, pos);
    }

    private void markDirty(World world, BlockPos pos) {
        Set<BlockPos> positions = dirty.get(world.provider.getDimension());
        positions.add(pos);
    }

    private Set<BlockPos> getTickable(World world) {
        HashSet<BlockPos> result = new HashSet<>();
        Iterator<Chunk> chunks = Common.getTickableChunks(world);
        if (chunks == null)
            return result;
        chunks.forEachRemaining((Chunk chunk) -> {
            for (BlockPos pos: getKeys(world, chunk.getPos()))
                result.add(pos);
        });
        return result;
    }

    private boolean justLoaded = true;
    private Set<BlockPos> getDirty(World world) {
        if (justLoaded) {
            justLoaded = false;
            return getTickable(world);
        }
        return dirty.get(world.provider.getDimension());
    }

    private Set<BlockPos> getActive(World world) {
        HashSet<BlockPos> result = new HashSet<>();
        for (BlockPos pos: getDirty(world)) {
            result.add(pos);
            for (EnumFacing dir: EnumFacing.values()) {
                BlockPos pos2 = pos.offset(dir);
                if (capacity(world, pos2) > 0)
                    result.add(pos2);
            }
        }
        return result;
    }

    // Update

    public void update(World world) {
        Set<BlockPos> active = getActive(world);
        dirty.clear();
        ManaFlow.flow(world, this, active);
    }

}
