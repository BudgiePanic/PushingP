package com.BudgiePanic.rendering.io;

import java.io.FileNotFoundException;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;

import com.BudgiePanic.rendering.util.Canvas;
import com.BudgiePanic.rendering.util.Pair;

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

    public static Canvas createCanvas(String fileName) throws ParsingException {
        throw new UnsupportedOperationException("method not implemented yet");
    }

    // TODO need to handle case where arbitrary line is a comment

    protected static boolean check(List<String> lines, Predicate<String> condition, final int targetLine) {
        int linesConsumed = 0;          // The current line number for non comment lines
        int lineCount = 0;              // The current line number for comment and non comment lines
        int numbLines = lines.size();   // The total number of lines
        while (lineCount < numbLines) {
            String line = lines.get(lineCount++);
            if (line.startsWith("#")) { continue; } // don't increment lines consumed on comment lines
            if (linesConsumed == targetLine) {
                return condition.test(line);
            }
            linesConsumed++;
        }
        return false;
    }

    /**
     * Check that the first line in the ppm file is correct.
     * @param lines
     *   The lines extracted from the ppm file
     * @return
     *   True if the first line is 'P3'
     */
    protected static boolean checkMagic(List<String> lines) { 
        return check(lines, s -> s.equals(magic), 0);
    }

    protected static Optional<Pair<Integer, Integer>> checkDimensions(List<String> lines) {
        if (lines.size() < 1) { return Optional.empty(); }
        String dimensionLine = lines.get(1);
        String[] tokens = dimensionLine.split(" ");
        if (tokens.length != 2) { return Optional.empty(); }
        try {
            int width = Integer.parseInt(tokens[0]);
            int height = Integer.parseInt(tokens[1]);
            if (width < 1 || height < 1) {
                return Optional.empty();
            }
            return Optional.of(new Pair<>(width, height));
        } catch (NumberFormatException e) {
            System.out.println("WARN: could not parse image dimensions form line 2 -> [" + dimensionLine + "] " + e.getLocalizedMessage());
            return Optional.empty();
        }
    }




}
