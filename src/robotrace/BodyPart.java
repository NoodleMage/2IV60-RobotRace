/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package robotrace;

import com.jogamp.opengl.util.gl2.GLUT;
import static javax.media.opengl.GL.GL_FRONT;
import javax.media.opengl.GL2;
import static javax.media.opengl.fixedfunc.GLLightingFunc.GL_DIFFUSE;
import static javax.media.opengl.fixedfunc.GLLightingFunc.GL_SHININESS;
import static javax.media.opengl.fixedfunc.GLLightingFunc.GL_SPECULAR;
import javax.media.opengl.glu.GLU;
import static java.lang.Math.abs;

/**
 *
 * @author Indi Nijhof
 */
public abstract class BodyPart {

    // Declarations the bodyparts material objects
    public Material material;
    public Material accent;

    // Declarations for body stats
    public double lenght;
    public double width;
    public double bodyWidthRadius;

    // Declarations for drawing resolution
    public final int stacks = 30;
    public final int slices = 30;

    // Declarations for neccesary OpenGL library's
    public GL2 gl;
    public GLU glu;
    public GLUT glut;

    // Declaration for the animation ticker
    public float tAnim;
    
    /**
     * Draw function for the body part
     */
    public void Draw(){}
    
    /**
     *
     * @param radius The radius of the object
     * @param height The height of the object
     */
    public void SolidCylinder(Double radius, Double height){
        glut.glutSolidCylinder(radius, height, slices, stacks);
    }
    
    /**
     *
     * @param radius The radius of the sphere
     */
    public void SolidSphere(Double radius){
        glut.glutSolidSphere(radius, slices, stacks);
    }
    
    /**
     *
     * @param radius The cones base radius
     * @param height The cones height
     */
    public void SolidCone(Double radius, Double height ){
        glut.glutSolidCone(radius, height, slices, stacks);
    }

    public void setMaterial(Material material) {
        //Set material determined by given robot type
        gl.glMaterialf(GL_FRONT, GL_SHININESS, material.shininess);
        gl.glMaterialfv(GL_FRONT, GL_DIFFUSE, material.diffuse, 0);
        gl.glMaterialfv(GL_FRONT, GL_SPECULAR, material.specular, 0);
    }
}

class Head extends BodyPart {

    @Override
    public void Draw() {
        gl.glPushMatrix();
        //Calculate width of head with respect to bodywidth
        double headWidth = (this.width / 11) * 5;
        //Calculate height of face (cylinder without hemisphere) with respect to headWidth
        double faceHeight = headWidth * 1.5;
        //Calculate foreheadradius with respect to headWidth
        double foreheadRadius = headWidth / 2;
        //Calculate radius of antenne base with respect to foreheadRadius
        double antennaBaseRadius = foreheadRadius / 5;
        
        // Draw the face (cylinder without hemisphere)
        gl.glTranslated(0, 0, this.lenght);
        SolidCylinder(foreheadRadius, faceHeight);
        
        // Draw the forehead (the hemishpere on top of face cylinder).
        gl.glTranslated(0, 0, faceHeight);
        SolidSphere(foreheadRadius);
        
        //Draw antenna-base
        gl.glTranslated(0, 0, foreheadRadius);
        SolidCone(antennaBaseRadius * 0.75, antennaBaseRadius * 6);
        SolidSphere(antennaBaseRadius);
        gl.glTranslated(0, 0, antennaBaseRadius * 5);
        SolidSphere(antennaBaseRadius / 2);
        
        // Draw the eyesocket
        gl.glTranslated(0, foreheadRadius * 1.5, -faceHeight * .8);
        gl.glPushMatrix();
        gl.glRotated(90, 1, 0, 0);
        gl.glScaled(1, 0.5, 0.5);
        SolidCylinder(foreheadRadius, faceHeight);
        gl.glPopMatrix();

        //Draw the eyes
        int[] t = new int[]{1, -1}; //t for translation
        for (int i = 0; i < 2; i++) {
            gl.glPushMatrix();

            gl.glTranslated((headWidth * .2) * t[i], .3, 0);
            gl.glRotated(90, 1, 0, 0);

            //Set material to white
            this.setMaterial(Material.WHITE);
            
            SolidCylinder(foreheadRadius * .4, foreheadRadius);

            gl.glTranslated(0, 0, -.25);
            
            //Set material to black
            this.setMaterial(Material.BLACK);
            
            //glut.glutSolidCylinder(foreheadRadius*.2, foreheadRadius, slices, stacks);
            glut.glutSolidCube((float) (foreheadRadius * .2));
            gl.glPopMatrix();
        }

        //Set material to original
        this.setMaterial(this.material);
        gl.glPopMatrix();
    }
}

