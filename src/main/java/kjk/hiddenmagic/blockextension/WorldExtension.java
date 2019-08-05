package kjk.hiddenmagic.blockextension;

import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

public class WorldExtension<T> {
    private BlockExtension<T> blockExtension;
    private World world;
    private HashMap<ChunkPos, ChunkExtension<T>> chunkExtensions = new HashMap<>();

    public WorldExtension(BlockExtension<T> blockExtension, World world) {
        this.blockExtension = blockExtension;
        this.world = world;
    }

    public ChunkExtension<T> getChunkExtension(ChunkPos pos, boolean create) {
        ChunkExtension<T> ce = chunkExtensions.get(pos);
        if (create && ce == null) {
            ce = new ChunkExtension<T>();
            chunkExtensions.put(pos, ce);
        }
        return ce;
    }

    public void clear() {
        chunkExtensions.clear();
    }

    public void removeChunk(ChunkPos pos) {
        chunkExtensions.remove(pos);
    }

    public T get(BlockPos pos) {
        ChunkPos chunkPos = new ChunkPos(pos);
        ChunkExtension<T> chunkExtension = getChunkExtension(chunkPos, false);
        if (chunkExtension == null)
            return blockExtension.defaultValue();
        return chunkExtension.getOrDefault(pos, blockExtension.defaultValue());
    }

    public void set(BlockPos pos, T value) {
        set(pos, value, false);
    }

    public void set(BlockPos pos, T value, boolean loading) {
        ChunkPos chunkPos = new ChunkPos(pos);
        ChunkExtension<T> chunkExtension = getChunkExtension(chunkPos, true);

        if (!loading) {
            Chunk chunk = world.getChunkFromChunkCoords(chunkPos.x, chunkPos.z);
            chunk.markDirty();
        }

        boolean remove = blockExtension.defaultValue().equals(value);
        if (remove) {
            chunkExtension.remove(pos);
            if (chunkExtension.size() == 0)
                chunkExtensions.remove(chunkPos);
        } else {
            chunkExtension.put(pos, value);
        }
    }

    public Set<BlockPos> getKeys(ChunkPos pos) {
        ChunkExtension ce = chunkExtensions.get(pos);
        if (ce == null)
            return new HashSet<>();
        return ce.keySet();
    }
}
