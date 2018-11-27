package kjk.hiddenmagic.magictype;

import com.google.common.collect.Sets;
import net.minecraft.block.Block;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.Set;

public abstract class MagicType {

    private static int idCounter = 0;

    public final int id;
    public final String name;

    public MagicType(String name){
        this.name = name;
        id = idCounter++;
    }

    public static Set<Block> set(Block... blocks) {
        return Sets.newHashSet(blocks);
    }

    public abstract int flowInto(World world, BlockPos pos, int amount);
}