class Body extends BodyPart {

    @Override
    public void Draw() {
        gl.glPushMatrix();
        gl.glPushMatrix();
        
        SolidCylinder(3.0, 0.0);

        gl.glPushMatrix();
        
        double[] eqn = {0.0, 0.0, 1.0, 0.0};
        //Create clip plane and enable plane
        gl.glClipPlane(GL2.GL_CLIP_PLANE0, eqn, 0);
        //Translate plane to correct position
        gl.glTranslated(0, 0, this.lenght);
        //Enable plane to cut of cone
        gl.glEnable(GL2.GL_CLIP_PLANE0);

        //Draw solidCone
        SolidCone(4.5, -this.lenght * 3);
        
        //Disable plane (otherwise all shapes are affected)
        gl.glDisable(GL2.GL_CLIP_PLANE0);
        
        gl.glPopMatrix();

        gl.glPopMatrix();

        gl.glTranslated(0, 0, this.lenght);
        
        // Draw the "neck"
        SolidCone(this.width / 2, this.lenght * 0.3);
        
        gl.glPopMatrix();
    }
}

class Arm extends BodyPart {

    int side;
    int upperAnim = 0;
    int lowerAnim = 0;
    
    int upperstep;
    int lowerstep;

    public Arm(int s) {
        side = s;
        upperstep = 5 * s;
        lowerstep = 5;
    }

    @Override
    public void Draw() {
        // Step up the animation tickers
        upperAnim += upperstep;
        lowerAnim += lowerstep * 1;    
        
        if (abs(upperAnim) == 80 && upperAnim >= 0 || upperAnim < 0 && abs(upperAnim) == 30) {
            upperstep = upperstep * -1;
        }
        
        if (lowerAnim == 80 || lowerAnim == 0) {
            lowerstep = lowerstep * -1;
        }
        
        gl.glPushMatrix();
        
        //Translate for correct position on body
        gl.glTranslated(0, 0, this.lenght * .9);

        gl.glPushMatrix();
        
        //Set accent color
        setMaterial(this.accent);
        
        //Translate to correct postition next to body
        gl.glTranslated((this.width * .5) * side, 0, 0);
        
        //Rotate arm
        gl.glRotated(170 * side, 0, 1, 0);
        gl.glRotated(-upperAnim, 1, 0, 0);
        
        //Draw shoulder
        SolidSphere(this.bodyWidthRadius / 6);
        
        // Draw upper arm
        //glut.glutSolidCylinder(this.bodyWidthRadius / 6, this.lenght * .75, slices, stacks);
        SolidCylinder(this.bodyWidthRadius / 6, (this.lenght * .75)/2);
        
        // Draw elbow joint
        gl.glTranslated(0, 0, (this.lenght * .75)/2);
        SolidSphere(this.bodyWidthRadius / 6);
        
        // Draw lower arm
        gl.glRotated(-lowerAnim, 1, 0, 0);
        SolidCylinder(this.bodyWidthRadius / 6, (this.lenght * .75)/2);
        
        //Translate for hand position
        gl.glTranslated(0, 0, (this.lenght * .80)/2);

        //Set original color
        setMaterial(this.material);

        //Draw hand
        SolidCone(this.bodyWidthRadius / 3, -this.lenght * .3);
        
        gl.glPopMatrix();
        gl.glPopMatrix();
    }
}

class Leg extends BodyPart {
    int side;

    public Leg(int s) {
        side = s;
    }

    @Override
    public void Draw() {
        gl.glPushMatrix();
        //Translate for correct leg to body position
        gl.glTranslated(0, 0, -this.lenght * 0.70);
        
        gl.glPushMatrix();
                 //Set accent color
                setMaterial(this.accent);
                
                //Translate legs for correct position to body
                gl.glTranslated((this.width*.3)*side, 0, 0);
                gl.glPushMatrix();
                
                //Draw rotated leg
                //gl.glRotated(-7*side, 0, 1, 0);
                SolidCylinder(this.bodyWidthRadius/6, this.lenght*.9);
                gl.glPopMatrix();
                
                //Set original color
                setMaterial(this.material);
                
                //Create clipping plane for hemisphere feet
                double[] eqn = {0.0, 0.0, 1.0, 0.0};
                gl.glClipPlane (GL2.GL_CLIP_PLANE0, eqn,0);
                gl.glEnable (GL2.GL_CLIP_PLANE0);
                
                //Draw feet
                SolidSphere(bodyWidthRadius*.4);
                gl.glDisable (GL2.GL_CLIP_PLANE0);
                
                gl.glPopMatrix();
        
        gl.glPopMatrix();
    }
}
