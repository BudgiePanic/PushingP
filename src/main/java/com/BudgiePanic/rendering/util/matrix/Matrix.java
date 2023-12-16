package com.BudgiePanic.rendering.util.matrix;

/**
 * Top level type for matrices to capture common operations.
 * 
 * @author BudgiePanic
 */
public abstract class Matrix {

    /**
     * Checked exception for malformed 4 by 4 matrices.
     */
    public final static class MatrixShapeException extends Exception {
        public MatrixShapeException() { super(); }
        public MatrixShapeException(String message) { super(message); }
    }

    /**
     * Matrix values.
     * Even though this array is final, the internal elements can still be assigned because java has no mechianism to make arrays immutable without 
     * wrapper methods or defensive cloning.
     * For now (and for performance reasons) we will just have to trust that none of the other code will be tamporing with our matrices :)
     */
    public final float[][] matrix;

    protected Matrix(final float[][] matrix){
        this.matrix = matrix;
    }

    /**
     * Check that this matrix is in getDimension() by getDimension().
     * 
     * @throws MatrixShapeException
     *     thrown if the matrix has a non getDimension() by getDimension() shape, including if a row is null.
     */
    public abstract void validate() throws MatrixShapeException;

    /**
     * Get the dimension of this array
     *
     * @return
     *     The dimension of the array.
     */
    public abstract int getDimension();

    /**
     * Computes the determinant for this matrix.
     *
     * @return
     *   The determinant of the matrix.
     */
    public abstract float getDeterminant();

    /**
     * A copy of this matrix with 'row' and 'column' removed.
     * @param row
     *   The row to remove.
     * @param column
     *   The column to remove.
     * @return
     *   A smaller matrix with row and column removed.
     */
    public abstract Matrix getSubMatrix(int row, int column);

    /**
     * Computes the determinant of the submatrix at [row, column].
     * @param row
     *   The row to exclude
     * @param column
     *   The columt to exclude
     * @return
     *   The determinant of the submatrix
     */
    public float getMinor(int row, int column) {
        return getSubMatrix(row, column).getDeterminant();
    };

    /**
     * Determine the cofactor of this matrix.
     *
     * @param row
     *   The row to exclude
     * @param column
     *   The columt to exclude
     * @return
     *   The cofactor of the matrix.
     */
    public float getCofactor(int row, int column) {
        var minor = getMinor(row, column);
        if ((row + column) % 2 != 0) {
            minor *= -1f;
        }
        return minor;
    }
}
