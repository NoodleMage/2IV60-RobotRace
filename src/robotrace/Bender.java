package robotrace;

import com.jogamp.opengl.util.gl2.GLUT;
import static javax.media.opengl.GL.GL_FRONT;
import javax.media.opengl.GL2;
import static javax.media.opengl.fixedfunc.GLLightingFunc.GL_DIFFUSE;
import static javax.media.opengl.fixedfunc.GLLightingFunc.GL_SHININESS;
import static javax.media.opengl.fixedfunc.GLLightingFunc.GL_SPECULAR;
import javax.media.opengl.glu.GLU;
import javax.media.opengl.glu.*;

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
    private final int stacks = 30;
    private final int slices = 30;
    private boolean stick;
    private final double scale = 0.1;
    private double bodyWidth;
    private double bodyWidthRadius;
    private double bodyHeight;
    

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
        
        // Set the base values fot the bot
        bodyWidth = 9;
        bodyWidthRadius = bodyWidth/2;
        bodyHeight = 11;
        
        // Draw the bot
        gl.glPushMatrix();
        // Apply the universal scaling of the bot
        gl.glScaled(scale, scale, scale);
        gl.glTranslated(0,0,bodyHeight*.7);
        
        drawBody(tAnim);
        drawLegs(tAnim);
        drawArms(tAnim);
        drawHead(tAnim);
       
       gl.glPopMatrix();
    }
    
    /***
     * Draws the robot head
     */
    public void drawHead(float tAnim){
        gl.glPushMatrix();
        // set the dimensions
        double headWidth = (bodyWidth/11)*5;
        double faceHeight = headWidth * 1.5;
        double foreheadRadius = headWidth/2;
        double antennaBaseRadius = foreheadRadius/5;
        // Draw the face
        gl.glTranslated(0, 0, bodyHeight);
        glut.glutSolidCylinder(foreheadRadius, faceHeight, slices, stacks);
        // Draw the forehead
        gl.glTranslated(0, 0, faceHeight);
        glut.glutSolidSphere(foreheadRadius, slices, stacks);
        //Draw antenna-base
        gl.glTranslated(0, 0, foreheadRadius);
        glut.glutSolidCone(antennaBaseRadius*0.75, antennaBaseRadius*6, slices, stacks);
        glut.glutSolidSphere(antennaBaseRadius, slices, stacks);
        gl.glTranslated(0, 0, antennaBaseRadius*5);
        glut.glutSolidSphere(antennaBaseRadius/2, slices, stacks);
        // Draw the eyesocket
        gl.glTranslated(0, foreheadRadius*1.5, -faceHeight);
        gl.glPushMatrix();  
        gl.glRotated(90, 1, 0, 0);
        gl.glScaled(1, 0.5, 0.5);
        glut.glutSolidCylinder(foreheadRadius, faceHeight, slices, stacks);
        gl.glPopMatrix();
        int[] t = new int[]{1, -1};
            for (int i = 0; i < 2; i++) {
                gl.glPushMatrix();
                
                gl.glTranslated((headWidth*.2)*t[i], .3 , 0);
                gl.glRotated(90, 1, 0, 0);
                glut.glutSolidCylinder(foreheadRadius*.4, foreheadRadius, slices, stacks);
                
                gl.glPopMatrix();
            } 
        gl.glPopMatrix();
    }
    
    /**
     * Draws the robot body
     */
    public void drawBody(float tAnim){
        gl.glPushMatrix();
        
        // Draw the body
        //glut.glutSolidCylinder(bodyWidthRadius, bodyHeight, slices, stacks
        
        gl.glPushMatrix();
            glut.glutSolidCylinder(3, 0, slices, slices);
            //gl.glTranslated(0, 0, bodyHeight/2);
            
            gl.glPushMatrix();
            //gl.glTranslated(0, 0, -bodyHeight);
            double[] eqn = {0.0, 0.0, 1.0, 0.0};
                //Create clip plane and enable plane
                gl.glClipPlane (GL2.GL_CLIP_PLANE0, eqn,0);
                gl.glTranslated(0, 0, bodyHeight);
                gl.glEnable (GL2.GL_CLIP_PLANE0);
                
                glut.glutSolidCone(4.5, -bodyHeight*3, slices, stacks);
                //Disable plane (otherwise all shapes are affected)
                gl.glDisable (GL2.GL_CLIP_PLANE0);
            gl.glPopMatrix();
            
        gl.glPopMatrix();
        
        gl.glTranslated(0, 0, bodyHeight);
        // Draw the "neck"
        glut.glutSolidCone(bodyWidthRadius, bodyHeight*0.3, slices, stacks);
        gl.glPopMatrix();
    }
    
    /**
     * Draws the robot Arm
     */
    public void drawArms(float tAnim){
        gl.glPushMatrix();
        gl.glTranslated(0, 0, bodyHeight*.9);
        int[] t = new int[]{1, -1};
            for (int i = 0; i < 2; i++) {
                gl.glPushMatrix();
                gl.glTranslated((bodyWidth*.5)*t[i], 0, 0);
                gl.glRotated(150*t[i], 0, 1, 0);
                glut.glutSolidSphere(bodyWidthRadius/6, slices, stacks);
                glut.glutSolidCylinder(bodyWidthRadius/6, bodyHeight*.75, slices, stacks);
                gl.glTranslated(0, 0, bodyHeight*.80);
                glut.glutSolidCone(bodyWidthRadius/3, -bodyHeight*.3, slices, stacks);
                gl.glPopMatrix();
            } 
        gl.glPopMatrix();
    }
    
    /**
     * Draws the robot leg
     */
    public void drawLegs(float tAnim){
        gl.glPushMatrix();
            gl.glTranslated(0, 0, -bodyHeight*0.70);
            int[] t = new int[]{1, -1};
            for (int i = 0; i < 2; i++) {
                gl.glPushMatrix();
                gl.glTranslated((bodyWidth*.3)*t[i], 0, 0);
                gl.glPushMatrix();
                gl.glRotated(-7*t[i], 0, 1, 0);
                glut.glutSolidCylinder(bodyWidthRadius/6, bodyHeight*.9, slices, stacks);
                gl.glPopMatrix();
                double[] eqn = {0.0, 0.0, 1.0, 0.0};
                gl.glClipPlane (GL2.GL_CLIP_PLANE0, eqn,0);
                gl.glTranslated(0, 0, 0);
                gl.glEnable (GL2.GL_CLIP_PLANE0);
                glut.glutSolidSphere(bodyWidthRadius*.4, slices, stacks);
                gl.glDisable (GL2.GL_CLIP_PLANE0);
                gl.glPopMatrix();
            }   
        gl.glPopMatrix();
    }
}
