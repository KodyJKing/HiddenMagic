package kjk.hiddenmagic.common;

import net.minecraft.client.Minecraft;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.EnumSkyBlock;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class CWorld {

    public static int skyLight(World world, BlockPos pos){
        int i = world.getLightFor(EnumSkyBlock.SKY, pos) - world.getSkylightSubtracted();
        float f = world.getCelestialAngleRadians(1.0F);

        if (i > 0)
        {
            float f1 = f < (float)Math.PI ? 0.0F : ((float)Math.PI * 2F);
            f = f + (f1 - f) * 0.2F;
            i = Math.round((float)i * MathHelper.cos(f));
        }

        return MathHelper.clamp(i, 0, 15);
    }

    @SideOnly(Side.CLIENT)
    public static World playerWorld() {
        return Minecraft.getMinecraft().world;
    }

}
