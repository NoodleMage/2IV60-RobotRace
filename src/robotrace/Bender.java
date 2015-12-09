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
    private final Material accent;
    
    // Make global variables to simplify the code
    private GL2 gl;
    private GLU glu;
    private GLUT glut;
    // Set the Robot stats
    private final int stacks = 30;
    private final int slices = 30;
    private boolean stick;
    private final double scale = 0.05;
    private double bodyWidth;
    private double bodyWidthRadius;
    private double bodyHeight;
    

    /**
     * Constructs the robot with initial parameters.
     */
    public Bender(Material material, Material accent
        /* add other parameters that characterize this robot */) {
        
        this.material = material;
        this.accent = accent;
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
        
        setMaterial(material);
        
        // Set the base values fot the bot
        bodyWidth = 9;
        bodyWidthRadius = bodyWidth/2;
        bodyHeight = 11;
        
        // Draw the bot
        gl.glPushMatrix();
        // Apply the universal scaling of the bot
        gl.glScaled(scale, scale, scale);
        // Draw robot at correct position on XOY plane.
        gl.glTranslated(0,0,bodyHeight*.7);
        
         //Draw Robot
        if (!stickFigure) {
        drawBody(tAnim);
        drawLegs(tAnim);
        drawArms(tAnim);
        drawHead(tAnim);
         }else{
            drawStick();
        } 
       
       gl.glPopMatrix();
    }
    
    /***
     * Draws the robot head
     */
    public void drawHead(float tAnim){
        gl.glPushMatrix();
        // set the dimensions
        //Calculate width of head with respect to bodywidth
        double headWidth = (bodyWidth/11)*5;
        //Calculate height of face (cylinder without hemisphere) with respect to headWidth
        double faceHeight = headWidth * 1.5;
        //Calculate foreheadradius with respect to headWidth
        double foreheadRadius = headWidth/2;
        //Calculate radius of antenne base with respect to foreheadRadius
        double antennaBaseRadius = foreheadRadius/5;
        // Draw the face (cylinder without hemisphere).
        gl.glTranslated(0, 0, bodyHeight);
        glut.glutSolidCylinder(foreheadRadius, faceHeight, slices, stacks);
        // Draw the forehead (the hemishpere on top of face cylinder).
        gl.glTranslated(0, 0, faceHeight);
        glut.glutSolidSphere(foreheadRadius, slices, stacks);
        //Draw antenna-base
        gl.glTranslated(0, 0, foreheadRadius);
        glut.glutSolidCone(antennaBaseRadius*0.75, antennaBaseRadius*6, slices, stacks);
        glut.glutSolidSphere(antennaBaseRadius, slices, stacks);
        gl.glTranslated(0, 0, antennaBaseRadius*5);
        glut.glutSolidSphere(antennaBaseRadius/2, slices, stacks);
        // Draw the eyesocket
        gl.glTranslated(0, foreheadRadius*1.5, -faceHeight*.8);
        gl.glPushMatrix();  
        gl.glRotated(90, 1, 0, 0);
        gl.glScaled(1, 0.5, 0.5);
        glut.glutSolidCylinder(foreheadRadius, faceHeight, slices, stacks);
        gl.glPopMatrix();
        
        //Draw the eyes
        int[] t = new int[]{1, -1}; //t for translation
            for (int i = 0; i < 2; i++) {
                gl.glPushMatrix();
                
                gl.glTranslated((headWidth*.2)*t[i], .3 , 0);
                gl.glRotated(90, 1, 0, 0);
                
                //Set material to white
                setMaterial(Material.WHITE);
                glut.glutSolidCylinder(foreheadRadius*.4, foreheadRadius, slices, stacks);
                
                gl.glTranslated(0,0,-.25);
                //Set material to black
                setMaterial(Material.BLACK);
                //glut.glutSolidCylinder(foreheadRadius*.2, foreheadRadius, slices, stacks);
                glut.glutSolidCube((float) (foreheadRadius*.2));
                gl.glPopMatrix();
            } 
            
        //Set material to original
        setMaterial(this.material);
        gl.glPopMatrix();
    }
    
    /**
     * Draws the robot body
     */
    public void drawBody(float tAnim){
        gl.glPushMatrix();
        gl.glPushMatrix();
            glut.glutSolidCylinder(3, 0, slices, slices);
            
            gl.glPushMatrix();
            double[] eqn = {0.0, 0.0, 1.0, 0.0};
                //Create clip plane and enable plane
                gl.glClipPlane (GL2.GL_CLIP_PLANE0, eqn,0);
                //Translate plane to correct position
                gl.glTranslated(0, 0, bodyHeight);
                //Enable plane to cut of cone
                gl.glEnable (GL2.GL_CLIP_PLANE0);
                
                //Draw solidCone
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
        //Translate for correct position on body
        gl.glTranslated(0, 0, bodyHeight*.9);
        //initialize value's for fancy for loop
        int[] t = new int[]{1, -1};
        //Start for loop
            for (int i = 0; i < 2; i++) {
                gl.glPushMatrix();
                //Set accent color
                setMaterial(this.accent);
                //Translate to correct postition next to body
                gl.glTranslated((bodyWidth*.5)*t[i], 0, 0);
                //Rotate arm
                gl.glRotated(170*t[i], 0, 1, 0);
                //Draw arm
                glut.glutSolidSphere(bodyWidthRadius/6, slices, stacks);
                glut.glutSolidCylinder(bodyWidthRadius/6, bodyHeight*.75, slices, stacks);
                //Translate for hand position
                gl.glTranslated(0, 0, bodyHeight*.80);
                
                //Set original color
                setMaterial(this.material);
                
                //Draw hand
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
            //Translate for correct arm to body position
            gl.glTranslated(0, 0, -bodyHeight*0.70);
            
            //Initalize value's for fancy for loop
            int[] t = new int[]{1, -1};
            //Start for loop
            for (int i = 0; i < 2; i++) {
                gl.glPushMatrix();
                 //Set accent color
                setMaterial(this.accent);
                
                //Translate legs for correct position next to body
                gl.glTranslated((bodyWidth*.3)*t[i], 0, 0);
                gl.glPushMatrix();
                //Draw rotated leg
                gl.glRotated(-7*t[i], 0, 1, 0);
                glut.glutSolidCylinder(bodyWidthRadius/6, bodyHeight*.9, slices, stacks);
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
        gl.glTranslated(0, 0, bodyHeight*0.75);
        glut.glutSolidSphere(bodyHeight*0.3, 30, 30);
        gl.glPopMatrix();
        
        // draw the shoulders
        gl.glPushMatrix();
        gl.glTranslated(0, 0, bodyHeight*0.5);
        gl.glScaled(1, 0.1, 0.1);
        glut.glutSolidCube((float) bodyWidth);
        gl.glPopMatrix();
        
        // Draw the arms
        gl.glPushMatrix();
        gl.glTranslated(bodyWidth*0.5, 0, bodyHeight* 0.25);
        gl.glScaled(0.1, 0.1, 1);
        glut.glutSolidCube((float) (bodyHeight* 0.5));
        gl.glPopMatrix();
        
        gl.glPushMatrix();
        gl.glTranslated(-bodyWidth*0.5, 0, bodyHeight* 0.25);
        gl.glScaled(0.1, 0.1, 1);
        glut.glutSolidCube((float) (bodyHeight* 0.5));
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
        glut.glutSolidCube((float) (bodyHeight* 0.25));
        gl.glPopMatrix();
        
        // Draw legs
        gl.glPushMatrix();
        gl.glTranslated(bodyWidth * .5, 0, -bodyHeight * .75);
        gl.glScaled(0.1, 0.1, 1);
        glut.glutSolidCube((float) (bodyHeight* 0.5));
        gl.glPopMatrix();
        
        gl.glPushMatrix();
        gl.glTranslated(-bodyWidth * .5, 0, -bodyHeight * .75);
        gl.glScaled(0.1, 0.1, 1);
        glut.glutSolidCube((float) (bodyHeight* 0.5));
        gl.glPopMatrix();
    }
    
    private void setMaterial(Material material)
    {
          //Set material determined by given robot type
        gl.glMaterialf(GL_FRONT, GL_SHININESS, material.shininess);
        gl.glMaterialfv(GL_FRONT, GL_DIFFUSE, material.diffuse, 0);
        gl.glMaterialfv(GL_FRONT, GL_SPECULAR, material.specular, 0);
    }
}
