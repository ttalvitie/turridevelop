package fi.helsinki.cs.turridevelop.gui;

import fi.helsinki.cs.turridevelop.logic.Transition;
import fi.helsinki.cs.turridevelop.util.Vec2;
import java.awt.Graphics2D;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Path2D;

/**
 * GUI utilities.
 */
public class Util {
    /**
     * Gets the text used to describe transition.
     * 
     * @param transition The transition to describe.
     * @param destination Set to true if the text should include the name of the
     * destination state.
     * @return The string describing the transition.
     */
    public static String getTransitionText(
        Transition transition,
        boolean destination
    ) {
        String input = transition.getInputCharacters();
        Character output = transition.getOutputCharacter();
        char movement = 'X';
        switch(transition.getMovement()) {
            case -1:
                movement = 'L';
                break;
            case 0:
                movement = 'S';
                break;
            case 1:
                movement = 'R';
                break;
        }
        
        String destination_str = "";
        if(destination) {
            destination_str = transition.getDestination().getName() +  " : ";
        }
        
        if(output == null) {
            return destination_str + input + " -> " + movement;
        } else {
            return destination_str + input + " -> " + output + ", " + movement;
        }
    }
    
    /**
     * Get intersection point of an ellipse and a halfline from its center.
     * 
     * @param ellipse The ellipse.
     * @param direction Direction of the halfline from the center of the
     * ellipse.
     * @return The intersection point.
     */
    public static Vec2 getEllipseHalflineIntersection(
        Ellipse2D.Double ellipse,
        Vec2 direction
    ) {
        // Write the ellipse in form a(x-x0)^2 + b(y-y0)^2 = 1 where (x0, y0)
        // is the center.
        double w = ellipse.getWidth();
        double h = ellipse.getHeight();
        double a = 4.0 / (w * w);
        double b = 4.0 / (h * h);
        
        // Solve t from equation a(dx*t)^2 + b(dy*t)^2 = 1 where dx, dy are
        // components of direction, i.e. get the intersection of an ellipse and
        // a line going through its center. Because we are dealing with a half
        // line, choose the positive solution.
        double dx2 = direction.x * direction.x;
        double dy2 = direction.y * direction.y;
        double t = 1.0 / Math.sqrt(a * dx2 + b * dy2);
        
        Vec2 center = new Vec2(ellipse.getCenterX(), ellipse.getCenterY());
        return Vec2.add(center, direction.mul(t));
    }
    
    /**
     * Gets a cubic Bezier between ellipses.
     * 
     * @param ellipse1 The starting ellipse.
     * @param ellipse2 The ending ellipse.
     * @return 4-element array of the start, the two control points and the end
     * point of the cubic Bezier.
     */
    public static Vec2[] getBezierBetweenEllipses(
        Ellipse2D.Double ellipse1,
        Ellipse2D.Double ellipse2
    ) {
        // Handle the case when the ellipses are exactly equal separately.
        if(ellipse1.equals(ellipse2)) {
            Vec2 start = getEllipseHalflineIntersection(
                ellipse1, new Vec2(-1.0, -2.0)
            );
            Vec2 end = getEllipseHalflineIntersection(
                ellipse1, new Vec2(1.0, -2.0)
            );
            Vec2 control1 = Vec2.add(start, new Vec2(-40, -60.0));
            Vec2 control2 = Vec2.add(end, new Vec2(40, -60.0));
            
            Vec2[] ret = new Vec2[4];
            ret[0] = start;
            ret[1] = control1;
            ret[2] = control2;
            ret[3] = end;
            return ret;
        }
        
        Vec2 center1 = new Vec2(ellipse1.getCenterX(), ellipse1.getCenterY());
        Vec2 center2 = new Vec2(ellipse2.getCenterX(), ellipse2.getCenterY());
        Vec2 diffnormal = Vec2.sub(center2, center1).normalized().rotate90();
        
        // Put the both control points a little distance away from the segment.
        Vec2 control1 = Vec2.add(
            Vec2.add(center1.mul(0.7), center2.mul(0.3)), diffnormal.mul(-40.0)
        );
        Vec2 control2 = Vec2.add(
            Vec2.add(center1.mul(0.3), center2.mul(0.7)), diffnormal.mul(-40.0)
        );
        
        // Put the start and end points to the intersections of the tangent line
        // with the ellipses.
        Vec2[] ret = new Vec2[4];
        ret[0] = getEllipseHalflineIntersection(
            ellipse1, Vec2.sub(control1, center1)
        );
        ret[1] = control1;
        ret[2] = control2;
        ret[3] = getEllipseHalflineIntersection(
            ellipse2, Vec2.sub(control2, center2)
        );
        return ret;
    }
    
    /**
     * Get the end of an arrow as a path.
     * 
     * @param point The point the arrow is pointing to.
     * @param direction The direction the arrow is pointing to.
     * @param size Size of the arrow.
     * @return The path for the arrow end.
     */
    public static Path2D.Double getArrowEnd(
        Vec2 point, Vec2 direction, double size
    ) {
        direction = direction.normalized();
        Vec2 corner1 =
            Vec2.add(point, direction.rotate(Math.PI * 0.86).mul(size));
        Vec2 corner2 =
            Vec2.add(point, direction.rotate(-Math.PI * 0.86).mul(size));
        
        Path2D.Double path = new Path2D.Double();
        path.moveTo(point.x, point.y);
        path.lineTo(corner1.x, corner1.y);
        path.lineTo(corner2.x, corner2.y);
        path.closePath();
        
        return path;
    }
}
