package cc.catgasm.world;

import cc.catgasm.entity.Player;
import cc.catgasm.util.MathHelper;
import cc.catgasm.util.SimpleVector2;
import cc.catgasm.world.async.ChunkBackgroundGenerator;
import cc.catgasm.world.block.Block;
import cc.catgasm.world.chunk.Chunk;
import cc.catgasm.world.chunk.ChunkModel;
import cc.catgasm.world.generator.MapGenerator;
import com.badlogic.gdx.math.Vector3;

import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import static cc.catgasm.world.chunk.Chunk.CHUNK_SIZE;

@SuppressWarnings("unused")
public class World {
    private final HashMap<SimpleVector2, Chunk> loadedChunks;
    private final HashMap<Chunk, ChunkModel> chunkModels;
    private int viewDistance; //In chunks
    private Player player;
    private MapGenerator mapGenerator;
    private ChunkBackgroundGenerator cbg;
    private Lock chunkLock;
    private SimpleVector2 playerChunkPos;
    private Lock playerPosLock;

    private static World world;

    public World(int seed) {
        loadedChunks = new HashMap<>();
        chunkModels = new HashMap<>();
        chunkLock = new ReentrantLock();
        playerChunkPos = new SimpleVector2();
        playerPosLock = new ReentrantLock();
        mapGenerator = new MapGenerator(seed);
        viewDistance = 8;
        cbg = new ChunkBackgroundGenerator(loadedChunks, chunkLock, playerChunkPos, playerPosLock, viewDistance, mapGenerator);
        cbg.start();
    }

    public synchronized static World getInstance() {
        return world;
    }

    public static World init(int seed) {
        world = new World(seed);
        return world;
    }

    public MapGenerator getMapGenerator() {
        return mapGenerator;
    }

    public void setPlayer(Player player) {
        this.player = player;
    }

    private int getSeed() {
        return mapGenerator.getSeed();
    }

    private void putChunk(Chunk chunk) {
        chunkLock.lock();
        loadedChunks.put(new SimpleVector2(chunk.getX(), chunk.getZ()),chunk);
        chunkLock.unlock();

        ChunkModel cm = new ChunkModel(chunk);
        chunkModels.put(chunk,cm);
    }

    private final SimpleVector2 tmpPlayerPos = new SimpleVector2();

    public List<ChunkModel> getModels(){
        List<ChunkModel> chunks = new LinkedList<>();

        player.getChunkPosition(tmpPlayerPos);

        playerPosLock.lock();
        playerChunkPos.set(tmpPlayerPos);
        playerPosLock.unlock();

        chunkLock.lock();
        for (Chunk chunk : loadedChunks.values()) {
            if(!chunkModels.containsKey(chunk)) { //Wenn model nicht geladen, model erzeugen
                chunkModels.put(chunk,new ChunkModel(chunk));
            }
            ChunkModel tmpModel = chunkModels.get(chunk);

            //Wenn chunk in viewDistance
            if(Math.abs(tmpPlayerPos.x - chunk.getX()) <= viewDistance && Math.abs(tmpPlayerPos.y - chunk.getZ()) <= viewDistance)
                chunks.add(tmpModel);
        }
        chunkLock.unlock();
        return chunks;
    }

    public Chunk getChunkAt(int x, int z) {
        chunkLock.lock();
        Chunk c = loadedChunks.getOrDefault(new SimpleVector2(x,z), null);
        chunkLock.unlock();
        return c;
    }

    public Collection<ChunkModel> getLoadedChunkModels() {
        return chunkModels.values();
    }

    public Block getBlockAt(Vector3 pos) {
        return getBlockAt((int)pos.x, (int)pos.y, (int)pos.z);
    }

    /*Setzt den übergebenen Block b in die Welt
    * Wenn forceUpdate = true --> updated den chunk und liefert null zurück
    * Wenn forceUpdate = false --> liefert den geänderten Chunk zurück. Muss manuell geupdatet werden
    */
    public Chunk setBlockAt(Block b, boolean forceUpdate) {
        return setBlockAt(b,forceUpdate,false);
    }

    public Chunk setBlockAt(Block b, boolean forceUpdate, boolean delete) {
        int x = b.getPos().x, y = b.getPos().y, z = b.getPos().z;

        //Finde Chunk in dem der Block liegt
        int chunkX = Math.floorDiv(x,CHUNK_SIZE), chunkZ = Math.floorDiv(z,CHUNK_SIZE);
        Chunk c = getChunkAt(chunkX,chunkZ); //TODO Chunk generieren, wenn noch nicht existiert

        if(c != null) {
            //Hol den Block aus dem gefundenen Chunk
            if (b.getType() == -1 || delete) {
                c.setBlockAt(Math.floorMod(x, CHUNK_SIZE), y, Math.floorMod(z, CHUNK_SIZE), null);
            } else
                c.setBlockAt(Math.floorMod(x, CHUNK_SIZE), y, Math.floorMod(z, CHUNK_SIZE), b);

            //Update Chunk
            if(forceUpdate) {
                chunkModels.get(c).update(c);
            }
        }
        return c;
    }

    public void delBlock(Block b){
        setBlockAt(b,true,true);
    }

    public void setBlocks(List<Block> blocks){
        List<Chunk> chunksToUpdate = new LinkedList<>();
        for (Block block : blocks) {
            Chunk c = setBlockAt(block,false);

            if(c != null) {
                if(!chunksToUpdate.contains(c))
                    chunksToUpdate.add(c);
            }
        }

        /*Alle Chunks auf einmal updaten*/
        for (Chunk chunk : chunksToUpdate) {
            chunkModels.get(chunk).update(chunk);
        }
    }

    public Chunk getChunkAt(SimpleVector2 chunkPosition) {
        return getChunkAt(chunkPosition.x, chunkPosition.y);
    }

    public Block getBlockAt(float x, float y, float z) {
        //Finde Chunk in dem der Block liegt
        int chunkX = MathHelper.floatDiv(x,CHUNK_SIZE), chunkZ = MathHelper.floatDiv(z, CHUNK_SIZE);
        Chunk c = getChunkAt(chunkX,chunkZ);
        //System.out.printf("getting chunk at %d %d", chunkX, chunkZ);
        //System.out.println(" --> " + c);

        //Hol den Block aus dem gefundenen
        //Chunk Koordinate ist (x % CHUNK_SIZE), modulo bringt aber probleme --> floorMod
        if (c == null) return null;
        int localX = (int) MathHelper.floatMod(x, CHUNK_SIZE);
        int localY = (int) MathHelper.floatMod(z, CHUNK_SIZE);
        return c.getBlockAt(localX, (int) y, localY);
    }

    public Block getBlockRelativeTo(Vector3 pos, float x, float y, float z) {
        return getBlockAt(pos.x + x, pos.y + y, pos.z + z);
    }

    public void reset(int seed) {
        mapGenerator.setSeed(seed);
        chunkLock.lock();
        loadedChunks.clear();
        chunkLock.unlock();
        chunkModels.clear();
        cbg.reset();
    }

    public void cleanUp() {
        cbg.interrupt();
        for (ChunkModel chunk : getLoadedChunkModels()) {
            chunk.getChunkModelFull().model.dispose();
            if (chunk.getChunkModelTransparent() != null) {
                chunk.getChunkModelTransparent().model.dispose();
            }
        }
    }
}
