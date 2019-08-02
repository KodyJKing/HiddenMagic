package kjk.hiddenmagic.magictype;

import kjk.hiddenmagic.flow.Flow;
import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.Set;

public class MagicLife extends MagicType {

    private static Set<Block> leaves = set(Blocks.LEAVES);
    private static Set<Block> mediums = set(Blocks.LOG, Blocks.LOG2);
    private static Set<Block> sinks = set(Blocks.MELON_BLOCK);

    public MagicLife(String name) {
        super(name);
        capacities.put(Blocks.LEAVES, 4);
        capacities.put(Blocks.LOG, 8);
        capacities.put(Blocks.LOG2, 8);
        capacities.put(Blocks.MELON_BLOCK, 2048);
    }

    @Override
    public int flowInto(World world, BlockPos pos, int amount) {
        Block block = world.getBlockState(pos).getBlock();
        if (block == Blocks.LEAVES)
            return flowToMediums(world, pos, amount);
        if (block == Blocks.LOG || block == Blocks.LOG2)
            return flowToSinks(world, pos, amount);
        return amount;
    }

    private int flowToMediums(World world, BlockPos pos, int amount) {
        return Flow.tryFlow(
                world, pos, amount,5, false,
                leaves, mediums,
                this::flowToSinks
        );
    }

    private int flowToSinks(World world, BlockPos pos, int amount) {
        return Flow.tryFlow(
                world, pos, amount,1024, true,
                mediums, sinks,
                this::flowToConsumer
        );
    }

    private int flowToConsumer(World world, BlockPos pos, int amount) {
        this.add(world, pos, amount);
        return 0;
    }

}
