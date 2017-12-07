package hiddenmagic.chunkextension;

import net.minecraftforge.common.MinecraftForge;

public class WorldExtensions {

    public static WELifeMagic LIFE_MAGIC;

    public static void initialize() {
        LIFE_MAGIC = new WELifeMagic();

        MinecraftForge.EVENT_BUS.register(LIFE_MAGIC);
    }

}
