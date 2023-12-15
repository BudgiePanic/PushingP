package com.BudgiePanic.rendering.util.matrix;

import java.util.Arrays;

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
     * Build a matrix by manually specifying values.
     *
     * @return
     */
    public static Matrix4 buildMatrix(
        float _00,
        float _01,
        float _02,
        float _03,
        float _10,
        float _11,
        float _12,
        float _13,
        float _20,
        float _21,
        float _22,
        float _23,
        float _30,
        float _31,
        float _32,
        float _33
    ){
        final float[][] matrix = new float[dimension][dimension];

        matrix[0][0] = _00; matrix[1][0] = _10; matrix[2][0] = _20; matrix[3][0] = _30;
        matrix[0][1] = _01; matrix[1][1] = _11; matrix[2][1] = _21; matrix[3][1] = _31;
        matrix[0][2] = _02; matrix[1][2] = _12; matrix[2][2] = _22; matrix[3][2] = _32;
        matrix[0][3] = _03; matrix[1][3] = _13; matrix[2][3] = _23; matrix[3][3] = _33;

        return new Matrix4(matrix);
    }

    /**
     * Help method. Check internal float arrays are the correct dimension and non null.
     * @param item
     *   The internal array to check.
     */
    private static void checkSize(float[] item){
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
    public static Matrix4 buildMatrixRow(final float[] row0, final float[] row1, final float[] row2, final float[] row3) {
        checkSize(row0); checkSize(row1); checkSize(row2); checkSize(row3);
        float[][] matrix = new float[dimension][];
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
    public static Matrix4 buildMatrixColumn(final float[] column0, final float[] column1, final float[] column2, final float[] column3){
        checkSize(column0); checkSize(column1); checkSize(column2); checkSize(column3);
        float[][] matrix = new float[dimension][dimension];

        for(int i = 0; i < dimension; i++){
            matrix[i][0] = column0[i];
            matrix[i][1] = column1[i];
            matrix[i][2] = column2[i];
            matrix[i][3] = column3[i];
        }

        return new Matrix4(matrix);
    }


    /**
     * Private constructor.
     * 
     * Private to try and help reduce aliasing of internal Arrays between matrices, because they are mutable.
     * 
     * @param matrix
     *     The internal matrix structure.
     */
    private Matrix4(final float[][] matrix){
        super(matrix);
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
        final float[][] matrix = new float[dimension][dimension];

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
            if (!Arrays.equals(this.matrix[row], mat4.matrix[row])) return false;
        }
        return true;
    }

}

