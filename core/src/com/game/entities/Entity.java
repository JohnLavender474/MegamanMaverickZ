package com.game.entities;

import com.game.Component;
import com.game.GameContext2d;
import com.game.messages.Message;
import com.game.messages.MessageListener;
import lombok.Getter;
import lombok.Setter;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@Getter
@Setter
public class Entity implements MessageListener {

    protected final GameContext2d gameContext;
    protected final Map<Class<? extends Component>, Component> components = new HashMap<>();

    private boolean dead = false;
    private boolean justSpawned = true;

    public Entity(GameContext2d gameContext) {
        this(gameContext, true);
    }

    public Entity(GameContext2d gameContext, boolean listenToMessages) {
        this.gameContext = gameContext;
        if (listenToMessages) {
            gameContext.addMessageListener(this);
        }
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "; dead=" + isDead() + "; components=" + components;
    }

    @Override
    public void listenToMessage(Message message) {
        switch (message.getMessageType()) {
            case LEVEL_PAUSED -> components.values().forEach(component -> component.setOn(false));
            case LEVEL_UNPAUSED -> components.values().forEach(component -> component.setOn(true));
        }
    }
    
    public void onDeath() {
        gameContext.removeMessageListener(this);
    }

    public <C> C getComponent(Class<C> componentClass) {
        return componentClass.cast(components.get(componentClass));
    }

    public boolean hasComponent(Class<? extends Component> clazz) {
        return components.containsKey(clazz) && clazz.isAssignableFrom(getComponent(clazz).getClass());
    }

    public boolean hasAllComponents(Collection<Class<? extends Component>> clazzes) {
        return clazzes.stream().allMatch(this::hasComponent);
    }

    public void addComponent(Component component) {
        components.put(component.getClass(), component);
    }

}
