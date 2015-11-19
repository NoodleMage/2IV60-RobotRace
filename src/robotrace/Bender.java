package robotrace;

import com.jogamp.opengl.util.gl2.GLUT;
import static javax.media.opengl.GL.GL_FRONT;
import javax.media.opengl.GL2;
import static javax.media.opengl.fixedfunc.GLLightingFunc.GL_DIFFUSE;
import static javax.media.opengl.fixedfunc.GLLightingFunc.GL_SHININESS;
import static javax.media.opengl.fixedfunc.GLLightingFunc.GL_SPECULAR;
import javax.media.opengl.glu.GLU;

/**
* Represents a Robot, to be implemented according to the Assignments.
*/
class Bender {
    
    /** The position of the robot. */
    public Vector position = new Vector(0, 0, 0);
    
    /** The direction in which the robot is running. */
    public Vector direction = new Vector(1, 0, 0);

    /** The material from which this robot is built. */
    private final Material material;
    
    // Make global variables to simplify the code
    private GL2 gl;
    private GLU glu;
    private GLUT glut;
    // Set the Robot stats
    private boolean stick;

    /**
     * Constructs the robot with initial parameters.
     */
    public Bender(Material material
        /* add other parameters that characterize this robot */) {
        this.material = material;
    }

    /**
     * Draws this robot (as a {@code stickfigure} if specified).
     */
    public void draw(GL2 gl1, GLU glu1, GLUT glut1, boolean stickFigure, float tAnim) {
        // Set the global variables
        this.gl = gl1;
        this.glu = glu1;
        this.glut = glut1;
        this.stick = stickFigure; 
        
       //Draw Robot
       drawBody(tAnim);
       drawLegs(tAnim);
       drawArms(tAnim);
       drawHead(tAnim);
    }
    
    /**
     * Draws the robot body
     */
    public void drawBody(float tAnim){
        gl.glPushMatrix();
    
        gl.glPopMatrix();
    }
    
    /**
     * Draws the robot Arm
     */
    public void drawArms(float tAnim){
        gl.glPushMatrix();

        gl.glPopMatrix();
    }
    
    public void drawLimb(float tAnim, float t){        
        gl.glPushMatrix();

        gl.glPopMatrix();
    }
    
    /**
     * Draws the robot leg
     */
    public void drawLegs(float tAnim){
        gl.glPushMatrix();

        gl.glPopMatrix();
    }
    
    /***
     * Draws the robot head
     */
    public void drawHead(float tAnim){
        gl.glPushMatrix();
        
        gl.glPopMatrix();
    }
}
