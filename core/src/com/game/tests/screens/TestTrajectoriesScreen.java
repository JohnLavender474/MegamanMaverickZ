package com.game.tests.screens;

import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.game.Component;
import com.game.System;
import com.game.core.IEntitiesAndSystemsManager;
import com.game.core.IEntity;
import com.game.debugging.DebugComponent;
import com.game.debugging.DebugSystem;
import com.game.trajectories.TrajectoryComponent;
import com.game.trajectories.TrajectorySystem;
import com.game.world.BodyComponent;
import com.game.world.BodyType;
import com.game.world.WorldSystem;
import lombok.Getter;
import lombok.Setter;

import java.util.*;

import static com.game.ConstVals.ViewVals.*;

public class TestTrajectoriesScreen extends ScreenAdapter {

    private Viewport viewport;
    private IEntitiesAndSystemsManager entitiesAndSystemsManager;

    @Override
    public void show() {
        viewport = new FitViewport(VIEW_WIDTH * PPM, VIEW_HEIGHT * PPM);
        entitiesAndSystemsManager = new EntitiesAndSystemsManager();
        entitiesAndSystemsManager.addSystem(new WorldSystem(null, new Vector2(), 1f / 120f));
        entitiesAndSystemsManager.addSystem(new TrajectorySystem());
        entitiesAndSystemsManager.addSystem(new DebugSystem(new ShapeRenderer(),
                (OrthographicCamera) viewport.getCamera()));
        entitiesAndSystemsManager.addEntity(new TestTrajectoryEntity(new Rectangle(0f, 0f, PPM, PPM)));
    }

    @Override
    public void render(float delta) {
        entitiesAndSystemsManager.updateSystems(delta);
        viewport.apply();
    }

    @Override
    public void resize(int width, int height) {
        viewport.update(width, height);
    }

    @Getter
    @Setter
    static class TestTrajectoryEntity implements IEntity {

        private final Map<Class<? extends Component>, Component> components = new HashMap<>();
        private boolean dead;

        public TestTrajectoryEntity(Rectangle bounds) {
            addComponent(defineBodyComponent(bounds));
            addComponent(defineTrajectoryComponent());
            addComponent(defineDebugComponent());
        }

        private BodyComponent defineBodyComponent(Rectangle bounds) {
            BodyComponent bodyComponent = new BodyComponent(BodyType.ABSTRACT);
            bodyComponent.setAffectedByResistance(false);
            bodyComponent.setGravityOn(false);
            bodyComponent.set(bounds);
            return bodyComponent;
        }

        private TrajectoryComponent defineTrajectoryComponent() {
            TrajectoryComponent trajectoryComponent = new TrajectoryComponent();
            trajectoryComponent.addTrajectory(new Vector2(2f * PPM, PPM), 1f);
            trajectoryComponent.addTrajectory(new Vector2(0f, 0f), .5f);
            trajectoryComponent.addTrajectory(new Vector2(-2f * PPM, -PPM), .75f);
            trajectoryComponent.addTrajectory(new Vector2(0f, 0f), .5f);
            return trajectoryComponent;
        }

        private DebugComponent defineDebugComponent() {
            DebugComponent debugComponent = new DebugComponent();
            debugComponent.addDebugHandle(() -> getComponent(BodyComponent.class).getCollisionBox(), () -> Color.RED);
            return debugComponent;
        }

    }

    static class EntitiesAndSystemsManager implements IEntitiesAndSystemsManager {

        @Getter private final Set<IEntity> entities = new HashSet<>();
        private final Map<Class<? extends System>, System> systems = new LinkedHashMap<>();
        private final Queue<IEntity> queuedEntities = new ArrayDeque<>();
        private boolean updating;

        @Override
        public void addEntity(IEntity entity) {
            if (updating) {
                queuedEntities.add(entity);
            } else {
                entities.add(entity);
            }
        }

        @Override
        public void purgeAllEntities() {
            entities.clear();
            systems.values().forEach(System::purgeAllEntities);
        }

        @Override
        public void addSystem(System system) {
            systems.put(system.getClass(), system);
        }

        @Override
        public <S extends System> S getSystem(Class<S> sClass) {
            return sClass.cast(systems.get(sClass));
        }

        @Override
        public void updateSystems(float delta) {
            updating = true;
            while (!queuedEntities.isEmpty()) {
                entities.add(queuedEntities.poll());
            }
            Iterator<IEntity> entityIterator = entities.iterator();
            while (entityIterator.hasNext()) {
                IEntity entity = entityIterator.next();
                if (entity.isDead()) {
                    systems.values().forEach(system -> {
                        if (system.entityIsMember(entity)) {
                            system.removeEntity(entity);
                        }
                    });
                    entityIterator.remove();
                } else {
                    systems.values().forEach(system -> {
                        if (!system.entityIsMember(entity) && system.qualifiesMembership(entity)) {
                            system.addEntity(entity);
                        } else if (system.entityIsMember(entity) && !system.qualifiesMembership(entity)) {
                            system.removeEntity(entity);
                        }
                    });
                }
            }
            systems.values().forEach(system -> system.update(delta));
            updating = false;
        }

    }

}
