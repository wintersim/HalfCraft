package cc.catgasm.world.sky;

import cc.catgasm.entity.Player;
import cc.catgasm.world.GameObject;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.VertexAttributes;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder;
import com.badlogic.gdx.math.Vector3;


//TODO
public class Skybox {
    private GameObject skybox;

    public Skybox() {
        ModelBuilder mb = new ModelBuilder();
        skybox = new GameObject(mb.createBox(100,100,100,
                new Material(ColorAttribute.createDiffuse(Color.BROWN)),
                VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal));
    }

    private Vector3 vector3 = new Vector3();
    public void update(Player player) {
        skybox.transform.setToTranslation(player.getX(),player.getY() - 100 ,player.getZ());
        skybox.transform.getTranslation(vector3);
    }

    public GameObject getSkybox() {
        return skybox;
    }
}
