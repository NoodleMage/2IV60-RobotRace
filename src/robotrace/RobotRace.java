package robotrace;

import com.jogamp.opengl.util.texture.Texture;
import java.util.Random;
import static javax.media.opengl.GL.GL_FRONT;
import javax.media.opengl.GL2;
import static javax.media.opengl.GL2.*;
import static javax.media.opengl.fixedfunc.GLLightingFunc.GL_DIFFUSE;
import static javax.media.opengl.fixedfunc.GLLightingFunc.GL_SHININESS;
import static javax.media.opengl.fixedfunc.GLLightingFunc.GL_SPECULAR;

/**
 * Handles all of the RobotRace graphics functionality, which should be extended
 * per the assignment.
 *
 * OpenGL functionality: - Basic commands are called via the gl object; -
 * Utility commands are called via the glu and glut objects;
 *
 * GlobalState: The gs object contains the GlobalState as described in the
 * assignment: - The camera viewpoint angles, phi and theta, are changed
 * interactively by holding the left mouse button and dragging; - The camera
 * view width, vWidth, is changed interactively by holding the right mouse
 * button and dragging upwards or downwards; - The center point can be moved up
 * and down by pressing the 'q' and 'z' keys, forwards and backwards with the
 * 'w' and 's' keys, and left and right with the 'a' and 'd' keys; - Other
 * settings are changed via the menus at the top of the screen.
 *
 * Textures: Place your "track.jpg", "brick.jpg", "head.jpg", and "torso.jpg"
 * files in the same folder as this file. These will then be loaded as the
 * texture objects track, bricks, head, and torso respectively. Be aware, these
 * objects are already defined and cannot be used for other purposes. The
 * texture objects can be used as follows:
 *
 * gl.glColor3f(1f, 1f, 1f); track.bind(gl); gl.glBegin(GL_QUADS);
 * gl.glTexCoord2d(0, 0); gl.glVertex3d(0, 0, 0); gl.glTexCoord2d(1, 0);
 * gl.glVertex3d(1, 0, 0); gl.glTexCoord2d(1, 1); gl.glVertex3d(1, 1, 0);
 * gl.glTexCoord2d(0, 1); gl.glVertex3d(0, 1, 0); gl.glEnd();
 *
 * Note that it is hard or impossible to texture objects drawn with GLUT. Either
 * define the primitives of the object yourself (as seen above) or add
 * additional textured primitives to the GLUT object.
 */
public class RobotRace extends Base {

    private Double step = 0d;

    private int luckCount = 0;

    private Double[] steps = {0.0, 0.0, 0.0, 0.0};

    private Boolean[] hasLuck = {false, false, false, false};

    private static int MOTOR_LANE = 6;

    private Vector motorPosition;

    private Double N = 10000d;
    private Double speed = 5.0;

    private Texture finish;
    private Texture sky;
    /**
     * Array of the four robots.
     */
    private final Robot[] robots;

    /**
     * Instance of the camera.
     */
    private final Camera camera;

    /**
     * Instance of the race track.
     */
    private final RaceTrack[] raceTracks;

    /**
     * Instance of the terrain.
     */
    private final Terrain terrain;

