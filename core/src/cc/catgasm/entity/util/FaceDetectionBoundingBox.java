package cc.catgasm.entity.util;

import cc.catgasm.entity.Player;
import cc.catgasm.world.block.Block;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.Ray;

import java.util.ArrayList;
import java.util.List;

public class FaceDetectionBoundingBox {
    private final Player player;
    private final List<Vector3> centers;
    private final List<Vector3> dimens;


    public FaceDetectionBoundingBox(Player player) {
        this.player = player;
        centers = new ArrayList<>(6);
        dimens = new ArrayList<>(6);
        setupVectors();
    }

    private void setupVectors() {
        //Anordnung analog zu Blocks.Orientation.FACES
        //Erstellt mittels visualisierung von: https://technology.cpm.org/general/3dgraph/
        //Könnte man evtl. auch ohne Hardcoding lösen, bin aber zu faul
        centers.add(new Vector3(0.5f,0.5f ,0 ));
        centers.add(new Vector3(0.5f,0.5f ,1f ));
        centers.add(new Vector3(1f,0.5f ,0.5f ));
        centers.add(new Vector3(0,0.5f ,0.5f ));
        centers.add(new Vector3(0.5f,1f ,0.5f ));
        centers.add(new Vector3(0.5f,0 ,0.5f ));

        dimens.add(new Vector3(1,1,0.00001f));
        dimens.add(new Vector3(1,1,0.00001f));
        dimens.add(new Vector3(0.00001f,1,1));
        dimens.add(new Vector3(0.00001f,1,1));
        dimens.add(new Vector3(1,0.00001f,1));
        dimens.add(new Vector3(1,0.00001f,1));
    }

    private final Vector3 tmpPos = new Vector3();
    private final Vector3 tmpCenter = new Vector3();


    /*
     * Angeschaute Seite des Blocks holen
     * Gibt Seite als int zurück, Zuordnung analog zu Blocks.Orientation.FACES
    */
    public int getFace(Ray ray, Block block) {
        tmpPos.set(block.getPos().x,block.getPos().y,block.getPos().z);

        float currentDistance;
        float minDistance = -1;
        int plane = -1;

        for (int i = 0; i < centers.size(); i++) {
            Vector3 cnt = centers.get(i);

            tmpCenter.set(tmpPos).add(cnt);

            currentDistance = tmpCenter.dst2(player.getCam().position);

            if(minDistance >= 0f && currentDistance > minDistance)
                continue;

            if (Intersector.intersectRayBoundsFast(ray,tmpCenter,dimens.get(i))) {
                plane = i;
                minDistance = currentDistance;
            }
        }
        return plane;
    }

}
