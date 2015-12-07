package robotrace;

import com.jogamp.opengl.util.gl2.GLUT;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import static javax.media.opengl.GL.GL_FRONT;
import static javax.media.opengl.GL.GL_LINE_LOOP;
import static javax.media.opengl.GL.GL_LINE_STRIP;
import javax.media.opengl.GL2;
import static javax.media.opengl.GL2GL3.GL_QUADS;
import static javax.media.opengl.fixedfunc.GLLightingFunc.GL_DIFFUSE;
import static javax.media.opengl.fixedfunc.GLLightingFunc.GL_SHININESS;
import static javax.media.opengl.fixedfunc.GLLightingFunc.GL_SPECULAR;
import javax.media.opengl.glu.GLU;

/**
 * Implementation of a race track that is made from Bezier segments.
 */
class RaceTrack {
    
    /** The width of one lane. The total width of the track is 4 * laneWidth. */
    private final static float laneWidth = 1.22f;

    /** Array with 3N control points, where N is the number of segments. */
    private Vector[] controlPoints = null;
    
    /**
     * Constructor for the default track.
     */
    public RaceTrack() {
    }
    
    /**
     * Constructor for a spline track.
     */
    public RaceTrack(Vector[] controlPoints) {
        this.controlPoints = controlPoints;
    }

    /**
     * Draws this track, based on the control points.
     */
    public void draw(GL2 gl, GLU glu, GLUT glut) {
        if (null == controlPoints) {
            //Double N = aantal polygons
            //5 lijsten 
            
            //for i 0 to 4
            
            
            
            
            //Number of polygons to draw
            Double N = 75D;            
            int numberOfLanes = 4;
            List points = new ArrayList();
            List normals;
            normals = new ArrayList();
            List<Vector> offset_points = new ArrayList<Vector>();
            Vector normal = null;
            
              // Compute all points and normals.
            for (int i = 0; i < numberOfLanes; i++) {
                
                for (int j = 0; j < N + 1; j++) {
                    if (i > 0 && j == 0)
                    {
                        points.clear();
                        offset_points.stream().forEach((v) -> {
                            points.add(v);
                        });
                        //points = offset_points;
                        offset_points.clear();
                    }
                    
                    double t = j / N;
                    
                    //Add point to list of points
                    Vector point = getPoint(t);
                    
                    if (i==0)
                    {
                    points.add(point);
                    }
                    
                    
                    //Get Tangent
                    Vector tangent = getTangent(t);
                    
                    //Calculate normal by rotation of -90 degrees
                    //x' = xcos(a) - ysin(a)
                    //x' = xcos(-90) - ysin(-90)
                    //x' = 0 - -y = y
                    //y' = xsin(a) + ycos(a)
                    //y' = xsin(-90) + ycos(-90)
                    //y' = -x
                    normal = new Vector (tangent.y,-tangent.x,tangent.z);
                        
                    normals.add(normal);
                    //Add scaled normal vector to point
                    Vector off = point.add(normal.normalized().scale((laneWidth * (i + 1))));
                    offset_points.add(off);
                }
                    
                switch (i) {
                    case 0:
                        //Set material determined by given robot type
                        gl.glMaterialf(GL_FRONT, GL_SHININESS, Material.ANDROID.shininess);
                        gl.glMaterialfv(GL_FRONT, GL_DIFFUSE, Material.ANDROID.diffuse, 0);
                        gl.glMaterialfv(GL_FRONT, GL_SPECULAR, Material.ANDROID.specular, 0);
                        break;
                    case 1:
                        //Set material determined by given robot type
                        gl.glMaterialf(GL_FRONT, GL_SHININESS, Material.GOLD.shininess);
                        gl.glMaterialfv(GL_FRONT, GL_DIFFUSE, Material.GOLD.diffuse, 0);
                        gl.glMaterialfv(GL_FRONT, GL_SPECULAR, Material.GOLD.specular, 0);
                        break;
                    case 2:
                        //Set material determined by given robot type
                        gl.glMaterialf(GL_FRONT, GL_SHININESS, Material.BLUE.shininess);
                        gl.glMaterialfv(GL_FRONT, GL_DIFFUSE, Material.BLUE.diffuse, 0);
                        gl.glMaterialfv(GL_FRONT, GL_SPECULAR, Material.BLUE.specular, 0);
                        break;
                    case 3:
                        //Set material determined by given robot type
                        gl.glMaterialf(GL_FRONT, GL_SHININESS, Material.WOOD.shininess);
                        gl.glMaterialfv(GL_FRONT, GL_DIFFUSE, Material.WOOD.diffuse, 0);
                        gl.glMaterialfv(GL_FRONT, GL_SPECULAR, Material.WOOD.specular, 0);
                        break;
                    default:
                        break;
                }
                   
                    drawCenterLine(points,normals,offset_points,N,gl);
                   drawInnerLine(points,offset_points,N,gl);
                   drawOuterLine(points,N,gl);
            }
            

            // draw the test track
        } else {
            // draw the spline track
        }
    }
    
