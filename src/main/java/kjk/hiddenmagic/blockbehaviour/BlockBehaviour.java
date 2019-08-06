package kjk.hiddenmagic.blockbehaviour;

import kjk.hiddenmagic.magictype.MagicType;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.HashMap;

public abstract class BlockBehaviour {
    public boolean consumesMagic(MagicType type) { return false; }
    public void randomTick(World world, BlockPos pos) {}
    public void magicTick(World world, BlockPos pos, MagicType type) {}

    private HashMap<MagicType, Integer> capacities = new HashMap<>();
    public final int capacity(MagicType type) {  return capacities.get(type); }
    public int conductance(World world, BlockPos pos, EnumFacing dir, boolean in, MagicType type) { return 1; }
}
