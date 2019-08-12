package kjk.hiddenmagic.blockbehaviour;

import kjk.hiddenmagic.magictype.MagicType;
import kjk.hiddenmagic.magictype.MagicTypes;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class BehaviourGrass extends BlockBehaviour {

    public BehaviourGrass() {
        setCapacity(MagicTypes.LIFE, 1000);
    }

    @Override
    public int conductance(World world, BlockPos pos, EnumFacing dir, boolean in, MagicType type) {
        if (type != MagicTypes.LIFE)
            return 0;
        if (in)
            return 1;
        else
            return dir.getAxis() == EnumFacing.Axis.Y ? 1 : 0;
    }
}
