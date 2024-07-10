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