    /**
     * Constructs this robot race by initializing robots, camera, track, and
     * terrain.
     */
    public RobotRace() {

        // Create a new array of four robots
        robots = new Robot[4];

        // Initialize bender 0
        robots[0] = new Robot(Material.GOLD, Material.GOLD_ACCENT);

        // Initialize bender 1
        robots[1] = new Robot(Material.SILVER, Material.SILVER_ACCENT);

        // Initialize bender 2
        robots[2] = new Robot(Material.WOOD, Material.WOOD_ACCENT);

        // Initialize bender 3
        robots[3] = new Robot(Material.ORANGE, Material.GOLD_ACCENT);

        // Initialize the camera
        camera = new Camera();

        // Initialize the race tracks
        raceTracks = new RaceTrack[5];

        // Test track
        raceTracks[0] = new RaceTrack();

        // O-track
        raceTracks[1] = new RaceTrack(new Vector[]{new Vector(-15, 0, 1),
            new Vector(-15, 15, 1),
            new Vector(15, 15, 1),
            new Vector(15, 0, 1),
            new Vector(15, -15, 1),
            new Vector(-15, -15, 1),
            new Vector(-15, 0, 1)});

        // L-track
        raceTracks[2] = new RaceTrack(new Vector[]{
            new Vector(0, 11, 1),
            new Vector(0, 10, 1),
            new Vector(0, 9, 1),
            new Vector(0, 7, 1),
            new Vector(0, 2, 1),
            new Vector(1, 0, 1),
            new Vector(9, 0, 1),
            new Vector(12, 0, 1),
            new Vector(14, 0, 1),
            new Vector(16, 0, 1),
            new Vector(19, 0, 1),
            new Vector(19, -5, 1),
            new Vector(16, -5, 2),
            new Vector(13, -5, 2),
            new Vector(6, -5, 1),
            new Vector(-3, -5, 1),
            new Vector(-5, -5, 1),
            new Vector(-8, -3, 1),
            new Vector(-8, -1, 1),
            new Vector(-8, 1, 1),
            new Vector(-8, 5, 1),
            new Vector(-8, 10, 1),
            new Vector(-8, 13, 1),
            new Vector(-5, 15, 1),
            new Vector(-2, 15, 1),
            new Vector(0, 15, 1),
            new Vector(0, 12, 1),
            new Vector(0, 11, 1),});
        // C-track
        raceTracks[3] = new RaceTrack(new Vector[]{
            new Vector(-5, 10, 1),
            new Vector(0, 15, 1),
            new Vector(10, 15, 1),
            new Vector(15, 10, 1),
            new Vector(16, 9, 1),
            new Vector(16, 6, 1),
            new Vector(15, 5, 1),
            new Vector(12.5, 2.5, 1),
            new Vector(7.5, 7.5, 1),
            new Vector(5, 5, 1),
            new Vector(2.5, 2.5, 1),
            new Vector(2.5, -2.5, 1),
            new Vector(5, -5, 1),
            new Vector(7.5, -7.5, 1),
            new Vector(12.5, -2.5, 1),
            new Vector(15, -5, 1),
            new Vector(16, -6, 1),
            new Vector(16, -9, 1),
            new Vector(15, -10, 1),
            new Vector(10, -15, 1),
            new Vector(0, -15, 1),
            new Vector(-5, -10, 1),
            new Vector(-10, -5, 1),
            new Vector(-10, 5, 1),
            new Vector(-5, 10, 1),});

        // Custom track
        raceTracks[4] = new RaceTrack(new Vector[]{
            new Vector(-5, 0, 8),
            new Vector(-5, 7.5, 8),
            new Vector(10, 7.5, 8),
            new Vector(10, 0, 8),
            new Vector(10, -7.5, 8),
            new Vector(-5, -7.5, 8),
            new Vector(-5, 0, 1),
            new Vector(-5, 7.5, 1),
            new Vector(-20, 7.5, 1),
            new Vector(-20, 0, 1),
            new Vector(-20, -15, 1),
            new Vector(-5, -15, 8),
            new Vector(-5, 0, 8),});

        // Initialize the terrain
        terrain = new Terrain();
    }

    /**
     * Called upon the start of the application. Primarily used to configure
     * OpenGL.
     */
    @Override
    public void initialize() {

        // Enable blending.
        gl.glEnable(GL_BLEND);
        gl.glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);

        // Enable depth testing.
        gl.glEnable(GL_DEPTH_TEST);
        gl.glDepthFunc(GL_LESS);

        // Normalize normals.
        gl.glEnable(GL_NORMALIZE);

        // Enable textures. 
        gl.glEnable(GL_TEXTURE_2D);
        gl.glHint(GL_PERSPECTIVE_CORRECTION_HINT, GL_NICEST);
        gl.glBindTexture(GL_TEXTURE_2D, 0);

