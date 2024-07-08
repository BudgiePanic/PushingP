package com.BudgiePanic.rendering.util.noise;

/**
 * Java implementation of Ken Perlin's 'improved noise' algorithm 
 * 
 * @see "Simplex noise, demystified. Includes explanation of Perlin noise."
 *          https://github.com/stegu/perlin-noise/blob/master/simplexnoise.pdf 
 *          https://github.com/stegu/perlin-noise 
 * @see "The original SIGGRAPH paper about Perlin noise"
 *          https://dl.acm.org/doi/pdf/10.1145/325165.325247
 * @see "A reference implementation of Perlin noise written in C#"
 *          https://gist.github.com/Flafla2/f0260a861be0ebdeef76
 * @see "an 'easy to follow' analysis of the Perlin noise algorithm"
 *          https://adrianb.io/2014/08/09/perlinnoise.html
 * @see "Speaker notes of a presentation given by Perlin, sharing details on the noise algorithm"
 *          https://web.archive.org/web/20071008165845/http://www.noisemachine.com/talk1/15.html
 * @see "Perlin's reference implementation of the noise algorithm"
 *          https://mrl.cs.nyu.edu/~perlin/noise/
 * @see "Perlin's improved noise algorithm" 
 *          https://mrl.cs.nyu.edu/~perlin/paper445.pdf
 * 
 * @author BudgiePanic
 */
public final class Perlin {
    private Perlin() {}

    /**
     * The perlin noise texture repeats after 256 (0-255) units, but "the features in the noise texture are soo small that zooming out far enough to see the pattern repeat won't show anything". 
     * @see https://web.archive.org/web/20071008170011/http://www.noisemachine.com/talk1/17.html
     */
    protected static final int noiseTextureSize = 255;

    /**
     * All the vectors from the center of a unit cube to its 12 edges. do not modify at runtime.  
     * These vectors are chosen over randomly generated vectors because they reduce directional artifacts in the noise output.
     * Each point in the integer lattice [x,y,z] will be pseudo randomly assigned one of these gradients via the hashing function.
     */
    protected static final double[][] gradients = {
        {1,1,0}, {-1,1,0}, {-1,-1,0}, {1,-1,0}, 
        {0,1,-1}, {0,1,1}, {0,-1,-1}, {0,-1,1}, 
        {-1,0,1}, {1,0,1}, {-1,0,-1}, {1,0,-1}, 
    };

    /**
     * The number of random gradients to choose from.
     */
    protected static final int numbGradients = gradients.length;

    /*
     * Unit cube points Q(x,y,z)
     * [0,0,0]  [1,0,0]  [1,0,1]  [0,0,1]
     * [0,1,0]  [1,1,0]  [1,1,1]  [0,1,1]
     */

