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
package com.BudgiePanic.rendering.util.matrix;

import static com.BudgiePanic.rendering.util.FloatHelp.compareFloat;

import java.util.Arrays;

import com.BudgiePanic.rendering.util.FloatHelp;
import com.BudgiePanic.rendering.util.Tuple;

/**
 * A four by four matrix with associated mathematical operations.
 * row, column
 * 
 * @author BudgiePanic
 */
public final class Matrix4 extends Matrix {

    /**
     * The dimension of the array
     */
    private static final int dimension = 4;

    /**
     * Cached reference to this matrix's inverse to speed calculations up.
     */
    private Matrix4 inverse;

    /**
     * Build a matrix by manually specifying values.
     *
     * @return
     */
    public static Matrix4 buildMatrix(
        double _00,
        double _01,
        double _02,
        double _03,
        double _10,
        double _11,
        double _12,
        double _13,
        double _20,
        double _21,
        double _22,
        double _23,
        double _30,
        double _31,
        double _32,
        double _33
    ){
        final double[][] matrix = new double[dimension][dimension];

        matrix[0][0] = _00; matrix[1][0] = _10; matrix[2][0] = _20; matrix[3][0] = _30;
        matrix[0][1] = _01; matrix[1][1] = _11; matrix[2][1] = _21; matrix[3][1] = _31;
        matrix[0][2] = _02; matrix[1][2] = _12; matrix[2][2] = _22; matrix[3][2] = _32;
        matrix[0][3] = _03; matrix[1][3] = _13; matrix[2][3] = _23; matrix[3][3] = _33;

        return new Matrix4(matrix);
    }

    /**
     * Help method. Check internal double arrays are the correct dimension and non null.
     * @param item
     *   The internal array to check.
     */
    private static void checkSize(double[] item){
        if (item == null || item.length != dimension) throw new IllegalArgumentException("matrix elements must be length 4 and not be null.");
    }

    /**
     * Build a matrix by manually specifiying rows.
     * @param row0
     * @param row1
     * @param row2
     * @param row3
     * @return
     */
    public static Matrix4 buildMatrixRow(final double[] row0, final double[] row1, final double[] row2, final double[] row3) {
        checkSize(row0); checkSize(row1); checkSize(row2); checkSize(row3);
        double[][] matrix = new double[dimension][];
        matrix[0] = row0;
        matrix[1] = row1;
        matrix[2] = row2;
        matrix[3] = row3;
        return new Matrix4(matrix);
    }

    /**
     * Build a matrix by manually specifying columns.
     * @param column0
     * @param column1
     * @param column2
     * @param column3
     * @return
     */
    public static Matrix4 buildMatrixColumn(final double[] column0, final double[] column1, final double[] column2, final double[] column3){
        checkSize(column0); checkSize(column1); checkSize(column2); checkSize(column3);
        double[][] matrix = new double[dimension][dimension];

        for(int i = 0; i < dimension; i++){
            matrix[i][0] = column0[i];
            matrix[i][1] = column1[i];
            matrix[i][2] = column2[i];
            matrix[i][3] = column3[i];
        }

        return new Matrix4(matrix);
    }

    /**
     * Returns a new Identity Matrix.
     *
     * @return
     *     The identity matrix.
     */
    public static Matrix4 identity() {
        return buildMatrixRow(
            new double[] {1f, 0f, 0f, 0f},
            new double[] {0f, 1f, 0f, 0f},
            new double[] {0f, 0f, 1f, 0f},
            new double[] {0f, 0f, 0f, 1f}
        );
    }

    /**
     * Private constructor.
     * 
     * Private to try and help reduce aliasing of internal Arrays between matrices, because they are mutable.
     * 
     * @param matrix
     *     The internal matrix structure.
     */
    private Matrix4(final double[][] matrix){
        super(matrix);
        this.inverse = null;
    }

