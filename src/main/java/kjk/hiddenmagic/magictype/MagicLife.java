package kjk.hiddenmagic.magictype;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.EnumFacing;

import java.util.Set;

public class MagicLife extends MagicType {

    public MagicLife(String name) {
        super(name);
//        capacities.put(Blocks.LEAVES, 16);
        capacities.put(Blocks.LOG, 32);
        capacities.put(Blocks.LOG2, 32);
        capacities.put(Blocks.MELON_BLOCK, 8192);
    }

    @Override
    public int conductance(IBlockState bs, EnumFacing dir, boolean in) {
        if (bs.getBlock() == Blocks.MELON_BLOCK) {
            if (dir.equals(EnumFacing.UP))
                return in ? 1 : 0;
            if (dir.equals(EnumFacing.DOWN))
                return in ? 0 : 1;
        }
        return 1;
    }
}
