package cc.catgasm.config;

import com.badlogic.gdx.graphics.GL20;

public class RuntimeConfig {
    //TODO Race Condition?

    public static int RENDER_MODE_SWITCH = 0;

    public static int[] RENDER_MODES = new int[] {
            GL20.GL_TRIANGLES,
            GL20.GL_POINTS,
            GL20.GL_LINES,
            GL20.GL_LINE_STRIP,
            GL20.GL_TRIANGLE_FAN,
            GL20.GL_TRIANGLE_STRIP
    };

    public static boolean DO_MAP_REBUILD = false;
}
