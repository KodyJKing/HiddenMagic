package kjk.hiddenmagic.chunkextension;

import kjk.hiddenmagic.common.Common;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagString;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.capabilities.CapabilityManager;

public class ChunkExtension {

    @CapabilityInject(ICapabilityChunkExtension.class)
    public static Capability<ICapabilityChunkExtension> capability;

    public static void initialize() {
        CapabilityManager.INSTANCE.register(
            ICapabilityChunkExtension.class,
            new Capability.IStorage<ICapabilityChunkExtension>() {

                public NBTBase writeNBT(Capability<ICapabilityChunkExtension> capability, ICapabilityChunkExtension instance, EnumFacing side) {
                    System.out.println("Saving stuff!");
                    return instance.get().writeNBT();
                }

                public void readNBT(Capability<ICapabilityChunkExtension> capability, ICapabilityChunkExtension instance, EnumFacing side, NBTBase nbt) {
                    instance.set(ChunkExtension.readNBT(nbt));
                }

            },
            CapabilityChunkExtension::new
        );
    }

    public NBTBase writeNBT() {
        return new NBTTagString(Common.gson.toJson(this));
    }

    public static ChunkExtension readNBT(NBTBase nbt) {
        NBTTagString str = (NBTTagString) nbt;
        if (str == null)
            throw new Error("Expected string tag.");
        return Common.gson.fromJson(str.getString(), ChunkExtension.class);
    }

    public int foo;

}