    /**
     * Mulitply this matrix4 with another matrix4.
     * Creates a new matrix for the operation, leaving 'this' unmodified.
     * 
     * @param other
     *   The matrix to multiply with 'this'.
     * @return
     *   A new matrix 'this' * other.
     */
    public Matrix4 multiply(Matrix4 other) {
        final double[][] matrix = new double[dimension][dimension];

        for (int row = 0; row < dimension; row++) {
            for (int column = 0; column < dimension; column++) {
                matrix[row][column] = 
                    this.matrix[row][0] * other.matrix[0][column] +
                    this.matrix[row][1] * other.matrix[1][column] +
                    this.matrix[row][2] * other.matrix[2][column] +
                    this.matrix[row][3] * other.matrix[3][column];
            }
        }

        return new Matrix4(matrix);
    }

    /**
     * Multiply a tuple by 'this' matrix.
     * 
     * @param tuple
     *   The tuple to multiply.
     * @return
     *   A new tuple: tuple * 'this'.
     */
    public Tuple multiply(Tuple tuple) {
        if (Double.isInfinite(tuple.x) || Double.isInfinite(tuple.y) || Double.isInfinite(tuple.y) || Double.isInfinite(tuple.z)) {
            return multiplyInfinity(tuple);
        }
        double a = (tuple.x * this.matrix[0][0]) + (tuple.y * this.matrix[0][1]) + (tuple.z * this.matrix[0][2]) + (tuple.w * this.matrix[0][3]);
        double b = (tuple.x * this.matrix[1][0]) + (tuple.y * this.matrix[1][1]) + (tuple.z * this.matrix[1][2]) + (tuple.w * this.matrix[1][3]);
        double c = (tuple.x * this.matrix[2][0]) + (tuple.y * this.matrix[2][1]) + (tuple.z * this.matrix[2][2]) + (tuple.w * this.matrix[2][3]);
        double d = (tuple.x * this.matrix[3][0]) + (tuple.y * this.matrix[3][1]) + (tuple.z * this.matrix[3][2]) + (tuple.w * this.matrix[3][3]);
        return new Tuple(a, b, c, d);
    }

    /**
     * Helper method to handle case where tuple contains infinities.
     * @param tuple
     *   the tuple
     * @return
     *   tuple * matrix output
     */
    protected Tuple multiplyInfinity(Tuple tuple) {
        double a = infiniteDoubleMul(tuple.x, this.matrix[0][0]) + infiniteDoubleMul(tuple.y, this.matrix[0][1]) + infiniteDoubleMul(tuple.z, this.matrix[0][2]) + infiniteDoubleMul(tuple.w, this.matrix[0][3]);
        double b = infiniteDoubleMul(tuple.x, this.matrix[1][0]) + infiniteDoubleMul(tuple.y, this.matrix[1][1]) + infiniteDoubleMul(tuple.z, this.matrix[1][2]) + infiniteDoubleMul(tuple.w, this.matrix[1][3]);
        double c = infiniteDoubleMul(tuple.x, this.matrix[2][0]) + infiniteDoubleMul(tuple.y, this.matrix[2][1]) + infiniteDoubleMul(tuple.z, this.matrix[2][2]) + infiniteDoubleMul(tuple.w, this.matrix[2][3]);
        double d = infiniteDoubleMul(tuple.x, this.matrix[3][0]) + infiniteDoubleMul(tuple.y, this.matrix[3][1]) + infiniteDoubleMul(tuple.z, this.matrix[3][2]) + infiniteDoubleMul(tuple.w, this.matrix[3][3]);
        return new Tuple(a, b, c, d);
    }

    /**
     * Multiply two doubles together. If one double is infinity, and the other is near zero, returns zero instead of typical behaviour: returning NaN.
     * @param a
     * @param b
     * @return
     */
    private double infiniteDoubleMul(double a, double b) {
        if(Double.isInfinite(a) && FloatHelp.compareFloat(b, 0) == 0 || Double.isInfinite(b) && FloatHelp.compareFloat(a, 0) == 0) {
            return 0;
        }
        return a*b;
    }