    /**
     * Pseudo random gradient noise function.
     *
     * @param x
     *   x component of a point
     * @param y
     *   y component of a point
     * @param z
     *   z component of a point
     * @return
     *   Perlin noise at the point (xyz).
     */
    public final static double noise(double x, double y, double z) {
        // first step of the algorithm is to determine which unit cube within the integer lattice the point (xyz) is located inside of.
        /* this operation can be sped up by replacing Math.floor(x) with [ (x) => (x>0) ? (int)x : (int)x - 1; ] */
        int unitCube000x = (int) Math.floor(x); 
        int unitCube000y = (int) Math.floor(y); 
        int unitCube000z = (int) Math.floor(z);
        // for each of the 8 Q points in the unit cube that we landed in:
          // get the pseudo random gradient for the Q point
          // compute dot product between the Q point's gradient (G) vector and the vector: (P-Q) | (this value is the output of a linear function with gradient G, y=0 @ x=Q)
            // finding the vector P (the relative position of point inside of the unit cube)
        x = x - unitCube000x;
        y = y - unitCube000y;
        z = z - unitCube000z;
        /* 
           Wrap the unit cube location to avoid out of bounds array access, 
           this causes the noise to repeat (tiling can be achieved with some more changes)
           but Perlin asserts that the repetition is a non issue due to the size of the noise features 
        */
        unitCube000x &= noiseTextureSize; 
        unitCube000y &= noiseTextureSize; 
        unitCube000z &= noiseTextureSize;
            // calculating G for each Q point
              // determine the index into the gradient array for each Q point
        final int gIndex000 = hash(unitCube000x, unitCube000y, unitCube000z);    
        final int gIndex001 = hash(unitCube000x, unitCube000y, unitCube000z + 1);
        final int gIndex010 = hash(unitCube000x, unitCube000y + 1, unitCube000z);
        final int gIndex100 = hash(unitCube000x + 1, unitCube000y, unitCube000z);
        final int gIndex110 = hash(unitCube000x + 1, unitCube000y + 1, unitCube000z);
        final int gIndex011 = hash(unitCube000x, unitCube000y + 1, unitCube000z + 1);
        final int gIndex101 = hash(unitCube000x + 1, unitCube000y, unitCube000z + 1);
        final int gIndex111 = hash(unitCube000x + 1, unitCube000y + 1, unitCube000z + 1);
              // index into the gradient array for each Q point, getting their gradients
        final double[] g000 = gradients[gIndex000];  
        final double[] g001 = gradients[gIndex001];
        final double[] g010 = gradients[gIndex010];
        final double[] g100 = gradients[gIndex100];
        final double[] g110 = gradients[gIndex110];
        final double[] g011 = gradients[gIndex011];
        final double[] g101 = gradients[gIndex101];
        final double[] g111 = gradients[gIndex111];
          // compute dot product between G and (P-Q)
        final double dot000 = dot(g000, x,y,z); /*(P-Q000) => cube point Q to point P, in this specific case point [0,0,0], it is implicitly (x-0,y-0,z-0). use minus operator because its P+[-Q]*/
        final double dot001 = dot(g001, x,y,z-1); /*P-Q001 etc. etc. SEE: page 3/17 in the simplex noise reference*/
        final double dot010 = dot(g010, x,y-1,z); 
        final double dot100 = dot(g100, x-1,y,z); 
        final double dot110 = dot(g110, x-1,y-1,z); 
        final double dot011 = dot(g011, x,y-1,z-1); 
        final double dot101 = dot(g101, x-1,y,z-1); 
        final double dot111 = dot(g111, x-1,y-1,z-1); 
        
        // interpolation factors between the calculated values via an "s shape" cross fade curve (Perlin asserts this produces nicer results than simple lerp) 
        final double u = fade(x);
        final double v = fade(y);
        final double w = fade(z);
        // combine dot productes via trilinear interpolation 
          // combine values along x axis
        final double x00 = lerp(dot000, dot100, u);
        final double x01 = lerp(dot001, dot101, u);
        final double x10 = lerp(dot010, dot110, u);
        final double x11 = lerp(dot011, dot111, u);
          // combine combinations along y axis
        final double y0 = lerp(x00,x10,v);
        final double y1 = lerp(x01, x11, v);
          // combine final 2 combinations along z axis
        final double z0 = lerp(y0,y1,w);
        return z0;
    }
    
    /**
     * Pseudo random gradient noise function with power scaling.
     * Each power increase decreases the noise amplitude by a power of 2 [noise * (1, 0.5, 0.125, 0.0625, etc, etc)].
     * Each power increase will increase the sample point power location by a power of 2 [1,2,4,8,16, etc, etc].
     * 
     * @param x
     *   x component of a point
     * @param y
     *   y component of a point
     * @param z
     *   z component of a point
     * @param power
     *   The fractional power to calculate the noise with. Leave as zero for default results. Higher values makes noise features smaller and weaker. 
     *   Noise values beyond 3 will be extremely small. 
     * @return
     *   A pseudo random value between 0.0 and 1.0
     */
    public final static double noise(double x, double y, double z, double power) {
      // apply the power
      x = Math.pow(2, power) * x;
      y = Math.pow(2, power) * y;
      z = Math.pow(2, power) * z;
      final double noise = noise(x, y, z);
      // scale the noise amplitude down
      return Math.pow(2, -power) * noise; 
    }

