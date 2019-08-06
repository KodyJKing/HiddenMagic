package kjk.hiddenmagic;

import kjk.hiddenmagic.blockbehaviour.BlockBehaviour;
import kjk.hiddenmagic.blockbehaviour.BlockBehaviours;
import kjk.hiddenmagic.magictype.MagicTypes;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import org.apache.logging.log4j.Logger;

@Mod(modid = HiddenMagic.MODID, name = HiddenMagic.NAME, version = HiddenMagic.VERSION)
public class HiddenMagic {
    public static final String MODID = "hiddenmagic";
    public static final String NAME = "Hidden Magic";
    public static final String VERSION = "1.0";

    private static Logger logger;

    @EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        logger = event.getModLog();
    }

    @EventHandler
    public void init(FMLInitializationEvent event) {
        MagicTypes.initialize();
        BlockBehaviours.initialize();
    }

    @EventHandler
    public void postInit(FMLInitializationEvent event) {
        MinecraftForge.EVENT_BUS.register(new ModEvents());
    }
}
