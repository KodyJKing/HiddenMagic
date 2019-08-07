package kjk.hiddenmagic.common;

import com.google.gson.Gson;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraft.world.chunk.Chunk;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

public class Common {

    public static Gson gson = new Gson();

    public static Iterator<Chunk> getTickableChunks(World world) {
        try {
            WorldServer ws = world.getMinecraftServer().worlds[world.provider.getDimension()];
            return world.getPersistentChunkIterable(ws.getPlayerChunkMap().getChunkIterator());
        } catch (Exception e) {
            return null;
        }
    }

    public static void message(EntityPlayer player, String msg) {
        System.out.println(msg);
        player.sendMessage( new TextComponentString(msg) );
    }

    public static <T> Set<T> set(T ... args) {
        return new HashSet<T>(Arrays.asList(args));
    }
}
