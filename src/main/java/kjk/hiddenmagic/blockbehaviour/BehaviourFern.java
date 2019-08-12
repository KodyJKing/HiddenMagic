package kjk.hiddenmagic.blockbehaviour;

import kjk.hiddenmagic.common.CMath;
import kjk.hiddenmagic.magictype.MagicType;
import kjk.hiddenmagic.magictype.MagicTypes;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.List;

public class BehaviourFern extends BlockBehaviour {

    public BehaviourFern() {
        setCapacity(MagicTypes.LIFE, 1000);
        setConsumes(MagicTypes.LIFE);
    }

    @Override
    public int conductance(World world, BlockPos pos, EnumFacing dir, boolean in, MagicType type) {
        if (type != MagicTypes.LIFE)
            return 0;
        if (dir == EnumFacing.DOWN)
            return 1;
        return 0;
    }

    @Override
    public void magicTick(World world, BlockPos pos, MagicType type) {
        int diameter = 9;
        int radius = diameter / 2;

        List<BlockPos> sinks = new ArrayList<>();

        int netMagicNeed = 0;

        for (int x = -radius; x <= radius; x++) {
            for (int z = -radius; z <= radius; z++) {
                for (int y = -1; y <= 1; y++) {
                    if (x == 0 && y == 0 && z == 0)
                        continue;
                    BlockPos scanPos = pos.add(x, y, z);
                    if (!BlockBehaviours.consumesMagic(world, scanPos, MagicTypes.LIFE))
                        continue;

                    sinks.add(scanPos);
                    netMagicNeed += MagicTypes.LIFE.room(world, scanPos);
                }
            }
        }

        if (netMagicNeed == 0)
            return;


        int magic = MagicTypes.LIFE.get(world, pos);
        double magicRation = ((double) magic) / netMagicNeed / 2;

        int spent = 0;

        for (BlockPos scanPos: sinks) {
            double dMagicToGive = MagicTypes.LIFE.room(world, scanPos) * magicRation;
            int magicToGive = (int) Math.floor(dMagicToGive);
            int excess = MagicTypes.LIFE.add(world, scanPos, magicToGive);
            spent += magicToGive - excess;
        }

        MagicTypes.LIFE.add(world, pos, -spent);
    }
}
