package kjk.hiddenmagic.blockextension;

import net.minecraftforge.common.MinecraftForge;

public class BlockExtensions {

    public static BELifeMagic LIFE_MAGIC;

    public static void initialize() {
        LIFE_MAGIC = new BELifeMagic();

        MinecraftForge.EVENT_BUS.register(LIFE_MAGIC);
    }

}