    /**
     * Creates a new matrix and writes 'this' matrice's transpose to it.
     *
     * @return
     *   The transposition of this matrix.
     */
    public Matrix4 transpose() {
        return buildMatrixColumn(
            Arrays.copyOf(matrix[0], dimension),
            Arrays.copyOf(matrix[1], dimension),
            Arrays.copyOf(matrix[2], dimension),
            Arrays.copyOf(matrix[3], dimension));
    }

    /**
     * Calculate the inverse of this 4 by 4 matrix.
     * An exception is thrown if the matrix is not invertible.
     *
     * @return
     *   The inverse of this matrix.
     */
    public synchronized Matrix4 inverse() {
        if (this.inverse != null) {
            return this.inverse;
        }
        if (!isInvertible()) throw new RuntimeException("cannot invert matrix: " + this.toString());
        var result = new double[dimension][dimension];
        var det = getDeterminant();
        // Use optimized technique in Jamis Buck's book The Ray Tracer Challenge
        for (int row = 0; row < dimension; row++) {
            for (int col = 0; col < dimension; col++) {
                var c = getCofactor(row, col);
                result[col][row] = c / det;
            }
        }
        this.inverse = new Matrix4(result);
        return this.inverse;
    }

    @Override
    public void validate() throws MatrixShapeException {
        if (this.matrix == null || this.matrix.length != dimension) throw new MatrixShapeException("matrix does not have 4 rows");
        if (this.matrix[0] == null || this.matrix[0].length != dimension) throw new MatrixShapeException("matrix row 0 was malformed."); 
        if (this.matrix[1] == null || this.matrix[1].length != dimension) throw new MatrixShapeException("matrix row 1 was malformed."); 
        if (this.matrix[2] == null || this.matrix[2].length != dimension) throw new MatrixShapeException("matrix row 2 was malformed."); 
        if (this.matrix[3] == null || this.matrix[3].length != dimension) throw new MatrixShapeException("matrix row 3 was malformed."); 
    }

    @Override
    public int getDimension() {
        return dimension;
    }

    @Override
    public String toString() {
        try {
            validate();
        } catch (MatrixShapeException e) {
            return Arrays.toString(matrix);
        }
        return String.format("[%s, %s, %s, %s]", 
        Arrays.toString(matrix[0]),
        Arrays.toString(matrix[1]),
        Arrays.toString(matrix[2]),
        Arrays.toString(matrix[3]));
    }

    @Override
    public boolean equals(Object other) {
        if (this == other ) return true;
        if (other == null || other.getClass() != this.getClass()) return false;
        var mat4 = (Matrix4) other;
        for (int row = 0; row < dimension; row++) {
            for (int col = 0; col < dimension; col++) {
                if (compareFloat(this.matrix[row][col], mat4.matrix[row][col]) != 0) return false;
            }
        }
        return true;
    }

    @Override
    public Matrix getSubMatrix(int row, int column) {
        // TODO the logic in the method is the exact same as in Matrix2, and it a prime candidate for refactorization, pulling up into the parent class.
        if (row < 0 || column < 0 || row > dimension - 1 || column > dimension - 1)
            throw new IllegalArgumentException(String.format("row %d column %d is out of bounds for %d by %d matrix", row, column, dimension, dimension));
        double[][] rows = new double[dimension - 1][dimension - 1];
        int _row = 0, _col = 0;
        for (int r = 0; r < dimension; r++) {
            if (r == row) continue;
            for (int c = 0; c < dimension; c++) {
                if (c == column) continue;
                rows[_row][_col++] = this.matrix[r][c];
                if (_col == dimension - 1) {
                    _col = 0;
                    _row++;
                }
            }
        }
        return Matrix3.buildMatrixRow(
            Arrays.copyOf(rows[0], dimension - 1),
            Arrays.copyOf(rows[1], dimension - 1),
            Arrays.copyOf(rows[2], dimension - 1));
    }

    @Override
    public double getDeterminant() {
        // cofactor expansion technique
        double det = 0.0f;
        det += matrix[0][0] * getCofactor(0, 0);
        det += matrix[0][1] * getCofactor(0, 1);
        det += matrix[0][2] * getCofactor(0, 2);
        det += matrix[0][3] * getCofactor(0, 3);
        return det;
    }

}

