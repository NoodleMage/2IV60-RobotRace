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
class Robot {
    
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
    // Determine whether a stuck figure needs to be drawn
    private boolean stick;
    // Sets the scale of the robot
    private final double scale = 0.5;
    // Calculates the bodywidth to scale
    private final double bodyWidth  = scale * 0.75;
    // Calculates the bodylength to scale
    private final float bodyLength = (float) (scale * 1);

    /**
     * Constructs the robot with initial parameters.
     */
    public Robot(Material material
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
        
        //Set material determined by given robot type
        gl.glMaterialf(GL_FRONT, GL_SHININESS, material.shininess);
        gl.glMaterialfv(GL_FRONT, GL_DIFFUSE, material.diffuse, 0);
        gl.glMaterialfv(GL_FRONT, GL_SPECULAR, material.specular, 0);
        
       //Draw Robot
        if (!stickFigure) {
            drawBody(tAnim);
            drawLegs(tAnim);
            drawArms(tAnim);
            drawHead(tAnim);
        }else{
            drawStick();
        } 
    }
    
    /**
     * Draws the robot body
     */
    public void drawBody(float tAnim){
        gl.glPushMatrix();
        // Check whether stick figure shoulld be drawn
        if (stick)
        {
            gl.glScalef(0.05f, 0.2f, 1f);
        }
        // Draw a cylinder for the body
        glut.glutSolidCylinder(bodyWidth,bodyLength,30,30);    
        gl.glPopMatrix();
    }
    
    /**
     * Draws the robot Arm
     */
    public void drawArms(float tAnim){
        // Translate so the arms are besides the body
        double translation = bodyWidth * 1.25;
        gl.glPushMatrix();
        // Check whether stick figure shoulld be drawn
        if (stick)
        {
            gl.glScalef(0.2f, 0.2f, 1f);
        }
        // Draw the right arm
        drawLimb(tAnim, (float) translation);
        // Draw the left arm
        drawLimb(tAnim, -(float) translation);
        gl.glPopMatrix();
    }
    
    public void drawLimb(float tAnim, float t){
        // Calculate the limb length with respect to the body
        double limbLength = bodyLength*0.70;
        // Calculate the limb width with respect to the body
        double limbWidth = bodyWidth*0.20; 
        
        gl.glPushMatrix();
        // Translate limb to be besides body
        gl.glTranslatef(t, 0f, (float) limbWidth);
        
        gl.glPushMatrix();
        // Translate to be below the body
        gl.glTranslatef(0f, 0f, (float) limbLength);
        // Draw the top sphere of the limb
        glut.glutSolidSphere(limbWidth, 30, 30);
        gl.glPopMatrix();
        // Draw the bottom sphere of the limb
        glut.glutSolidSphere(limbWidth, 30, 30);
        // Draw limb cylinder
        glut.glutSolidCylinder(limbWidth,limbLength,30,30);
        gl.glPopMatrix();
    }
    
    /**
     * Draws the robot leg
     */
    public void drawLegs(float tAnim){
        gl.glPushMatrix();
        // calculate to be below body
        float translation = bodyLength * 0.6f;
        // Calculate distance between the legs (thighgap)
        float distance = (float) (bodyWidth * 0.35f);
        // Check whether stick figure shoulld be drawn
        if (stick)
        {
            gl.glScalef(0.2f, 0.2f, 1f);
        }
        // Translate to be below body
        gl.glTranslatef(0f, 0f,-translation); 
        // Draw the limbs
        drawLimb(tAnim, distance);
        drawLimb(tAnim, -distance);
        gl.glPopMatrix();
    }
    
