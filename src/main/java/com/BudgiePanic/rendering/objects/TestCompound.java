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
package com.BudgiePanic.rendering.objects;

import com.BudgiePanic.rendering.util.matrix.Matrix4;
import static com.BudgiePanic.rendering.util.matrix.Matrix4.identity;

import com.BudgiePanic.rendering.util.Colors;
import com.BudgiePanic.rendering.util.Material;
import com.BudgiePanic.rendering.util.shape.Cylinder;
import com.BudgiePanic.rendering.util.shape.Sphere;
import com.BudgiePanic.rendering.util.shape.composite.CompoundOperation;
import com.BudgiePanic.rendering.util.shape.composite.CompoundShape;
import com.BudgiePanic.rendering.util.transform.Transforms;

/**
 * A compound shape comprised a non-solid sub shape. Used for experimenting with the CSG algorithm.
 * 
 * @author BudgiePanic
 */
public class TestCompound extends CompoundShape {

    public TestCompound(Matrix4 transform) {
        super(
            CompoundOperation.difference,
            new Cylinder(identity(), Material.color(Colors.red), 1, 0, false),
            new Sphere(Transforms.identity().scale(0.5f).translate(0, 0.5f, -1f).assemble()),
            transform);
    }
    
}
