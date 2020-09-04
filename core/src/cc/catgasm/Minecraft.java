package cc.catgasm;

import cc.catgasm.config.RuntimeConfig;
import cc.catgasm.entity.Player;
import cc.catgasm.input.DefaultInputProcessor;
import cc.catgasm.render.ChunkRenderer;
import cc.catgasm.render.SkyboxRenderer;
import cc.catgasm.ui.DebugScreen;
import cc.catgasm.ui.HUD;
import cc.catgasm.ui.Hotbar;
import cc.catgasm.world.World;
import cc.catgasm.world.chunk.ChunkModel;
import cc.catgasm.world.sky.Skybox;
import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.environment.DirectionalLight;
import com.badlogic.gdx.graphics.g3d.utils.DefaultShaderProvider;
import com.badlogic.gdx.utils.TimeUtils;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

public class Minecraft implements ApplicationListener {

    private Player player;
    private HUD hud;
    private DebugScreen debugScreen;
    private Hotbar hotbar;
    private World world;
    private ChunkRenderer chunkRenderer;

    @Override
    public void create() {
        PerspectiveCamera cam = new PerspectiveCamera(67, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        cam.near = 0.3f;
        cam.far = 300f;

        Environment environment = new Environment();
        environment.set(new ColorAttribute(ColorAttribute.AmbientLight, 0.51f, 0.5f, 0.5f, 1f));
        environment.add(new DirectionalLight().set(0.8f, 0.8f, 0.8f, -1f, -0.8f, -0.2f));

        ModelBatch modelBatch = new ModelBatch();
        chunkRenderer = new ChunkRenderer(modelBatch, environment, cam);

        world = World.init((int)TimeUtils.millis());
        player = new Player(cam);
        world.setPlayer(player);

        hud = new HUD(player);
        debugScreen = hud.getDebugScreen();
        hotbar = hud.getHotbar();


        DefaultInputProcessor camController = new DefaultInputProcessor(player);
        Gdx.input.setInputProcessor(camController);
        Gdx.input.setCursorCatched(true);
    }

    /*
        Idee:
        1. Oberfläche (Sichtbarer block) mit 2d noise generieren
        2. Alles unterhalb der Oberfläche ausfüllen
        3. Höhlen, Ores, etc... mit 3D noise generieren
        4. Beide Addieren
        5. ???
        6. Profit
     */

    private void generateMap() {
        world.reset((int) TimeUtils.millis());
    }

    @Override
    public void resize(int width, int height) {
        hud.resize(width, height);
    }

    @Override
    public void render() {
        Gdx.gl.glViewport(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT |
                GL20.GL_DEPTH_BUFFER_BIT |
                (Gdx.graphics.getBufferFormat().coverageSampling?GL20.GL_COVERAGE_BUFFER_BIT_NV:0));

        //Face Culling --> Nur sichtbare Seiten rendern
        Gdx.gl.glEnable(GL20.GL_CULL_FACE);
        Gdx.gl.glCullFace(GL20.GL_FRONT);

        player.update();


        List<ChunkModel> chunksToRender = world.getModels();

        //render World
        int visibleChunks = chunkRenderer.render(chunksToRender);


        //render HUD
        hotbar.setSelectedIndex(player.getSelectedIndex());
        debugScreen.setVisibleEnts(visibleChunks);

        hud.update();

        if (RuntimeConfig.DO_MAP_REBUILD) {
            generateMap();
            RuntimeConfig.DO_MAP_REBUILD = false;
        }
    }


    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void dispose() {
        chunkRenderer.cleanup();
        world.cleanUp();
        hud.dispose();
    }
}
