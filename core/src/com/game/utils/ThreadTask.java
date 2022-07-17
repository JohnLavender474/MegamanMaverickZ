package com.game.utils;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

@Getter
@RequiredArgsConstructor
public class ThreadTask<T> {

    private final ExecutorService executorService;

    private T currentVal;
    private Future<T> future;

    private int numFramesToWait;
    private int elapsedFrames;
    private boolean reset;

    public T call(Callable<T> callable) {
        try {
            elapsedFrames += 1;
            if (elapsedFrames >= numFramesToWait) {
                if (reset || future == null) {
                    reset = false;
                    future = executorService.submit(callable);
                }
                if (future.isDone()) {
                    reset = true;
                    elapsedFrames = 0;
                    return future.get();
                }
            }
            return null;
        } catch (InterruptedException | ExecutionException ignored) {
            return null;
        }
    }

}
