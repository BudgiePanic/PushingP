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
    public static Tuple makeVector(double x, double y, double z) {
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
    public static Tuple makePoint(double x, double y, double z) {
        return new Tuple(x, y, z);
    }

    /**
     * Creates a new zeroed point.
     * @return
     *   A new Zeroed point.
     */
    public static Tuple makePoint(){
        return makePoint(0.0, 0.0, 0.0);
    }

    /**
     * The horizontal component.
     */
    public final double x, 
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
        this.x = 0.0;
        this.y = 0.0;
        this.z = 0.0;
        this.w = 0.0;
    }

    /**
     * Custom Tuple, specify the values.
     */
    public Tuple(double x, double y, double z, double w){
        this.x = x;
        this.y = y;
        this.z = z;
        this.w = w;
    }

    /**
     * Shortcut position tuple, sets w component to 1 automatically.
     */
    public Tuple(double x, double y, double z) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.w = 1.0;
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
        return compareFloat(this.w, 0.0) == 0;
    }

    /**
     * Checks if this tuple represents a point in space (position).
     */
    public boolean isPoint(){
        return compareFloat(this.w, 1.0) == 0;
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
    public Tuple add(double x, double y, double z) {
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
    public Tuple add(double x, double y, double z, double w) {
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
    public Tuple subtract(double x, double y, double z) {
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
    public Tuple subtract(double x, double y, double z, double w) {
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
    public Tuple multiply(double value) {
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
    public Tuple divide(double value) {
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
    public double magnitude() {
        return Math.sqrt(
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
        double mag = magnitude();
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
    public double dot(Tuple other) {
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

    /**
     * Reflect's this tuple about a normal vector.
     * @param normal
     *     Normal vector
     * @return
     *     A new vector reflected about the normal
     */
    public Tuple reflect(Tuple normal) {
        var scalar = 2.0 * this.dot(normal);
        return this.subtract(normal.multiply(scalar));
    }

    /**
     * Get the angle between this vector and another vector.
     *
     * @param vector
     *   The other vector.
     * @return
     *   The angle between 'this' vector and 'vector' in radians.
     */
    public double angleBetween(final Tuple vector) {
        final var normDot = (this.dot(vector)) / (this.magnitude() * vector.magnitude());
        final double angle = Math.acos(normDot);
        return angle;
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
