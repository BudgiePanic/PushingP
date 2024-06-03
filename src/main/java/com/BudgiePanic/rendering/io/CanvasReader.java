package com.BudgiePanic.rendering.io;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;

import org.apache.commons.io.FileUtils;

import com.BudgiePanic.rendering.util.Canvas;

/**
 * Reads ppm image data and stores it in a canvas.
 *
 * @author BudgiePanic
 */
public class CanvasReader {
    private CanvasReader () {}

    protected static final String magic = "P3";

    /**
     * Exception thrown when the canvas reader is unable to parse a file.
     */
    public static final class ParsingException extends Exception {
        ParsingException() { super(); }
        ParsingException(String reason) { super(reason); }
        ParsingException(Throwable cause) { super(cause); }
    }

    protected static sealed interface ParsingStage permits MagicChecker, DimensionParser, ConfigParser, CanvasBuilder{
        ParsingStage consumeLine(String line) throws ParsingException;
        default void initialize() {}
    }

    /**
     * Checks if a line conforms to the PPM standard by starting with 'P3'
     * This parser is always first in line, and will recieve the first non comment line from the file.
     */
    protected final static record MagicChecker(ParsingStage next) implements ParsingStage {
        @Override
        public ParsingStage consumeLine(final String line) throws ParsingException {
            if (line.equals(magic)) { return next; }
            throw new ParsingException("First line of file was not P3");
        }
    }

    /**
     * The next non comment line after the Magic should be the dimensions of the image
     */
    protected final static class DimensionParser implements ParsingStage {
        final ParsingStage next;
        Optional<Integer> width = Optional.empty(), height = Optional.empty();
        DimensionParser(ParsingStage next) { this.next = next; }
        @Override
        public ParsingStage consumeLine(final String line) throws ParsingException {
            String[] tokens = line.split(" ");
            if (tokens.length != 2) { throw new ParsingException("Image dimension line [" + line + "] could not be parsed into width height"); }
            try {
                final int width = Integer.parseInt(tokens[0]);
                final int height = Integer.parseInt(tokens[1]);
                if (width <= 0 || height <= 0) { throw new ParsingException("Image dimension line [" + line + "] contained invalid width height"); }
                this.width = Optional.of(width);
                this.height = Optional.of(height);
                return next;
            } catch (NumberFormatException e) {
                throw new ParsingException("Image dimension line [" + line + "] could not be parsed into width height " + e.getLocalizedMessage()); 
            }
        }
    }

    /**
     * The next non comment line after the image dimensions should be the max color value contained within the image.
     */
    protected static final class ConfigParser implements ParsingStage {
        final ParsingStage next;
        Function<Integer, Double> mapper = null;
        ConfigParser(ParsingStage next) { this.next = next; }
        @Override
        public ParsingStage consumeLine(String line) throws ParsingException {
            try {
                final int value = Integer.parseInt(line);
                mapper =  (final Integer number) -> { 
                    final double oldMin = 0.0, oldMax = value, newMin = 0.0, newMax = 1.0;
                    return ((number - oldMin) / (oldMax - oldMin)) * (newMax - newMin) + newMin;
                };
                return next;
            } catch (NumberFormatException e) {
                throw new ParsingException(e);
            }
        }
    }

    /**
     * All lines after the image config line should be RGB value triples.
     */
    protected static final class CanvasBuilder implements ParsingStage {
        DimensionParser dimension = null;
        ConfigParser config = null;
        CanvasBuilder() {}
        @Override
        public void initialize() {
            if (dimension == null || config == null) { throw new RuntimeException("Canvas builder was not configured"); }
        }
        @Override
        public ParsingStage consumeLine(String line) throws ParsingException {
            String[] tokens = line.split(" ");
            // every three tokens should be collected, mapped and converted to a Color
            return this;
        }
        public Canvas collect() {
            return null;
        }
    }

    public static Canvas createCanvas(final String fileName) throws ParsingException {
        try {
            System.out.println("INFO: trying to read file [" + fileName + "]");
            final File file = new File(fileName);
            List<String> lines = FileUtils.readLines(file, Charset.defaultCharset());
            return parseLines(lines);
        } catch (IOException e) {
            throw new ParsingException(e);
        }
    }
    
    protected static Canvas parseLines(final List<String> lines) throws ParsingException {
        System.out.println("INFO: trying to parse file contents into image");
        if (lines.isEmpty()) { throw new ParsingException("file is empty"); }
        CanvasBuilder builder = new CanvasBuilder();
        ConfigParser config = new ConfigParser(builder);
        DimensionParser dimension = new DimensionParser(config);
        builder.config = config; // not the best code, but it gets the job done
        builder.dimension = dimension;
        MagicChecker checker = new MagicChecker(dimension);

        ParsingStage parser = checker;
        for (final String line : lines) {
            if (line.startsWith("#")) {
                System.out.println("INFO: skipping comment line " + line);
            }
            ParsingStage next = parser.consumeLine(line);
            if (next != parser) {
                parser = next;
                parser.initialize();
            }
        }
        return builder.collect();
    }
}
