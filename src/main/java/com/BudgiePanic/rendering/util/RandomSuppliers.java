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
    public static final Supplier<Double> threadSafeRandomSupplier = () -> { return ThreadLocalRandom.current().nextDouble(); };

    private static final long seed = 8545785207L;

    private static final Random random = new Random(seed);

    /**
     * Generates pseudorandom floats with a random object. Uses the same seed everytime. 
     * Will cause lock contention in a threaded environment. Consistent between runs of the program.
     */
    public static final Supplier<Double> consistentRandomSupplier = () -> { return random.nextDouble(); };

    /**
     * Generates the same value all the time. Useful for testing. Generates 0.
     */
    public static final Supplier<Double> noRandom = () -> 0.0;
}
