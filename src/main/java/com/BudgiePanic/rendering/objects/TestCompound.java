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

public class TestCompound extends CompoundShape {

    public TestCompound(Matrix4 transform) {
        super(
            CompoundOperation.difference,
            new Cylinder(identity(), Material.color(Colors.red), 1, 0, false),
            new Sphere(Transforms.identity().scale(0.5f).translate(0, 0.5f, -1f).assemble()),
            transform);
    }
    
}
