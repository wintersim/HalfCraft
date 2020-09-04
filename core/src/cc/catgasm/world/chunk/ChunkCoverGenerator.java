package cc.catgasm.world.chunk;

import cc.catgasm.texture.TextureAtlas;
import cc.catgasm.world.World;
import cc.catgasm.world.block.Block;
import cc.catgasm.world.block.Blocks;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Mesh;
import com.badlogic.gdx.graphics.VertexAttributes;
import com.badlogic.gdx.graphics.g3d.utils.MeshBuilder;
import com.badlogic.gdx.graphics.g3d.utils.MeshPartBuilder;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import static cc.catgasm.world.chunk.Chunk.CHUNK_HEIGHT;
import static cc.catgasm.world.chunk.Chunk.CHUNK_SIZE;

public class ChunkCoverGenerator {

    private static final MeshBuilder meshBuilder = new MeshBuilder();

    private final MeshPartBuilder.VertexInfo v1 = new MeshPartBuilder.VertexInfo();
    private final MeshPartBuilder.VertexInfo v2 = new MeshPartBuilder.VertexInfo();
    private final MeshPartBuilder.VertexInfo v3 = new MeshPartBuilder.VertexInfo();
    private final MeshPartBuilder.VertexInfo v4 = new MeshPartBuilder.VertexInfo();
    private final TextureAtlas ta = new TextureAtlas(4, 4);


    public Mesh getChunkMesh(Chunk c, boolean isTransparentMode) {
        meshBuilder.begin(VertexAttributes.Usage.Position
                | VertexAttributes.Usage.Normal
                | VertexAttributes.Usage.TextureCoordinates, GL20.GL_TRIANGLES);


        Map<Block, List<Integer>> blocknface;

        if(!isTransparentMode)
            blocknface = getFullBlocksWithTransparentFaces(c);
        else
            blocknface = getTransparentBlocksWithFullFaces(c);

        if(blocknface.size() == 0) {
            meshBuilder.end();
            return null;
        }

        for (Map.Entry<Block, List<Integer>> entry : blocknface.entrySet()) {
            int x = entry.getKey().getPos().x;
            int y = entry.getKey().getPos().y;
            int z = entry.getKey().getPos().z;

            float[][] uvs;


            //In Chunk-Koordinaten konvertieren
            x = Math.floorMod(x, CHUNK_SIZE);
            z = Math.floorMod(z, CHUNK_SIZE);

            int btype = entry.getKey().getType();


            for(Integer face : entry.getValue()) {
                float[][] vert = Blocks.getBlockFaceVertices(face);
                float[] norms = Blocks.getBlockFaceNormals(face);

                uvs = ta.getUVCoordinate(btype);

                if(face == Blocks.Orientation.UP) {
                    if(Blocks.hasUpTexture(btype)) {
                        uvs = ta.getUVCoordinate(btype + 1);
                    }

                    if(btype == Blocks.WATER) {
                        vert = Blocks.getLiquidUpVertices();
                    }
                }

                if(face == Blocks.Orientation.DOWN) {
                    if(Blocks.hasDownTexture(btype)) {
                        uvs = ta.getUVCoordinate(btype + 2);
                    } else if(Blocks.hasUpTexture(btype)) {
                        uvs = ta.getUVCoordinate(btype + 1);
                    }
                }

                v1.setPos(vert[0][0] + x, vert[0][1] + y, vert[0][2] + z)
                        .setNor(norms[0], norms[1], norms[2])
                        .setUV(uvs[0][0], uvs[0][1]);

                v2.setPos(vert[1][0] + x, vert[1][1] + y, vert[1][2] + z)
                        .setNor(norms[0], norms[1], norms[2])
                        .setUV(uvs[1][0], uvs[1][1]);

                v3.setPos(vert[2][0] + x, vert[2][1] + y, vert[2][2] + z)
                        .setNor(norms[0], norms[1], norms[2])
                        .setUV(uvs[2][0], uvs[2][1]);

                v4.setPos(vert[3][0] + x, vert[3][1] + y, vert[3][2] + z)
                        .setNor(norms[0], norms[1], norms[2])
                        .setUV(uvs[3][0], uvs[3][1]);

                meshBuilder.rect(v1, v2, v3, v4);
            }
        }

        return meshBuilder.end();
    }

