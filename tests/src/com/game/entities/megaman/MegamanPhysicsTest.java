package com.game.entities.megaman;

import com.badlogic.gdx.math.Rectangle;
import org.junit.Before;
import org.junit.Test;

import static com.game.ConstVals.ViewVals.PPM;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class MegamanPhysicsTest {

    private static final float DELTA = 0.0167f;

    private Rectangle megamanBody;
    private Rectangle mockScreen;
    private Rectangle[][] mockScreenQuadrants;

    @Before
    public void setUp() {
        megamanBody = new Rectangle(0f, 0f, PPM, 2f * PPM);
        megamanBody.setCenter(0f, 0f);
        mockScreen = new Rectangle(0f, 0f, 16f * PPM, 14f * PPM);
        mockScreen.setCenter(0f, 0f);
        mockScreenQuadrants = new Rectangle[16][14];
        for (int i = 0; i < 16; i++) {
            for (int j = 0; j < 14; j++) {
                mockScreenQuadrants[i][j] = new Rectangle(
                        mockScreen.x + (i * PPM), mockScreen.y + (j * PPM), PPM, PPM);
            }
        }
    }

    @Test
    public void runLeft() {
        // given
        for (int i = 0; i < 60; i++) {
            megamanBody.x -= Megaman.RUN_SPEED_PER_SECOND * PPM * DELTA;
        }
        // then
        assertEquals((-3.5 * PPM) - megamanBody.width / 2f, megamanBody.x, 0.25f);
        assertTrue(mockScreen.overlaps(megamanBody));
        System.out.println(megamanBody);
        System.out.println(mockScreenQuadrants[4][7]);
        assertTrue(mockScreenQuadrants[4][7].overlaps(megamanBody));
    }

   
}
