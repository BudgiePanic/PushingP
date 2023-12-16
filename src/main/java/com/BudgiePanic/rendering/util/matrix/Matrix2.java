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
        float _00,
        float _01,
        float _10,
        float _11
    ){
        final float[][] matrix = new float[dimension][dimension];

        matrix[0][0] = _00; matrix[1][0] = _10;
        matrix[0][1] = _01; matrix[1][1] = _11;

        return new Matrix2(matrix);
    }

    /**
     * Help method. Check internal float arrays are the correct dimension and non null.
     * @param item
     *   The internal array to check.
     */
    private static void checkSize(float[] item){
        if (item == null || item.length != dimension) throw new IllegalArgumentException("matrix elements must be length 2 and not be null.");
    }

    /**
     * Build a matrix by manually specifiying rows.
     * @param row0
     * @param row1
     * @param row2
     * @return
     */
    public static Matrix2 buildMatrixRow(final float[] row0, final float[] row1) {
        checkSize(row0); checkSize(row1);
        float[][] matrix = new float[dimension][];
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
    public static Matrix2 buildMatrixColumn(final float[] column0, final float[] column1){
        checkSize(column0); checkSize(column1);
        float[][] matrix = new float[dimension][dimension];

        for(int i = 0; i < dimension; i++){
            matrix[i][0] = column0[i];
            matrix[i][1] = column1[i];
        }

        return new Matrix2(matrix);
    }

    protected Matrix2(float[][] matrix) {
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
    public float getDeterminant() {
        return (matrix[0][0] * matrix[1][1]) - (matrix[0][1] * matrix[1][0]);
    }

    @Override
    public Matrix getSubMatrix(int row, int column) {
        throw new UnsupportedOperationException("Cannot make sub matrix from 2 by 2 matrix.");
    }

    @Override
    public float getMinor(int row, int column) {
        throw new UnsupportedOperationException("Cannot calculate minor with 2 by 2 matrix.");
    }

}