    /*
     * Hole alle festen Blöcke die Transparente Blöcke berühren
     */
    private Map<Block, List<Integer>> getFullBlocksWithTransparentFaces(Chunk c) {
        //3300 --> Durchschnittliche Blockanzahl in generierten Chunk (weniger Resize operations)
        Map<Block, List<Integer>> ret = new HashMap<>(3300);
        LinkedList<Integer> faces;
        World w = World.getInstance();

        Block[][][] blocks = c.getBlocks();
        int x = c.getX();
        int z = c.getZ();

        for (int i = 0; i < CHUNK_SIZE; i++) {
            for (int j = 0; j < CHUNK_HEIGHT; j++) {
                for (int k = 0; k < CHUNK_SIZE; k++) {
                    if(blocks[i][j][k] == null) //Skip air Blocks
                        continue;
                    if(Blocks.isTransparent(blocks[i][j][k].getType()))
                        continue;

                    faces = new LinkedList<>();

                    //Code = 🤮🤮🤮🤮🤮🤮, aber geht

                    //Up
                    if(j + 1 >= CHUNK_HEIGHT)
                        faces.add(Blocks.Orientation.UP);
                    else if(isTransparent(blocks[i][j + 1][k]))
                        faces.add(Blocks.Orientation.UP);

                    //Down
                    if(j - 1 < 0)
                        faces.add(Blocks.Orientation.DOWN);
                    else if(isTransparent(blocks[i][j - 1][k]))
                        faces.add(Blocks.Orientation.DOWN);

                    //South
                    if(k + 1 >= CHUNK_SIZE) { //Wenn Koordinate nicht mehr im Chunk liegt, Nachbar Chunk checken
                        if(w.getChunkAt(x, z + 1) != null) { //Nachbar Chunk existiern
                            if(isTransparent(w.getChunkAt(x, z + 1).getBlockAt(i,j,0))) //Block aus Chunk holen
                                faces.add(Blocks.Orientation.SOUTH);
                        } else //Kein Nachbar Chunk vorhanden --> Außenkante der Welt
                            faces.add(Blocks.Orientation.SOUTH);
                    } else if(isTransparent(blocks[i][j][k + 1]))
                        faces.add(Blocks.Orientation.SOUTH);

                    //North
                    if(k - 1 < 0) {
                        if (w.getChunkAt(x, z - 1) != null) {
                            if (isTransparent(w.getChunkAt(x, z - 1).getBlockAt(i, j, CHUNK_SIZE - 1)))
                                faces.add(Blocks.Orientation.NORTH);
                        } else
                            faces.add(Blocks.Orientation.NORTH);
                    } else if(isTransparent(blocks[i][j][k - 1]))
                        faces.add(Blocks.Orientation.NORTH);

                    //East
                    if(i + 1 >= CHUNK_SIZE) {
                        if (w.getChunkAt(x + 1, z) != null) {
                            if (isTransparent(w.getChunkAt(x + 1, z).getBlockAt(0, j, k)))
                                faces.add(Blocks.Orientation.EAST);
                        } else
                            faces.add(Blocks.Orientation.EAST);
                    }else if(isTransparent(blocks[i + 1][j][k]))
                        faces.add(Blocks.Orientation.EAST);

                    //West
                    if(i - 1 < 0) {
                        if (w.getChunkAt(x - 1, z) != null) {
                            if (isTransparent(w.getChunkAt(x - 1, z).getBlockAt(CHUNK_SIZE - 1, j, k)))
                                faces.add(Blocks.Orientation.WEST);
                        } else
                            faces.add(Blocks.Orientation.WEST);
                    }else if(isTransparent(blocks[i - 1][j][k]))
                        faces.add(Blocks.Orientation.WEST);

                    if(!faces.isEmpty()) {
                        ret.put(blocks[i][j][k], faces);
                    }
                }
            }
        }

        return ret;
    }

