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
		// test_World_System(config);
		// test_Camera_Room_Shift(config);
		// test_Ground_Dash_And_Friction(config);
		// test_Special_Movements(config);
		test_Megaman_Animations(config);
	}

	private static void test_PPM_And_Movement_Speeds(Lwjgl3ApplicationConfiguration config) {
		new Lwjgl3Application(new GameTestRunner(
				TestScreen.TEST_PPM_AND_MOVEMENT_SPEEDS), config);
	}

	private static void test_World_System(Lwjgl3ApplicationConfiguration config) {
		new Lwjgl3Application(new GameTestRunner(
				TestScreen.TEST_WORLD_SYSTEM), config);
	}

	private static void test_Camera_Room_Shift(Lwjgl3ApplicationConfiguration config) {
		new Lwjgl3Application(new GameTestRunner(
				TestScreen.TEST_CAMERA_ROOM_SHIFT), config);
	}

	private static void test_Ground_Dash_And_Friction(Lwjgl3ApplicationConfiguration config) {
		new Lwjgl3Application(new GameTestRunner(
				TestScreen.TEST_GROUND_DASH_AND_FRICTION), config);
	}

	private static void test_Special_Movements(Lwjgl3ApplicationConfiguration config) {
		new Lwjgl3Application(new GameTestRunner(
				TestScreen.TEST_SPECIAL_MOVEMENTS), config);
	}

	private static void test_Megaman_Animations(Lwjgl3ApplicationConfiguration config) {
		new Lwjgl3Application(new GameTestRunner(
				TestScreen.TEST_MEGAMAN_ANIMATIONS), config);
	}

}
