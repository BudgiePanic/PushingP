package com.BudgiePanic.rendering.util;

import static com.BudgiePanic.rendering.util.FloatHelp.compareFloat;

/**
 * Mathematics primative. Can reperesent position and direction.
 * 
 * @author BudgiePanic
 */
public class Tuple {

    /**
     * Factory method for making new vectors.
     * @param x 
     *   horizontal axis
     * @param y
     *   vertical axis
     * @param z
     *   depth axis (+ve -> away from the camera)
     * @return
     *   A new vector Tuple.
     */
    public static Tuple makeVector(float x, float y, float z) {
        return new Tuple(x, y, z, 0.0f);
    }

    /**
     * Creates a new vector with zeroed components.
     * @return
     *   A new zeroed vector.
     */
    public static Tuple makeVector(){
        return new Tuple();
    }

    /**
     * Ready made exception for math methods.
     */
    private static IllegalArgumentException nullArgument = new IllegalArgumentException("Tuple math operation cannot accept null.");

    /**
     * Factory method for making new points.
     * @param x
     *   horizontal axis
     * @param y
     *   vertical axis
     * @param z
     *   depth axis (+ve -> away from the camera)
     * @return
     *   A new point Tuple.
     */
    public static Tuple makePoint(float x, float y, float z) {
        return new Tuple(x, y, z);
    }

    /**
     * Creates a new zeroed point.
     * @return
     *   A new Zeroed point.
     */
    public static Tuple makePoint(){
        return makePoint(0.0f, 0.0f, 0.0f);
    }

    /**
     * The horizontal component.
     */
    public final float x, 
    /**
     * The vertical component
     */
    y, 
    /**
     * The depth component (Using LHS, so +ve values are moving away from the camera)
     */
    z, 
    /**
     * The W component. Used to distinguish between vectors and points.
     */
    w;

    /**
     * Default Tuple. All values are zeroed.
     */
    public Tuple() {
        this.x = 0.0f;
        this.y = 0.0f;
        this.z = 0.0f;
        this.w = 0.0f;
    }

    /**
     * Custom Tuple, specify the values.
     */
    public Tuple(float x, float y, float z, float w){
        this.x = x;
        this.y = y;
        this.z = z;
        this.w = w;
    }

