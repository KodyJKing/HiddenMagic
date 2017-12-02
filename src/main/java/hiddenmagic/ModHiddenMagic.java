package hiddenmagic;

import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;

@Mod(modid = ModHiddenMagic.MODID, version = ModHiddenMagic.VERSION)
public class ModHiddenMagic
{
    public static final String MODID = "hiddenmagic";
    public static final String VERSION = "1.0";
    
    @EventHandler
    public void init(FMLInitializationEvent event)
    {
        System.out.println("Loading Hidden Magic...");
    }
}
