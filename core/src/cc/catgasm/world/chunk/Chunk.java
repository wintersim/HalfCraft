package cc.catgasm.world.chunk;

import cc.catgasm.util.SimpleVector2;
import cc.catgasm.world.block.Block;
import cc.catgasm.world.generator.MapGenerator;
import com.badlogic.gdx.graphics.Mesh;

import java.util.LinkedList;
import java.util.List;

@SuppressWarnings("unused")
public class Chunk {
    private Block[][][] blocks;
    private MapGenerator mg;
    private final int x;
    private final int z;
    private Mesh chunkMeshOpaque;
    private Mesh chunkMeshTransparent;

    private final List<Block> blockList;
    private boolean blockListNeedsUpdate;

    private boolean opaqueMeshNeedsUpdate;
    private boolean transparentMeshNeedsUpdate;

    public static final int CHUNK_SIZE = 16;
    public static final int CHUNK_HEIGHT = 128;

    public Chunk(MapGenerator mg, int x, int z) {
        blocks = new Block[CHUNK_SIZE][CHUNK_HEIGHT][CHUNK_SIZE];
        blockList = new LinkedList<>();
        blockListNeedsUpdate = true;
        this.mg = mg;
        this.x = x;
        this.z = z;
        //long start = TimeUtils.millis();
        generateBlocks();
        //long end = TimeUtils.millis();
        //float secs = (end - start);
        //System.out.println("Generating Chunk " + this + " took: " + secs + " ms");
    }

    private void generateBlocks() {
        blocks = mg.generateChunk(x, z);
        blockListNeedsUpdate = true;
    }

    public Block[][][] getBlocks() {
        return blocks;
    }

    public int getX() {
        return x;
    }

    public int getZ() {
        return z;
    }

    public Block getBlockAt(int x, int y, int z) {
        if(x < 0 || y < 0 || z < 0 || x >= CHUNK_SIZE || y >= CHUNK_HEIGHT || z >= CHUNK_SIZE)
            return null;
        return blocks[x][y][z];
    }

    public void setBlockAt(int x, int y, int z, Block b) {
        if(!(x < 0 || y < 0 || z < 0 || x > CHUNK_SIZE || y > CHUNK_HEIGHT || z > CHUNK_SIZE))
            blocks[x][y][z] = b;

        blockListNeedsUpdate = true;
        if(b == null) {
            transparentMeshNeedsUpdate = true;
            opaqueMeshNeedsUpdate = true;
        } else {
            if (b.isTransparent())
                transparentMeshNeedsUpdate = true;
            else
                opaqueMeshNeedsUpdate = true;
        }
    }

    public SimpleVector2 getBlockPosition(){
        return new SimpleVector2(x * CHUNK_SIZE, z * CHUNK_SIZE);
    }

    /*
     * Gibt alle Blöcke im Chunk zurück, die nicht Luft sind
     */
    public List<Block> getBlocksArray() {
        if(blockListNeedsUpdate)
            updateBlockList();
        return blockList;
    }

    private void updateBlockList(){
        blockList.clear();
        for (int i = 0; i < CHUNK_SIZE; i++) {
            for (int j = 0; j < CHUNK_HEIGHT; j++) {
                for (int k = 0; k < CHUNK_SIZE; k++) {
                    if (blocks[i][j][k] != null && blocks[i][j][k].getType() != 0) {
                        blockList.add(blocks[i][j][k]);
                    }
                }
            }
        }
        blockListNeedsUpdate = false;
    }

    public Mesh getChunkMeshOpaque() {
        return chunkMeshOpaque;
    }

    public Mesh getChunkMeshTransparent() {
        return chunkMeshTransparent;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Chunk)) return false;

        Chunk chunk = (Chunk) o;

        if (x != chunk.x) return false;
        return z == chunk.z;
    }

    @Override
    public int hashCode() {
        int result = x;
        result = 31 * result + z;
        return result;
    }

    @Override
    public String toString() {
        return "Chunk{" +
                "x=" + x +
                ", z=" + z +
                '}';
    }
}