        // Try to load four textures, add more if you like.
        track = loadTexture("track.jpg");
        brick = loadTexture("brick.jpg");
        head = loadTexture("head.jpg");
        torso = loadTexture("torso.jpg");
        finish = loadTexture("finish.jpg");
        sky = loadTexture("sky.jpg");
    }

    /**
     * Configures the viewing transform.
     */
    @Override
    public void setView() {
        // Select part of window.
        gl.glViewport(0, 0, gs.w, gs.h);

        // Set projection matrix.
        gl.glMatrixMode(GL_PROJECTION);
        gl.glLoadIdentity();

        // Set the perspective.
        // calculate angle by dividing opposite by adjacent line
        Double angle = Math.atan2(gs.vDist, (0.5 * gs.vWidth));
        //Set perspective equal to angle in degrees 
        glu.gluPerspective(Math.toDegrees(angle) * 0.5, (float) gs.w / (float) gs.h, 0.035 * gs.vDist, 10 * gs.vDist);

        // Set camera.
        gl.glMatrixMode(GL_MODELVIEW);
        gl.glLoadIdentity();

        // Update the view according to the camera mode and robot of interest.
        // For camera modes 1 to 4, determine which robot to focus on.
        int best = 0;
        int worst = 0;

        for (int i = 0; i < 4; i++) {
            if (steps[i] > steps[best]) {
                best = i;
            }
            if (steps[i] < steps[worst]) {
                worst = i;
            }
        }

        motorPosition = raceTracks[gs.trackNr].getLanePoint(MOTOR_LANE, steps[best] / N);

        camera.update(gs, robots[best], robots[worst], motorPosition);
        glu.gluLookAt(camera.eye.x(), camera.eye.y(), camera.eye.z(),
                camera.center.x(), camera.center.y(), camera.center.z(),
                camera.up.x(), camera.up.y(), camera.up.z());

        setLighting();
    }

    /**
     * Draws the entire scene.
     */
    @Override
    public void drawScene() {

        // Background color.
        gl.glClearColor(1f, 1f, 1f, 0f);

        // Clear background.
        gl.glClear(GL_COLOR_BUFFER_BIT);

        // Clear depth buffer.
        gl.glClear(GL_DEPTH_BUFFER_BIT);

        gl.glPolygonMode(GL_FRONT_AND_BACK, GL_FILL);

        // Draw the axis frame if desired.
        if (gs.showAxes) {
            drawAxisFrame();
        }

        // create array of speeds variations
        Double[] speeds = new Double[4];
//        System.out.println(luckCount);
        if (luckCount >= 250) {
            luckCount = 0;
            for (int i = 0; i < 4; i++) {
                Random rand = new Random();
                hasLuck[i] = rand.nextBoolean();
            }
        }

        for (int i = 0; i < 4; i++) {
            if (hasLuck[i]) {
                Random rand = new Random();
                speeds[i] = speed + (rand.nextDouble() * 4);
            } else {
                Random rand = new Random();
                speeds[i] = speed + rand.nextDouble();
            }
        }

//         Get the position and direction of the first robot.
        for (int i = 0; i < 4; i++) {
            robots[i].position = raceTracks[gs.trackNr].getLanePoint(i, steps[i] / N);
            robots[i].direction = raceTracks[gs.trackNr].getLaneTangent(i, steps[i] / N);
            // System.out.println(raceTracks[gs.trackNr].getLanePoint(i,steps[i]/N).x);
            // camera.update(gs, robots[i]);
//           System.out.println("Bot: " + i + " speed: " + steps[i]);
            steps[i] += speeds[i];
            luckCount++;
            if (steps[i] / N >= 1) {
                steps[i] = 0d;
            }
        }

        //Draw robots
        gl.glPushMatrix();

        for (int i = 0; i < 4; i++) {
            gl.glPushMatrix();
            gl.glTranslated(robots[i].position.x, robots[i].position.y, robots[i].position.z);
            //double angle = Math.atan2(robots[i].direction.y, robots[i].direction.x);

            // Calculate the dot product between the tangent and the Y axis.
            double dot = robots[i].direction.dot(Vector.Y);

            //Divide by length of y-axis and direction to get cos
            double cosangle = dot / (robots[i].direction.length() * Vector.Y.length());

            //Check if x is negative of possitive     
            double angle;
            if (robots[i].direction.x() >= 0) {
                angle = -Math.acos(cosangle);
            } else {
                angle = Math.acos(cosangle);
            }
            gl.glRotated(Math.toDegrees(angle), 0, 0, 1);

            // Rotate bender to stand perpendicular to lange tangent
            // gl.glRotated(Math.toDegrees(angle) - 90, 0, 0, 1);
            robots[i].draw(gl, glu, glut, gs.showStick, gs.tAnim);
            gl.glPopMatrix();
        }

        gl.glPopMatrix();

        // Draw the race track.
        raceTracks[gs.trackNr].draw(gl, glu, glut, track, brick, finish);

        // Draw the terrain.
//        terrain.draw(gl, glu, glut);
terrain.draw(gl,glut,sky);
    }

    /**
     * Draws the x-axis (red), y-axis (green), z-axis (blue), and origin
     * (yellow).
     */
    public void drawAxisFrame() {
        // Push new matrix to stack to modify safely
        gl.glPushMatrix();

        // Array to store the material
        Material[] colors = new Material[]{Material.RED, Material.GREEN, Material.BLUE};

        // 2D array to store the line translations
        float[][] translation = new float[][]{
            {0.5f, 0f, 0f}, // Red
            {0f, 0.5f, 0f}, // Green
            {0f, 0f, 0.5f} // Blue
        };
        // 2D array to store the rotation coordinates
        float[][] rotation = new float[][]{
            {90f, 0f, 1f, 0f}, // Red
            {90f, -1f, 0f, 0f}, // Green
            {0, 0, 0, 0} // Blue
        };

        // Iterate through i = 0; i < 3; in order to draw the three exis
        // Multidimensional float arrays are used to iterate through the colors
        // Multidimensional float arrays are used to iterate through the translations
        // Multidimensional float arrays are used to iterate through the rotations
        for (int i = 0; i < 3; i++) {
            // Push new matrix to stack to modify safely
            gl.glPushMatrix();
            // Set the color accordingly
            gl.glMaterialf(GL_FRONT, GL_SHININESS, colors[i].shininess);
            gl.glMaterialfv(GL_FRONT, GL_DIFFUSE, colors[i].diffuse, 0);
            gl.glMaterialfv(GL_FRONT, GL_SPECULAR, colors[i].specular, 0);
            // Translate the matrix accordingly
            gl.glTranslatef(translation[i][0], translation[i][1], translation[i][2]);
            // Rotate the matrix accordingly
            gl.glRotatef(rotation[i][0], rotation[i][1], rotation[i][2], rotation[i][3]);

            // Push new matrix to stack to edit safely
            gl.glPushMatrix();
            // Translate cone to end of line
            gl.glTranslatef(0f, 0f, 0.5f);
            // Draw the cone
            glut.glutSolidCone(0.1f, 0.25f, 20, 20);
            // Pop matrix of stack to return origin matrix
            gl.glPopMatrix();

            // Scale the matrix to convert cubes into lines sortof
            gl.glScalef(0.035f, 0.035f, 1f);
            // Draw a cube with 1 meter lenght
            glut.glutSolidCube(1f);
            // Pop matrix of stack to return origin matrix
            gl.glPopMatrix();
        }

        // Push new matrix to stack to edit safely
        gl.glPushMatrix();
        // Set the Draw color to yellow
        gl.glMaterialf(GL_FRONT, GL_SHININESS, Material.YELLOW.shininess);
        gl.glMaterialfv(GL_FRONT, GL_DIFFUSE, Material.YELLOW.diffuse, 0);
        gl.glMaterialfv(GL_FRONT, GL_SPECULAR, Material.YELLOW.specular, 0);
        // Draw the sphere
        glut.glutSolidSphere(0.15f, 20, 20);
        // Pop the matrix back to original state
        gl.glPopMatrix();

        // Pop the matrix back to original state
        gl.glPopMatrix();
    }

    public void setLighting() {
        //Enable shading, ambient light and one light source. Use a light source at infinity.
        //The direction of the light is such that light comes more or less from the direction
        //of the camera. The direction of the light is shifted by 10 degrees to the left and
        //upwards with regard to the view direction.
        // Prepare light parameters.

        //In future can be moved to camera
        //Get position by camera vector. Modify to extend by 10 degree angle
        float[] lightPos = {(float) camera.eye.x - 1f, (float) camera.eye.y, (float) camera.eye.z + 1f, 1.0f};

        //Set ambient lighting
        float[] lightColorAmbient = {0.5f, 0.5f, 0.5f, 1f};

        // Set light parameters.
        gl.glLightfv(GL2.GL_LIGHT0, GL2.GL_POSITION, lightPos, 0);
        gl.glLightfv(GL2.GL_LIGHT0, GL2.GL_AMBIENT, lightColorAmbient, 0);

        // Enable smooth shadow in GL.
        gl.glShadeModel(GL_SMOOTH);

        //Enable lighting
        gl.glEnable(GL2.GL_LIGHT0);
        gl.glEnable(GL2.GL_LIGHTING);
    }

    /**
     * Main program execution body, delegates to an instance of the RobotRace
     * implementation.
     */
    public static void main(String args[]) {
        RobotRace robotRace = new RobotRace();
        robotRace.run();
    }
}