    /**
     * A hashing function that outputs hash values between 0 and 12. Used to map a point to a gradient value array index.
     * @param x
     *   x component of the vector being hashed
     * @param y
     *   y component of the vector being hashed
     * @param z
     *   z component of the vector being hashed
     * @return
     *   A psuedo random hash value derived from (x,y,z)
     */
    private static int hash(int x, int y, int z) {
        assert x < perm.length && x >= 0; 
        assert y < perm.length && y >= 0;
        assert z < perm.length && z >= 0;
        return perm[ x + perm[ y + perm[z] ] ] % numbGradients;
    }

    /**
     * Computes the dot product between two 3 dimensional vectors.
     * @param g
     *   Vector a, encoded as a length 3 array of floats (the gradient vector of the Q point of interest)
     * @param x
     *   x component of vector b
     * @param y
     *   y component of vector b
     * @param z
     *   z component of vector b
     * @return
     *   the dot product between vector a and vector b.
     */
    private static double dot(double[] g, double x, double y, double z){
        assert g != null && g.length == 3;
        return (g[0] * x) + (g[1] * y) + (g[2] * z);
    }

    /**
     * Used in the hashing function to thrash the input values around.
     * Array of length 256, contains randomly generated, uniformly distributed, values between 0-255.
     * In this perlin noise implementation, we are simply caching the values that were chosen by Ken Perlin instead of generating our own.
     */
    private static final int[] perms = { 151,160,137,91,90,15,
        131,13,201,95,96,53,194,233,7,225,140,36,103,30,69,142,8,99,37,240,21,10,23,
        190, 6,148,247,120,234,75,0,26,197,62,94,252,219,203,117,35,11,32,57,177,33,
        88,237,149,56,87,174,20,125,136,171,168, 68,175,74,165,71,134,139,48,27,166,
        77,146,158,231,83,111,229,122,60,211,133,230,220,105,92,41,55,46,245,40,244,
        102,143,54, 65,25,63,161, 1,216,80,73,209,76,132,187,208, 89,18,169,200,196,
        135,130,116,188,159,86,164,100,109,198,173,186, 3,64,52,217,226,250,124,123,
        5,202,38,147,118,126,255,82,85,212,207,206,59,227,47,16,58,17,182,189,28,42,
        223,183,170,213,119,248,152, 2,44,154,163, 70,221,153,101,155,167, 43,172,9,
        129,22,39,253, 19,98,108,110,79,113,224,232,178,185, 112,104,218,246,97,228,
        251,34,242,193,238,210,144,12,191,179,162,241, 81,51,145,235,249,14,239,107,
        49,192,214, 31,181,199,106,157,184, 84,204,176,115,121,50,45,127, 4,150,254,
        138,236,205,93,222,114,67,29,24,72,243,141,128,195,78,66,215,61,156,180
    };

    /**
     * A bigger version perms, so we don't need to worry about index overflow in the hasing function.
     * Is simply perms repeated twice.
     */
    private static final int[] perm;

    /**
     * Fill up perm with values at class initialization
     */
    static {
        final int size = (noiseTextureSize + 1);
        perm = new int[size * 2]; 
        for (int i = 0; i < size; i++) {    
            perm[i] = perms[i];
            perm[size + i] = perms[i]; // a copy
        }
    }

    /**
     * Determines the interpolation amount between different dot products.
     * This scaled interpolation is used in favor of a simple linear interpolation between the gradients.
     * @param t
     *   relative position of the point in the unit cube along one axis
     * @return
     *   fade = (6t^5) - (15t^4) + (10t^3)
     */
    private static double fade(double t) {
        // (6t^5) - (15t^4) + (10t^3) => (6*(t*t*t*t*t)) - (15*(t*t*t*t)) + (10*(t*t*t));
        // t^4(6t - 15) + 10t^3             // pull out t^4 
        // t^3 * (t(6t - 15) + 10)          // pull out t^3
        // t^3 * (t*(6t-15)+10)             // more computationally efficient implementation
        // t*t*t*(t*(6*t-15)+10)            // unwrap the t^3
        return t*t*t*((t*(6*t-15))+10);
    }

    /**
     * The humble linear interpolation
     * @param a
     *   first number
     * @param b
     *   second number
     * @param amount
     *   the proportion of b add, the proportion of a to remove from the output
     * @return
     *   a quantity between a and b
     */
    protected static double lerp(double a, double b, double amount) {
        return ((1-amount) * a) + (amount * b);
    }
}
