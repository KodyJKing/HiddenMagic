package kjk.hiddenmagic.blockextension;

import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;

import java.util.HashMap;

public class WorldExtension<T> {
    private BlockExtension<T> blockExtension;
    private HashMap<ChunkPos, ChunkExtension<T>> chunkExtensions = new HashMap<>();

    public WorldExtension(BlockExtension<T> blockExtension) {
        this.blockExtension = blockExtension;
    }

    public ChunkExtension<T> getChunkExtension(ChunkPos pos, boolean create) {
        ChunkExtension<T> ce = chunkExtensions.get(pos);
        if (create && ce == null) {
            ce = new ChunkExtension<T>();
            chunkExtensions.put(pos, new ChunkExtension<T>());
        }
        return ce;
    }

    public T get(BlockPos pos) {
        ChunkPos chunkPos = new ChunkPos(pos);
        ChunkExtension<T> chunk = getChunkExtension(chunkPos, false);
        if (chunk == null)
            return blockExtension.defaultValue();
        return chunk.getOrDefault(pos, blockExtension.defaultValue());
    }

    public void set(BlockPos pos, T value) {
        ChunkPos chunkPos = new ChunkPos(pos);
        ChunkExtension<T> chunk = getChunkExtension(chunkPos, true);
        chunk.put(pos, value);
        if (value == null && chunk.size() == 0)
            chunkExtensions.remove(chunkPos);
    }
}
