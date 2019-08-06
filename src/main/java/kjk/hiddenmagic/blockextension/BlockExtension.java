package kjk.hiddenmagic.blockextension;

import kjk.hiddenmagic.HiddenMagic;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.world.ChunkDataEvent;
import net.minecraftforge.event.world.ChunkEvent;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.util.HashMap;
import java.util.Set;

public abstract class BlockExtension<T> {

    // Interface

    public String name;
    protected abstract NBTBase toNBT(T value);
    protected abstract T fromNBT(NBTBase nbt);
    protected abstract T defaultValue();

    // Representation / access

    private HashMap<Integer, WorldExtension<T>> worldExtensions = new HashMap<>();

    private String extensionName() {
        return HiddenMagic.MODID + "_" + name;
    }

    public WorldExtension<T> getWorldExtension(World world) {
        int dimension = world.provider.getDimension();
        WorldExtension<T> we = worldExtensions.get(dimension);
        if (we == null) {
            we = new WorldExtension<T>(this, world);
            worldExtensions.put(dimension, we);
        }
        return we;
    }

    public void clear() {
        worldExtensions.clear();
    }

    public Set<BlockPos> getKeys(World world, ChunkPos pos) {
        WorldExtension we = getWorldExtension(world);
        return we.getKeys(pos);
    }

    public T get(World world, BlockPos pos) {
        return getWorldExtension(world).get(pos);
    }

    public void set(World world, BlockPos pos, T value) { getWorldExtension(world).set(pos, value); }

    public BlockExtension(String name) {
        this.name = name;
        MinecraftForge.EVENT_BUS.register(this);
    }

    // Persistence

    @SubscribeEvent
    public void loadChunk(ChunkDataEvent.Load event) {
        NBTTagCompound nbt = event.getData();
        if (!nbt.hasKey(extensionName()))
            return;

        NBTTagList blockExtensions = nbt.getTagList(extensionName(), 10);

//        System.out.println("Loading chunk extension: " + name);
//        System.out.println(blockExtensions.toString());

        WorldExtension<T> worldExtension = getWorldExtension(event.getWorld());

        blockExtensions.forEach((NBTBase nbtBase) -> {
            NBTTagCompound blockExtension = (NBTTagCompound)nbtBase;
            BlockPos pos = BlockPos.fromLong(blockExtension.getLong("p"));
            T value = fromNBT(blockExtension.getTag("d"));
            worldExtension.set(pos, value, true);

//            System.out.println("Loading at: " + pos.toString());
//            System.out.println("Value: " + String.valueOf(value));
        });
    }

    @SubscribeEvent
    public void saveChunk(ChunkDataEvent.Save event) {
        WorldExtension<T> worldExtension = getWorldExtension(event.getWorld());
        ChunkExtension<T> chunk = worldExtension.getChunkExtension(event.getChunk().getPos(), false);
        if (chunk == null)
            return;

//        System.out.println("Saving chunk extension: " + name);

        NBTTagCompound nbt = event.getData();
        NBTTagList blockExtensions = new NBTTagList();

        chunk.forEach((BlockPos pos, T value) -> {
            NBTTagCompound blockExtension = new NBTTagCompound();
            blockExtension.setLong("p", pos.toLong());
            blockExtension.setTag("d", toNBT(worldExtension.get(pos)));
            blockExtensions.appendTag(blockExtension);

//            System.out.println("Saving at: " + pos.toString());
//            System.out.println("Value: " + String.valueOf(value));
        });
        nbt.setTag(extensionName(), blockExtensions);

//        System.out.println(blockExtensions.toString());
    }

    @SubscribeEvent
    public void unloadChunk(ChunkEvent.Unload event) {
        WorldExtension<T> worldExtension = getWorldExtension(event.getWorld());
        worldExtension.removeChunk(event.getChunk().getPos());
    }

    @SubscribeEvent
    public void unloadWorld(WorldEvent.Unload event) {
        WorldExtension<T> we = getWorldExtension(event.getWorld());
        we.clear();
    }
}
