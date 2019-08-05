package kjk.hiddenmagic.flow;

import kjk.hiddenmagic.common.CMath;
import kjk.hiddenmagic.common.Common;
import kjk.hiddenmagic.magictype.MagicType;
import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;

import java.util.*;

public class ManaFlow {

    static int msgTimer0 = 0;
    public static void flow(World world, MagicType type, Set<BlockPos> active) {
        ArrayList<BlockPos> activeList = new ArrayList<BlockPos>(active);
        CMath.shuffle(activeList);
        for (BlockPos pos: activeList)
            flowAt(world, pos, type);

        if (msgTimer0++ % 20 == 0) {
            System.out.println(type.name + " updates: " + activeList.size());

            HashMap<String, Integer> counts = new HashMap<>();
            for (BlockPos pos: active) {
                Block block = world.getBlockState(pos).getBlock();
                String name = block.getLocalizedName();
                int amount = counts.getOrDefault(name, 0) + 1;
                counts.put(name, amount);
            }

            System.out.println(counts);
        }

    }

    private static List<EnumFacing> dirs = Arrays.asList(EnumFacing.values());
    public static void flowAt(World world, BlockPos pos, MagicType type) {
        CMath.shuffle(dirs);
        for (EnumFacing dir: dirs) {
            BlockPos otherPos = pos.offset(dir);
            int amount = flowBetween(world, pos, otherPos, dir, type);
            type.add(world, pos, amount);
            type.add(world, otherPos, -amount);
        }

    }

    static int msgTimer1 = 0;
    public static int flowBetween(World world, BlockPos a, BlockPos b, EnumFacing dir, MagicType type) {
        int capacityA = type.capacity(world, a);
        int capacityB = type.capacity(world, b);

        if (capacityA == 0 || capacityB == 0)
            return 0;

        int magicA = type.get(world, a);
        int magicB = type.get(world, b);

        int roomInA = capacityA - magicA;
        int roomInB = capacityB - magicB;

        int receptanceA = type.conductance(world, a, dir, true);
        int receptanceB = type.conductance(world, b, dir.getOpposite(), true);
        int transmissionA = type.conductance(world, a, dir, false);
        int transmissionB = type.conductance(world, b, dir.getOpposite(), false);
        int transmittedA = magicA * transmissionA * receptanceB;
        int transmittedB = magicB * transmissionB * receptanceA;

        double diffusivity = 0.5;
        double flow = Math.round((transmittedB - transmittedA) * diffusivity);
        flow = CMath.clamp(flow, -roomInB, roomInA);

//        Block ba = world.getBlockState(a).getBlock();
//        Block bb = world.getBlockState(b).getBlock();
//        if (ba == Blocks.MELON_BLOCK || bb == Blocks.MELON_BLOCK ) {
//            if (msgTimer1++ % 10 == 0) {
//                System.out.println("Transmitted magic: " + dir.getName());
//                System.out.println(ba.getLocalizedName() + ": " + transmittedA + " of " + magicA);
//                System.out.println(bb.getLocalizedName() + ": " + transmittedB + " of " + magicB);
//                System.out.println("InstantFlow: " + (int) flow);
//                System.out.println();
//            }
//        }

        return (int) flow;
    }
}
