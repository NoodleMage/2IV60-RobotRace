package robotrace;

import com.jogamp.opengl.util.gl2.GLUT;
import com.jogamp.opengl.util.texture.Texture;
import static javax.media.opengl.GL.GL_FRONT;
import javax.media.opengl.GL2;
import static javax.media.opengl.fixedfunc.GLLightingFunc.GL_DIFFUSE;
import static javax.media.opengl.fixedfunc.GLLightingFunc.GL_SHININESS;
import static javax.media.opengl.fixedfunc.GLLightingFunc.GL_SPECULAR;
import javax.media.opengl.glu.GLU;
import static java.lang.Math.abs;
import java.util.Random;
import static javax.media.opengl.GL2GL3.GL_QUADS;
import static java.lang.Math.abs;

/**
 *
 * @author Indi Nijhof “ Always code as if the guy who ends up maintaining your
 * code will be a violent psychopath who knows where you live. ”
 */
public abstract class BodyPart {

    /**
     * The bodypart's material
     */
    public Material material;

    /**
     * The bodypart's accent material
     */
    public Material accent;

    /**
     * Length of the body
     */
    public double lenght;

    /**
     * Width of the body
     */
    public double width;

    /**
     * Radius of the body
     */
    public double bodyWidthRadius;

    /**
     * Width of the head
     */
    public double headWidth;

    /**
     * Height (length) of the face
     */
    public double faceHeight;

    /**
     * Radius of the forehead
     */
    public double foreheadRadius;

    /**
     * Radius of th antenna base
     */
    public double antennaBaseRadius;

    /**
     * Radius of the limbs
     */
    public double limbRadius;

    /**
     * Length of the arm (not whole but upper|lower)
     */
    public double armLength;

    /**
     * Number of stacks when drawing (resolution)
     */
    public final int stacks = 30;

    /**
     * Number of slices when drawing (resolution)
     */
    public final int slices = 30;

    // Declarations for neccesary OpenGL library's
    public GL2 gl;
    public GLU glu;
    public GLUT glut;

    public Texture texture;

    /**
     * Declaration for the animation ticker
     */
    public float tAnim;

    /**
     * Object used for generation of pseudorandoms
     */
    public Random rand = new Random();

    /**
     * Draw function for the body part
     */
    public void Draw() {
    }

    /**
     * Draw function for the body part for a stick figure
     */
    public void DrawStick() {
    }

    /**
     * Draws a cylinder
     *
     * @param radius The radius of the object
     * @param height The height of the object
     */
    public void SolidCylinder(Double radius, Double height) {
        glut.glutSolidCylinder(radius, height, slices, stacks);
    }

    /**
     * Draws a circle
     *
     * @param radius Radius of the circle
     */
    public void SolidCircle(Double radius) {
        this.SolidCylinder(radius, 0.0);
    }

    /**
     * Draws a solidSphere
     *
     * @param radius The radius of the sphere
     */
    public void SolidSphere(Double radius) {
        glut.glutSolidSphere(radius, slices, stacks);
    }

    /**
     * Draws a cone
     *
     * @param radius The cones base radius
     * @param height The cones height
     */
    public void SolidCone(Double radius, Double height) {
        glut.glutSolidCone(radius, height, slices, stacks);
    }

    /**
     * Sets the parts material
     *
     * @param material Set material determined by given robot type
     */
    public void setMaterial(Material material) {
        gl.glMaterialf(GL_FRONT, GL_SHININESS, material.shininess);
        gl.glMaterialfv(GL_FRONT, GL_DIFFUSE, material.diffuse, 0);
        gl.glMaterialfv(GL_FRONT, GL_SPECULAR, material.specular, 0);
    }
}

class Head extends BodyPart {

    @Override
    public void DrawStick() {
        // Push me, and then just touch me, till I can't get my..satisfaction
        gl.glPushMatrix();

        // Translate for position on top of neck
        gl.glTranslated(0, 0, this.lenght + (this.faceHeight / 2));
        // Draw the head
        this.SolidSphere(foreheadRadius);

        // Set material to original
        this.setMaterial(this.material);

        // Pop it like its hot
        gl.glPopMatrix();
    }

