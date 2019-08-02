package kjk.hiddenmagic.magictype;

import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class MagicTypes {
    public static MagicType LIFE;

    public static void initialize() {
        LIFE = new MagicLife("life");
    }
}
