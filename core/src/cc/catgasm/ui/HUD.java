package cc.catgasm.ui;

import cc.catgasm.entity.Player;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class HUD {
    //Keine Collection wegen Performance (Iterator zu langsam)
    private final HUDElement[] elements;

    public HUD(Player player) {
        elements = new HUDElement[] {
                new DebugScreen(player),
                new Crosshair(new Texture(Gdx.files.internal("xhair.png")), new SpriteBatch()),
                new Hotbar(new Texture(Gdx.files.internal("hotbar.png")), new Texture(Gdx.files.internal("selectedBox.png")), new SpriteBatch())
        };
    }

    public void resize(int w, int h){
        for (HUDElement element : elements) {
            element.resize(w, h);
        }
    }

    public void update(){
        for (HUDElement element : elements) {
            element.update();
        }
    }

    public void dispose() {
        for (HUDElement element : elements) {
            element.dispose();
        }
    }

    public DebugScreen getDebugScreen(){
        return (DebugScreen) elements[0];
    }

    public Crosshair getCrosshair() {
        return (Crosshair) elements[1];
    }

    public Hotbar getHotbar() {
        return (Hotbar) elements[2];
    }
}
