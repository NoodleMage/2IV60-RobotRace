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
        float bodyLength = 1;
        double bodyWidth = 0.75;
        
        gl.glColor3f(0f,1f,0f); // Colour the thing green
        gl.glTranslatef(1f, 1f,1f); // Translate so it doesnt interfere with axis frame
        
        drawHead(gl, glu, glut, stickFigure, tAnim, bodyLength, bodyWidth);
        drawBody(gl, glu, glut, stickFigure, tAnim, bodyLength, bodyWidth);
        drawLegs(gl, glu, glut, stickFigure, tAnim, bodyLength, bodyWidth);
        drawArms(gl, glu, glut, stickFigure, tAnim, bodyLength, bodyWidth);
    }
    
    /**
     * Draws the robot body
     */
    public void drawBody(GL2 gl, GLU glu, GLUT glut, boolean stickFigure, float tAnim, float bodyLength, double bodyWidth){
        gl.glPushMatrix();
        glut.glutSolidCylinder(bodyWidth,bodyLength,30,30);    
        gl.glPopMatrix();
    }
    
    /**
     * Draws the robot Arm
     */
    public void drawArms(GL2 gl, GLU glu, GLUT glut, boolean stickFigure, float tAnim, float bodyLength, double bodyWidth){
        double translation = bodyWidth * 1.25;
        gl.glPushMatrix();
        drawLimb(gl, glu, glut, stickFigure, tAnim, bodyLength, bodyWidth, (float) translation); // Left arm
        drawLimb(gl, glu, glut, stickFigure, tAnim, bodyLength, bodyWidth, -(float) translation); // Right arm
        gl.glPopMatrix();
    }
    
    public void drawLimb(GL2 gl, GLU glu, GLUT glut, boolean stickFigure, float tAnim, float bodyLength, double bodyWidth, float t){
        double limbLength = bodyLength*0.70;
        double limbWidth = bodyWidth*0.20; 
        
        gl.glPushMatrix();
        gl.glTranslatef(t, 0f, (float) limbWidth);
        
        gl.glPushMatrix();
        gl.glTranslatef(0f, 0f, (float) limbLength);
        glut.glutSolidSphere(limbWidth, 30, 30);
        gl.glPopMatrix();
        
        glut.glutSolidSphere(limbWidth, 30, 30);
        glut.glutSolidCylinder(limbWidth,limbLength,30,30);
        gl.glPopMatrix();
    }
    
    /**
     * Draws the robot leg
     */
    public void drawLegs(GL2 gl, GLU glu, GLUT glut, boolean stickFigure, float tAnim, float bodyLength, double bodyWidth){
        gl.glPushMatrix();
        gl.glTranslatef(0f, 0f,-0.6f); // Translate to be below body
        drawLimb(gl, glu, glut, stickFigure, tAnim, bodyLength, bodyWidth, 0.25f); // Left arm
        drawLimb(gl, glu, glut, stickFigure, tAnim, bodyLength, bodyWidth, -0.25f); // Right arm
        gl.glPopMatrix();
    }
    
    /**
     * Draws the robot head
     */
    public void drawHead(GL2 gl, GLU glu, GLUT glut, boolean stickFigure, float tAnim, float bodyLength, double bodyWidth){
        gl.glPushMatrix();
        gl.glTranslatef(0f, 0f,01f); // Translate to be above body
        glut.glutSolidSphere(bodyWidth, 30, 30);
        
        // draw eyes
        gl.glPushMatrix();
        gl.glColor3f(0f,0f,0f); // Colour black
        gl.glTranslatef(-0.2f,0.65f,0.3f);
        glut.glutSolidSphere(0.05, 30, 30);
        gl.glColor3f(0f,1f,0f);
        gl.glPopMatrix();
        
        gl.glPushMatrix();
        gl.glColor3f(0f,0f,0f); // Colour black
        gl.glTranslatef(0.2f,0.65f,0.3f);
        glut.glutSolidSphere(0.05, 30, 30);
        gl.glColor3f(0f,1f,0f);
        gl.glPopMatrix();
        
        gl.glPopMatrix();
    }
}
