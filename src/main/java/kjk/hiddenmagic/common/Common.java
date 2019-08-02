package kjk.hiddenmagic.common;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraft.world.chunk.Chunk;

import java.util.Iterator;

public class Common {
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
}
