package com.game;

import com.game.core.IEntity;
import lombok.Getter;
import lombok.Setter;

import java.util.HashMap;
import java.util.Map;

@Getter
@Setter
public class Entity implements IEntity {
    protected final Map<Class<? extends Component>, Component> components = new HashMap<>();
    protected boolean dead;
}
