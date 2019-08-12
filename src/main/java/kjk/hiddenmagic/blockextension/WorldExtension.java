package kjk.hiddenmagic.blockextension;

import kjk.hiddenmagic.common.DefaultMap;
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
    protected DefaultMap<ChunkPos, ChunkExtension<T>> chunkExtensions = new DefaultMap<>(ChunkExtension::new);

    public WorldExtension(BlockExtension<T> blockExtension, World world) {
        this.blockExtension = blockExtension;
        this.world = world;
    }

    public void clear() {
        chunkExtensions.clear();
    }

    public void removeChunk(ChunkPos pos) {
        chunkExtensions.remove(pos);
    }

    public T get(BlockPos pos) {
        ChunkPos chunkPos = new ChunkPos(pos);
        ChunkExtension<T> chunkExtension = chunkExtensions.get(chunkPos);
        if (chunkExtension == null)
            return blockExtension.defaultValue();
        return chunkExtension.getOrDefault(pos, blockExtension.defaultValue());
    }

    public void set(BlockPos pos, T value) { set(pos, value, false); }

    public void set(BlockPos pos, T value, boolean loading) {
        ChunkPos chunkPos = new ChunkPos(pos);
        ChunkExtension<T> chunkExtension = chunkExtensions.getOrCreate(chunkPos);

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
