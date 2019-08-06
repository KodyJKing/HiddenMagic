package kjk.hiddenmagic.blockbehaviour;

import kjk.hiddenmagic.common.CMath;
import kjk.hiddenmagic.common.CWorld;
import kjk.hiddenmagic.common.Common;
import kjk.hiddenmagic.common.DefaultMap;
import kjk.hiddenmagic.flow.InstantFlow;
import kjk.hiddenmagic.magictype.MagicType;
import kjk.hiddenmagic.magictype.MagicTypes;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.AxisAlignedBB;
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
        behaviours.get(block).add(new Entry(behaviour, meta));
    }

    static void add(Block block, BlockBehaviour behaviour) {
        add(block, -1, behaviour);
    }

    static void add(IBlockState bs, BlockBehaviour behaviour) {
        int meta = bs.getBlock().getMetaFromState(bs);
        add(bs.getBlock(), meta, behaviour);
    }

    public static BlockBehaviour get(IBlockState bs) {
        for (Entry entry: behaviours.get(bs.getBlock())) {
            int behaviourMeta = entry.meta;
            int blockMeta = bs.getBlock().getMetaFromState(bs);
            if (behaviourMeta == -1 || behaviourMeta == blockMeta)
                return entry.behaviour;
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

        BlockBehaviour LEAVES = new BlockBehaviour() {
            @Override
            public void randomTick(World world, BlockPos pos) {
                int light = 0;
                for(int x = -2; x <= 2; x++){
                    for(int y = -2; y <= 2; y++){
                        for(int z = -2; z <= 2; z++) {
                            BlockPos p = pos.add(x, y, z);
                            if (world.getBlockState(p).getMaterial() == Material.LEAVES)
                                light += CWorld.skyLight(world, p);
                        }
                    }
                }

                double lightFactor = 0.1;
                InstantFlow.tryFlow(
                        world, pos, (int) (light * lightFactor), 5, true,
                        Common.set(Blocks.LEAVES, Blocks.LEAVES2),
                        Common.set(Blocks.LOG, Blocks.LOG2),
                        (world2, pos2, amount) -> MagicTypes.LIFE.add(world2, pos2, amount)
                );
            }
        };

        add(Blocks.LEAVES, LEAVES);
        add(Blocks.LEAVES2, LEAVES);

        BlockBehaviour LOG = new BlockBehaviour() {
            @Override
            public int capacity(MagicType type) {
                return type == MagicTypes.LIFE ? 32 : 0;
            }
        };

        add(Blocks.LOG, LOG);
        add(Blocks.LOG2, LOG);

        add(Blocks.CACTUS, new BlockBehaviour() {

            @Override
            public boolean consumesMagic(MagicType type) {
                return type == MagicTypes.LIFE;
            }

            @Override
            public void magicTick(World world, BlockPos pos, MagicType type) {
                if (CMath.rand.nextFloat() > 0.025)
                    return;

                List<EntityLivingBase> entities = world.getEntitiesWithinAABB(
                        EntityLivingBase.class, new AxisAlignedBB(pos).grow(5) );
                EntityLivingBase entityToHurt = null;

                for (EntityLivingBase entity: entities) {
                    if (entity instanceof EntityPlayer && ((EntityPlayer) entity).capabilities.isCreativeMode)
                        continue;
                    if (entity.hurtResistantTime <= 0) {
                        entityToHurt = entity;
                        break;
                    }
                }

                if (entityToHurt != null && type.consume(world, pos, 512))
                        entityToHurt.attackEntityFrom(DamageSource.CACTUS, 8);
            }
        });
    }

}
