package com.game.cull;

import com.game.messages.Message;
import com.game.messages.MessageType;
import com.game.Component;
import lombok.AccessLevel;
import lombok.Getter;

import java.util.*;
import java.util.function.Predicate;

import static java.util.Arrays.stream;

@Getter(AccessLevel.PACKAGE)
public class CullOnMessageComponent extends Component {

    private final List<Predicate<Message>> cullMessagePredicates = new ArrayList<>();

    public boolean isCullMessage(Message message) {
        return cullMessagePredicates.stream().anyMatch(m -> m.test(message));
    }

    public void addCullMessagePredicate(MessageType... messageTypes) {
        stream(messageTypes).forEach(messageType ->
            addCullMessagePredicate(message -> message.getMessageType().equals(messageType)));
    }

    public void addCullMessagePredicate(Predicate<Message> predicate) {
        cullMessagePredicates.add(predicate);
    }

}