    private void drawCenterLine(List<Vector> points,List<Vector> normals, List<Vector> offset_points,Double N,GL2 gl)
    {
            int minHeight = -1;
            gl.glBegin(GL_QUADS);
            for (int i = 0; i < N; i++) {
                // Draw inside of the track.
                Vector normal = normals.get(i).scale(-1); // use reverse normal
                gl.glNormal3d(normal.x(), normal.y(), normal.z());

                // Draw quad spanning between two points between minHeight, maxHeight.
                Vector point = points.get(i); // point on inside of the track
                Vector next_point = points.get(i + 1);
                gl.glTexCoord2f(0, 0);
                gl.glVertex3d(point.x(), point.y(), point.z());
                gl.glTexCoord2f(1, 0);
                gl.glVertex3d(next_point.x(), next_point.y(), next_point.z());
                gl.glTexCoord2f(1, 1);
                gl.glVertex3d(next_point.x(), next_point.y(), minHeight);
                gl.glTexCoord2f(0, 1);
                gl.glVertex3d(point.x(), point.y(), minHeight);

                // Draw outside of the track.
                normal = normals.get(i);
                gl.glNormal3d(normal.x(), normal.y(), normal.z());
                // Draw quad spanning between two points between minHeight, maxHeight.
                point = offset_points.get(i); // point on outside of the track
                next_point = offset_points.get(i + 1);
                gl.glTexCoord2f(0, 0);
                gl.glVertex3d(point.x(), point.y(), point.z());
                gl.glTexCoord2f(1, 0);
                gl.glVertex3d(next_point.x(), next_point.y(), next_point.z());
                gl.glTexCoord2f(1, 1);
                gl.glVertex3d(next_point.x(), next_point.y(), minHeight);
                gl.glTexCoord2f(0, 1);
                gl.glVertex3d(point.x(), point.y(), minHeight);
            }

            gl.glEnd();

    }
    
    private void drawOuterLine(List<Vector> points,Double N,GL2 gl)
    {
         gl.glBegin(GL_LINE_STRIP);
            // Draw a line on the inside of the track.
            for (int i = 0; i < N + 1; i++) {
                Vector point = points.get(i);
                gl.glVertex3d(point.x(), point.y(), point.z());
            }
            gl.glEnd();
    }
    
    private void drawInnerLine(List<Vector> points, List<Vector> offset_points,Double N,GL2 gl)
    {
        
            gl.glBegin(GL_QUADS);
            // Draw the top of the track.
            for (int i = 0; i < N; i++) {
                Vector point = points.get(i); // point on the inside
                Vector off = offset_points.get(i); // point on the outside
                Vector next_off = offset_points.get(i + 1); // next point (outside)
                Vector next_point = points.get(i + 1); // next point (inside)

                gl.glNormal3d(0, 0, 1); // upwards pointing normal
                gl.glTexCoord2f(0, 0);
                gl.glVertex3d(point.x(), point.y(), point.z());
                gl.glTexCoord2f(1, 0);
                gl.glVertex3d(off.x(), off.y(), off.z());
                gl.glTexCoord2f(1, 1);
                gl.glVertex3d(next_off.x(), next_off.y(), next_off.z());
                gl.glTexCoord2f(0, 1);
                gl.glVertex3d(next_point.x(), next_point.y(), next_point.z());
            }
            gl.glEnd();
    }
    
    /**
     * Returns the center of a lane at 0 <= t < 1.
     * Use this method to find the position of a robot on the track.
     */
    public Vector getLanePoint(int lane, double t) {
        if (null == controlPoints) {
            return Vector.O; // <- code goes here
        } else {
            return Vector.O; // <- code goes here
        }
    }
    
    /**
     * Returns the tangent of a lane at 0 <= t < 1.
     * Use this method to find the orientation of a robot on the track.
     */
    public Vector getLaneTangent(int lane, double t) {
        if (null == controlPoints) {
            return Vector.O; // <- code goes here
        } else {
            return Vector.O; // <- code goes here
        }
    }

    /**
     * Returns a point on the test track at 0 <= t < 1.
     */
    private Vector getPoint(double t) {
        //P(t) = (10 cos(2πt), 14 sin(2πt), 1)
        double x = 10 * Math.cos(2 * Math.PI * t);
        double y = 14 * Math.sin(2 * Math.PI * t);
        return new Vector(x,y,1);
    }

    /**
     * Returns a tangent on the test track at 0 <= t < 1.
     */
    private Vector getTangent(double t) {
        //P(t) = (10 cos(2πt), 14 sin(2πt), 1)
        //f(x) = 10 cos (2πt)
        //tangent line of x = fx/dx
        //-20π * sin(2πt)
        double x = -20 * Math.PI * Math.sin(2 * Math.PI * t);
        //f(y) = 14 sin(2πt)
        //tangent line of y = fy/dy
        //28π * cos(2πt)
        double y = 28 * Math.PI * Math.cos(2 * Math.PI * t);
        //z = 0;
        return new Vector(x,y,0);
    }
    
    /**
     * Returns a point on a bezier segment with control points
     * P0, P1, P2, P3 at 0 <= t < 1.
     */
    private Vector getCubicBezierPoint(double t, Vector P0, Vector P1,
                                                 Vector P2, Vector P3) {
        return Vector.O; // <- code goes here
    }
    
    /**
     * Returns a tangent on a bezier segment with control points
     * P0, P1, P2, P3 at 0 <= t < 1.
     */
    private Vector getCubicBezierTangent(double t, Vector P0, Vector P1,
                                                   Vector P2, Vector P3) {
        return Vector.O; // <- code goes here
    }
}
