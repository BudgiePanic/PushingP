package com.BudgiePanic.rendering.util;

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
    float x, 
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
        this.x = other.x;
        this.y = other.y;
        this.z = other.z;
        this.w = other.w;
    }

    /**
     *  Checks if this tuple represents a vector (direction).
     */
    public boolean isVector(){
        return this.w == 0f;
    }

    /**
     * Checks if this tuple represents a point in space (position).
     */
    public boolean isPoint(){
        return this.w == 1f;
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
        return Float.compare(this.x, otherTuple.x) == 0 &&
               Float.compare(this.y, otherTuple.y) == 0 &&
               Float.compare(this.z, otherTuple.z) == 0 &&
               Float.compare(this.w, otherTuple.w) == 0;
    }
}