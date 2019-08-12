package kjk.hiddenmagic;

import net.minecraft.block.BlockDispenser;
import net.minecraft.block.state.IBlockState;
import net.minecraft.dispenser.BehaviorDefaultDispenseItem;
import net.minecraft.dispenser.IBehaviorDispenseItem;
import net.minecraft.dispenser.IBlockSource;
import net.minecraft.item.*;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraftforge.common.util.FakePlayer;
import net.minecraftforge.common.util.FakePlayerFactory;

public class BehaviorDispenseBlock implements IBehaviorDispenseItem {

    public static void initialize() {
        for (Item item: Item.REGISTRY) {
            if (!(BlockDispenser.DISPENSE_BEHAVIOR_REGISTRY.getObject(item) instanceof BehaviorDefaultDispenseItem))
                continue;

            if (
                    item instanceof ItemBlock ||
                    item instanceof ItemBlockSpecial ||
                    item instanceof ItemSeeds ||
                    item instanceof ItemDoor ||
                    item instanceof ItemRedstone
            )
                BlockDispenser.DISPENSE_BEHAVIOR_REGISTRY.putObject(item, new BehaviorDispenseBlock());
        }
    }

    public ItemStack dispense(IBlockSource source, ItemStack stack) {
        World world = source.getWorld();
        if (world.isRemote)
            return stack;

        IBlockState dispenserState = source.getBlockState();

        EnumFacing dir = dispenserState.getValue(BlockDispenser.FACING);
        BlockPos pos = source.getBlockPos().offset(dir);

        IBlockState obstacleState = world.getBlockState(pos);
        if (!obstacleState.getBlock().isReplaceable(world, pos))
            return stack;

        FakePlayer fp = FakePlayerFactory.getMinecraft((WorldServer) world);

        fp.inventory.setInventorySlotContents(0, stack);
        fp.inventory.currentItem = 0;

        Vec3d playerPos = new Vec3d(pos.offset(dir)).addVector(0.5, 0, 0.5);
        fp.setPosition(playerPos.x, playerPos.y, playerPos.z);
        fp.rotationYaw = dir.getOpposite().getHorizontalAngle();

        Item item = stack.getItem();
        EnumFacing facing = dir;
        BlockPos placePos = pos;
        if (item instanceof ItemSeeds || item instanceof ItemDoor) {
            facing = EnumFacing.UP;
            placePos = placePos.down();
        }
        item.onItemUse(
                fp, world, placePos, EnumHand.MAIN_HAND, facing,
                0.5F + 0.25F * dir.getFrontOffsetX(),
                0.5F + 0.25F * dir.getFrontOffsetY(),
                0.5F + 0.25F * dir.getFrontOffsetZ()
        );
        return stack;
    }
}
