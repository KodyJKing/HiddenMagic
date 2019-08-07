package kjk.hiddenmagic.blockbehaviour;

import kjk.hiddenmagic.magictype.MagicType;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.HashMap;
import java.util.HashSet;

public class BlockBehaviour {

    protected HashSet<MagicType> consumes = new HashSet<>();
    public final BlockBehaviour setConsumes(MagicType type) {
        consumes.add(type);
        return this;
    }
    public final boolean consumesMagic(MagicType type) { return consumes.contains(type); }

    public void randomTick(World world, BlockPos pos) {}
    public void magicTick(World world, BlockPos pos, MagicType type) {}

    protected HashMap<MagicType, Integer> capacities = new HashMap<>();
    public final BlockBehaviour setCapacity(MagicType type, int value) {
        capacities.put(type, value);
        return this;
    }
    public final int capacity(MagicType type) {  return capacities.getOrDefault(type, 0); }
    public int conductance(World world, BlockPos pos, EnumFacing dir, boolean in, MagicType type) { return 1; }
}
