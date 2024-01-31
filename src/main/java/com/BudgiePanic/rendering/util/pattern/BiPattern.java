package com.BudgiePanic.rendering.util.pattern;

import java.util.Objects;

import com.BudgiePanic.rendering.util.Color;
import com.BudgiePanic.rendering.util.matrix.Matrix4;

/**
 * BiPattern is a pattern that alternates between two patterns.
 * Encapsulates the common elements of all bi patterns.
 * 
 * Solid Color is a mono pattern.
 * What would a tri pattern look like?
 * 
 * @author BudgiePanic
 */
abstract class BiPattern implements Pattern {
    
    protected final Pattern a;
    protected final Pattern b;
    protected final Matrix4 transform;

    public BiPattern(Pattern a, Pattern b, Matrix4 transform) {
        this.a = a;
        this.b = b;
        this.transform = transform;
    }

    public BiPattern(Pattern a, Pattern b) {
        this(a, b, Matrix4.identity());
    }

    public BiPattern(Color a, Color b, Matrix4 transform) {
        this(new SolidColor(a), new SolidColor(b), transform);
    }

    public BiPattern(Color a, Color b) {
        this(a, b, Matrix4.identity());
    }

    @Override
    public String toString() {
        return String.format("%s [a=\"%s\", b=\"%s\", transform=\"%s\"]", getClass().getSimpleName(), a, b, transform);
    }

    @Override
    public int hashCode() { return Objects.hash(this.a, this.b, this.transform); }

    @Override
    public boolean equals(Object obj) { // auto generated
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        BiPattern other = (BiPattern) obj;
        if (a == null) {
            if (other.a != null)
                return false;
        } else if (!a.equals(other.a))
            return false;
        if (b == null) {
            if (other.b != null)
                return false;
        } else if (!b.equals(other.b))
            return false;
        if (transform == null) {
            if (other.transform != null)
                return false;
        } else if (!transform.equals(other.transform))
            return false;
        return true;
    }

    

    

}
