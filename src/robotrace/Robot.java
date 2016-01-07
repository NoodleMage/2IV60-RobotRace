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
   
     // The position of the robot
    public Vector position = new Vector(0, 0, 0);

    // The direction in which the robot is running
    public Vector direction = new Vector(1, 0, 0);

    // The material from which this robot is built
    private final Material material;
    private final Material accent;

    // Make global variables to simplify the code
    private GL2 gl;
    private GLUT glut;

    //  Set the Robot stats
    private final double scale = 0.05;
    private final double bodyWidth = 9;
    private final double bodyWidthRadius = this.bodyWidth / 2;
    private final double bodyHeight = 11;
    public double headWidth  = (this.bodyWidth / 11) * 5;
    public double faceHeight = this.headWidth * 1.5;
    public double foreheadRadius  = this.headWidth / 2;
    public double antennaBaseRadius  = this.foreheadRadius / 5;
    public double limbRadius  = this.bodyWidthRadius / 6;
    public double armLength = this.bodyHeight * .75 / 2;

    // New declaration for parameterization update
    private final Head head = new Head();
    private final Body body = new Body();
    private final Arm armL = new Arm(1);
    private final Arm armR = new Arm(-1);
    private final Leg legL = new Leg(1);
    private final Leg legR = new Leg(-1);
    
    // Put the body parts in an array
    BodyPart[] parts = new BodyPart[]{head, body, armL, armR, legL, legR};

    /**
     * Constructs the robot with initial parameters.
     */
    public Robot(Material material, Material accent
    /* add other parameters that characterize this robot */) {
        this.material = material;
        this.accent = accent;
        
        // Assign static values to bodypart attributes
        for (BodyPart p : parts) {
            p.lenght = this.bodyHeight;
            p.width = this.bodyWidth;
            p.bodyWidthRadius = this.bodyWidthRadius;
            p.material = this.material;
            p.accent = this.accent;           
            p.headWidth = this.headWidth;
            p.faceHeight = this.faceHeight;
            p.foreheadRadius = this.foreheadRadius;
            p.antennaBaseRadius = this.antennaBaseRadius;
            p.limbRadius = this.limbRadius;
            p.armLength = this.armLength;
        }
    }

    /**
     * Draws this robot (as a {@code stickfigure} if specified).
     */
    public void draw(GL2 gl1, GLU glu1, GLUT glut1, boolean stickFigure, float tAnim) {      

        this.gl = gl1;
        this.glut = glut1;
        
        // Assign static values to bodypart attributes
        for (BodyPart p : parts) {
            p.gl = gl1;
            p.glu = glu1;
            p.glut = glut1;
            p.tAnim = tAnim;
        }

        // Set the robots material
        setMaterial(material);

        gl.glPushMatrix();
        
        // Apply the universal scaling of the bot
        gl.glScaled(scale, scale, scale);
        
        // Translate for correct position on XOY plane.
        gl.glTranslated(0, 0, bodyHeight * .7);

        // Draw the bodyParts
        for (BodyPart p : parts) {
            p.Draw();
        }

        gl.glPopMatrix();
    }

    private void drawStick() {
        // Draw the Body
        gl.glPushMatrix();
        gl.glScaled(0.1, 0.1, 1);
        glut.glutSolidCube((float) bodyHeight);
        gl.glPopMatrix();

        // Draw the head
        gl.glPushMatrix();
        gl.glTranslated(0, 0, bodyHeight * 0.75);
        glut.glutSolidSphere(bodyHeight * 0.3, 30, 30);
        gl.glPopMatrix();

        // draw the shoulders
        gl.glPushMatrix();
        gl.glTranslated(0, 0, bodyHeight * 0.5);
        gl.glScaled(1, 0.1, 0.1);
        glut.glutSolidCube((float) bodyWidth);
        gl.glPopMatrix();

        // Draw the arms
        gl.glPushMatrix();
        gl.glTranslated(bodyWidth * 0.5, 0, bodyHeight * 0.25);
        gl.glScaled(0.1, 0.1, 1);
        glut.glutSolidCube((float) (bodyHeight * 0.5));
        gl.glPopMatrix();

        gl.glPushMatrix();
        gl.glTranslated(-bodyWidth * 0.5, 0, bodyHeight * 0.25);
        gl.glScaled(0.1, 0.1, 1);
        glut.glutSolidCube((float) (bodyHeight * 0.5));
        gl.glPopMatrix();

        //Draw the hip joint
        gl.glPushMatrix();
        gl.glTranslated(0, 0, -bodyHeight * .5);
        gl.glScaled(1, 0.1, .1);
        glut.glutSolidCube((float) (bodyWidth));
        gl.glPopMatrix();

        // Draw the special part for (childish) comedic purposes!!!!!! 
        gl.glPushMatrix();
        gl.glTranslated(0, 0, -bodyHeight * .6);
        gl.glScaled(0.1, 0.1, 1);
        glut.glutSolidCube((float) (bodyHeight * 0.25));
        gl.glPopMatrix();

        // Draw legs
        gl.glPushMatrix();
        gl.glTranslated(bodyWidth * .5, 0, -bodyHeight * .75);
        gl.glScaled(0.1, 0.1, 1);
        glut.glutSolidCube((float) (bodyHeight * 0.5));
        gl.glPopMatrix();

        gl.glPushMatrix();
        gl.glTranslated(-bodyWidth * .5, 0, -bodyHeight * .75);
        gl.glScaled(0.1, 0.1, 1);
        glut.glutSolidCube((float) (bodyHeight * 0.5));
        gl.glPopMatrix();
    }

    private void setMaterial(Material material) {
        //Set material determined by given robot type
        gl.glMaterialf(GL_FRONT, GL_SHININESS, material.shininess);
        gl.glMaterialfv(GL_FRONT, GL_DIFFUSE, material.diffuse, 0);
        gl.glMaterialfv(GL_FRONT, GL_SPECULAR, material.specular, 0);
    }
}
