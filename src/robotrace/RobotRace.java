package robotrace;

import javax.media.opengl.GL;
import static javax.media.opengl.GL2.*;

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

        // Initialize robot 0
        robots[0] = new Robot(Material.GOLD
        /* add other parameters that characterize this robot */);

        // Initialize robot 1
        robots[1] = new Robot(Material.SILVER
        /* add other parameters that characterize this robot */);

        // Initialize robot 2
        robots[2] = new Robot(Material.WOOD
        /* add other parameters that characterize this robot */);

        // Initialize robot 3
        robots[3] = new Robot(Material.ORANGE
        /* add other parameters that characterize this robot */);

        // Initialize the camera
        camera = new Camera();

        // Initialize the race tracks
        raceTracks = new RaceTrack[5];

        // Test track
        raceTracks[0] = new RaceTrack();

        // O-track
        raceTracks[1] = new RaceTrack(new Vector[]{ /* add control points like:
            new Vector(10, 0, 1), new Vector(10, 5, 1), new Vector(5, 10, 1),
            new Vector(..., ..., ...), ...
         */});

        // L-track
        raceTracks[2] = new RaceTrack(new Vector[]{ /* add control points */});

        // C-track
        raceTracks[3] = new RaceTrack(new Vector[]{ /* add control points */});

        // Custom track
        raceTracks[4] = new RaceTrack(new Vector[]{ /* add control points */});

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
        // Modify this to meet the requirements in the assignment.
        Double angle = Math.atan2(gs.vDist,(0.5*gs.vWidth));
        glu.gluPerspective(Math.toDegrees(angle) * 0.5, (float) gs.w / (float) gs.h, 0.1*gs.vDist, 10 * gs.vDist);

        // Set camera.
        gl.glMatrixMode(GL_MODELVIEW);
        gl.glLoadIdentity();

        // Update the view according to the camera mode and robot of interest.
        // For camera modes 1 to 4, determine which robot to focus on.
        camera.update(gs, robots[0]);
        glu.gluLookAt(camera.eye.x(), camera.eye.y(), camera.eye.z(),
                camera.center.x(), camera.center.y(), camera.center.z(),
                camera.up.x(), camera.up.y(), camera.up.z());
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

        // Set color to black.
        gl.glColor3f(0f, 0f, 0f);

        gl.glPolygonMode(GL_FRONT_AND_BACK, GL_FILL);

        // Draw the axis frame.
        if (gs.showAxes) {
            drawAxisFrame();
        }

        // Get the position and direction of the first robot.
        robots[0].position = raceTracks[gs.trackNr].getLanePoint(0, 0);
        robots[0].direction = raceTracks[gs.trackNr].getLaneTangent(0, 0);

        // Draw the first robot.
        //robots[0].draw(gl, glu, glut, false, gs.tAnim);
        robots[0].draw(gl, glu, glut, true, gs.tAnim);

        // Draw the race track.
        raceTracks[gs.trackNr].draw(gl, glu, glut);

        // Draw the terrain.
        terrain.draw(gl, glu, glut);

        // Unit box around origin.
        //glut.glutWireCube(1f);

        // Move in x-direction.
        gl.glTranslatef(2f, 0f, 0f);

        // Rotate 30 degrees, around z-axis.
        gl.glRotatef(30f, 0f, 0f, 1f);

        // Scale in z-direction.
        gl.glScalef(1f, 1f, 2f);

        // Translated, rotated, scaled box.
        //glut.glutWireCube(1f);
    }

    /**
     * Draws the x-axis (red), y-axis (green), z-axis (blue), and origin
     * (yellow).
     */
    public void drawAxisFrame() {
        // Push new matrix to stack to modify safely
        gl.glPushMatrix();
        
        // 2D array to store the colors
        float[][] colors = new float[][]{
            {255,0,0},  // Red
            {0,255,0},  // Green
            {0,0,255}   // Blue
        };
        // 2D array to store the line translations
        float[][] translation = new float[][]{
            {0.5f, 0f, 0f},  // Red
            {0f, 0.5f, 0f},  // Green
            {0f, 0f, 0.5f}   // Blue
        };
        // 2D array to store the rotation coordinates
        float[][] rotation = new float[][]{
            {90f, 0f, 1f, 0f},   // Red
            {90f, -1f, 0f, 0f},  // Green
            {0,0,0,0}            // Blue
        };

        // Iterate through i = 0; i < 3; in order to draw the three exis
        // Multidimensional float arrays are used to iterate through the colors
        // Multidimensional float arrays are used to iterate through the translations
        // Multidimensional float arrays are used to iterate through the rotations
        for (int i = 0; i < 3; i++) {
            // Push new matrix to stack to modify safely
            gl.glPushMatrix();
            // Set the color accordingly
            gl.glColor3f(colors[i][0], colors[i][1], colors[i][2]);
            // Translate the matrix accordingly
            gl.glTranslatef(translation[i][0], translation[i][1], translation[i][2]);
            // Rotate the matrix accordingly
            gl.glRotatef(rotation[i][0],rotation[i][1],rotation[i][2],rotation[i][3]);

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
        gl.glColor3f(255f, 255f, 0f);
        // Draw the sphere
        glut.glutSolidSphere(0.15f, 20, 20);
        // Pop the matrix back to original state
        gl.glPopMatrix();
        
        // Pop the matrix back to original state
        gl.glPopMatrix();
        // Reset the draw color to black
        gl.glColor3f(0f, 0f, 0f);
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
