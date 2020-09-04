package cc.catgasm.render;

import cc.catgasm.world.GameObject;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.math.Vector3;

import java.util.List;

public abstract class Renderer<T> {
    protected final Camera camera;

    public Renderer(Camera camera) {
        this.camera = camera;
    }

    /*Gibt anzahl der gerenderten Objekte zurück*/
    public abstract int render(T targets);

    public abstract void cleanup();

    private final Vector3 camPosition = new Vector3();

    protected boolean isVisible(final GameObject instance) {
        instance.transform.getTranslation(camPosition);
        camPosition.add(instance.center);
        return camera.frustum.sphereInFrustum(camPosition, instance.radius);
    }
}
