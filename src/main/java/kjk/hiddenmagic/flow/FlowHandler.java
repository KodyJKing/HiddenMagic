package kjk.hiddenmagic.flow;

import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

@FunctionalInterface
public interface FlowHandler {
    public int apply(World world, BlockPos pos, int amount);
}
