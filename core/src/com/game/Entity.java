package com.game;

import com.game.events.Event;
import com.game.events.EventListener;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import static com.game.events.EventType.LEVEL_PAUSED;
import static com.game.events.EventType.LEVEL_UNPAUSED;

@Getter
@Setter
@RequiredArgsConstructor
public class Entity implements EventListener {

    protected final GameContext2d gameContext;
    protected final Map<Class<? extends Component>, Component> components = new HashMap<>();
    
    protected boolean dead;

    @Override
    public String toString() {
        return getClass().getSimpleName() + "; dead=" + isDead() + "; components=" + components;
    }

    @Override
    public void listenToEvent(Event event, float delta) {
        if (event.is(LEVEL_PAUSED)) {
            getComponents().values().forEach(component -> component.setOn(false));
        } else if (event.is(LEVEL_UNPAUSED)) {
            getComponents().values().forEach(component -> component.setOn(true));
        }
    }
    
    public void onDeath() {
        gameContext.removeEventListener(this);
    }

    public <C> C getComponent(Class<C> componentClass) {
        return componentClass.cast(getComponents().get(componentClass));
    }

    public boolean hasComponent(Class<? extends Component> clazz) {
        return getComponents().containsKey(clazz) && clazz.isAssignableFrom(getComponent(clazz).getClass());
    }

    public boolean hasAllComponents(Collection<Class<? extends Component>> clazzes) {
        return clazzes.stream().allMatch(this::hasComponent);
    }

    public void addComponent(Component component) {
        getComponents().put(component.getClass(), component);
    }

}
