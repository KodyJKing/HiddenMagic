package hiddenmagic.flow;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
import java.util.Stack;

import net.minecraft.block.Block;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class Flow {

    public static int tryFlow(World world, BlockPos source, int amount, int maxDepth, boolean shareFlow, Set<Block> mediums, Set<Block> sinks, FlowHandler flowInto){
        Flow flow = new Flow(world, source, maxDepth, mediums, sinks);
        ArrayList<BlockPos> sinkPositions = new ArrayList<>();
        while(true){
            BlockPos pos = flow.next();
            if(pos == null)
                break;
            else
                sinkPositions.add(pos);
                if (!shareFlow)
                    break;
        }
        // TODO: Recalculate the share after flowing to each block to avoid waste. Flow to the blocks in a random order to avoid bias.
        int share = sinkPositions.size() == 0 ? 0 : Math.max(amount / sinkPositions.size(), 1);
        for(BlockPos p: sinkPositions){
            if(amount < share)
                share = amount;
            if(share == 0)
                break;
            amount -= share;
            amount += flowInto.apply(world, p, share);
        }
        return amount;
    }

    private HashSet<Long> closed;
    private Stack<BlockPos> open;
    private Stack<BlockPos> nextOpen;
    private World world;
    private int maxDepth;
    private Set<Block> mediums;
    private Set<Block> sinks;
    private int depth = 0;

    public Flow(World world, BlockPos source, int maxDepth, Set<Block> mediums, Set<Block> sinks){
        this.world = world;
        this.maxDepth = maxDepth;
        this.mediums = mediums;
        this.sinks = sinks;
        closed = new HashSet<>();
        open = new Stack<>();
        nextOpen = new Stack<>();

        open.add(source);
        close(source);
    }

    private boolean isClosed(BlockPos pos){
        return closed.contains(pos.toLong());
    }

    private void close(BlockPos pos){
        closed.add(pos.toLong());
    }

    public BlockPos next(){
        while(true){
            if(open.empty()){
                if(nextOpen.empty() || depth >= maxDepth)
                    return null;
                open = nextOpen;
                nextOpen = new Stack<>();
                depth++;
            }

            BlockPos p = open.pop();
            for(BlockPos n: neighbors(p)){
//					System.out.println(
//						"Scanning: "
//						+ Integer.toString(n.getX()) + ", "
//						+ Integer.toString(n.getY()) + ", "
//						+ Integer.toString(n.getZ()));
                if(isClosed(n))
                    continue;
                close(n);
                Block block = world.getBlockState(n).getBlock();
                //System.out.println(block.getLocalizedName());
                if(mediums.contains(block)){
                    nextOpen.push(n);
                }
                if(sinks.contains(block)){
                    open.push(p);
                    return n;
                }
            }
        }
    }

    private BlockPos[] neighbors(BlockPos pos){
        BlockPos[] result = {pos.north(), pos.south(), pos.east(), pos.west(), pos.up(), pos.down()};
        return result;
    }
}
