package kjk.hiddenmagic.blockbehaviour;

import kjk.hiddenmagic.magictype.MagicType;
import kjk.hiddenmagic.magictype.MagicTypes;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class BehaviourBattery extends BlockBehaviour {

    private boolean feedFromTop;
    public BehaviourBattery(boolean feedFromTop) {
        setCapacity(MagicTypes.LIFE, 8192);
        this.feedFromTop = feedFromTop;
    }

    @Override
    public int conductance(World world, BlockPos pos, EnumFacing dir, boolean in, MagicType type) {
        if (dir.equals(EnumFacing.UP))
            return (in == feedFromTop) ? 1 : 0;
        if (dir.equals(EnumFacing.DOWN))
            return (in == feedFromTop) ? 0 : 1;
        return 1;
    }
}
