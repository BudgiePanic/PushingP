package com.BudgiePanic.rendering.io;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import com.BudgiePanic.rendering.util.Material;
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
     * @param
     *   The transform to apply to the group.
     * @return
     *   A group containing object shapes.
     */
    public static Group objectToGroup(ObjectData object, Matrix4 transform) {
        final Group uberGroup = new Group(transform);
        if (object.groups().isEmpty()) {
            System.out.println("INFO: could not find any groups, making new group from raw triangles");
            uberGroup.addShape(object.rawTriangles(transform));
        } else {
            System.out.println("INFO: building uber group from object's groups");
            for (var group : object.groups) {
                uberGroup.addShape(group.b());
            }
        }
        return uberGroup;
    }

    /**
     * Places the object's internal groups into an uber group for rendering. Ubergroup will have default transform.
     * @param object
     *   The object data.
     * @return
     *   A group containing object shapes.
     */
    public static Group objectToGroup(ObjectData object) { return objectToGroup(object, Matrix4.identity()); }

    /**
     * Create a shape group from a wavefront obj object with the default material.
     * @param lines
     *   Lines of text from the obj file.
     * @return
     *   Geometry information extracted from the obj file.
     */
    public static ObjectData parseObj(List<String> lines) { return parseObj(lines, Material.defaultMaterial()); }

    /**
     * Create a shape group from a wavefront obj object with the default material.
     * @param lines
     *   Lines of text from the obj file.
     * @param material
     *   The material to apply to the generated triangles.
     * @return
     *   Geometry information extracted from the obj file.
     */
    public static ObjectData parseObj(List<String> lines, Material material) {
        // TODO this method needs code improvement
        final List<Tuple> vertices = new ArrayList<>();
        final List<Triangle> triangles = new ArrayList<>();
        final List<String> groupNames = new ArrayList<>();
        final List<Group> groups = new ArrayList<>();
        int linesSkipped = 0;
        Optional<Group> currentGroup = Optional.empty();
        Optional<String> currentName = Optional.empty();
        vertices.add(null); // the WF OBJ file format is 1 indexed, not 0 indexed.

        for (var line : lines) {
            if (line.isEmpty()) {
                linesSkipped++;
                continue;
            }
            String[] tokens = line.split(" ");
            if (line.charAt(0) == 'v') {
                // might be a vertex
                // try to read tokens 1 - 3 as floats
                if (tokens.length != 4) {
                    System.out.println("WARN: could not process line [" + line + "] as vertex");
                    linesSkipped++;
                    continue;
                }
                try {
                    float x = Float.parseFloat(tokens[1]);
                    float y = Float.parseFloat(tokens[2]);
                    float z = Float.parseFloat(tokens[3]);
                    var vertex = Tuple.makePoint(x, y, z);
                    vertices.add(vertex);
                } catch (NumberFormatException e) {
                    linesSkipped++;
                    System.out.println("WARN: could not process line [" + line + "] as vertex");
                    continue;
                }
                // parsed line successfully
                continue;
            }
            if (line.charAt(0) == 'f') {
                // might be a face (triangle)
                // a face has 3 or more vertices + 1 identifier char at position 0
                // if there are more than 3, we must triangulate the face ourselves
                // if a group is active, this face should be added to the active group
                if (tokens.length < 4) {
                    System.out.println("WARN: could not process line [" + line + "] as face");
                    linesSkipped++;
                    continue;
                }
                if (tokens.length > 4) { // need to triangulate
                    try {
                        List<Tuple> verts = Arrays.stream(tokens).skip(1).map(Integer::parseInt).map(vertices::get).toList();
                        // assuming a convex polygon, whose internal angles sum to 180 degrees or less, allows for fan triangulation
                        for (int i = 1; i < tokens.length - 1; i++) {
                            Tuple p1 = verts.get(0);
                            Tuple p2 = verts.get(i);
                            Tuple p3 = verts.get(i + 1);
                            Triangle triangle = new Triangle(p1, p2, p3, material);
                            triangles.add(triangle);
                        }
                    } catch (NumberFormatException | ArrayIndexOutOfBoundsException e) {
                        linesSkipped++;
                        System.out.println("WARN: could not process line [" + line + "] as face");
                        continue;
                    }
                }
                // try get the verts specified, if all found, build a triangle and save it
                try {
                    int index1 = Integer.parseInt(tokens[1]);
                    int index2 = Integer.parseInt(tokens[2]);
                    int index3 = Integer.parseInt(tokens[3]);
                    Tuple p1 = vertices.get(index1);
                    Tuple p2 = vertices.get(index2);
                    Tuple p3 = vertices.get(index3);
                    Triangle triangle = new Triangle(p1, p2, p3, material);
                    triangles.add(triangle);
                    if (currentGroup.isPresent()) {
                        currentGroup.get().addShape(triangle);
                    }
                } catch (NumberFormatException | ArrayIndexOutOfBoundsException e) {
                    linesSkipped++;
                    System.out.println("WARN: could not process line [" + line + "] as face");
                    continue;
                }
                // parsed line successfully
                continue;
            }
            if (line.charAt(0) == 'g') {
                // might be a group
                // assuming group names have to be one token long
                if (tokens.length != 2 && tokens[0].length() != 1) {
                    linesSkipped++;
                    System.out.println("WARN: could not process line [" + line + "] as group identifier");
                    continue;
                }
                // add the current group to the groups list
                if (currentGroup.isPresent()) {
                    groups.add(currentGroup.get());
                    assert currentName.isPresent();
                    groupNames.add(currentName.get());
                }
                // update the current group to a new group and group name
                currentGroup = Optional.of(new Group(Matrix4.identity()));
                currentName = Optional.of(tokens[1]);
                // parsed line successfully
                continue;
            }
            // did not match against any known lines
            linesSkipped++;
        }

        // add the currentGroup, if it exists
        if (currentGroup.isPresent()) {
            groups.add(currentGroup.get());
            assert currentName.isPresent();
            groupNames.add(currentName.get());
        }

        if (linesSkipped == lines.size()) {
            System.out.println("WARN: couldn't process any information from OBJ file");
        }

        assert groups.size() == groupNames.size();
        final List<Pair<String, Group>> groupas = new ArrayList<>();
        for (int i = 0; i < groups.size(); i++) {
            groupas.add(new Pair<String,Group>(groupNames.get(i), groups.get(i)));
        }
        return new ObjectData(linesSkipped, vertices, triangles, groupas);
    }

}
