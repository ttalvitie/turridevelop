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
    
    /**
     * Get the norm of the vector.
     * 
     * @return The norm/length/absolute value of the vector.
     */
    public double getNorm() {
        return Math.sqrt(x * x + y * y);
    }
    
    /**
     * Return the vector normalized.
     * 
     * @return The vector normalized. If the vector is zero, (1, 0) is returned.
     */
    public Vec2 normalized() {
        double norm = getNorm();
        if(norm == 0.0) {
            return new Vec2(1.0, 0.0);
        }
        
        return this.mul(1.0 / norm);
    }
    
    /**
     * Rotate the vector by 90 degrees counterclockwise.
     * 
     * @return The vector rotated by 90 degrees counterclockwise.
     */
    public Vec2 rotate90() {
        return new Vec2(-y, x);
    }
    
    /**
     * Rotate the vector by given angle.
     * 
     * @return The angle to rotate.
     * @return The rotated vector.
     */
    public Vec2 rotate(double angle) {
        double c = Math.cos(angle);
        double s = Math.sin(angle);
        return new Vec2(x * c - y * s, x * s + y * c);
    }
    
    @Override
    public String toString() {
        return "(" + x + ", " + y + ")";
    }
}
