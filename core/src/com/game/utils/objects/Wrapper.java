package com.game.utils.objects;

import lombok.*;

@Setter
@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class Wrapper<T> {

    private T data;

    public static <T> Wrapper<T> empty() {
        return new Wrapper<>();
    }

    public static <T> Wrapper<T> of(T data) {
        return new Wrapper<>(data);
    }

}
