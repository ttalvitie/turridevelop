
package fi.helsinki.cs.turridevelop.util;

/**
 * Two-dimensional vector with double components.
 */
public class Vec2 {
    public final double x;
    public final double y;
    
    /**
     * Create vector.
     * 
     * @param x The x coordinate of the vector.
     * @param y The y coordinate of the vector.
     */
    public Vec2(double x, double y) {
        this.x = x;
        this.y = y;
    }
    
    /**
     * Create zero vector.
     */
    public Vec2() {
        this.x = 0.0;
        this.y = 0.0;
    }
    
    /**
     * Add two vectors.
     * 
     * @return Vector a + b.
     */
    public static Vec2 add(Vec2 a, Vec2 b) {
        return new Vec2(a.x + b.x, a.y + b.y);
    }
    
    /**
     * Subtract two vectors.
     * 
     * @return Vector a - b.
     */
    public static Vec2 sub(Vec2 a, Vec2 b) {
        return new Vec2(a.x - b.x, a.y - b.y);
    }
    
    /**
     * Multiply vector with a scalar.
     * 
     * @param c The scalar coefficient.
     * @return The vector multiplied with c.
     */
    public Vec2 mul(double c) {
        return new Vec2(c * x, c * y);
    }
}