    /***
     * Draws the robot head
     */
    public void drawHead(float tAnim){
        gl.glPushMatrix();
        // Check whether stick figure shoulld be drawn
        if (stick)
        {
            gl.glScalef(0.2f, 0.2f, 1f);
        }
        // Translate to be above body
        gl.glTranslatef(0f, 0f, bodyLength); 
        
        gl.glPushMatrix();
        // Translate for gap between body.
        gl.glTranslatef(0f, 0f, bodyLength * 0.1f); 
        
        //Create ClipPlane for hemisphere shape
        //Create double for plan on z-axis
        double[] eqn = {0.0, 0.0, 1.0, 0.0};
        //Create clip plane and enable plane
        gl.glClipPlane (GL2.GL_CLIP_PLANE0, eqn,0);
        gl.glEnable (GL2.GL_CLIP_PLANE0);
        
        //Rotate 90 degrees for correct location with respect to plane
        gl.glRotatef (90.0f, 1.0f, 0.0f, 0.0f);
        //Draw sphere (which is divided by plane)
        glut.glutSolidSphere(bodyWidth, 30, 30);
        //Disable plane (otherwise all shapes are affected)
        gl.glDisable (GL2.GL_CLIP_PLANE0);
        gl.glPopMatrix();
        
        
        //Draw Left ear
        gl.glPushMatrix();
        //Translate for correct position of the ear
        gl.glTranslatef((float) (scale *0.4f),0f, (float) (scale * 0.55f));
        //Rotate for oblique.
        gl.glRotatef(45, 0f, 1f, 0f);
        gl.glPushMatrix();
        
        //Translate for correct end position of ear
        gl.glTranslatef(0f,0f, (float) (scale * 0.4f));
        //Draw sphere (half of this shape will go inside cylinder to create hemisphere)
        glut.glutSolidSphere(scale * 0.05f, 30, 30);
        gl.glPopMatrix();
        //Draw cylinder
        glut.glutSolidCylinder(scale * 0.05f, scale * 0.4,30,30);
        gl.glPopMatrix();
                
        //Draw right ear
        //Same as left ear but negative
        gl.glPushMatrix();
        gl.glTranslatef((float) (scale *-0.4f),0f, (float) (scale * 0.55f));
        gl.glRotatef(45, 0f, -1f, 0f);
        gl.glPushMatrix();
        gl.glTranslatef(0f,0f, (float) (scale * 0.4f));
        gl.glRotatef (90.0f, 1.0f, 0.0f, 0.0f);
        glut.glutSolidSphere(scale * 0.05f, 30, 30);
        gl.glPopMatrix();
        glut.glutSolidCylinder(scale * 0.05, scale * 0.4,30,30);
        gl.glPopMatrix();
        
        
        //Set material to black
        gl.glMaterialf(GL_FRONT, GL_SHININESS, Material.BLACK.shininess);
        gl.glMaterialfv(GL_FRONT, GL_DIFFUSE, Material.BLACK.diffuse, 0);
        gl.glMaterialfv(GL_FRONT, GL_SPECULAR, Material.BLACK.specular, 0);
        // draw eyes
        gl.glPushMatrix();
        gl.glTranslatef((float) (-bodyWidth * 0.3f), (float) (bodyWidth* 0.9f),bodyLength * 0.3f);
        glut.glutSolidSphere(0.05 * scale, 30, 30);
        gl.glPopMatrix();
        
        gl.glPushMatrix();
        gl.glTranslatef((float) (bodyWidth * 0.3f), (float) (bodyWidth* 0.9f),bodyLength * 0.3f);
        glut.glutSolidSphere(0.05 * scale, 30, 30);
        gl.glPopMatrix();
        
        gl.glPopMatrix();
    }

    private void drawStick() {
        // Draw the Body
        gl.glPushMatrix();
        gl.glScaled(0.1, 0.1, 1);
        glut.glutSolidCube(bodyLength);
        gl.glPopMatrix();
        
        // Draw the head
        gl.glPushMatrix();
        gl.glTranslated(0, 0, bodyLength*0.75);
        glut.glutSolidSphere(bodyLength*0.3, 30, 30);
        gl.glPopMatrix();
        
        // draw the shoulders
        gl.glPushMatrix();
        gl.glTranslated(0, 0, bodyLength*0.5);
        gl.glScaled(1, 0.1, 0.1);
        glut.glutSolidCube((float) bodyWidth);
        gl.glPopMatrix();
        
        // Draw the arms
        gl.glPushMatrix();
        gl.glTranslated(bodyWidth*0.5, 0, bodyLength* 0.25);
        gl.glScaled(0.1, 0.1, 1);
        glut.glutSolidCube((float) (bodyLength* 0.5));
        gl.glPopMatrix();
        
        gl.glPushMatrix();
        gl.glTranslated(-bodyWidth*0.5, 0, bodyLength* 0.25);
        gl.glScaled(0.1, 0.1, 1);
        glut.glutSolidCube((float) (bodyLength* 0.5));
        gl.glPopMatrix();
        
        //Draw the hip joint
        gl.glPushMatrix();
        gl.glTranslated(0, 0, -bodyLength * .5);
        gl.glScaled(1, 0.1, .1);
        glut.glutSolidCube((float) (bodyWidth));
        gl.glPopMatrix();
        
        // Draw the special part for (childish) comedic purposes!!!!!! 
        gl.glPushMatrix();
        gl.glTranslated(0, 0, -bodyLength * .6);
        gl.glScaled(0.1, 0.1, 1);
        glut.glutSolidCube((float) (bodyLength* 0.25));
        gl.glPopMatrix();
        
        // Draw legs
        gl.glPushMatrix();
        gl.glTranslated(bodyWidth * .5, 0, -bodyLength * .75);
        gl.glScaled(0.1, 0.1, 1);
        glut.glutSolidCube((float) (bodyLength* 0.5));
        gl.glPopMatrix();
        
        gl.glPushMatrix();
        gl.glTranslated(-bodyWidth * .5, 0, -bodyLength * .75);
        gl.glScaled(0.1, 0.1, 1);
        glut.glutSolidCube((float) (bodyLength* 0.5));
        gl.glPopMatrix();
    }
}