    /**
     * Shortcut position tuple, sets w component to 1 automatically.
     */
    public Tuple(float x, float y, float z) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.w = 1.0f;
    }

    /**
     * Copy constructor.
     */
    public Tuple(Tuple other){
        if (other == null) throw new IllegalArgumentException("Cannot invoke copy constructor because 'other' is null");
        this.x = other.x;
        this.y = other.y;
        this.z = other.z;
        this.w = other.w;
    }

    /**
     *  Checks if this tuple represents a vector (direction).
     */
    public boolean isVector(){
        return compareFloat(this.w, 0f) == 0;
    }

    /**
     * Checks if this tuple represents a point in space (position).
     */
    public boolean isPoint(){
        return compareFloat(this.w, 1f) == 0;
    }

    /**
     * Creates a new tuple that is the addition of tuple 'this' and tuple 'other'.
     * Because these math operations make new Tuples, the garbage collector is going to be working overtime.
     * Oh well, that's what we pay him for.
     * 
     * @param other
     *   The tuple to add
     * @return
     *   A new tuple that is the result of adding the two tupples together.
     */
    public Tuple add(Tuple other) {
        if (other == null) throw nullArgument;
        return new Tuple(
            this.x + other.x,
            this.y + other.y,
            this.z + other.z,
            this.w + other.w
        );
    }

    /**
     * Add overload. Creates a new Tuple that is 'nudged' [x,y,z] away from 'this'.
     * @param x
     *     The x displacement.
     * @param y
     *     The y displacement.
     * @param z
     *     The z displacement.
     * @return
     *     A new tuple displaced by [x,y,z] from 'this'.
     */
    public Tuple add(float x, float y, float z) {
        return new Tuple(
            this.x + x,
            this.y + y,
            this.z + z,
            this.w
        );
    }

    /**
     * Add overload
     * @param x
     * @param y
     * @param z
     * @param w
     * @return
     */
    public Tuple add(float x, float y, float z, float w) {
        return new Tuple(
            this.x + x,
            this.y + y,
            this.z + z,
            this.w + w
        );
    }

    /**
     * Creates a new tuple that is 'other' subtracted from 'this'.
     * Subtracting two points gives a vector.
     * Subtracting a vector from a point gives a point.
     * Subtracting a point from a vector is illogical.
     * 
     * @param other
     *   The other tuple.
     * @return
     *   A new tuple representing 'this' - 'other'.
     */
    public Tuple subtract(Tuple other) {
        if (other == null) throw nullArgument;
        return new Tuple(
            this.x - other.x,
            this.y - other.y,
            this.z - other.z,
            this.w - other.w
        );
    }

    /**
     * subtract overload
     * @param x
     * @param y
     * @param z
     * @return
     */
    public Tuple subtract(float x, float y, float z) {
        return new Tuple(
            this.x - x,
            this.y - y,
            this.z - z,
            this.w
        );
    }

    /**
     * subtract overload
     * @param x
     * @param y
     * @param z
     * @param w
     * @return
     */
    public Tuple subtract(float x, float y, float z, float w) {
        return new Tuple(
            this.x - x,
            this.y - y,
            this.z - z,
            this.w - w
        );
    }

    /**
     * Creates a new tuple pointing in the opposite direction as 'this'.
     * 
     * @return
     *     A tuple pointing in the opposite direction.
     */
    public Tuple negate() {
        return new Tuple(
            -this.x,
            -this.y,
            -this.z,
            -this.w
        );
    }

    /**
     * Create's a scaled tuple.
     * 
     * @param value
     *     The amount to scale the tuple up by.
     * @return
     *     A new tuple scaled by the value.
     */
    public Tuple multiply(float value) {
        return new Tuple(
            value * this.x,
            value * this.y,
            value * this.z,
            value * this.w
        );
    }

    /**
     * Create's a scaled tuple.
     * 
     * @param value
     *     The amount to scale the tuple down by.
     * @return
     *     A new tuple scaled by the value.
     */
    public Tuple divide(float value) {
        return new Tuple(
            this.x / value,
            this.y / value,
            this.z / value,
            this.w / value
        );
    }

    /**
     * Calculates the size of 'this' using pythagoras theorem.
     * 
     * @return
     *     The size of this tuple.
     */
    public float magnitude() {
        return (float)Math.sqrt(
            (this.x * this.x) +
            (this.y * this.y) +
            (this.z * this.z) +
            (this.w * this.w) 
        );
    }

    /**
     * Create a new vector with a magnitude of one, pointing in the same direction as 'this'.
     * 
     * @return
     *     Create a unit vector pointing in the same direction as 'this' vector.
     */
    public Tuple normalize() {
        // if performance becomes an issue, you would use the fast inverse square root algorithm here
        float mag = magnitude();
        return new Tuple(
            this.x / mag,
            this.y / mag,
            this.z / mag,
            this.w / mag
        );
    }

    /**
     * A vector tuple operation. Calculates the cosine of the angle between 'this' vector and 'other'.
     * 
     * @param other
     *     The other vector.
     * @return
     *     The dot product of 'this' and 'other'.
     */
    public float dot(Tuple other) {
        if (other == null) throw nullArgument;
        return (this.x * other.x) +
               (this.y * other.y) +
               (this.z * other.z) +
               (this.w * other.w);
    }

    /**
     * Operation for vector tuples. creates a new vector that is orthoginal to 'this' and 'other'.
     * 
     * @param other
     *     Another vector
     * @return
     *     A vector tuple orthogical to 'this' and 'other'.
     */
    public Tuple cross(Tuple other) {
        if (other == null) throw nullArgument;
        return Tuple.makeVector(
            (this.y * other.z) - (this.z * other.y), 
            (this.z * other.x) - (this.x * other.z), 
            (this.x * other.y) - (this.y * other.x));
    }

    @Override
    public String toString(){
        return "[" + this.x + "," + this.y + "," + this.z + "," + this.w + "]";
    }

    @Override
    public boolean equals(Object other){
        if (this == other){
            return true;
        }
        if (other == null || this.getClass() != other.getClass()){
            return false;
        }
        Tuple otherTuple = (Tuple) other;
        return compareFloat(this.x, otherTuple.x) == 0 &&
               compareFloat(this.y, otherTuple.y) == 0 &&
               compareFloat(this.z, otherTuple.z) == 0 &&
               compareFloat(this.w, otherTuple.w) == 0;
    }
}