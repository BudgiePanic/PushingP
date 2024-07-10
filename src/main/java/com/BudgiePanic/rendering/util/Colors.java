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

/**
 * A helper class that contains several ready made colors.
 */
public final class Colors {
    private Colors() {}

    /**
     * Black.
     */
    public final static Color black = new Color(0f, 0f, 0f);

    /**
     * Red.
     */
    public final static Color red = new Color(1f, 0f, 0f);

    /**
     * Green.
     */
    public final static Color green = new Color(0f, 1f, 0f);

    /**
     * Blue.
     */
    public final static Color blue = new Color(0f, 0f, 1f);

    /**
     * White
     */
    public final static Color white = new Color(1f, 1f, 1f);
}
