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
    // Set the Robot stats
    private boolean stick;
    private final double scale = 0.5;
    private final double bodyWidth  = scale * 0.75;
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
        
        //Set material
        gl.glMaterialf(GL_FRONT, GL_SHININESS, material.shininess);
        gl.glMaterialfv(GL_FRONT, GL_DIFFUSE, material.diffuse, 0);
        gl.glMaterialfv(GL_FRONT, GL_SPECULAR, material.specular, 0);
        
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
        if (stick)
        {
            gl.glScalef(0.05f, 0.2f, 1f);
        }
        glut.glutSolidCylinder(bodyWidth,bodyLength,30,30);    
        gl.glPopMatrix();
    }
    
    /**
     * Draws the robot Arm
     */
    public void drawArms(float tAnim){
        double translation = bodyWidth * 1.25;
        gl.glPushMatrix();
        if (stick)
        {
            gl.glScalef(0.2f, 0.2f, 1f);
        }
        drawLimb(tAnim, (float) translation); // Left arm
        drawLimb(tAnim, -(float) translation); // Right arm
        gl.glPopMatrix();
    }
    
    public void drawLimb(float tAnim, float t){
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
    public void drawLegs(float tAnim){
        gl.glPushMatrix();
        float translation = bodyLength * 0.6f;
        float distance = (float) (bodyWidth * 0.35f);
        
        if (stick)
        {
            gl.glScalef(0.2f, 0.2f, 1f);
        }
        gl.glTranslatef(0f, 0f,-translation); // Translate to be below body
        drawLimb(tAnim, distance);
        drawLimb(tAnim, -distance);
        gl.glPopMatrix();
    }
    
    /***
     * Draws the robot head
     */
    public void drawHead(float tAnim){
        gl.glPushMatrix();
        if (stick)
        {
            gl.glScalef(0.2f, 0.2f, 1f);
        }
        gl.glTranslatef(0f, 0f, bodyLength); // Translate to be above body
        
        gl.glPushMatrix();
        gl.glTranslatef(0f, 0f, bodyLength * 0.1f); // Translate for gap between body.
        double[] eqn = {0.0, 0.0, 1.0, 0.0};
        gl.glClipPlane (GL2.GL_CLIP_PLANE0, eqn,0);
        gl.glEnable (GL2.GL_CLIP_PLANE0);
        gl.glRotatef (90.0f, 1.0f, 0.0f, 0.0f);
        glut.glutSolidSphere(bodyWidth, 30, 30);
        gl.glDisable (GL2.GL_CLIP_PLANE0);
        gl.glPopMatrix();
        
        
        //Draw Left ear
        gl.glPushMatrix();
        gl.glTranslatef((float) (scale *0.4f),0f, (float) (scale * 0.55f));
        gl.glRotatef(45, 0f, 1f, 0f);
        gl.glPushMatrix();
        gl.glTranslatef(0f,0f, (float) (scale * 0.4f));
        glut.glutSolidSphere(scale * 0.05f, 30, 30);
        gl.glPopMatrix();
        glut.glutSolidCylinder(scale * 0.05f, scale * 0.4,30,30);
        gl.glPopMatrix();
                
          //Draw right ear
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
        
        
        //Set black
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
}
