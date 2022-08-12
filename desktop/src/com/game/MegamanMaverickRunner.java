package com.game;

import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration;
import com.game.tests.GameTestRunner;
import com.game.tests.GameTestRunner.TestScreen;

import static com.game.tests.GameTestRunner.TestScreen.*;

/**
 * The type Megaman maverick runner. Please note that on macOS your application needs to be started with the
 * -XstartOnFirstThread JVM argument.
 */
public class MegamanMaverickRunner {

	/**
	 * The entry point of application.
	 *
	 * @param arg the input arguments
	 */
	public static void main(String[] arg) {
		Lwjgl3ApplicationConfiguration config = new Lwjgl3ApplicationConfiguration();
		config.setIdleFPS(60);
		config.setForegroundFPS(60);
		config.setWindowedMode(1920, 1080);
		config.setTitle("Megaman Maverick");
		// new Lwjgl3Application(new GameTestRunner(TEST_BOSS_SELECT), config);
		new Lwjgl3Application(new MegamanMaverick(), config);
	}

}
