/*
 * Copyright 2023-2024 Benjamin Sanson
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *    https://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * 
 */
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
