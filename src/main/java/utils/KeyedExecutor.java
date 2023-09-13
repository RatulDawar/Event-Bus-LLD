package utils;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.function.Supplier;

public class KeyedExecutor<T> {
    private final Integer MAX_THREADS = 5;
    private final Executor executor[];


    public KeyedExecutor() {
        this.executor = new Executor[MAX_THREADS];
        for (int i = 0; i < MAX_THREADS; i++) {
            executor[i] = Executors.newSingleThreadExecutor();
        }
    }

    public CompletionStage<T> submit(final String id, final Supplier<T> task) {
        return CompletableFuture.supplyAsync(task, executor[id.hashCode() % MAX_THREADS]);
    }
}
