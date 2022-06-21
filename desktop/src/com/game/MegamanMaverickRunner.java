package com.game;

import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration;
import com.game.tests.GameTestRunner;
import com.game.tests.GameTestRunner.TestScreen;

// Please note that on macOS your application needs to be started with the -XstartOnFirstThread JVM argument
public class MegamanMaverickRunner {
	public static void main(String[] arg) {
		Lwjgl3ApplicationConfiguration config = new Lwjgl3ApplicationConfiguration();
		config.setForegroundFPS(60);
		config.setTitle("Megaman Maverick");
		config.setWindowedMode(1920, 1080);
		test(TestScreen.TEST_MEGABUSTER, config);
		// new Lwjgl3Application(new MegamanMaverick(), config);
	}

	private static void test(TestScreen testScreen, Lwjgl3ApplicationConfiguration config) {
		new Lwjgl3Application(new GameTestRunner(testScreen), config);
	}

}
