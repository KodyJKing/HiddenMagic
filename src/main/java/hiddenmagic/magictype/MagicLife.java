package hiddenmagic.magictype;

import hiddenmagic.flow.Flow;
import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class MagicLife extends MagicType {

    public MagicLife(String name) {
        super(name);
    }

    @Override
    public int flowInto(World world, BlockPos pos, int amount) {
        Block block = world.getBlockState(pos).getBlock();
        if (block == Blocks.LEAVES)
            return flowToLog(world, pos, amount);
        if (block == Blocks.LOG || block == Blocks.LOG2)
            return flowToConsumers(world, pos, amount);
        return amount;
    }

    private int flowToLog(World world, BlockPos pos, int amount) {
        return Flow.tryFlow(
                world, pos, amount,5, false,
                set(Blocks.LEAVES), set(Blocks.LOG, Blocks.LOG2),
                (World w, BlockPos p, int a) -> flowToConsumers(w, p, a)
        );
    }

    private int flowToConsumers(World world, BlockPos pos, int amount) {
        return Flow.tryFlow(
                world, pos, amount,1024, true,
                set(Blocks.LOG, Blocks.LOG2), set(Blocks.DOUBLE_PLANT),
                (World w, BlockPos p, int a) -> flowToConsumer(w, p, a)
        );
    }

    private int flowToConsumer(World world, BlockPos pos, int amount) {
        System.out.println("flowToConsumer");
        return 0;
    }

}
