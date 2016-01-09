package robotrace;

import com.jogamp.opengl.util.gl2.GLUT;
import com.jogamp.opengl.util.texture.Texture;
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
     * The numbers of lanes of which the track
     */
    private final static int NUMBER_OF_LANES = 4;

    /**
     * List containing points of quad.
     */
    private List<Vector> points;

    /**
     * Number of segments
     */
    private final static Double N = 200D;

    /**
     * List containing offset points of quad
     */
    private List<Vector> offset_points;

    /**
     * List containing normals of track
     */
    private List<Vector> normals;

    /**
     * MIN_HEIGHT of track. No used at the moment
     */
    private final static int MIN_HEIGHT = -1;
    
    /**
     * LANE_HEIGHT of track.
     */
    private final static int LANE_HEIGHT = 2;
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
     * @param gl
     * @param glu
     * @param glut
     * @param track
     * @param brick
     * @param finish 
     */
    public void draw(GL2 gl, GLU glu, GLUT glut, Texture track, Texture brick, Texture finish) {
        Vector finishPoint = null;
        Vector finishOff = null;
        Vector finishPointNext = null;
        Vector finishOffNext = null;

        //Initialize arraylist of points
        points = new ArrayList();
        //Initialize arraylist of offset_points;
        offset_points = new ArrayList<>();

        //Initialize arraylist of normals
        normals = new ArrayList<>();

        Vector normal;

        //Check if default track        
        if (null == controlPoints) {
            //Loop through number of lanes.
            for (int i = 0; i < NUMBER_OF_LANES; i++) {
                //Loop through number of segements.
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
                    //This means new points will only be added on first list generation.
                    if (points.size() <= j) {
                        points.add(point);
                    }

                    //Get Tangent
                    Vector tangent = getTangent(t);

                    //Calculate normal by cross with negative z-axis
                    normal = tangent.cross(new Vector(0, 0, 1));
                    normals.add(normal);

                    //Add normal to list of normals
                    //Add unit normal vector to point and scale to lane width
                    Vector off = point.add(normal.normalized().scale((LANE_WIDTH * (i + 1))));
                    //Add offset point
                    offset_points.add(off);

                    //If 1st segement and first lane
                    if (j == 0 && i == 0) {
                        //get inner and outer finish line point 
                        finishPoint = point;
                        finishOff = point.add(normal.normalized().scale((LANE_WIDTH * (NUMBER_OF_LANES))));
                    }
                    //If 5th segment and first lane
                    if (j == 5 && i == 0) {
                        //get inner and outer finish line point 
                        finishPointNext = point;
                        finishOffNext = point.add(normal.normalized().scale((LANE_WIDTH * (NUMBER_OF_LANES))));
                    }
                }
                //set lane colors
                setLaneColors(i, gl);

                //Draw the test track
                drawTrack(points, offset_points, N, gl, track, brick);

            }
        } else {
            //Loop through number of controlpoints with step of 3 (bezier curve)
            for (int k = 0; k < controlPoints.length - 1; k = k + 3) {
                //Loop through number of lanes
                for (int i = 0; i < NUMBER_OF_LANES; i++) {
                    //Loop through number of segments
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
                        normals.add(normal);

                        //Add unit normal vector to point and scale to lane width
                        Vector off = point.add(normal.normalized().scale((LANE_WIDTH * (i + 1))));

                        //Add offset point
                        offset_points.add(off);

                        //if 1st controlpoint and 1st land and 1st segment.
                        if (j == 0 && i == 0 && k == 0) {
                            //Calculate finish line points
                            finishPoint = point;
                            finishOff = finishPoint.add(normal.normalized().scale(LANE_WIDTH * NUMBER_OF_LANES));
                        }
                        if (j == (controlPoints.length / 3) * 5 && i == 0 & k == 0) {
                            //Calculate finish line point
                            finishPointNext = point;
                            finishOffNext = finishPointNext.add(normal.normalized().scale(LANE_WIDTH * NUMBER_OF_LANES));
                        }
                    }

                    //Set the lane colors
                    setLaneColors(i, gl);
                    //Draw track
                    drawTrack(points, offset_points, N, gl, track, brick);
                }
                //Clear list of points
                points.clear();
                //Clear list of offset_points
                offset_points.clear();
            }

        }
        //Draw the finish line
        drawFinish(gl, finish, finishPoint, finishOff, finishPointNext, finishOffNext);
    }

    //Update lane colors.
    private void setLaneColors(int lane_number, GL2 gl) {
        switch (lane_number) {
            case 0:
                setMaterial(Material.RED, gl);
                break;
            case 1:
                setMaterial(Material.GREEN, gl);
                break;
            case 2:
                setMaterial(Material.BLUE, gl);
                break;
            case 3:
                setMaterial(Material.YELLOW, gl);
                break;
            default:
                setMaterial(Material.BLACK, gl);
                break;
        }

    }

    /**
     * Draw the track
     * @param points
     * @param offset_points
     * @param N
     * @param gl
     * @param track
     * @param brick 
     */
    private void drawTrack(List<Vector> points, List<Vector> offset_points, Double N, GL2 gl, Texture track, Texture brick) {
        // Start using track texture.
        track.enable(gl);
        track.bind(gl);
        //Draw the sides of the lane
        drawTrackLane(points, offset_points, N, gl);
        track.disable(gl);
        //Draw the flat top race track
        brick.enable(gl);
        brick.bind(gl);
        drawTrakSides(points, offset_points, normals, N, gl);
        brick.disable(gl);
    }

    /**
     * Draw sides of track
     * @param points
     * @param offset_points
     * @param normals
     * @param N
     * @param gl 
     */
    private void drawTrakSides(List<Vector> points, List<Vector> offset_points, List<Vector> normals, Double N, GL2 gl) {
        // 2D array to store the line translations
        int[][] coordinates2d = new int[][]{
            {0, 0},
            {1, 0},
            {1, 1},
            {0, 1},
            {0, 0},
            {1, 0},
            {1, 1},
            {0, 1}};

        //Draw sides of lane with squads.
        gl.glBegin(GL_QUADS);
        //Loop trhough number of segements
        for (int i = 0; i < N; i++) {
             // 3D array to store the line translations
             //Calculate points by point list and offset_list
             //We first draw the left side with lane height = LANE_HEIGHT; 
             //NOTE: points.get(.).z - LANE_HEIGHT can be changed to min height to get track with height z to MIN_HEIGHT
             //Then we draw the right side with lane height = LANE_HEIGHT
            double[][] coordinates3d = new double[][]{
                {points.get(i).x, points.get(i).y, points.get(i).z},
                {points.get(i + 1).x, points.get(i + 1).y, points.get(i + 1).z},
                {points.get(i + 1).x, points.get(i + 1).y, points.get(i + 1).z - LANE_HEIGHT},
                {points.get(i).x, points.get(i).y, points.get(i).z - LANE_HEIGHT},
                {offset_points.get(i).x, offset_points.get(i).y, offset_points.get(i).z},
                {offset_points.get(i + 1).x, offset_points.get(i + 1).y, offset_points.get(i + 1).z},
                {offset_points.get(i + 1).x, offset_points.get(i + 1).y, offset_points.get(i + 1).z - LANE_HEIGHT},
                {offset_points.get(i).x, offset_points.get(i).y, offset_points.get(i).z - LANE_HEIGHT}};

            //Start looping through multidimensinal array to draw track
            gl.glNormal3d(normals.get(i).x, normals.get(i).y, normals.get(i).z); // upwards pointing normal equal to normal
            for (int j = 0; j < coordinates2d.length; j++) {
                //Draw 2D coordinates
                gl.glTexCoord2f(coordinates2d[j][0], coordinates2d[j][1]);
                //Draw 3D coordinates
                gl.glVertex3d(coordinates3d[j][0], coordinates3d[j][1], coordinates3d[j][2]);
            }

        }
        gl.glEnd();

    }

    /**
     * Draw track lane (top and bottom)
     * @param points
     * @param offset_points
     * @param N
     * @param gl 
     */
    private void drawTrackLane(List<Vector> points, List<Vector> offset_points, Double N, GL2 gl) {
        // 2D array to store the line translations
        int[][] coordinates2d = new int[][]{
            {0, 0},
            {1, 0},
            {1, 1},
            {0, 1}};

        // Draw the top of the track.
        gl.glBegin(GL_QUADS);
        for (int i = 0; i < N; i++) {
            double[][] coordinates3d = new double[][]{
                {points.get(i).x, points.get(i).y, points.get(i).z},
                {offset_points.get(i).x, offset_points.get(i).y, offset_points.get(i).z},
                {offset_points.get(i + 1).x, offset_points.get(i + 1).y, offset_points.get(i + 1).z},
                {points.get(i + 1).x, points.get(i + 1).y, points.get(i + 1).z}};

            gl.glNormal3d(0, 0, 1); // upwards pointing normal equal to z axis
            for (int j = 0; j < coordinates2d.length; j++) {
                gl.glTexCoord2f(coordinates2d[j][0], coordinates2d[j][1]);
                gl.glVertex3d(coordinates3d[j][0], coordinates3d[j][1], coordinates3d[j][2]);
            }
        }
        gl.glEnd();

        // Draw the bottom of the track.
        gl.glBegin(GL_QUADS);
        for (int i = 0; i < N; i++) {
            double[][] coordinates3d = new double[][]{
                {points.get(i).x, points.get(i).y, points.get(i).z - 2},
                {offset_points.get(i).x, offset_points.get(i).y, offset_points.get(i).z - 2},
                {offset_points.get(i + 1).x, offset_points.get(i + 1).y, offset_points.get(i + 1).z - 2},
                {points.get(i + 1).x, points.get(i + 1).y, points.get(i + 1).z - 2}};

            gl.glNormal3d(0, 0, 1); // upwards pointing normal equal to z axis
            for (int j = 0; j < coordinates2d.length; j++) {
                gl.glTexCoord2f(coordinates2d[j][0], coordinates2d[j][1]);
                gl.glVertex3d(coordinates3d[j][0], coordinates3d[j][1], coordinates3d[j][2]);
            }
        }
        gl.glEnd();
    }

    /**
     * Draw finish line
     * @param gl
     * @param finish
     * @param point
     * @param off
     * @param point_next
     * @param off_next 
     */
    private void drawFinish(GL2 gl, Texture finish, Vector point, Vector off, Vector point_next, Vector off_next) {
        //Set material to white
        setMaterial(Material.WHITE, gl);

        //Map finish texture
        finish.enable(gl);
        finish.bind(gl);

        // 2D array to store the line translations
        int[][] coordinates2d = new int[][]{
            {0, 0},
            {1, 0},
            {1, 1},
            {0, 1}};

        double[][] coordinates3d = new double[][]{
            {point.x, point.y, point.z + 0.05},
            {off.x, off.y, off.z + 0.05},
            {off_next.x, off_next.y, off_next.z + 0.05},
            {point_next.x, point_next.y, point_next.z + 0.05}};

        gl.glBegin(GL_QUADS);
        for (int i = 0; i < coordinates2d.length; i++) {
            gl.glTexCoord2f(coordinates2d[i][0], coordinates2d[i][1]);
            gl.glVertex3d(coordinates3d[i][0], coordinates3d[i][1], coordinates3d[i][2]);
        }
        gl.glEnd();

        finish.disable(gl);
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

    /**
     * Returns center point on lane
     * @param lane
     * @param point
     * @param normal
     * @return 
     */
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

    /**
     * Returns the tangent or position on bezier line.
     * @param t between 0<=t<1
     * @param isTangent if true return tangent
     * @return 
     */
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

    private void setMaterial(Material material, GL2 gl) {
        //Set material determined by given robot type
        gl.glMaterialf(GL_FRONT, GL_SHININESS, material.shininess);
        gl.glMaterialfv(GL_FRONT, GL_DIFFUSE, material.diffuse, 0);
        gl.glMaterialfv(GL_FRONT, GL_SPECULAR, material.specular, 0);
    }
}
