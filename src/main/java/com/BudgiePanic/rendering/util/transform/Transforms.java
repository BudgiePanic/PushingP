package com.BudgiePanic.rendering.util.transform;

import java.util.Optional;

import com.BudgiePanic.rendering.util.matrix.Matrix4;

/**
 * Fluent API for chaining matrix operations together.
 * 
 * @author BudgiePanic
 */
public class Transforms {
    
    // We depend on all the other classes in the transform package.

    /**
     * The matrix operation.
     */
    protected final Matrix4 operation;

    /**
     * The next operation in the chain.
     */
    protected final Optional<Transforms> parent;

    /**
     * The start of a new matrix operation chain.
     *
     * @return
     *   Identity Matrix.
     */
    public static Transforms identity() {
        return new Transforms();
    }

    /**
     * Base of API chain. Always identity matrix operation.
     */
    private Transforms() {
        this.parent = Optional.empty();
        this.operation = Matrix4.identity();
    }

    /**
     * Call chain extension constructor.
     *
     * @param parent
     *   The last call in the chain
     * @param operation
     *   The matrix operation
     */
    private Transforms(Transforms parent, Matrix4 operation) {
        if (parent == null || operation == null) throw new IllegalArgumentException("Transforms cannot take null parameters");
        this.operation = operation;
        this.parent = Optional.of(parent);
    }

    /**
     * Adds a pitch rotation to this transform.
     * Use AngleHelpers Util Methods to convert degrees to radians if needed.
     * 
     * @param degrees
     *   The amount of pitch. +ve values pitch down.
     * @return
     *   Transforms assembler chain.
     */
    public Transforms rotateX(float radians) {
        var matrix = Rotation.buildXRotationMatrix(radians);
        return new Transforms(this, matrix);
    }

    /**
     * Adds a yaw rotation to this transform.
     * Use AngleHelpers Util Methods to convert degrees to radians if needed.
     * 
     * @param degrees
     *   The amount of yaw. +ve values rotate right.
     * @return
     *   Transforms assembler chain.
     */
    public Transforms rotateY(float radians) {
        var matrix = Rotation.buildYRotationMatrix(radians);
        return new Transforms(this, matrix);
    }

    /**
     * Adds a roll rotation to this transform.
     * Use AngleHelpers Util Methods to convert degrees to radians if needed.
     * 
     * @param degrees
     *   The amount of roll. +ve values roll left.
     * @return
     *   Transforms assembler chain.
     */
    public Transforms rotateZ(float radians) {
        var matrix = Rotation.buildZRotationMatrix(radians);
        return new Transforms(this, matrix);
    }

    /**
     * Adds a scaling to this transform.
     *
     * @param x
     *   Scale factor in X dimension.
     * @param y
     *   Scale factor in Y dimension.
     * @param z
     *   Scale factor in Z dimension.
     * @return
     *   Transforms assembler chain.
     */
    public Transforms scale(float x, float y, float z) {
        var matrix = Scale.makeScaleMatrix(x, y, z);
        return new Transforms(this, matrix);
    }

    /**
     * Adds a scaling to this transform.
     *
     * @param scale
     *   uniform scale amount to apply to xyz axis
     * @return
     *   Transforms assembler chain.
     */
    public Transforms scale(float scale) {
        var matrix = Scale.makeScaleMatrix(scale, scale, scale);
        return new Transforms(this, matrix);
    }

    /**
     * Adds a axis reflection to this transform.
     *
     * @param x
     *   Should the x axis be flipped
     * @param y
     *   Should the y axis be flipped
     * @param z
     *   Should the z axis be flipped
     * @return
     *   Transforms assembler chain.
     */
    public Transforms reflect(boolean x, boolean y, boolean z) {
        var matrix = Scale.makeReflectMatrix(x, y, z);
        return new Transforms(this, matrix);
    }

    /**
     * Adds a shear operation to this transform.
     *
     * @param xy
     *   The shear of X proportional to Y
     * @param xz
     *   The shear of X proportional to Z
     * @param yx
     *   The shear of Y proportional to X
     * @param yz
     *   The shear of Y proportional to Z
     * @param zx
     *   The shear of Z proportional to X
     * @param zy
     *   The shear of Z proportional to Y
     * @return
     *   Transforms assembler chain.
     */
    public Transforms shear(float xy, float xz, float yx, float yz, float zx, float zy) {
        var matrix = Shear.buildShearMatrix(xy, xz, yx, yz, zx, zy);
        return new Transforms(this, matrix);
    }

    /**
     * Adds a translation to this transform.
     *
     * @param x
     *   The x offset of the translation
     * @param y
     *   The y offset of the translation
     * @param z
     *   The z offset of the translation
     * @return
     *   Transforms assembler chain.
     */
    public Transforms translate(float x, float y, float z) {
        var matrix = Translation.makeTranslationMatrix(x, y, z);
        return new Transforms(this, matrix);
    }

    /**
     * Inverse the last operation.
     * Could throw a runtime exception if the operation cannot be inverted.
     * 
     * @return
     *   Transforms assembler chain.
     */
    public Transforms inverse() {
        var matrix = this.operation.inverse();
        return new Transforms(this, matrix);
    }

    /**
     * Terminate this matrix chain and combine operations.
     * Example: rotate().scale().translate().assemble => translate * scale * rotate.
     * 
     * @return
     *   A new transform matrix, assembled in reverse order of the API calls.
     */
    public Matrix4 assemble() {
        if (parent.isPresent()) {
            return this.operation.multiply(parent.get().assemble());
        } else {
            return this.operation;
        }
    }

}
