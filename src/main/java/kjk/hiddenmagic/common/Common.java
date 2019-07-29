package kjk.hiddenmagic.common;

import com.google.gson.Gson;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraft.world.chunk.Chunk;

import java.util.Iterator;

public class Common {

    public static Gson gson = new Gson();

    public static Iterator<Chunk> getTickableChunks(World world) {
        WorldServer ws = world.getMinecraftServer().worlds[world.provider.getDimension()];
        return world.getPersistentChunkIterable(ws.getPlayerChunkMap().getChunkIterator());
    }

    public static void message(EntityPlayer player, String msg) {
        System.out.println(msg);
        player.sendMessage( new TextComponentString(msg) );
    }
}
