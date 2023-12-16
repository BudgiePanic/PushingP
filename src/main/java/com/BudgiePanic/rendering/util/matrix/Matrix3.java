package com.BudgiePanic.rendering.util.matrix;

import static com.BudgiePanic.rendering.util.FloatHelp.compareFloat;

import java.util.Arrays;

public final class Matrix3 extends Matrix {

    private static final int dimension = 3;

    /**
     * Build a matrix by manually specifying values.
     * TODO this matrix building logic is currently repeated 3 times in matrix4, matrix3, and matrix2
     * This logic could be pulled up into a matrix factory class to avoid code duplication in the future.
     * The same is also true for the build from rows, and build from column methods.
     *
     * @param _00
     * @param _01
     * @param _02
     * @param _10
     * @param _11
     * @param _12
     * @param _20
     * @param _21
     * @param _22
     * @return
     *   A three by three matrix
     */
    public static Matrix3 buildMatrix(
        float _00,
        float _01,
        float _02,
        float _10,
        float _11,
        float _12,
        float _20,
        float _21,
        float _22
    ){
        final float[][] matrix = new float[dimension][dimension];

        matrix[0][0] = _00; matrix[1][0] = _10; matrix[2][0] = _20;
        matrix[0][1] = _01; matrix[1][1] = _11; matrix[2][1] = _21;
        matrix[0][2] = _02; matrix[1][2] = _12; matrix[2][2] = _22;

        return new Matrix3(matrix);
    }

    /**
     * Help method. Check internal float arrays are the correct dimension and non null.
     * @param item
     *   The internal array to check.
     */
    private static void checkSize(float[] item){
        if (item == null || item.length != dimension) throw new IllegalArgumentException("matrix elements must be length 3 and not be null.");
    }

    /**
     * Build a matrix by manually specifiying rows.
     * @param row0
     * @param row1
     * @param row2
     * @return
     */
    public static Matrix3 buildMatrixRow(final float[] row0, final float[] row1, final float[] row2) {
        checkSize(row0); checkSize(row1); checkSize(row2);
        float[][] matrix = new float[dimension][];
        matrix[0] = row0;
        matrix[1] = row1;
        matrix[2] = row2;
        return new Matrix3(matrix);
    }

    /**
     * Build a matrix by manually specifying columns.
     * @param column0
     * @param column1
     * @param column2
     * @return
     */
    public static Matrix3 buildMatrixColumn(final float[] column0, final float[] column1, final float[] column2){
        checkSize(column0); checkSize(column1); checkSize(column2);
        float[][] matrix = new float[dimension][dimension];

        for(int i = 0; i < dimension; i++){
            matrix[i][0] = column0[i];
            matrix[i][1] = column1[i];
            matrix[i][2] = column2[i];
        }

        return new Matrix3(matrix);
    }

    protected Matrix3(float[][] matrix) {
        super(matrix);
    }

    @Override
    public void validate() throws MatrixShapeException {
        if (this.matrix == null || this.matrix.length != dimension) throw new MatrixShapeException("matrix does not have 3 rows");
        if (this.matrix[0] == null || this.matrix[0].length != dimension) throw new MatrixShapeException("matrix row 0 was malformed."); 
        if (this.matrix[1] == null || this.matrix[1].length != dimension) throw new MatrixShapeException("matrix row 1 was malformed."); 
        if (this.matrix[2] == null || this.matrix[2].length != dimension) throw new MatrixShapeException("matrix row 2 was malformed."); 
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
        return String.format("[%s, %s, %s]", 
        Arrays.toString(matrix[0]),
        Arrays.toString(matrix[1]),
        Arrays.toString(matrix[2]));
    }
    
    @Override
    public boolean equals(Object other) {
        if (this == other ) return true;
        if (other == null || other.getClass() != this.getClass()) return false;
        var mat3 = (Matrix3) other;
        for (int row = 0; row < dimension; row++) {
            for (int col = 0; col < dimension; col++) {
                if (compareFloat(this.matrix[row][col], mat3.matrix[row][col]) != 0) return false;
            }
        }
        return true;
    }

    @Override
    public Matrix getSubMatrix(int row, int column) {
        if (row < 0 || column < 0 || row > dimension - 1 || column > dimension - 1)
            throw new IllegalArgumentException(String.format("row %d column %d is out of bounds for %d by %d matrix", row, column, dimension, dimension));
        float[][] rows = new float[dimension - 1][dimension - 1];
        int _row = 0, _col = 0;
        for (int r = 0; r < dimension; r++) {
            if (r == row) continue;
            for (int col = 0; col < dimension; col++) {
                if (col == column) continue;
                rows[_row][_col++] = this.matrix[r][col];
                if (_col == dimension - 1) {
                    _col = 0;
                    _row++;
                }
            }
        }
        return Matrix2.buildMatrixRow(rows[0], rows[1]);
    }

}
