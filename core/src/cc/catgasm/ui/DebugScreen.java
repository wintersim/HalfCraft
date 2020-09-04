package cc.catgasm.ui;

import cc.catgasm.entity.Player;
import cc.catgasm.util.MathHelper;
import cc.catgasm.world.chunk.Chunk;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;

@SuppressWarnings("unused")
public class DebugScreen implements HUDElement{

    private final StringBuilder sb;
    private final Label label;
    private final Stage stage;
    private final Player player;

    private int visibleEnts;

    public DebugScreen(Player player) {
        stage = new Stage();
        BitmapFont font = new BitmapFont();
        font.getData().setScale(1.4f);
        label = new Label("\n\n\n\n\n\n", new Label.LabelStyle(font, Color.WHITE));
        stage.addActor(label);
        label.setPosition(0, Gdx.graphics.getHeight()- label.getHeight() - 10);
        sb = new StringBuilder();
        this.player = player;
    }

    @Override
    public void resize(int w, int h) {
        stage.getViewport().update(w, h, true);
    }

    @Override
    public void update() {
        Vector3 pPos = player.getPosition();

        sb.setLength(0);
        sb.append("FPS: ").append(Gdx.graphics.getFramesPerSecond());
        sb.append("\nVisible Entities: ").append(visibleEnts);
        sb.append("\nX: ").append(pPos.x).append(" Y: ").append(pPos.y).append(" Z: ").append(pPos.z);
        //sb.append("\nYaw: ").append(player.getYaw()).append(" Pitch: ").append(player.getPitch());
        sb.append("\nChunk: ").append(MathHelper.floatDiv(pPos.x, Chunk.CHUNK_SIZE)).append("/").append(MathHelper.floatDiv(pPos.z, Chunk.CHUNK_SIZE));
        sb.append("\nVel: ").append(player.velocity).append(" VelGoal: ").append(player.velocityGoal);
        label.setText(sb);
        stage.draw();
    }

    @Override
    public void dispose() {
        stage.dispose();
    }

    public int getVisibleEnts() {
        return visibleEnts;
    }

    public void setVisibleEnts(int visibleEnts) {
        this.visibleEnts = visibleEnts;
    }
}