    /*
     * Hole alle transparente Blöcke die feste Blöcke berühren
     */
    private Map<Block, List<Integer>> getTransparentBlocksWithFullFaces(Chunk c) {
        //3300 --> Durchschnittliche Blockanzahl in generierten Chunk (weniger Resize operations)
        Map<Block, List<Integer>> ret = new HashMap<>(1000);
        LinkedList<Integer> faces;
        World w = World.getInstance();

        Block[][][] blocks = c.getBlocks();
        int x = c.getX();
        int z = c.getZ();


        for (int i = 0; i < CHUNK_SIZE; i++) {
            for (int j = 0; j < CHUNK_HEIGHT; j++) {
                for (int k = 0; k < CHUNK_SIZE; k++) {
                    if(blocks[i][j][k] == null) //Skip air Blocks
                        continue;
                    if(!Blocks.isTransparent(blocks[i][j][k].getType())) //Derzeit nur Wasser transparent
                        continue;

                    faces = new LinkedList<>();

                    //Up
                    if(j + 1 >= CHUNK_HEIGHT)
                        faces.add(Blocks.Orientation.UP);
                    else if(isFullOrAir(blocks[i][j + 1][k]))
                        faces.add(Blocks.Orientation.UP);

                    //Down
                    if(j - 1 < 0)
                        faces.add(Blocks.Orientation.DOWN);
                    else if(isFullOrAir(blocks[i][j - 1][k]))
                        faces.add(Blocks.Orientation.DOWN);

                    //South
                    if(k + 1 >= CHUNK_SIZE) { //Wenn Koordinate nicht mehr im Chunk liegt, Nachbar Chunk checken
                        if(w.getChunkAt(x, z + 1) != null) { //Nachbar Chunk existiern
                            if(isFullOrAir(w.getChunkAt(x, z + 1).getBlockAt(i,j,0))) //Block aus Chunk holen
                                faces.add(Blocks.Orientation.SOUTH);
                        }
                    } else if(isFullOrAir(blocks[i][j][k + 1]))
                        faces.add(Blocks.Orientation.SOUTH);

                    //North
                    if(k - 1 < 0) {
                        if (w.getChunkAt(x, z - 1) != null) {
                            if (isFullOrAir(w.getChunkAt(x, z - 1).getBlockAt(i, j, CHUNK_SIZE - 1)))
                                faces.add(Blocks.Orientation.NORTH);
                        }
                    } else if(isFullOrAir(blocks[i][j][k - 1]))
                        faces.add(Blocks.Orientation.NORTH);

                    //East
                    if(i + 1 >= CHUNK_SIZE) {
                        if (w.getChunkAt(x + 1, z) != null) {
                            if (isFullOrAir(w.getChunkAt(x + 1, z).getBlockAt(0, j, k)))
                                faces.add(Blocks.Orientation.EAST);
                        }
                    }else if(isFullOrAir(blocks[i + 1][j][k]))
                        faces.add(Blocks.Orientation.EAST);

                    //West
                    if(i - 1 < 0) {
                        if (w.getChunkAt(x - 1, z) != null) {
                            if (isFullOrAir(w.getChunkAt(x - 1, z).getBlockAt(CHUNK_SIZE - 1, j, k)))
                                faces.add(Blocks.Orientation.WEST);
                        }
                    }else if(isFullOrAir(blocks[i - 1][j][k]))
                        faces.add(Blocks.Orientation.WEST);

                    if(!faces.isEmpty()) {
                        ret.put(blocks[i][j][k], faces);
                    }
                }
            }
        }

        return ret;
    }

    private boolean isTransparent(Block b){
        return b == null || Blocks.isTransparent(b.getType());
    }

    private boolean isFullOrAir(Block b){
        if(b == null)
            return true;
        else return !Blocks.isTransparent(b.getType());
    }
}