    @Override
    public void Draw() {
        // Push me, and then just touch me, till I can't get my..satisfaction
        gl.glPushMatrix();

        // Translate for face to be ontop of neck
        gl.glTranslated(0, 0, this.lenght);
        // Draw the face (cylinder without hemisphere)
        this.SolidCylinder(foreheadRadius, faceHeight);
        
         //Set material to white
            this.setMaterial(Material.WHITE);
        // 2D array to store the line translations
        int[][] coordinates2d = new int[][]{
            {0, 0},
            {1, 0},
            {1, 1},
            {0, 1}};

        gl.glPushMatrix();
        if (texture != null) {
            texture.enable(gl);
            texture.bind(gl);
        }
        
        double[][] coordinates3d = new double[][]{
            {this.foreheadRadius*0.8, this.foreheadRadius, this.faceHeight/1.5},
            {-this.foreheadRadius*0.8, this.foreheadRadius, this.faceHeight/1.5},
            {-this.foreheadRadius*0.8, this.foreheadRadius, this.foreheadRadius},
            {this.foreheadRadius*0.8, this.foreheadRadius, this.foreheadRadius}};

        gl.glBegin(GL_QUADS);
        for (int i = 0; i < coordinates2d.length; i++) {
            gl.glTexCoord2f(coordinates2d[i][0], coordinates2d[i][1]);
            gl.glVertex3d(coordinates3d[i][0], coordinates3d[i][1], coordinates3d[i][2]);
        }
        if (texture != null) {
            gl.glEnd();
        }
        

        texture.disable(gl);
        // Pop it like it's hot 
        gl.glPopMatrix();
         //Set material to white
            this.setMaterial(this.material);
        // Translate for forehead to be (semi) ontop of face
        gl.glTranslated(0, 0, faceHeight);
        // Draw the forehead (the hemishpere on top of face cylinder).
        this.SolidSphere(foreheadRadius);

        // Translate to be ontop of head
        gl.glTranslated(0, 0, foreheadRadius);
        // Draw a cone to be the antenna
        this.SolidCone(antennaBaseRadius * 0.75, antennaBaseRadius * 6);
        // Draw a (hemi)sphere
        this.SolidSphere(antennaBaseRadius);
        // Translate to top of antenna cone
        gl.glTranslated(0, 0, antennaBaseRadius * 5);
        // Draw the top sphere of the antenna
        this.SolidSphere(antennaBaseRadius / 2);

        // Translate to place eyesocket on face
        gl.glTranslated(0, foreheadRadius * 1.5, -faceHeight * .8);
        // Push me, and then just touch me, till I can't get my..satisfaction
        gl.glPushMatrix();
        // Rotate over x-axis to place perpendicular on face
        gl.glRotated(90, 1, 0, 0);
        // Scale to form an elipsoid
        gl.glScaled(1, 0.5, 0.5);
        // Draw the eyesocket
        this.SolidCylinder(foreheadRadius, faceHeight);

        // Pop it like its hot
        gl.glPopMatrix();

        //Draw the eyes
        int[] t = new int[]{1, -1}; //t for translation

        for (int i = 0; i < 2; i++) {
            // Push me, and then just touch me, till I can't get my..satisfaction
            gl.glPushMatrix();

            // Translate as to not create a cyclops
            gl.glTranslated((headWidth * .2) * t[i], .3, 0);

            // Rotate over x-axis for eye (cylinder) to be perpendicular to face
            gl.glRotated(90, 1, 0, 0);

            //Set material to white
            this.setMaterial(Material.WHITE);

            // Draw the eye (eye of the tiger|eye of the beholder)
            SolidCylinder(foreheadRadius * .4, foreheadRadius);

            // Translate to centre the pupil
            gl.glTranslated(0, 0, -.25);

            //Set material to black
            this.setMaterial(Material.BLACK);

            // Draw the pupil
            glut.glutSolidCube((float) (foreheadRadius * .2));

            // Pop it like it's hot
            gl.glPopMatrix();
        }

        //Set material to original
        this.setMaterial(this.material);

        // Pop it like it's hot
        gl.glPopMatrix();
    }
}

class Body extends BodyPart {

    @Override
    public void DrawStick() {
        // Push me, and then just touch me, till I can't get my..satisfaction
        gl.glPushMatrix();

        // Draw the body
        this.SolidCylinder(this.bodyWidthRadius / 20, this.lenght);

        // Translate for neck to be ontop of body
        gl.glTranslated(0, 0, this.lenght);

        // Draw the "neck"
        this.SolidCylinder(this.bodyWidthRadius / 20, this.lenght * 0.3);

        // Pop it like it's hot
        gl.glPopMatrix();
    }

