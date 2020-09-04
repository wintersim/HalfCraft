package cc.catgasm.input;

import cc.catgasm.config.RuntimeConfig;
import cc.catgasm.entity.Player;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.math.Vector3;

@SuppressWarnings("unused")
public class DefaultInputProcessor implements InputProcessor {

    private final Player player;

    public DefaultInputProcessor(Player player) {
        this.player = player;
    }

    @Override
    public boolean keyDown(int keycode) {
        if(keycode == Input.Keys.W) {
            player.movingForward = true;
        }
        if(keycode == Input.Keys.S) {
            player.movingBackward = true;
        }
        if(keycode == Input.Keys.A) {
            player.movingLeft = true;
        }
        if(keycode == Input.Keys.D) {
            player.movingRight = true;
        }
        if(keycode == Input.Keys.SPACE) {
            player.movingUp = true;
            player.jumpPressed = true;
        }
        if(keycode == Input.Keys.SHIFT_LEFT) {
            player.movingDown = true;
        }
        if(keycode == Input.Keys.B) {
            RuntimeConfig.RENDER_MODE_SWITCH = ++RuntimeConfig.RENDER_MODE_SWITCH % RuntimeConfig.RENDER_MODES.length;
        }
        if(keycode == Input.Keys.R) {
            RuntimeConfig.DO_MAP_REBUILD = true;
        }
        if(keycode == Input.Keys.ESCAPE) {
            Gdx.app.exit();
        }

        return true;
    }

    @Override
    public boolean keyUp(int keycode) {
        if(keycode == Input.Keys.W) {
            player.movingForward = false;
        }
        if(keycode == Input.Keys.S) {
            player.movingBackward = false;
        }
        if(keycode == Input.Keys.A) {
            player.movingLeft = false;
        }
        if(keycode == Input.Keys.D) {
            player.movingRight = false;
        }
        if(keycode == Input.Keys.SPACE) {
            player.movingUp = false;
            player.jumpPressed = false;
        }
        if(keycode == Input.Keys.SHIFT_LEFT) {
            player.movingDown = false;
        }
        if(keycode == Input.Keys.F){
            player.isFlying = !player.isFlying;
        }
        return true;
    }

    @Override
    public boolean keyTyped(char character) {
        return false;
    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        if(button == Input.Buttons.LEFT) {
            player.isAttacking = true;
        }
        if(button == Input.Buttons.RIGHT) {
            player.isPlacing = true;
        }
        return true;
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        if(button == Input.Buttons.LEFT) {
            player.isAttacking = false;
            player.canAttack = true;
        }
        if(button == Input.Buttons.RIGHT) {
            player.isPlacing = false;
        }
        return true;
    }

    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer) {
        return false;
    }

    private int lastCursorX = Gdx.graphics.getWidth() / 2;
    private int lastCursorY = Gdx.graphics.getHeight() / 2;
    private final float MOUSE_SENSITIVITY = 0.2f;

    private final Vector3 tmp = new Vector3();

    @Override
    public boolean mouseMoved(int screenX, int screenY) {
        float dy = lastCursorY - screenY;
        float dx = lastCursorX - screenX;

        player.rotateYaw(dx * MOUSE_SENSITIVITY);
        player.rotatePitch(dy * MOUSE_SENSITIVITY);

        lastCursorX = screenX;
        lastCursorY = screenY;

        return true;
    }

    @Override
    public boolean scrolled(int amount) {
        player.increaseSelectedIndex(amount);
        return true;
    }
}
