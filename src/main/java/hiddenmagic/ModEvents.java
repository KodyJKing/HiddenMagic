package hiddenmagic;

import hiddenmagic.chunkextension.WorldExtensions;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class ModEvents {

    @SubscribeEvent
    public void onClick(PlayerInteractEvent.RightClickBlock event) {
        BlockPos pos = event.getPos();
        System.out.println( "Life magic: " + String.valueOf(WorldExtensions.LIFE_MAGIC.get(pos)) );
    }

}