    @Override
    public void Draw() {
        // Push me, and then just touch me, till I can't get my..satisfaction
        gl.glPushMatrix();
        // Push me, and then just touch me, till I can't get my..satisfaction
        gl.glPushMatrix();

        // Draw circle for bots ass
        this.SolidCylinder(3.0, 0.0);

        // Define values for clipping pane
        double[] eqn = {0.0, 0.0, 1.0, 0.0};
        //Create clip plane and enable plane
        gl.glClipPlane(GL2.GL_CLIP_PLANE0, eqn, 0);
        //Translate plane to correct position
        gl.glTranslated(0, 0, this.lenght);
        //Enable plane to cut of cone
        gl.glEnable(GL2.GL_CLIP_PLANE0);

        //Draw the body (cone)
        this.SolidCone(4.5, -this.lenght * 3);

        // 2D array to store the line translations
        int[][] coordinates2d = new int[][]{
            {0, 0},
            {1, 0},
            {1, 1},
            {0, 1}};

        gl.glPushMatrix();
        if (texture != null) {
            texture.enable(gl);
            texture.bind(gl);
        }
        
        gl.glRotated(-7, 1, 0, 0);
        double[][] coordinates3d = new double[][]{
            {this.bodyWidthRadius, this.bodyWidthRadius, -this.lenght},
            {-this.bodyWidthRadius, this.bodyWidthRadius, -this.lenght},
            {-this.bodyWidthRadius, this.bodyWidthRadius, 0},
            {this.bodyWidthRadius, this.bodyWidthRadius, 0}};

        gl.glBegin(GL_QUADS);
        for (int i = 0; i < coordinates2d.length; i++) {
            gl.glTexCoord2f(coordinates2d[i][0], coordinates2d[i][1]);
            gl.glVertex3d(coordinates3d[i][0], coordinates3d[i][1], coordinates3d[i][2]);
        }
        gl.glEnd();
        if (texture != null) {
            texture.disable(gl);
        }
        
        // Pop it like it's hot 
        gl.glPopMatrix();

        //Disable plane (otherwise all shapes are affected)
        gl.glDisable(GL2.GL_CLIP_PLANE0);

        // Pop it like it's hot
        gl.glPopMatrix();

        // Translate for neck to be ontop of body
        gl.glTranslated(0, 0, this.lenght);

        // Draw the "neck"
        this.SolidCone(this.width / 2, this.lenght * 0.3);

        // Pop it like it's hot
        gl.glPopMatrix();
    }
}

class Arm extends BodyPart {

    // Left | Right
    public int side;

    // Angle of the upper arm with respect to shoulder joint
    private int upperAnim = 0;
    // Angle of lowerarm with respect to upper arm
    private int lowerAnim = 0;

    // Animation step (speed?) for te rotations
    private int upperstep;
    private int lowerstep;

    public Arm(int s) {
        side = s;
        // modify so left and right arm are not the same
        upperstep = 8 * -s;
        lowerstep = 5;
    }

    @Override
    public void DrawStick() {
        // Step up the animation tickers
        upperAnim += upperstep;
        lowerAnim += lowerstep;

        // switch arm sway direction when needed
        if (abs(upperAnim) >= 80 && upperAnim >= 0 || upperAnim < 0 && abs(upperAnim) >= 45) {
            upperstep = upperstep * -1;
        }

        // switch arm sway direction when needed
        if (lowerAnim == 80 || lowerAnim == 0) {
            lowerstep = lowerstep * -1;
        }

        // Push me, and then just touch me, till I can't get my..satisfaction
        gl.glPushMatrix();

        //Translate for correct position on body
        gl.glTranslated(0, 0, this.lenght * .9);

        // Push me, and then just touch me, till I can't get my..satisfaction
        gl.glPushMatrix();

        //Set accent color
        this.setMaterial(this.accent);

        //Translate to correct postition next to body
        gl.glTranslated((this.width * .5) * this.side, 0, 0);

        //Rotate over y-axis to offset from the body
        gl.glRotated(170 * this.side, 0, 1, 0);

        // Animate swaying of uppe arm
        gl.glRotated(-this.upperAnim, 1, 0, 0);

        //Draw shoulder
        this.SolidSphere(this.limbRadius / 2);

        // Draw upper arm
        this.SolidCylinder(this.limbRadius / 5, this.armLength);

        // Translate for joint to be at end of upper arm
        gl.glTranslated(0, 0, this.armLength);

        // Draw the joint
        this.SolidSphere(this.limbRadius / 2);

        // Set rotation of lower arm
        gl.glRotated(-lowerAnim, 1, 0, 0);

        // Draw the lower arm
        this.SolidCylinder(this.limbRadius / 5, this.armLength);

        //Set original color
        this.setMaterial(this.material);

        // Pop it like it's hot
        gl.glPopMatrix();
        // Pop it like it's hot
        gl.glPopMatrix();
    }

