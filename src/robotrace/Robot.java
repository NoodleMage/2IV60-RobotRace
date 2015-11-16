package robotrace;

import com.jogamp.opengl.util.gl2.GLUT;
import javax.media.opengl.GL2;
import javax.media.opengl.glu.GLU;

/**
* Represents a Robot, to be implemented according to the Assignments.
*/
class Robot {
    
    /** The position of the robot. */
    public Vector position = new Vector(0, 0, 0);
    
    /** The direction in which the robot is running. */
    public Vector direction = new Vector(1, 0, 0);

    /** The material from which this robot is built. */
    private final Material material;

    /**
     * Constructs the robot with initial parameters.
     */
    public Robot(Material material
        /* add other parameters that characterize this robot */) {
        this.material = material;

        // code goes here ...
    }

    /**
     * Draws this robot (as a {@code stickfigure} if specified).
     */
    public void draw(GL2 gl, GLU glu, GLUT glut, boolean stickFigure, float tAnim) {
        // code goes here ...
        drawBody(gl, glu, glut, stickFigure, tAnim);
    }
    
    /**
     * Draws the robot body
     */
    public void drawBody(GL2 gl, GLU glu, GLUT glut, boolean stickFigure, float tAnim){
        gl.glPushMatrix();
        if (stickFigure) {
            gl.glColor3f(0f,1f,0f);
            gl.glTranslatef(1f, 1f, 1.35f);
            // gl.glRotatef(90f, 1f, 0f, 0f);
           
            //Body
            gl.glPushMatrix();
            gl.glScalef(0.2f, 0.2f, 1f);
            glut.glutSolidCube(1f);
            gl.glPopMatrix();
            
            //Joint biatch
            gl.glPushMatrix();
            gl.glTranslatef(0f, 0f, 0.5f);
            gl.glScalef(1f, 0.2f, 0.1f);
            glut.glutSolidCube(1f);
            gl.glPopMatrix();
            
            //Draw arms
            //Left arm
            gl.glPushMatrix();
            gl.glTranslatef(0.4f, 0f, 0f);
            gl.glScalef(0.2f, 0.2f, 0.75f);
            glut.glutSolidCube(1f);
            gl.glPopMatrix();
            
            //Right arm
            gl.glPushMatrix();
            gl.glTranslatef(-0.4f, 0f, 0f);
            gl.glScalef(0.2f, 0.2f, 0.75f);
            glut.glutSolidCube(1f);
            gl.glPopMatrix();
             
            
            //Draw head
            gl.glTranslatef(0f,0f,0.75f);
            gl.glPushMatrix();
            gl.glScalef(0.35f, 0.2f, 0.25f);
            glut.glutSolidCube(1f);
            gl.glPopMatrix();
        }
        
        gl.glPopMatrix();
    }
    
    /**
     * Draws the robot Arm
     */
    public void drawArm(GL2 gl, GLU glu, GLUT glut, boolean stickFigure, float tAnim){
        //
    }
    
    /**
     * Draws the robot leg
     */
    public void drawLeg(GL2 gl, GLU glu, GLUT glut, boolean stickFigure, float tAnim){
        //
    }
    
    /**
     * Draws the robot head
     */
    public void drawHead(GL2 gl, GLU glu, GLUT glut, boolean stickFigure, float tAnim){
        //
    }
}
