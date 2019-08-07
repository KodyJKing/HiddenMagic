package kjk.hiddenmagic.magictype;

import kjk.hiddenmagic.blockbehaviour.BlockBehaviour;
import kjk.hiddenmagic.blockbehaviour.BlockBehaviours;
import kjk.hiddenmagic.blockextension.IntBlockExtension;
import kjk.hiddenmagic.common.CMath;
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

public class MagicType {

    private static int idCounter = 0;

    public final int id;
    public final String name;

    private final IntBlockExtension field;

    public final HashMap<Block, Integer> capacities = new HashMap<Block, Integer>();

    public DefaultMap<Integer, Set<BlockPos>> dirty = new DefaultMap<>(HashSet::new);

    public Set<BlockPos> tick = new HashSet<>();

    public MagicType(String name) {
        this.name = name;
        id = idCounter++;
        this.field = new IntBlockExtension(name);
    }

    // Access

    public Integer get(World world, BlockPos pos) {
        return field.get(world, pos);
    }

    public int set(World world, BlockPos pos, Integer value) {
        int oldValue = field.get(world, pos);

        int clamped = CMath.clamp(value, 0, capacity(world, pos));
        field.set(world, pos, clamped);

        if (BlockBehaviours.consumesMagic(world, pos, this)) {
            if (clamped > 0)
                tick.add(pos);
            else
                tick.remove(pos);
        }

        if (oldValue != clamped)
            markDirty(world, pos);

        return value - clamped;
    }

    public int add(World world, BlockPos pos, int amount) {
        amount += field.get(world, pos);
        return set(world, pos, amount);
    }

    public boolean consume(World world, BlockPos pos, int amount) {
        int magic = get(world, pos);
        if (magic >= amount) {
            add(world, pos, -amount);
            return true;
        }
        return false;
    }

    public void clear() {
        field.clear();
    }

    public int capacity(World world, BlockPos pos) {
        BlockBehaviour bb = BlockBehaviours.get(world, pos);
        return bb != null ? bb.capacity(this) : 0;
    }

    public int conductance(World world, BlockPos pos, EnumFacing dir, boolean in) {
//        return conductance(world.getBlockState(pos), dir, in);
        BlockBehaviour bb = BlockBehaviours.get(world, pos);
        return bb != null ? bb.conductance(world, pos, dir, in, this) : 0;
    }

    public Set<BlockPos> getKeys(World world, ChunkPos pos) {
        return field.getKeys(world, pos);
    }

    // Dirty tracking

    public void notifyChange(World world, BlockPos pos) {
        if (capacity(world, pos) > 0)
            markDirty(world, pos);
        else
            set(world, pos, 0);
    }

    private void markDirty(World world, BlockPos pos) {
        Set<BlockPos> positions = dirty.getOrCreate(world.provider.getDimension());
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
        return dirty.getOrCreate(world.provider.getDimension());
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
        HashSet<BlockPos> currentTick = new HashSet<>();
        currentTick.addAll(tick);
        for (BlockPos tickPos: currentTick)
            BlockBehaviours.magicTick(world, tickPos, this);
    }

}
