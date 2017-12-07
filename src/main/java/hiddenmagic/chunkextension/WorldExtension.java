package hiddenmagic.chunkextension;

import hiddenmagic.ModHiddenMagic;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraftforge.event.world.ChunkDataEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.util.HashMap;

public abstract class WorldExtension<T> {

    // TODO: Store the actual chunk in the chunk extension so we can mark it dirty as needed.
    private class ChunkExtension extends HashMap<BlockPos, T> {}
    // TODO: Use dimension id in addition to chunk pos when fetching chunk extension.
    private HashMap<ChunkPos, ChunkExtension> chunks;

    public WorldExtension() {
        chunks = new HashMap<>();
    }

    private String extensionName() {
        return ModHiddenMagic.MODID + "_" + name();
    }

    @SubscribeEvent
    public void loadChunk(ChunkDataEvent.Load event) {
        NBTTagCompound nbt = event.getData();
        if (!nbt.hasKey(extensionName()))
            return;

        NBTTagList blockExtensions = nbt.getTagList(extensionName(), 10);

        System.out.println("Loading chunk extension: " + name());
        System.out.println(blockExtensions.toString());

        ChunkPos chunkPos = event.getChunk().getPos();
        ChunkExtension chunk = new ChunkExtension();
        chunks.put(chunkPos, chunk);

        blockExtensions.forEach((NBTBase nbtBase) -> {
            NBTTagCompound blockExtension = (NBTTagCompound)nbtBase;
            BlockPos pos = BlockPos.fromLong(blockExtension.getLong("p"));
            T value = fromNBT(blockExtension.getTag("d"));
            set(pos, value);

            System.out.println("Loading at: " + pos.toString());
            System.out.println("With long: " + String.valueOf(pos.toLong()));
            System.out.println("Value: " + String.valueOf(value));
        });
    }

    @SubscribeEvent
    public void saveChunk(ChunkDataEvent.Save event) {
        ChunkPos chunkPos = event.getChunk().getPos();
        ChunkExtension chunk = chunks.get(chunkPos);
        if (chunk == null)
            return;

        System.out.println("Saving chunk extension: " + name());

        NBTTagCompound nbt = event.getData();
        NBTTagList blockExtensions = new NBTTagList();

        chunk.forEach((BlockPos pos, T value) -> {
            NBTTagCompound blockExtension = new NBTTagCompound();
            blockExtension.setLong("p", pos.toLong());
            blockExtension.setTag("d", toNBT(get(pos)));
            blockExtensions.appendTag(blockExtension);

            System.out.println("Saving at: " + pos.toString());
            System.out.println("With long: " + String.valueOf(pos.toLong()));
            System.out.println("Value: " + String.valueOf(value));
        });
        nbt.setTag(extensionName(), blockExtensions);

        System.out.println(blockExtensions.toString());
    }

    public T get(BlockPos pos) {
        ChunkExtension chunk = chunks.getOrDefault(new ChunkPos(pos), null);
        if (chunk == null)
            return defaultValue();
        return chunk.getOrDefault(pos, defaultValue());
    }

    // TODO: Mark the chunk as dirty when a value changes so the chunk will be saved.
    public void set(BlockPos pos, T value) {
        ChunkPos chunkPos = new ChunkPos(pos);
        if (value != null && !chunks.containsKey(chunkPos))
            chunks.put(chunkPos, new ChunkExtension());
        ChunkExtension chunk = chunks.get(chunkPos);
        chunk.put(pos, value);
        if (value == null && chunk.size() == 0)
            chunks.remove(chunkPos);
    }

    public abstract String name();
    protected abstract NBTBase toNBT(T value);
    protected abstract T fromNBT(NBTBase nbt);
    protected abstract T defaultValue();
}
