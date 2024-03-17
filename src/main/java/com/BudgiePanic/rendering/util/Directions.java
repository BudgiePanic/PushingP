package com.BudgiePanic.rendering.util;

import static com.BudgiePanic.rendering.util.Tuple.makeVector;

/**
 * Ready made unit direction vectors.
 * 
 * @author BudgiePanic
 */
public final class Directions {
    private Directions() {}

    public static final Tuple up = makeVector(0, 1, 0);

    public static final Tuple down = makeVector(0, -1, 0);

    public static final Tuple left = makeVector(-1, 0, 0);

    public static final Tuple right = makeVector(1, 0, 0);

    public static final Tuple forward = makeVector(0, 0, 1);

    public static final Tuple backward = makeVector(0, 0, -1);
}
