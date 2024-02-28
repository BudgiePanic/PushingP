package com.BudgiePanic.rendering.io;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.regex.Pattern;

import com.BudgiePanic.rendering.util.Material;
import com.BudgiePanic.rendering.util.Pair;
import com.BudgiePanic.rendering.util.Tuple;
import com.BudgiePanic.rendering.util.matrix.Matrix4;
import com.BudgiePanic.rendering.util.shape.Group;
import com.BudgiePanic.rendering.util.shape.SmoothTriangle;
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
    public static record ObjectData(int linesSkipped, List<Tuple> normals, List<Tuple> verticies, List<Triangle> triangles, List<Pair<String, Group>> groups) {
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
        public ObjectData(int linesSkipped, List<Tuple> normals, List<Tuple> verticies, List<Triangle> triangles, List<Pair<String, Group>> groups) {
            this.linesSkipped = linesSkipped; this.normals = normals; this.verticies = verticies; this.triangles = triangles; this.groups = groups;
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

    /**
     * Map from line type identifier token to line parser
     */
    protected final Map<String, LineParser<?>> parsers;

    /**
     * 
     * @param material
     *   The material the object will be made out of.
     */
    private WavefrontObjectLoader (Material material) {
        final var faceParser = new FaceParser(new VertexParser(), new VertexNormalParser(), new GroupParser(), material);
        parsers = Map.ofEntries(
            Map.entry("f", faceParser),
            Map.entry("v", faceParser.vertices),
            Map.entry("vn", faceParser.normals),
            Map.entry("g", faceParser.groups)
        );
    }

    /**
     * Line parsers consume lines of input from the wavefront obj file.
     */
    protected static interface LineParser<T> {

        /**
         * Parse a line of wf obj file input.
         *
         * @param tokens
         *   The tokens from the line of text being parsed
         * @return
         *   Whether the line was successfully parsed by this parser.
         */
        boolean parseLine(String[] tokens);

        /**
         * Add an observer to this parser that will be notified when a datum is successfully parsed by this parser 
         * @param observer
         *   The object that wants to know when the line parser successfully parses a datum
         */
        default void addObserver(Observer<T> observer) { getObservers().add(observer); }
        /**
         * Tell the observers that a datum was successfully parsed.
         */
        default void emitCollectionEvent(T datum) { getObservers().forEach(o -> o.collected(datum)); }

        /**
         * Get the observers of this line parser.
         * @return
         *   The observers of the line parsers.
         */
        Collection<Observer<T>> getObservers();

        /**
         * Get the data collected by this line parser so far
         * @return
         *   The data collected by this line parser
         */
        List<T> collect();
    }

    /**
     * An object that recieves updates when a line parser successfully parses a datum from the obj file.
     */
    protected static interface Observer<D> {
        /**
         * 
         * @param datum
         */
        void collected(D datum);
    }

    /**
     * core functionality of all line parsers
     */
    protected static abstract class BaseParser<T> implements LineParser<T> {
        protected final List<T> data = new ArrayList<>();
        protected final List<Observer<T>> observers = new ArrayList<>(1);
        @Override 
        public Collection<Observer<T>> getObservers() { return observers; }
        @Override 
        public List<T> collect() { return Collections.unmodifiableList(data); }
        protected void printParseFailMessage(String[] tokens) { System.out.println("WARN: " + this.getClass().getSimpleName() + " could not process " + Arrays.toString(tokens)); }
    }

    /**
     * The vertex parser parses vertex lines which start with "v".
     */
    protected static class VertexParser extends BaseParser<Tuple> {
        public VertexParser() { this.data.add(null); } // need to add a dummy at position 0 because obj files use 1 indexing.
        @Override
        public boolean parseLine(String[] tokens) {
            // if you wanted to be real wack, you could refactor out this validation check out of all the parsers and push 
            // the validation logic somewhere else, technically these methods are each doing 2 things, validation and parsing.
            if (tokens.length != 4) { 
                printParseFailMessage(tokens);
                return false;
            }
            try {
                float x = Float.parseFloat(tokens[1]);
                float y = Float.parseFloat(tokens[2]);
                float z = Float.parseFloat(tokens[3]);
                var vertex = Tuple.makePoint(x, y, z);
                data.add(vertex);
                emitCollectionEvent(vertex);
                return true;
            } catch (NumberFormatException e) {
                printParseFailMessage(tokens);
                return false;
            }
        }
    }

    /**
     * VertexNormalParser parses vertex normal lines, which start with "vn".
     */
    protected static class VertexNormalParser extends BaseParser<Tuple> {
        public VertexNormalParser() { this.data.add(null); } // need to add a dummy at position 0 because obj files use 1 indexing.
        @Override
        public boolean parseLine(String[] tokens) {
            if (tokens.length != 4) { printParseFailMessage(tokens); return false; }
            try {
                final float x = Float.parseFloat(tokens[1]);
                final float y = Float.parseFloat(tokens[2]);
                final float z = Float.parseFloat(tokens[3]);
                final var vertexNormal = Tuple.makeVector(x, y, z);
                data.add(vertexNormal);
                emitCollectionEvent(vertexNormal);
                return true;
            } catch (NumberFormatException e) {
                printParseFailMessage(tokens);
                return false;
            }
        }
    }

    /**
     * Group Parser parses group lines that start with a "g".
     */
    protected static class GroupParser extends BaseParser<Pair<String, Group>> {
        private static final Matrix4 identity = Matrix4.identity();
        @Override
        public boolean parseLine(String[] tokens) {
            // assuming group names have to be one token long
            if (tokens.length != 2 && tokens[0].length() != 1) { printParseFailMessage(tokens); return false; }
            this.data.add(new Pair<String,Group>(tokens[1], new Group(identity)));
            emitCollectionEvent(data.getLast());
            return true;
        }
    }

    /**
     * The face parser builds triangles from face lines that start with an "f".
     */
    protected static class FaceParser extends BaseParser<Triangle> {

        private final VertexParser vertices;
        private final VertexNormalParser normals;
        private final GroupParser groups;
        private final Material material;
        private Optional<Group> currentGroup;
        /**
         * starts with a digit, allows 0 or more digits, string ends
         */
        protected final static Pattern simpleFace = Pattern.compile("^[0-9]+$");
        /**
         * matches strings that contain a vertex index a forward slash, an optional texture vertex index, a forward slash, and a vertex normal index
         */
        protected final static Pattern smoothFace = Pattern.compile("^[0-9]+\\/[0-9]*\\/[0-9]+$");

        private final Function<String, Boolean> isSimpleFaceToken = (token) -> simpleFace.matcher(token).matches();
        private final Function<String, Boolean> isSmoothFaceToken = (token) -> smoothFace.matcher(token).matches();

        
        public FaceParser(VertexParser vertices, VertexNormalParser normals, GroupParser groups, Material material) {
            this.vertices = vertices; this.groups = groups; this.normals = normals; this.material = material;
            currentGroup = Optional.empty();
            groups.addObserver((group)-> { currentGroup = Optional.of(group.b()); });
            this.addObserver(triangle -> {
                if (currentGroup.isPresent()) {
                    currentGroup.get().addShape(triangle);
                }
            });
        }

        /**
         * Helper method to check if a line's tokens matches the expected pattern for a face line.
         * @param tokens
         * @param condition
         * @return
         */
        private boolean checkLine(String[] tokens, Function<String, Boolean> condition) {
            boolean flag = true;
            for (int i = 1; i < tokens.length; i++) {
                flag &= condition.apply(tokens[i]);
            }
            return flag;
        }

        @Override
        public boolean parseLine(String[] tokens) {
            if (tokens.length < 4) { printParseFailMessage(tokens); return false; }
            // I have decided there will be no mix matching of token types | i.e. either f 1 2 3 OR f 1/2/3 1/2/3 1/2/3 
            final boolean shouldTriangulate = tokens.length > 4;
            if (checkLine(tokens, isSimpleFaceToken)) {
                if (shouldTriangulate) {
                    return triangulateFace(tokens);
                }
                return parseFace(tokens);
            } 
            else if (checkLine(tokens, isSmoothFaceToken)) {
                if (shouldTriangulate) {
                    return triangulateSmoothFace(tokens);
                }
                return parseSmoothFace(tokens);
            } else {
                printParseFailMessage(tokens);
                return false;
            }
        }

        private boolean parseSmoothFace(String[] tokens) {
            // we expect 4 tokens, f 'line' identifier, 3 tokens being either d/d/d or d//d
            // so split by '/' and parse sub token 0 and 2
            final var verts = this.vertices.data;
            final var vertNormals = this.normals.data;
            final Tuple[] p = new Tuple[3];
            final Tuple[] n = new Tuple[3];
            try {
                for (int i = 1; i < tokens.length; i++) {
                    final String[] subtokens = tokens[i].split("/");
                    final int vertIndex = Integer.parseInt(subtokens[0]);
                    final int vertNormalIndex = Integer.parseInt(subtokens[2]);
                    p[i-1] = verts.get(vertIndex);
                    n[i-1] = vertNormals.get(vertNormalIndex);
                }   
                final var triangle = new SmoothTriangle(p[0], p[1], p[2], n[0], n[1], n[2], material);
                this.data.add(triangle);
                emitCollectionEvent(triangle);
                return true;
            } catch (NumberFormatException | ArrayIndexOutOfBoundsException e) {
                printParseFailMessage(tokens);
                return false;
            }
        }

        private boolean parseFace(String[] tokens) {
            try {
                final var verts = this.vertices.data;
                final int index1 = Integer.parseInt(tokens[1]);
                final int index2 = Integer.parseInt(tokens[2]);
                final int index3 = Integer.parseInt(tokens[3]);
                final Tuple p1 = verts.get(index1);
                final Tuple p2 = verts.get(index2);
                final Tuple p3 = verts.get(index3);
                final Triangle triangle = new Triangle(p1, p2, p3, material);
                this.data.add(triangle);
                emitCollectionEvent(triangle);
                return true;
            } catch (NumberFormatException | ArrayIndexOutOfBoundsException e) {
                printParseFailMessage(tokens);
                return false;
            }
        }

        private boolean triangulateFace(String[] tokens) {
            try {
                final List<Tuple> verts = Arrays.stream(tokens).skip(1).map(Integer::parseInt).map(vertices.data::get).toList();
                // assuming a convex polygon, whose internal angles sum to 180 degrees or less, allows for fan triangulation
                for (int i = 1; i < tokens.length - 2; i++) { // token.length - 2 because the stream removed the first token
                    final Tuple p1 = verts.get(0);
                    final Tuple p2 = verts.get(i);
                    final Tuple p3 = verts.get(i + 1);
                    final Triangle triangle = new Triangle(p1, p2, p3, material);
                    data.add(triangle);
                    emitCollectionEvent(triangle);
                }
                return true;
            } catch (NumberFormatException | ArrayIndexOutOfBoundsException e) {
                printParseFailMessage(tokens);
                return false;
            }
        } 

        private boolean triangulateSmoothFace(String[] tokens) {
            // tokens are in the form [f 1/2/3 1/2/3 1/2/3 1/2/3 ...] &| [f 1//3 1//3 1//3 1//3 ...]
            final List<Tuple> verts = new ArrayList<>();
            final List<Tuple> vertNorms = new ArrayList<>();
            for (int i = 1; i < tokens.length; i++) {
                try {
                    String[] subtokens = tokens[i].split("/");
                    final var vertIndex = Integer.parseInt(subtokens[0]);
                    final var normalIndex = Integer.parseInt(subtokens[2]);
                    verts.add(vertices.data.get(vertIndex));
                    vertNorms.add(normals.data.get(normalIndex));
                } catch (NumberFormatException | ArrayIndexOutOfBoundsException e) {
                    printParseFailMessage(tokens);
                    return false;
                }
            }
            try {
                final Tuple p1 = verts.get(0);
                final Tuple n1 = vertNorms.get(0);
                for (int i = 1; i < tokens.length - 2; i++) {
                    final Tuple p2 = verts.get(i);
                    final Tuple n2 = vertNorms.get(i);
                    final Tuple p3 = verts.get(i + 1);
                    final Tuple n3 = vertNorms.get(i + 1);
                    final var triangle = new SmoothTriangle(p1, p2, p3, n1, n2, n3, material);
                    data.add(triangle);
                    emitCollectionEvent(triangle);
                }
                return true;
            } catch (ArrayIndexOutOfBoundsException e) {
                printParseFailMessage(tokens);
                return false;
            }
        }
    }

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
        if (object.groups().isEmpty()) {
            System.out.println("INFO: could not find any groups, making new group from raw triangles");
            return object.rawTriangles(transform);
        }
        final Group uberGroup = new Group(transform);
        System.out.println("INFO: building uber group from object's groups");
        for (var group : object.groups) {
            uberGroup.addShape(group.b());
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
        final var loader = new WavefrontObjectLoader(material);
        int linesSkipped = 0;
        for (var line : lines) {
            if (line.isEmpty()) {
                linesSkipped++;
                continue;
            }
            String[] tokens = line.split(" ");
            final var parser = loader.parsers.get(tokens[0]);
            if (parser == null) {
                System.out.println("INFO: encountered unknown type of obj line, skipping: " + line);
                linesSkipped++;
                continue;
            }
            final var parsed = parser.parseLine(tokens);
            if (!parsed) { linesSkipped++; }
        }
        if (linesSkipped == lines.size()) { System.out.println("WARN: couldn't process any information from OBJ file"); } 
        return new ObjectData(
            linesSkipped,
            ((VertexNormalParser) loader.parsers.get("vn")).collect(),
            ((VertexParser) loader.parsers.get("v")).collect(), // I wonder if there is a better way to go about doing this instead of doing these casts.
            ((FaceParser) loader.parsers.get("f")).collect(),
            ((GroupParser) loader.parsers.get("g")).collect());
    }

}
