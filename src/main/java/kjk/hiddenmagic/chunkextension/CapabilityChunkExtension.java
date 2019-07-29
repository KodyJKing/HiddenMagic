package kjk.hiddenmagic.chunkextension;


public class CapabilityChunkExtension implements ICapabilityChunkExtension {

    public ChunkExtension chunkExtension;

    @Override
    public ChunkExtension get() {
        return chunkExtension;
    }

    @Override
    public void set(ChunkExtension value) {
        chunkExtension = value;
    }
}
