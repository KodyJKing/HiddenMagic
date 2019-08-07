package kjk.hiddenmagic.blockbehaviour;

import kjk.hiddenmagic.common.CMath;
import kjk.hiddenmagic.common.CWorld;
import kjk.hiddenmagic.magictype.MagicType;
import kjk.hiddenmagic.magictype.MagicTypes;
import kjk.hiddenmagic.network.Message;
import kjk.hiddenmagic.network.Network;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.util.*;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;

import java.util.List;
import java.util.Random;

public class BehaviourCactus extends BlockBehaviour {

    public BehaviourCactus() {
        setCapacity(MagicTypes.LIFE, 1024);
        setConsumes(MagicTypes.LIFE);
    }

    @Override
    public void magicTick(World world, BlockPos pos, MagicType type) {
        if (CMath.rand.nextFloat() > 0.025)
            return;

        List<EntityLivingBase> entities = world.getEntitiesWithinAABB(
                EntityLivingBase.class, new AxisAlignedBB(pos).grow(5) );
        EntityLivingBase entityToHurt = null;

        for (EntityLivingBase entity: entities) {
            if (entity instanceof EntityPlayer && ((EntityPlayer) entity).capabilities.isCreativeMode)
                continue;
            if (entity.hurtResistantTime <= 0) {
                entityToHurt = entity;
                break;
            }
        }

        if (entityToHurt != null && type.consume(world, pos, 512)) {
            entityToHurt.attackEntityFrom(DamageSource.CACTUS, 8);
            Vec3d source = new Vec3d(pos).addVector(0.5, 0.5, 0.5);
            Vec3d target = entityToHurt.getPositionVector().addVector(0, entityToHurt.getEyeHeight(), 0);
            Network.INSTANCE.sendToAllTracking(new Shock(source, target), entityToHurt);

        }
    }

    static { Network.register(Shock.class, Side.CLIENT); }
    public static class Shock extends Message {
        Vec3d source;
        Vec3d target;
        public Shock() {}
        public Shock(Vec3d source, Vec3d target) {
            this.source = source;
            this.target = target;
        }
        @Override
        public void runClient(MessageContext ctx) {
            World world = CWorld.playerWorld();

            double dist = source.distanceTo(target);
            int numParticles = (int) (dist * 10);

            Vec3d offset = new Vec3d(0, 0, 0);
            for (int i = 0; i < numParticles; i++) {
                double completion = i / (double) numParticles;
                double phase = CMath.phaseQuartic(completion);

                offset = offset.scale(0.9).add(CMath.randVec(0.2));

                Vec3d lerp = CMath.lerp(source, target, completion);
                Vec3d p = lerp.add(offset.scale(phase));

                world.spawnParticle(
                        EnumParticleTypes.SMOKE_NORMAL,
                        p.x, p.y, p.z,
                        0, 0, 0
                );
            }

            Random rand = CMath.rand;
            Vec3d mid = CMath.lerp(source, target, 0.5);
            world.playSound(
                    mid.x, mid.y, mid.z,
                    SoundEvents.BLOCK_REDSTONE_TORCH_BURNOUT, SoundCategory.BLOCKS,
                    1.5F + 0.5F * rand.nextFloat(), rand.nextFloat() * 0.3F + 0.7F,
                    false
            );
        }
    }

    @Override
    public int conductance(World world, BlockPos pos, EnumFacing dir, boolean in, MagicType type) {
        if (!in) return dir == EnumFacing.DOWN ? 1 : 0;
        return 1;
    }
}
