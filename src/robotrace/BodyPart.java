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

/**
 *
 * @author Indi
 */
public abstract class BodyPart {

    /**
     * The material from which this robot is built.
     */
    public Material material;
    public Material accent;

    public double lenght;
    public double width;
    public double bodyWidthRadius;

    public final int stacks = 30;
    public final int slices = 30;

    public GL2 gl;
    public GLU glu;
    public GLUT glut;

    public float tAnim;

    public void Draw() {
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
        // set the dimensions
        //Calculate width of head with respect to bodywidth
        double headWidth = (this.width / 11) * 5;
        //Calculate height of face (cylinder without hemisphere) with respect to headWidth
        double faceHeight = headWidth * 1.5;
        //Calculate foreheadradius with respect to headWidth
        double foreheadRadius = headWidth / 2;
        //Calculate radius of antenne base with respect to foreheadRadius
        double antennaBaseRadius = foreheadRadius / 5;
        // Draw the face (cylinder without hemisphere).
        gl.glTranslated(0, 0, this.lenght);
        glut.glutSolidCylinder(foreheadRadius, faceHeight, slices, stacks);
        // Draw the forehead (the hemishpere on top of face cylinder).
        gl.glTranslated(0, 0, faceHeight);
        glut.glutSolidSphere(foreheadRadius, slices, stacks);
        //Draw antenna-base
        gl.glTranslated(0, 0, foreheadRadius);
        glut.glutSolidCone(antennaBaseRadius * 0.75, antennaBaseRadius * 6, slices, stacks);
        glut.glutSolidSphere(antennaBaseRadius, slices, stacks);
        gl.glTranslated(0, 0, antennaBaseRadius * 5);
        glut.glutSolidSphere(antennaBaseRadius / 2, slices, stacks);
        // Draw the eyesocket
        gl.glTranslated(0, foreheadRadius * 1.5, -faceHeight * .8);
        gl.glPushMatrix();
        gl.glRotated(90, 1, 0, 0);
        gl.glScaled(1, 0.5, 0.5);
        glut.glutSolidCylinder(foreheadRadius, faceHeight, slices, stacks);
        gl.glPopMatrix();

        //Draw the eyes
        int[] t = new int[]{1, -1}; //t for translation
        for (int i = 0; i < 2; i++) {
            gl.glPushMatrix();

            gl.glTranslated((headWidth * .2) * t[i], .3, 0);
            gl.glRotated(90, 1, 0, 0);

            //Set material to white
            this.setMaterial(Material.WHITE);
            glut.glutSolidCylinder(foreheadRadius * .4, foreheadRadius, slices, stacks);

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
        glut.glutSolidCylinder(3, 0, slices, slices);

        gl.glPushMatrix();
        double[] eqn = {0.0, 0.0, 1.0, 0.0};
        //Create clip plane and enable plane
        gl.glClipPlane(GL2.GL_CLIP_PLANE0, eqn, 0);
        //Translate plane to correct position
        gl.glTranslated(0, 0, this.lenght);
        //Enable plane to cut of cone
        gl.glEnable(GL2.GL_CLIP_PLANE0);

        //Draw solidCone
        glut.glutSolidCone(4.5, -this.lenght * 3, slices, stacks);
        //Disable plane (otherwise all shapes are affected)
        gl.glDisable(GL2.GL_CLIP_PLANE0);
        gl.glPopMatrix();

        gl.glPopMatrix();

        gl.glTranslated(0, 0, this.lenght);
        // Draw the "neck"
        glut.glutSolidCone(this.width / 2, this.lenght * 0.3, slices, stacks);
        gl.glPopMatrix();
    }
}

class Arm extends BodyPart {

    int side;

    public Arm(int s) {
        side = s;
    }

    @Override
    public void Draw() {
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
        //Draw arm
        glut.glutSolidSphere(this.bodyWidthRadius / 6, slices, stacks);
        glut.glutSolidCylinder(this.bodyWidthRadius / 6, this.lenght * .75, slices, stacks);
        //Translate for hand position
        gl.glTranslated(0, 0, this.lenght * .80);

        //Set original color
        setMaterial(this.material);

        //Draw hand
        glut.glutSolidCone(this.bodyWidthRadius / 3, -this.lenght * .3, slices, stacks);
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
                
                //Translate legs for correct position next to body
                gl.glTranslated((this.width*.3)*side, 0, 0);
                gl.glPushMatrix();
                //Draw rotated leg
                gl.glRotated(-7*side, 0, 1, 0);
                glut.glutSolidCylinder(this.bodyWidthRadius/6, this.lenght*.9, slices, stacks);
                gl.glPopMatrix();
                
                //Set original color
                setMaterial(this.material);
                
                //Create clipping plane for hemisphere feet
                double[] eqn = {0.0, 0.0, 1.0, 0.0};
                gl.glClipPlane (GL2.GL_CLIP_PLANE0, eqn,0);
                gl.glEnable (GL2.GL_CLIP_PLANE0);
                //Draw feet
                glut.glutSolidSphere(bodyWidthRadius*.4, slices, stacks);
                gl.glDisable (GL2.GL_CLIP_PLANE0);
                gl.glPopMatrix();
        
        gl.glPopMatrix();
    }
}
