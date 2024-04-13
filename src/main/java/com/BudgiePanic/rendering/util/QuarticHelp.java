package com.BudgiePanic.rendering.util;

import static com.BudgiePanic.rendering.util.FloatHelp.compareFloat;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.ejml.data.DMatrixRMaj;
import org.ejml.dense.row.factory.DecompositionFactory_DDRM;
import org.ejml.interfaces.decomposition.EigenDecomposition_F64;

/**
 * Adapter class to interface with a quartic equation solver
 * 
 * @author BudgiePanic
 */
public final class QuarticHelp {
    private QuarticHelp() {}

    /**
     * Finds the real roots of a quartic equation in the form ax^4 + bx^3 + cx^2 +
     * dx + e = 0
     * 
     * @param a
     *          the first coefficient
     * @param b
     *          the second coefficient
     * @param c
     *          the third coefficient
     * @param d
     *          the fourth coefficient
     * @param e
     *          the constant term
     * @return
     *         The real roots of the equation. may be empty if no roots were found.
     */
    public static final List<Double> solveQuartic(double a, double b, double c, double d, double e) {
        // see: http://ejml.org/wiki/index.php?title=Example_Polynomial_Roots
        // TODO use a root finding algorithm that is for the specific case N=4
        // TODO the method used here is meant for N >= 5, there should be faster-more efficient algorithms for N=4 polynomials
        final int N = 4;
        final double[] coefficients = new double[] {e, d, c, b, a};
        DMatrixRMaj companion = new DMatrixRMaj(N, N);
        for (int i = 0; i < N; i++) {
            companion.set(i, N-1, -coefficients[i]/a);
        }
        for (int i = 1; i < N; i++) {
            companion.set(i, i-1, 1);
        }
        EigenDecomposition_F64<DMatrixRMaj> evd = DecompositionFactory_DDRM.eig(N, false);
        evd.decompose(companion);

        double[] real_roots = new double[N];
        double[] imag_roots = new double[N];
        for (int i = 0; i < N; i++) {
            var root = evd.getEigenvalue(i);
            real_roots[i] = root.getReal();
            imag_roots[i] = root.getImaginary();
        }
        List<Double> realRoots = new ArrayList<>(4);
        
        for (int i = 0; i < N; i++) {
            if (compareFloat(0f, (float)imag_roots[i]) == 0) {
                realRoots.add(real_roots[i]);
            }
        }

        Collections.sort(realRoots);
        return realRoots;
    }
}
