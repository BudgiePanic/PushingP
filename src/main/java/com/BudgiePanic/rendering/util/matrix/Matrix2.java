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

public final class Matrix2 extends Matrix {

    private static final int dimension = 2;

    /**
     * Build a matrix by manually specifying values.
     * TODO this matrix building logic is currently repeated 3 times in matrix4, matrix3, and matrix2
     * This logic could be pulled up into a matrix factory class to avoid code duplication in the future.
     * The same is also true for the build from rows, and build from column methods.
     *
     * @param _00
     * @param _01
     * @param _10
     * @param _11
     * @return
     *   A two by two matrix
     */
    public static Matrix2 buildMatrix(
        double _00,
        double _01,
        double _10,
        double _11
    ){
        final double[][] matrix = new double[dimension][dimension];

        matrix[0][0] = _00; matrix[1][0] = _10;
        matrix[0][1] = _01; matrix[1][1] = _11;

        return new Matrix2(matrix);
    }

    /**
     * Help method. Check internal double arrays are the correct dimension and non null.
     * @param item
     *   The internal array to check.
     */
    private static void checkSize(double[] item){
        if (item == null || item.length != dimension) throw new IllegalArgumentException("matrix elements must be length 2 and not be null.");
    }

    /**
     * Build a matrix by manually specifiying rows.
     * @param row0
     * @param row1
     * @param row2
     * @return
     */
    public static Matrix2 buildMatrixRow(final double[] row0, final double[] row1) {
        checkSize(row0); checkSize(row1);
        double[][] matrix = new double[dimension][];
        matrix[0] = row0;
        matrix[1] = row1;
        return new Matrix2(matrix);
    }

    /**
     * Build a matrix by manually specifying columns.
     * @param column0
     * @param column1
     * @param column2
     * @return
     */
    public static Matrix2 buildMatrixColumn(final double[] column0, final double[] column1){
        checkSize(column0); checkSize(column1);
        double[][] matrix = new double[dimension][dimension];

        for(int i = 0; i < dimension; i++){
            matrix[i][0] = column0[i];
            matrix[i][1] = column1[i];
        }

        return new Matrix2(matrix);
    }

    protected Matrix2(double[][] matrix) {
        super(matrix);
    }

    @Override
    public void validate() throws MatrixShapeException {
        if (this.matrix == null || this.matrix.length != dimension) throw new MatrixShapeException("matrix does not have 2 rows");
        if (this.matrix[0] == null || this.matrix[0].length != dimension) throw new MatrixShapeException("matrix row 0 was malformed."); 
        if (this.matrix[1] == null || this.matrix[1].length != dimension) throw new MatrixShapeException("matrix row 1 was malformed."); 
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
        return String.format("[%s, %s]", 
        Arrays.toString(matrix[0]),
        Arrays.toString(matrix[1]));
    }
    
    @Override
    public boolean equals(Object other) {
        if (this == other ) return true;
        if (other == null || other.getClass() != this.getClass()) return false;
        var mat2 = (Matrix2) other;
        for (int row = 0; row < dimension; row++) {
            for (int col = 0; col < dimension; col++) {
                if (compareFloat(this.matrix[row][col], mat2.matrix[row][col]) != 0) return false;
            }
        }
        return true;
    }

    @Override
    public double getDeterminant() {
        return (matrix[0][0] * matrix[1][1]) - (matrix[0][1] * matrix[1][0]);
    }

    @Override
    public Matrix getSubMatrix(int row, int column) {
        throw new UnsupportedOperationException("Cannot make sub matrix from 2 by 2 matrix.");
    }

    @Override
    public double getMinor(int row, int column) {
        throw new UnsupportedOperationException("Cannot calculate minor with 2 by 2 matrix.");
    }

    @Override
    public double getCofactor(int row, int column) {
        throw new UnsupportedOperationException("Matrix2 does not support cofactor operation");
    }

}
