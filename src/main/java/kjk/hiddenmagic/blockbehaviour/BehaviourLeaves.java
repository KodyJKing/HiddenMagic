package kjk.hiddenmagic.blockbehaviour;

import kjk.hiddenmagic.common.CWorld;
import kjk.hiddenmagic.common.Common;
import kjk.hiddenmagic.flow.InstantFlow;
import kjk.hiddenmagic.magictype.MagicTypes;
import net.minecraft.block.material.Material;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class BehaviourLeaves extends BlockBehaviour {
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
}
