package kjk.hiddenmagic.flow;

import kjk.hiddenmagic.common.Common;
import kjk.hiddenmagic.magictype.MagicType;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;

import java.util.HashMap;
import java.util.Iterator;
import java.util.function.Function;

public class Flow2 {

    static class Flow extends HashMap<BlockPos, Integer>{}

    public static void flow(World world, MagicType type) {
        Flow flow = new Flow();

        Iterator<Chunk> chunks = Common.getTickableChunks(world);
        if (chunks == null)
            return;

        // Todo: Randomize order so there is no directional bias.
        chunks.forEachRemaining((Chunk chunk) -> {
            for (BlockPos pos: type.getActive(world, chunk.getPos()))
                equalize(world, pos, type, flow);
        });

        flow.forEach((BlockPos pos, Integer amount) -> {
//            System.out.println("Flow into " + pos.toString() + ", " + amount);
            type.add(world, pos, amount);
        });

    }

    public static void equalize(World world, BlockPos pos, MagicType type, Flow flow) {

        Function<BlockPos, Double> getShare = (BlockPos _pos) -> (double) type.capacity(world, _pos);

        double netShare = getShare.apply(pos);
//        double netShare = type.capacity(world, pos);
        for (EnumFacing dir: EnumFacing.values())
            netShare += getShare.apply(pos.offset(dir));
//            netShare += type.remainingCapacity(world, pos.offset(dir));

        int magic = type.get(world, pos);
        int magicLeft = magic;
        for (EnumFacing dir: EnumFacing.values()) {
            BlockPos pos2 = pos.offset(dir);
            double share = getShare.apply(pos2) / netShare;
//            double share = type.remainingCapacity(world, pos2) / netShare;
            int amount = (int) Math.floor(magic * share);
            amount = addFlow(world, pos2, amount, type, flow);
            magicLeft -= amount;
        }

        int magicLost = magic - magicLeft;
        addFlow(world, pos, -magicLost, type, flow);
    }

    // Todo: Clamp flow to not exceed capacity.
    // Note: This could lead to directional bias, which could be resolved
    // either by backflow or randomizing the order blocks are equalized.
    public static int addFlow(World world, BlockPos pos, int amount, MagicType type, Flow flow) {
        int oldAmount = flow.getOrDefault(pos, 0);
        int newAmount = amount + oldAmount;
        newAmount = Math.min(newAmount, type.remainingCapacity(world, pos));
        flow.put(pos, newAmount);
        return newAmount - oldAmount;
    }
}
