package cc.catgasm.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class Crosshair implements HUDElement{
    private final Texture crosshairTexture;
    private final SpriteBatch spriteBatch;
    private float middleY;
    private float middleX;

    public Crosshair(Texture crosshairTexture, SpriteBatch spriteBatch) {
        this.crosshairTexture = crosshairTexture;
        this.spriteBatch = spriteBatch;
        resize(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
    }

    @Override
    public void resize(int w, int h){
        middleX = w / 2f - crosshairTexture.getWidth() / 2f;
        middleY = h / 2f - crosshairTexture.getHeight() / 2f;
    }

    @Override
    public void update() {
        spriteBatch.begin();
        spriteBatch.draw(crosshairTexture,middleX,middleY);
        spriteBatch.end();
    }

    @Override
    public void dispose() {
        crosshairTexture.dispose();
        spriteBatch.dispose();
    }
}
