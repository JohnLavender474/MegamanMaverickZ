package com.game.game;

import com.badlogic.gdx.Gdx;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

@RunWith(GdxTestRunner.class)
public class ExampleTest {

    @Test
    public void stupidTest() {
        assertEquals(1, 1);
    }

    @Test
    public void badlogicLogoFileExists() {
        assertTrue("This test will only pass when the badlogic.jpg file coming with a new project setup has " +
                           "not been deleted.", Gdx.files.internal("../assets/badlogic.jpg").exists());
    }

}
