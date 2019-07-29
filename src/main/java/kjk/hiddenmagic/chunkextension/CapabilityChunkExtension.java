package kjk.hiddenmagic.chunkextension;


import net.minecraftforge.common.capabilities.ICapabilitySerializable;

public class CapabilityChunkExtension implements ICapabilityChunkExtension {

    public ChunkExtension chunkExtension;

//    public CapabilityChunkExtension() {
//        System.out.println("new ChunkExtensionCapability!!!");
//    }

    @Override
    public ChunkExtension get() {
        if (chunkExtension == null)
            this.chunkExtension = new ChunkExtension();
        return chunkExtension;
    }

    @Override
    public void set(ChunkExtension value) {
        chunkExtension = value;
    }
}
