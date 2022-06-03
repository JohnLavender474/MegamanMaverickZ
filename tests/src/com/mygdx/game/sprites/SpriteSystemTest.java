package com.mygdx.game.sprites;

import com.mygdx.game.core.Entity;
import com.mygdx.game.GdxTestRunner;
import com.mygdx.game.utils.Drawable;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.*;

@RunWith(GdxTestRunner.class)
public class SpriteSystemTest {

    private Entity entity;
    private SpriteSystem spriteSystem;
    private SpriteComponent spriteComponent;
    private Map<String, Collection<Drawable>> sprites;

    @Before
    public void setUp() {
        sprites = new HashMap<>();
        // TODO: initialize sprites
        spriteSystem = new SpriteSystem(sprites);
        entity = new Entity();
        spriteComponent = new SpriteComponent();
        entity.addComponent(spriteComponent);
    }

    @Test
    public void spriteCenteredOnEntity() {

    }

}