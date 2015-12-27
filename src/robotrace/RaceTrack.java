package robotrace;

import com.jogamp.opengl.util.gl2.GLUT;
import java.util.ArrayList;
import java.util.List;
import static javax.media.opengl.GL.GL_FRONT;
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

    /**
     * The width of one lane. The total width of the track is 4 * laneWidth.
     */
    private final static float LANE_WIDTH = 1.22f;

    /**
     * TODO*
     */
    private final static int NUMBER_OF_LANES = 4;

    /**
     * TODO*
     */
    private List<Vector> points;

    /**
     * TODO*
     */
    private List<Vector> normals;

    /**
     * TODO*
     */
    private final static Double N = 200D;

    /**
     * TODO*
     */
    private List<Vector> offset_points;

    /**
     * TODO*
     */
    private final static int MIN_HEIGHT = -1;

    /**
     * Array with 3N control points, where N is the number of segments.
     */
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
        //Initialize arraylist of points
        points = new ArrayList();
        //Initialize arraylist of normal
        normals = new ArrayList();
        //Initialize arraylist of offset_points;
        offset_points = new ArrayList<>();

        Vector normal;
        if (null == controlPoints) {

            for (int i = 0; i < NUMBER_OF_LANES; i++) {

                for (int j = 0; j < N + 1; j++) {

                    //If not first time creating list
                    if (i > 0 && j == 0) {
                        //Clear list of points
                        points.clear();

                        //Set list of points equal to list of offset points off previous lane
                        //We used stream because "=" gave the wrong results.
                        offset_points.stream().forEach((v) -> {
                            points.add(v);
                        });
                        //Clear list of offset points
                        offset_points.clear();
                    }

                    //Calculate t
                    double t = j / N;

                    //Calulate point on lane with t
                    Vector point = getPoint(t);

                    //If point list is smaller then needed points add point.
                    //This means new points will only be added on first lis generation.
                    if (points.size() <= j) {
                        points.add(point);
                    }

                    //Get Tangent
                    Vector tangent = getTangent(t);

                    //Calculate normal by cross with negative z-axis
                    normal = tangent.cross(new Vector(0, 0, 1));

                    //Add normal to list of normals
                    normals.add(normal);

                    //Add unit normal vector to point and scale to lane width
                    Vector off = point.add(normal.normalized().scale((LANE_WIDTH * (i + 1))));
                    //Add offset point
                    offset_points.add(off);
                }
                //set lane colors
                setLaneColors(i, gl);

                //Draw the test track
                drawTrack(points, normals, offset_points, N, gl);

            }
        } else {
            for (int k = 0; k < controlPoints.length - 1; k = k + 3) {
                for (int i = 0; i < NUMBER_OF_LANES; i++) {
                    for (int j = 0; j < N + 1; j++) {
                        //Calculate t
                        double t = j / N;

                        //If not first time creating list
                        if (i > 0 && j == 0) {
                            //Clear list of points
                            points.clear();

                            //Set list of points equal to list of offset points off previous lane
                            offset_points.stream().forEach((v) -> {
                                points.add(v);
                            });
                            //Clear list of offset points
                            offset_points.clear();
                        }

                        //Add point to list of points
                        Vector point = getCubicBezierPoint(t, controlPoints[k], controlPoints[k + 1], controlPoints[k + 2], controlPoints[k + 3]);

                        //If point list is smaller then needed points add point
                        if (points.size() <= j) {
                            points.add(point);
                        }

                        //Get Tangent
                        Vector tangent = getCubicBezierTangent(t, controlPoints[k], controlPoints[k + 1], controlPoints[k + 2], controlPoints[k + 3]);

                        //Calculate normal by cross with z-axis
                        normal = tangent.cross(new Vector(0, 0, -1));
                        //Add normal
                        normals.add(normal);

                        //Add unit normal vector to point and scale to lane width
                        Vector off = point.add(normal.normalized().scale((LANE_WIDTH * (i + 1))));

                        //Add offset point
                        offset_points.add(off);
                    }

                    //Set the lane colors
                    setLaneColors(i, gl);
                    //Draw track
                    drawTrack(points, normals, offset_points, N, gl);
                }
                points.clear();
                normals.clear();
                offset_points.clear();
            }

        }
    }

    private void setLaneColors(int lane_number, GL2 gl) {
        switch (lane_number) {
            case 0:
                //Set material determined by given robot type
                gl.glMaterialf(GL_FRONT, GL_SHININESS, Material.RED.shininess);
                gl.glMaterialfv(GL_FRONT, GL_DIFFUSE, Material.RED.diffuse, 0);
                gl.glMaterialfv(GL_FRONT, GL_SPECULAR, Material.RED.specular, 0);
                break;
            case 1:
                //Set material determined by given robot type
                gl.glMaterialf(GL_FRONT, GL_SHININESS, Material.GREEN.shininess);
                gl.glMaterialfv(GL_FRONT, GL_DIFFUSE, Material.GREEN.diffuse, 0);
                gl.glMaterialfv(GL_FRONT, GL_SPECULAR, Material.GREEN.specular, 0);
                break;
            case 2:
                //Set material determined by given robot type
                gl.glMaterialf(GL_FRONT, GL_SHININESS, Material.BLUE.shininess);
                gl.glMaterialfv(GL_FRONT, GL_DIFFUSE, Material.BLUE.diffuse, 0);
                gl.glMaterialfv(GL_FRONT, GL_SPECULAR, Material.BLUE.specular, 0);
                break;
            case 3:
                //Set material determined by given robot type
                gl.glMaterialf(GL_FRONT, GL_SHININESS, Material.YELLOW.shininess);
                gl.glMaterialfv(GL_FRONT, GL_DIFFUSE, Material.YELLOW.diffuse, 0);
                gl.glMaterialfv(GL_FRONT, GL_SPECULAR, Material.YELLOW.specular, 0);
                break;
            default:
                break;
        }

    }

    private void drawTrack(List<Vector> points, List<Vector> normals, List<Vector> offset_points, Double N, GL2 gl) {
        //Draw the sides of the lane
        drawTrakLane(points, normals, offset_points, N, gl);
        //Draw the flat top race track
        drawTestSides(points, offset_points, N, gl);
    }

    private void drawTrakLane(List<Vector> points, List<Vector> normals, List<Vector> offset_points, Double N, GL2 gl) {
        gl.glBegin(GL_QUADS);
        for (int i = 0; i < N; i++) {
            // Draw inside of the track.
            Vector normal = normals.get(i); 
            gl.glNormal3d(normal.x(), normal.y(), normal.z());

            // Draw quad spanning between two points between minHeight, maxHeight.
            Vector point = points.get(i); // point on inside of the track
            Vector next_point = points.get(i + 1);
            gl.glTexCoord2f(0, 0);
            gl.glVertex3d(point.x(), point.y(), point.z());
            gl.glTexCoord2f(1, 0);
            gl.glVertex3d(next_point.x(), next_point.y(), next_point.z());
            gl.glTexCoord2f(1, 1);
            gl.glVertex3d(next_point.x(), next_point.y(), MIN_HEIGHT);
            gl.glTexCoord2f(0, 1);
            gl.glVertex3d(point.x(), point.y(), MIN_HEIGHT);

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
            gl.glVertex3d(next_point.x(), next_point.y(), MIN_HEIGHT);
            gl.glTexCoord2f(0, 1);
            gl.glVertex3d(point.x(), point.y(), MIN_HEIGHT);
        }

        gl.glEnd();

    }

    private void drawTestSides(List<Vector> points, List<Vector> offset_points, Double N, GL2 gl) {
        //Draw Quads
        gl.glBegin(GL_QUADS);
        // Draw the top of the track.
        for (int i = 0; i < N; i++) {
            Vector point = points.get(i); // point on the inside
            Vector off = offset_points.get(i); // point on the outside
            Vector next_point = points.get(i + 1); // next point on the inside
            Vector next_off = offset_points.get(i + 1); // next point on the outside

            gl.glNormal3d(0, 0, 1); // upwards pointing normal equal to z axis
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
     * Returns the center of a lane at 0 <= t < 1. Use this method to find the
     * position of a robot on the track.
     */
    public Vector getLanePoint(int lane, double t) {
        if (null == controlPoints) {
            Vector point = getPoint(t);
            //Get Tangent
            Vector tangent = getTangent(t);

            //Calculate normal by cross with negative z-axis
            Vector normal = tangent.cross(new Vector(0, 0, 1));
            //Add scaled normal vector to point

            return getPointOnLane(lane, point, normal);
        } else {
            Vector point = getLaneBezier(t, false);
            //Get Tangent
            Vector tangent = getLaneBezier(t, true);

            //Calculate normal by cross with z-axis
            Vector normal = tangent.cross(new Vector(0, 0, -1));
            //Add scaled normal vector to point
            return getPointOnLane(lane, point, normal);

        }
    }

    private Vector getPointOnLane(int lane, Vector point, Vector normal) {
        Vector point2 = new Vector(0, 0, 0);
        if (lane != 0) {
            point2 = point.add(normal.normalized().scale((LANE_WIDTH * (lane))));
        }
        Vector off = point.add(normal.normalized().scale((LANE_WIDTH * (lane + 1))));

        Vector pointOnLane;

        if (lane == 0) {
            pointOnLane = new Vector((point.x + off.x) / 2, ((point.y + off.y) / 2), point.z);
        } else {
            pointOnLane = new Vector((point2.x + off.x) / 2, ((point2.y + off.y) / 2), point.z);
        }
        return pointOnLane;
    }

    /**
     * Returns the tangent of a lane at 0 <= t < 1. Use this method to find the
     * orientation of a robot on the track.
     */
    public Vector getLaneTangent(int lane, double t) {
        if (null == controlPoints) {
            return getTangent(t);
        } else {
            return getLaneBezier(t, true);
        }
    }

    public Vector getLaneBezier(double t, boolean isTangent) {

        // Calculate number of segments
        double segmentCount = (controlPoints.length - 1) / 3.0;

        // Calculate in which segment the t falls
        int segment = (int) (t * segmentCount) % (int) segmentCount;

        // Calculate relative t
        double tRel = t * segmentCount - segment;

        segment = 3 * segment;

        if (isTangent) {
            return getCubicBezierTangent(tRel, controlPoints[segment], controlPoints[segment + 1], controlPoints[segment + 2], controlPoints[segment + 3]);
        } else {
            return getCubicBezierPoint(tRel, controlPoints[segment], controlPoints[segment + 1], controlPoints[segment + 2], controlPoints[segment + 3]);
        }
    }

    /**
     * Returns a point on the test or test-track at 0 <= t < 1.
     */
    private Vector getPoint(double t) {
        //P(t) = (10 cos(2πt), 14 sin(2πt), 1)
        double x = 10 * Math.cos(2 * Math.PI * t);
        double y = 14 * Math.sin(2 * Math.PI * t);
        return new Vector(x, y, 1);
    }

    /**
     * Returns a tangent on the test or test-track at 0 <= t < 1.
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
        return new Vector(x, y, 0);
    }

    /**
     * Returns a point on a bezier segment with control points P0, P1, P2, P3 at
     * 0 <= t < 1.
     */
    private Vector getCubicBezierPoint(double t, Vector P0, Vector P1,
            Vector P2, Vector P3) {

        //P(u)= (1-u)^3 * P0 + 3u(1-u)^2P1 + (1-u)3u^2P2 + u^3P3
        Double x = Math.pow((1 - t), 3) * P0.x + 3 * t * Math.pow((1 - t), 2) * P1.x + 3 * Math.pow(t, 2) * (1 - t) * P2.x + Math.pow(t, 3) * P3.x;
        Double y = Math.pow((1 - t), 3) * P0.y + 3 * t * Math.pow((1 - t), 2) * P1.y + 3 * Math.pow(t, 2) * (1 - t) * P2.y + Math.pow(t, 3) * P3.y;
        Double z = Math.pow((1 - t), 3) * P0.z + 3 * t * Math.pow((1 - t), 2) * P1.z + 3 * Math.pow(t, 2) * (1 - t) * P2.z + Math.pow(t, 3) * P3.z;

        return new Vector(x, y, z);
    }

    /**
     * Returns a tangent on a bezier segment with control points P0, P1, P2, P3
     * at 0 <= t < 1.
     */
    private Vector getCubicBezierTangent(double t, Vector P0, Vector P1,
            Vector P2, Vector P3) {

        //To get the tangent we take the derivative
        //P'(u) = 3 * (1-u)^2 * (P1 - P0) + 6 * (1-u) * u * (P2 - P1) + 3 * u^2 * (P3 - P2)
        Double x = 3 * Math.pow(1 - t, 2) * (P1.x - P0.x) + 6 * (1 - t) * t * (P2.x - P1.x) + 3 * Math.pow(t, 2) * (P3.x - P2.x);
        Double y = 3 * Math.pow(1 - t, 2) * (P1.y - P0.y) + 6 * (1 - t) * t * (P2.y - P1.y) + 3 * Math.pow(t, 2) * (P3.y - P2.y);
        Double z = 3 * Math.pow(1 - t, 2) * (P1.z - P0.z) + 6 * (1 - t) * t * (P2.z - P1.z) + 3 * Math.pow(t, 2) * (P3.z - P2.z);

        return new Vector(x, y, z);
    }
}
