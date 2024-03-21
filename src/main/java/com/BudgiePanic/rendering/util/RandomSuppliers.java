package com.BudgiePanic.rendering.util;

import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.Supplier;

/**
 * Singleton holder of randomness sources.
 * 
 * @author BudgiePanic
 */
public final class RandomSuppliers {
    private RandomSuppliers() {}

    /**
     * Generator of pseudorandom floats between 0 and 1 without lock contention.
     */
    public static final Supplier<Float> threadSafeRandomSupplier = () -> { return ThreadLocalRandom.current().nextFloat(); };

    private static final long seed = 8545785207L;

    private static final Random random = new Random(seed);

    /**
     * Generates pseudorandom floats with a random object. Uses the same seed everytime.
     */
    public static final Supplier<Float> consistentRandomSupplier = () -> { return random.nextFloat(); };

    /**
     * Generates the same value all the time. Useful for testing. Generates 0.
     */
    public static final Supplier<Float> noRandom = () -> 0f;
}
