package cc.catgasm.render;

import cc.catgasm.world.GameObject;
import cc.catgasm.world.sky.Skybox;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.ModelInstance;

import java.util.List;

//TODO
public class SkyboxRenderer extends Renderer<GameObject> {
    private ModelBatch modelBatch;

    public SkyboxRenderer(Camera camera, ModelBatch modelBatch) {
        super(camera);
        this.modelBatch = modelBatch;
    }

    @Override
    public int render(GameObject target) {
        ModelInstance m = target.copy();
        m.transform.translate(0,100,0);
        modelBatch.begin(camera);
        modelBatch.render(m);
        modelBatch.end();
        return 1;
    }

    @Override
    public void cleanup() {
        modelBatch.dispose();
    }
}
