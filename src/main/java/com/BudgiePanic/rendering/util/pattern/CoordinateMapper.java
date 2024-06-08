package com.BudgiePanic.rendering.util.pattern;

import static com.BudgiePanic.rendering.util.Tuple.makeVector;

import com.BudgiePanic.rendering.util.Tuple;

/**
 * Coordinate system remapping functions to go from 3D point on a shape to 2D point on a texture.
 * 
 * @author BudgiePanic
 */
public sealed abstract class CoordinateMapper permits CoordinateMapper.Sphere, CoordinateMapper.Planar, CoordinateMapper.Cylindircal, CoordinateMapper.Cube {
    private CoordinateMapper() {}

    /**
     * Remaps points onto a sphere surface.
     */
    protected static final class Sphere extends CoordinateMapper {
        @Override
        public final double vMap(final Tuple point) {
            final Tuple vector = makeVector(point.x, point.y, point.z);
            final double radius = vector.magnitude();
            final double phi = Math.acos(point.y / radius); // polar angle
            return 1.0 - phi / Math.PI;
        }
        @Override
        public final double uMap(final Tuple point) {
            final double theta = Math.atan2(point.x, point.z); // azimuth
            final double u = theta / (2 * Math.PI); // remap from [-pi to pi] to [-0.5 to 0.5]
            return 1.0 - (u + 0.5); // remap to be in range [0 to 1] and flip direction
        }
    }

    /**
     * Remaps 3D points onto a XY plane.
     */
    protected static final class Planar extends CoordinateMapper {
        @Override
        public double vMap(Tuple point) { return modulo(point.z); }
        @Override
        public double uMap(Tuple point) { return modulo(point.x); }
    }

    /**
     * modulo operation where the result is converted to the sign of the dividend.
     * @param value
     * @return
     */
    private final static double modulo(double value) {
        value = value % 1.0;
        return value < 0 ? value + 1.0 : value;
    }

    /**
     * Remaps 3D points onto a cylindrical surface.
     */
    protected static final class Cylindircal extends CoordinateMapper {
        @Override
        public double vMap(Tuple point) { return modulo(point.y); }
        @Override
        public double uMap(Tuple point) {
            final double theta = Math.atan2(point.x, point.z);
            final double u = theta / (2.0 * Math.PI);
            return 1.0 - (u + 0.5);
        }
    }

    /**
     * Remaps points onto a cube surface.
     */
    protected static final class Cube extends CoordinateMapper {

        /**
         * The six cube faces
         */
        static enum Face {
            Front {
                @Override
                final double u(Tuple point) { return plusOne(point.x); }
                @Override
                final double v(Tuple point) { return plusOne(point.y); }
            },
            Up {
                @Override
                final double u(Tuple point) { return plusOne(point.x); }
                @Override
                final double v(Tuple point) { return oneMinus(point.z); }
            },
            Down {
                @Override
                final double u(Tuple point) { return plusOne(point.x); }
                @Override
                final double v(Tuple point) { return plusOne(point.z); }
            },
            Back {
                @Override
                final double u(Tuple point) { return oneMinus(point.x); }
                @Override
                final double v(Tuple point) { return plusOne(point.y); }
            },
            Left {
                @Override
                final double u(Tuple point) { return plusOne(point.z); }
                @Override
                final double v(Tuple point) { return plusOne(point.y); }
            },
            Right {
                @Override
                final double u(Tuple point) { return oneMinus(point.z); }
                @Override
                final double v(Tuple point) { return plusOne(point.y); }
            };
            
            private static final double oneMinus(final double value) { return ((1 - value) % 2.0) * 0.5; }
            private static final double plusOne(final double value) { return ((value + 1) % 2.0) * 0.5; }

            abstract double u(Tuple point);
            abstract double v(Tuple point);
        }

        /**
         * Determine which face of the unit cube the point lies on.
         *
         * @param point
         *   A point in local space.
         * @return
         *   The cube face the point is on.
         */
        protected final static Face getFace(Tuple point) {
            final double absX = Math.abs(point.x), absY = Math.abs(point.y), absZ = Math.abs(point.z);
            final double maxCoord = Math.max(Math.max(absX, absY), absZ);
            if (maxCoord == point.x) { return Face.Right; }
            if (maxCoord == -point.x) { return Face.Left; }
            if (maxCoord == point.y) { return Face.Up; }
            if (maxCoord == -point.y) { return Face.Down; }
            if (maxCoord == point.z) { return Face.Front; }
            return Face.Back;
        }

        @Override
        public double vMap(Tuple point) { return getFace(point).v(point); }

        @Override
        public double uMap(Tuple point) { return getFace(point).u(point); }
    }

    /**
     * Sphere surface remapping functions.
     */
    public static final CoordinateMapper sphere = new Sphere();

    /**
     * Flat surface remapping functions.
     */
    public static final CoordinateMapper planar = new Planar();

    /**
     * Cylinder surface remapping functions.
     */
    public static final CoordinateMapper cylindircal = new Cylindircal();

    /**
     * Cube surface remapping functions.
     */
    public static final CoordinateMapper cube = new Cube();

    /**
     * Project a point on a shape in 3D space onto a 2D plane.
     *
     * @param point
     *   The point in local space.
     * @return
     *   The v coordinate of the point projected onto a flat surface.
     */
    public abstract double vMap(Tuple point);

    /**
     * Project a point on a shape in 3D space onto a 2D plane.
     *
     * @param point
     *   The point in local space.
     * @return
     *   The u coordinate of the point projected onto a flat surface.
     */
    public abstract double uMap(Tuple point);
}
