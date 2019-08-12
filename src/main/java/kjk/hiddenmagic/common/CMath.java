package kjk.hiddenmagic.common;

import java.util.List;
import java.util.Random;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;

public class CMath {

    public static Random rand = new Random();

    public static Vec3d randVec(double mag){
        double x, y, z;
        x = rand.nextGaussian();
        y = rand.nextGaussian();
        z = rand.nextGaussian();
        mag /= Math.sqrt(x * x + y * y + z * z);
        return new Vec3d(x * mag, y * mag, z * mag);
    }

    public static BlockPos randNeighbor(BlockPos pos){
        switch(rand.nextInt(6)){
            case 0:
                return pos.down();
            case 1:
                return pos.up();
            case 2:
                return pos.north();
            case 3:
                return pos.south();
            case 4:
                return pos.west();
            case 5:
                return pos.east();
        }
        return pos;
    }

    public static Vec3d lookVec(EntityPlayer player){
        float adjust = (float)Math.PI / 180.0F;
        float dx = -MathHelper.sin(player.rotationYaw * adjust);
        float dz = MathHelper.cos(player.rotationYaw * adjust);

        float dy = -MathHelper.sin(player.rotationPitch * adjust);
        float r = MathHelper.cos(player.rotationPitch * adjust);

        dx *= r;
        dz *= r;

        return new Vec3d(dx, dy, dz);
    }

    public static double lerp(double a, double b, double amount) {
        return a * (1 - amount) + b * amount;
    }

    public static double clamp(double x, double min, double max) {
        if (x < min) return min;
        if (x > max) return max;
        return x;
    }

    public static int clamp(int x, int min, int max) {
        if (x < min) return min;
        if (x > max) return max;
        return x;
    }


    public static <T> void shuffle(List<T> list) {
        for (int i = 0; i < list.size(); i++) {
            int j = i + rand.nextInt(list.size() - i);

            T tmp = list.get(i);
            list.set(i, list.get(j));
            list.set(j, tmp);
        }
    }

    public static Vec3d lerp(Vec3d a, Vec3d b, double amount) {
        return a.scale(1 - amount).add(b.scale(amount));
    }

    public static double easeCubic(double x) {
        return 3 * x * x - 2 * x * x * x;
    }

    // https://www.wolframalpha.com/input/?i=(2x)%5E4-4(2x)%5E3%2B4(2x)%5E2
    public static double bumpQuartic(double x) {
        double y = x * 2;
        double y4 = Math.pow(y, 4);
        double y3 = Math.pow(y, 3);
        double y2 = y * y;
        return y4 - 4 * y3 + 4 * y2;
    }

}
