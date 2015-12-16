/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package robotrace;

import com.jogamp.opengl.util.gl2.GLUT;
import javax.media.opengl.GL2;
import javax.media.opengl.glu.GLU;

/**
 *
 * @author Indi
 */
public abstract class BodyPart {
    
    /** The position of the robot. */
    public Vector position = new Vector(0, 0, 0);
    
    /** The direction in which the robot is running. */
    public Vector direction = new Vector(1, 0, 0);

    /** The material from which this robot is built. */
//    private final Material material;
//    private final Material accent;
    
    private double lenght;
    private double width;
    
    private final int stacks = 30;
    private final int slices = 30;
    
    private GL2 gl;
    private GLU glu;
    private GLUT glut;
    
    public class Head extends BodyPart {
    }

    public class Body extends BodyPart {
    }

    public class Arm extends BodyPart {
    }

    public class Leg extends BodyPart {
    }

}
