package kjk.hiddenmagic.blockbehaviour;

import kjk.hiddenmagic.common.DefaultMap;
import kjk.hiddenmagic.magictype.MagicType;
import kjk.hiddenmagic.magictype.MagicTypes;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.List;

public class BlockBehaviours {

    // Registry

    private static DefaultMap<Block, List<Entry>> behaviours = new DefaultMap<>(ArrayList::new);
    private static class Entry {
        public BlockBehaviour behaviour;
        public int meta;
        public Entry(BlockBehaviour behaviour, int meta) {
            this.behaviour = behaviour;
            this.meta = meta;
        }
    }

    static void add(Block block, int meta, BlockBehaviour behaviour) {
        behaviours.getOrCreate(block).add(new Entry(behaviour, meta));
    }

    static void add(Block block, BlockBehaviour behaviour) {
        add(block, -1, behaviour);
    }

    static void add(IBlockState bs, BlockBehaviour behaviour) {
        int meta = bs.getBlock().getMetaFromState(bs);
        add(bs.getBlock(), meta, behaviour);
    }

    public static BlockBehaviour get(IBlockState bs) {
        List<Entry> entries = behaviours.get(bs.getBlock());
        if (entries != null) {
            for (Entry entry: entries) {
                int behaviourMeta = entry.meta;
                int blockMeta = bs.getBlock().getMetaFromState(bs);
                if (behaviourMeta == -1 || behaviourMeta == blockMeta)
                    return entry.behaviour;
            }
        }
        return null;
    }

    // Convenient access

    public static BlockBehaviour get(World world, BlockPos pos) {
        return get(world.getBlockState(pos));
    }

    public static boolean consumesMagic(World world, BlockPos pos, MagicType type) {
        BlockBehaviour bb = get(world, pos);
        return bb == null ? false : bb.consumesMagic(type);
    }

    public static void randomTick(World world, BlockPos pos) {
        BlockBehaviour bb = get(world, pos);
        if (bb != null)
            bb.randomTick(world, pos);
    }

    public static void magicTick(World world, BlockPos pos, MagicType type) {
        BlockBehaviour bb = get(world, pos);
        if (bb != null)
            bb.magicTick(world, pos, type);
}

    // Content

    public static void initialize() {
        BlockBehaviour leavesBehaviour = new BehaviourLeaves();
        add(Blocks.LEAVES, leavesBehaviour);
        add(Blocks.LEAVES2, leavesBehaviour);
        BlockBehaviour logBehaviour = new BlockBehaviour().setCapacity(MagicTypes.LIFE, 1000);
        add(Blocks.LOG, logBehaviour);
        add(Blocks.LOG2, logBehaviour);
        add(Blocks.MELON_BLOCK, new BehaviourBattery(true));
        add(Blocks.PUMPKIN, new BehaviourBattery(false));
        add(Blocks.CACTUS, new BehaviourCactus());
        add(Blocks.GRASS, new BehaviourGrass());
        add(Blocks.TALLGRASS, 2, new BehaviourFern());
    }
}
