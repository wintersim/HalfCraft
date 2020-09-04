package cc.catgasm.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;


public class Hotbar implements HUDElement {
    private Texture hotbarTexture;
    private Texture selectBoxTexture;
    private HotbarItem[] items;
    private SpriteBatch spriteBatch;
    private int selectedIndex;
    private int hotbarX;
    private int hotbarY;
    private int scale;

    private int itemSize;
    private int selectBoxSize;

    public Hotbar(Texture hotbarTexture, Texture selectBoxTexture,SpriteBatch spriteBatch) {
        this.hotbarTexture = hotbarTexture;
        this.spriteBatch = spriteBatch;
        this.selectBoxTexture = selectBoxTexture;

        items = new HotbarItem[5];
        items[0] = new HotbarItem(new Texture(Gdx.files.internal("items/blocks/stone.png")), "Stone");
        items[1] = new HotbarItem(new Texture(Gdx.files.internal("items/blocks/grass.png")), "Grass");
        items[2] = new HotbarItem(new Texture(Gdx.files.internal("items/blocks/dirt.png")), "Dirt");
        items[3] = new HotbarItem(new Texture(Gdx.files.internal("items/blocks/wood.png")), "Wood");
        items[4] = new HotbarItem(new Texture(Gdx.files.internal("items/blocks/sand.png")), "Stone");

        itemSize = items[0].texture.getWidth() - 2;
        selectBoxSize = selectBoxTexture.getWidth();
        hotbarY = 30;
        scale = 6;
    }

    @Override
    public void resize(int w, int h) {
        hotbarX = w / 2 - (hotbarTexture.getWidth() * scale) / 2;
    }

    @Override
    public void update() {
        spriteBatch.begin();
        spriteBatch.draw(hotbarTexture,hotbarX,hotbarY, hotbarTexture.getWidth() * scale, hotbarTexture.getHeight() * scale);

        for(int i = 0; i < items.length; i++) {
            spriteBatch.draw(items[i].texture,i * (itemSize + 4) * scale + hotbarX + 3 * scale, hotbarY + 3 * scale,
                    itemSize * scale, itemSize * scale);
        }

        spriteBatch.draw(selectBoxTexture,selectedIndex * (selectBoxSize - 2) * scale + (hotbarX) , hotbarY,
                selectBoxTexture.getWidth() * scale, selectBoxTexture.getHeight() * scale);

        spriteBatch.end();
    }

    @Override
    public void dispose() {
        hotbarTexture.dispose();
        selectBoxTexture.dispose();
        for (HotbarItem item : items) {
            item.texture.dispose();
        }
        spriteBatch.dispose();
    }

    public void setSelectedIndex(int selectedIndex) {
        this.selectedIndex = selectedIndex;
    }

    public int getSelectedIndex() {
        return selectedIndex;
    }

    private static class HotbarItem{
        private Texture texture;
        private String name;

        public HotbarItem(Texture texture, String name) {
            this.texture = texture;
            this.name = name;
        }

        public Texture getTexture() {
            return texture;
        }

        public String getName() {
            return name;
        }
    }
}
