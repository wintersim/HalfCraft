package cc.catgasm.world;

import cc.catgasm.config.RuntimeConfig;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.Renderable;
import com.badlogic.gdx.graphics.g3d.model.Node;
import com.badlogic.gdx.graphics.g3d.model.NodePart;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.BoundingBox;

public class GameObject extends ModelInstance {

    private final static BoundingBox bounds = new BoundingBox();
    public final Vector3 center = new Vector3();
    public final Vector3 dimensions = new Vector3();
    public float radius = 0;

    public GameObject(Model model) {
        super(model);
        calculateBoundingBox(bounds);
        bounds.getCenter(center);
        bounds.getDimensions(dimensions);
        radius = dimensions.len() / 2;
    }

    @Override
    public Renderable getRenderable(Renderable out, Node node, NodePart nodePart) {
        super.getRenderable(out, node, nodePart);
        out.meshPart.primitiveType = RuntimeConfig.RENDER_MODES[RuntimeConfig.RENDER_MODE_SWITCH];
        return out;
    }
}