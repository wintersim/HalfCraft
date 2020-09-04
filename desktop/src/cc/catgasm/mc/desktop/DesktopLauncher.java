package cc.catgasm.mc.desktop;

import cc.catgasm.Minecraft;
import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;

public class DesktopLauncher {
	public static void main (String[] arg) {
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
		config.title = "HalfCraft";
		config.width = 1280;
		config.height = 720;
		config.vSyncEnabled = false;
		config.samples = 16;
		new LwjglApplication(new Minecraft(), config);
	}
}
