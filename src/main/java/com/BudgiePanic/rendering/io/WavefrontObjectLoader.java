package com.BudgiePanic.rendering.io;

import java.util.List;

import com.BudgiePanic.rendering.util.Pair;
import com.BudgiePanic.rendering.util.Tuple;
import com.BudgiePanic.rendering.util.matrix.Matrix4;
import com.BudgiePanic.rendering.util.shape.Group;
import com.BudgiePanic.rendering.util.shape.Triangle;

/**
 * Wavefront OBJ file loader. Creates shape groups consisting of triangles from the model data.
 * @author BudgiePanic
 */
public class WavefrontObjectLoader {

    /**
     * Container for the data that is extracted from object files.
     * 
     * A group containing all the triangles in the object can be created at the callers convience by adding all the triangles to a group (ObjectData::rawTriangles)
     * likewise, all the groups can be added to a single top level group at the callers convience (WavefrontObjectLoader::objectToGroup)
     */
    public static record ObjectData(int linesSkipped, List<Tuple> verticies, List<Triangle> triangles, List<Pair<String, Group>> groups) {
        /**
         * Wavefront object data container.
         * @param linesSkipped
         *   The number of lines in the obj file that were empty or could not be processed and were subsequently skipped
         * @param verticies
         *   A list of all the vertices in the object
         * @param triangles
         *   A list of all the triangles in the object
         * @param groups
         *   A list of all the groups of triangles in the object, accompanied by the group name.
         *   Object groups will always contain Triangle Shapes.
         */
        public ObjectData(int linesSkipped, List<Tuple> verticies, List<Triangle> triangles, List<Pair<String, Group>> groups) {
            this.linesSkipped = linesSkipped; this.verticies = verticies; this.triangles = triangles; this.groups = groups;
        }

        /**
         * Stuffs all the object's triangles into an unoptimized uber group.
         * @return
         *   A group containing all of the triangles in the object.
         */
        public Group rawTriangles() { return rawTriangles(Matrix4.identity()); }

        /**
         * Stuffs all the object's triangles into an unoptimized uber group.
         * @param transform
         *   group transform.
         * @return
         *   A group containing all of the triangles in the object.
         */
        public Group rawTriangles(Matrix4 transform) {
            Group group = new Group(transform);
            for (var triangle: triangles) {
                group.addShape(triangle);
            }
            return group;
        }
    }
    
    private WavefrontObjectLoader () {}

    /**
     * Places the object's internal groups into an uber group for rendering.
     * @param object
     *   The object data.
     * @return
     *   A group containing object shapes.
     */
    public static Group objectToGroup(ObjectData object) {
        return null;
    }

    public static ObjectData parseObj(List<String> lines) {
        return null;
    }

}
