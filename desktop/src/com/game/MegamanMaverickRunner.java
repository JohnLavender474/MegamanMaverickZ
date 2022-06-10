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
		// new Lwjgl3Application(new MegamanMaverick(), config);
		// test_PPM_And_Movement_Speeds(config);
		test_World_System_1(config);
	}

	private static void test_PPM_And_Movement_Speeds(Lwjgl3ApplicationConfiguration config) {
		new Lwjgl3Application(new GameTestRunner(TestScreen.PPM_AND_MOVEMENT_SPEEDS), config);
	}

	private static void test_World_System_1(Lwjgl3ApplicationConfiguration config) {
		new Lwjgl3Application(new GameTestRunner(TestScreen.TEST_WORLD_SYSTEM_1), config);
	}

}
