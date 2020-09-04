package cc.catgasm.world.async;

import cc.catgasm.util.SimpleVector2;
import cc.catgasm.world.chunk.Chunk;
import cc.catgasm.world.generator.MapGenerator;

import java.util.HashMap;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class ChunkBackgroundGenerator extends Thread {

    private HashMap<SimpleVector2, Chunk> loadedChunks;
    private final Lock chunkLock;
    private final SimpleVector2 playerChunkPos;
    private final Lock playerPosLock;
    private final Lock resetLock;
    private boolean doReset;
    private final int viewDistance;
    private final MapGenerator mg;

    private SimpleVector2 lastPos;


    public ChunkBackgroundGenerator(HashMap<SimpleVector2, Chunk> loadedChunks, Lock chunkLock, SimpleVector2 playerChunkPos, Lock playerPosLock, int viewDistance, MapGenerator mg) {
        this.loadedChunks = loadedChunks;
        this.chunkLock = chunkLock;
        this.playerChunkPos = playerChunkPos;
        this.playerPosLock = playerPosLock;
        this.viewDistance = viewDistance;
        resetLock = new ReentrantLock();
        doReset = false;
        this.mg = mg;
        lastPos = new SimpleVector2();
    }

    private final SimpleVector2 tmp = new SimpleVector2();

    @Override
    public void run() {
        genStartingChunks();
        while (!isInterrupted()) {
            //Position holen
            playerPosLock.lock();
            int x = playerChunkPos.x;
            int z = playerChunkPos.y;
            playerPosLock.unlock();

            //Wenn koordinate sich nicht geändert hat hat es keinen sinn die welt zu generieren
            resetLock.lock();
            boolean tmpReset = doReset;
            resetLock.unlock();
            if(!(lastPos.x == x && lastPos.y == z) || tmpReset) {
                resetLock.lock();
                doReset = false;
                resetLock.unlock();

                //Chunks rund um Spieler generieren
                genChunksAround(x, z);
            }

            lastPos.x = x;
            lastPos.y = z;
        }
    }


    private void genChunksAround(int x, int z) {
        int start = -1 * viewDistance;
        int end = viewDistance;

        for (int i = start + x; i < end + x; i++) {
            for (int j = start + z; j < end + z; j++) {
                tmp.x = i;
                tmp.y = j;
                chunkLock.lock();
                boolean existsNot = loadedChunks.getOrDefault(tmp, null) == null;
                chunkLock.unlock();

                if (existsNot) { //Nicht geladen
                    Chunk c = new Chunk(mg, i, j);
                    chunkLock.lock();
                    loadedChunks.put(new SimpleVector2(i, j), c);
                    chunkLock.unlock();
                }
            }
        }
    }

    public void genStartingChunks() {
        int sz = 3;
        int szn = -3;
        chunkLock.lock();
        for (int i = szn; i < sz; i++) {
            for (int j = szn; j < sz; j++) {
                loadedChunks.put(new SimpleVector2(i,j), new Chunk(mg, i,j));
            }
        }
        chunkLock.unlock();
    }

    public void reset() {
        resetLock.lock();
        doReset = true;
        resetLock.unlock();
    }
}
