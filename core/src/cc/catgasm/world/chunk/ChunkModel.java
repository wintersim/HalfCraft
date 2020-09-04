package cc.catgasm.world.chunk;

import cc.catgasm.world.GameObject;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Mesh;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.attributes.BlendingAttribute;
import com.badlogic.gdx.graphics.g3d.attributes.TextureAttribute;
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder;
import com.badlogic.gdx.math.Vector2;

import static cc.catgasm.world.chunk.Chunk.CHUNK_SIZE;

public class ChunkModel {
    private ChunkCoverGenerator ccg;
    private GameObject chunkModelFull;
    private GameObject chunkModelTransparent;
    private int x;
    private int z;

    private static final Texture t = new Texture(Gdx.files.internal("atlas.png"));

    public ChunkModel(Chunk c) {
        ccg = new ChunkCoverGenerator();
        chunkModelFull = generateChunkModel(ccg.getChunkMesh(c,false),false);
        chunkModelTransparent = generateChunkModel(ccg.getChunkMesh(c,true),true);

        x = c.getX();
        z = c.getZ();

        translate(c.getX(),c.getZ());
    }

    /*
     * Kombiniert alle sichtbares faces der Blöcke zu einem großen Model
     */
    private GameObject generateChunkModel(Mesh mesh, boolean isTransparentMode) {
        ModelBuilder mb = new ModelBuilder();
        mb.begin();

        GameObject go = null;

        if(mesh != null) {
            Material material = new Material(TextureAttribute.createDiffuse(t));

            if (isTransparentMode) {
                material.set(new BlendingAttribute(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA));
            }

            mb.part(isTransparentMode ? "chunk-trans" : "chunk-full", mesh,
                    GL20.GL_TRIANGLES, material);

            go = new GameObject(mb.end());
        }

        return go;
    }

    private void translate(float x, float z) {
        if(chunkModelTransparent != null)
            chunkModelTransparent.transform.translate(x * CHUNK_SIZE,0,z * CHUNK_SIZE);
        chunkModelFull.transform.translate(x * CHUNK_SIZE,0,z * CHUNK_SIZE);
    }

    public GameObject getChunkModelFull() {
        return chunkModelFull;
    }

    public GameObject getChunkModelTransparent() {
        return chunkModelTransparent;
    }

    public Vector2 getTranslation(Vector2 pos) {
        pos.x = x;
        pos.y = z;
        return pos;
    }

    public void update(Chunk c) {
        chunkModelFull = generateChunkModel(ccg.getChunkMesh(c,false),false);
        chunkModelTransparent = generateChunkModel(ccg.getChunkMesh(c,true),true);

        x = c.getX();
        z = c.getZ();

        translate(c.getX(),c.getZ());
    }
}