    @Override
    public void Draw() {
        // Step up the animation tickers
        upperAnim += upperstep;
        lowerAnim += lowerstep;

        // switch arm sway direction when needed
        if (abs(upperAnim) >= 80 && upperAnim >= 0 || upperAnim < 0 && abs(upperAnim) >= 45) {
            upperstep = upperstep * -1;
        }

        // switch arm sway direction when needed
        if (lowerAnim == 80 || lowerAnim == 0) {
            lowerstep = lowerstep * -1;
        }

        // Push me, and then just touch me, till I can't get my..satisfaction
        gl.glPushMatrix();

        //Translate for correct position on body
        gl.glTranslated(0, 0, this.lenght * .9);

        // Push me, and then just touch me, till I can't get my..satisfaction
        gl.glPushMatrix();

        //Set accent color
        this.setMaterial(this.accent);

        //Translate to correct postition next to body
        gl.glTranslated((this.width * .5) * this.side, 0, 0);

        //Rotate over y-axis to offset from the body
        gl.glRotated(170 * this.side, 0, 1, 0);

        // Animate swaying of uppe arm
        gl.glRotated(-this.upperAnim, 1, 0, 0);

        //Draw shoulder
        this.SolidSphere(this.limbRadius);

        // Draw upper arm
        this.SolidCylinder(this.limbRadius, this.armLength);

        // Translate for joint to be at end of upper arm
        gl.glTranslated(0, 0, this.armLength);

        // Draw the joint
        this.SolidSphere(this.limbRadius);

        // Set rotation of lower arm
        gl.glRotated(-lowerAnim, 1, 0, 0);

        // Draw the lower arm
        this.SolidCylinder(this.limbRadius, this.armLength);

        // Translate for hand position
        gl.glTranslated(0, 0, (this.lenght * .80) / 2);

        //Set original color
        this.setMaterial(this.material);

        // Draw hand (cone)
        this.SolidCone(this.bodyWidthRadius / 3, -this.lenght * .3);
        // Close the hand cone | create palm 
        this.SolidCircle(this.bodyWidthRadius / 3);

        // Pop it like it's hot
        gl.glPopMatrix();
        // Pop it like it's hot
        gl.glPopMatrix();
    }
}

class Leg extends BodyPart {

    int side;

    int step = 5;
    int angle = 0;

    public Leg(int s) {
        this.side = s;
        this.step = this.step * s;
    }

    @Override
    public void DrawStick() {
        this.angle += this.step;

        if (abs(this.angle) == 40) {
            this.step = this.step * -1;
        }

        // Push me, and then just touch me, till I can't get my..satisfaction
        gl.glPushMatrix();

        //Set accent color
        this.setMaterial(this.accent);

        //Translate legs for correct position to body
        gl.glTranslated((this.width * .2) * this.side, 0, this.limbRadius);
        gl.glPushMatrix();

        //Draw rotated leg
        gl.glRotated(-7 * this.side, 0, 1, 0);

        // Animate the leg swaying
        gl.glRotated(this.angle, 1, 0, 0);

        // Draw the leg
        this.SolidCylinder(this.limbRadius / 5, -this.lenght * .8);

        //Set original color
        this.setMaterial(this.material);

        gl.glPopMatrix();

        gl.glPopMatrix();
    }

    @Override
    public void Draw() {
        this.angle += this.step;

        if (abs(this.angle) == 40) {
            this.step = this.step * -1;
        }

        // Push me, and then just touch me, till I can't get my..satisfaction
        gl.glPushMatrix();

        //Set accent color
        this.setMaterial(this.accent);

        //Translate legs for correct position to body
        gl.glTranslated((this.width * .2) * this.side, 0, this.limbRadius);

        // Push me, and then just touch me, till I can't get my..satisfaction
        gl.glPushMatrix();

        //Draw rotated leg
        gl.glRotated(-7 * this.side, 0, 1, 0);

        // Animate the leg swaying
        gl.glRotated(this.angle, 1, 0, 0);

        // Draw the leg
        this.SolidCylinder(this.limbRadius, -this.lenght * .8);
        //gl.glPopMatrix();

        gl.glTranslated(0, 0, -this.lenght * 0.80);

        //Set original color
        this.setMaterial(this.material);

        //Create clipping plane for hemisphere foot
        double[] eqn = {0.0, 0.0, 1.0, 0.0};
        gl.glClipPlane(GL2.GL_CLIP_PLANE0, eqn, 0);
        gl.glEnable(GL2.GL_CLIP_PLANE0);

        //Draw foot
        this.SolidSphere(this.bodyWidthRadius * .4);
        gl.glDisable(GL2.GL_CLIP_PLANE0);

        // Draw foot sole
        this.SolidCircle(this.bodyWidthRadius * .4);

        gl.glPopMatrix();

        gl.glPopMatrix();
    }
}
