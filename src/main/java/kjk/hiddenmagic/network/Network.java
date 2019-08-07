package kjk.hiddenmagic.network;

import kjk.hiddenmagic.HiddenMagic;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import net.minecraftforge.fml.relauncher.Side;

public class Network {
    public static final SimpleNetworkWrapper INSTANCE = NetworkRegistry.INSTANCE.newSimpleChannel(HiddenMagic.MODID);
    private static int id;
    public static void register(Class clazz, Side side) {
        System.out.println("Registering message type " + clazz.getName() + " with id " + id + ".");
        INSTANCE.registerMessage(Message.Handler.class, clazz, id++, side);
    }
}
