package robotrace;

import com.jogamp.opengl.util.gl2.GLUT;
import com.jogamp.opengl.util.texture.Texture;
import java.awt.Color;
import java.util.Random;
import static javax.media.opengl.GL.GL_FRONT;
import static javax.media.opengl.GL.GL_TRIANGLES;
import static javax.media.opengl.GL2GL3.GL_QUADS;
import static javax.media.opengl.GL2GL3.GL_TEXTURE_1D;
import static javax.media.opengl.fixedfunc.GLLightingFunc.GL_DIFFUSE;
import static javax.media.opengl.fixedfunc.GLLightingFunc.GL_SHININESS;
import static javax.media.opengl.fixedfunc.GLLightingFunc.GL_SPECULAR;

import javax.media.opengl.GL2;

/**
 * Implementation of the terrain.
 */
public class Terrain {

    /**
     * Amount of trees
     */
    private static final int AMOUNT_TREES = 12;

    /**
     * height of tree
     */
    private final int[] tree_heights;
    /**
     * x-coordinate of tree
     */
    private final int[] tree_x;
    /**
     * y-coordinate of tree
     */
    private final int[] tree_y;
    /**
     * radius of tree
     */
    private final double[] tree_radius;

    /**
     * Whether the tree values have been calculated
     */
    private boolean treesCalculated = false;

    /**
     * Number of segments to be used to draw the terrain (per dimension per
     * direction).
     */
    private final static int SEGMENTS = 50;

    /**
     * First x of the terrain.
     */
    private final int xBegin = 0;

    /**
     * Size in x of the terrain.
     */
    private final int xSize = 40;

    /**
     * First y of the terrain.
     */
    private final int yBegin = 0;

    /**
     * Size in y of the terrain.
     */
    private final int ySize = 40;

    /**
     * Returns the height of the terrain at a specific x and y.
     */
    private double heightAt(double x, double y) {
        //for the field -25<x<25 and -25<y<25 set water
        if (x > - 25 && x < 25 && y > - 25 && y < 25) {
            double test = -1 * (Math.abs(-6 * Math.cos(0.3 * x + 0.15 * y) + 0.4 * Math.cos(x - 0.5 * y)));
            return test;
        } //else set mountains
        else {
            double test = Math.abs(-6 * Math.cos(0.3 * x + 0.2 * y) + 0.4 * Math.cos(x - 0.5 * y));
            return test;
        }
    }

    /**
     * The colors for the 1D texture
     */
    private final Vector[] textureColors = new Vector[]{
        new Vector(Color.BLUE.getRed() / 255, Color.BLUE.getGreen() / 255, Color.BLUE.getBlue() / 255),
        new Vector(Color.YELLOW.getRed() / 255, Color.YELLOW.getGreen() / 255, Color.YELLOW.getBlue() / 255),
        new Vector(Color.GREEN.getRed() / 255, Color.GREEN.getGreen() / 255, Color.GREEN.getBlue() / 255)};

    /**
     * The texid for the 1D texture
     */
    private Texture1D texture;

    /**
     * Constructs the terrain.
     */
    public Terrain() {
        //Initialize tree arrays.
        this.tree_heights = new int[AMOUNT_TREES];
        this.tree_x = new int[AMOUNT_TREES];
        this.tree_y = new int[AMOUNT_TREES];
        this.tree_radius = new double[AMOUNT_TREES];

    }

    /**
     * Draws the terrain.
     *
     * @param gl
     * @param glut
     * @param sky
     */
    public void draw(GL2 gl, GLUT glut, Texture sky) {
        // If the trees have not yet been calculated, calculate the tree value's
        if (!treesCalculated) {

            //Create new random
            Random random = new Random();

            //Loop through amount of trees
            for (int i = 0; i < AMOUNT_TREES; i++) {

                //Set random tree height between 6 and 10
                tree_heights[i] = random.nextInt(4) + 6;

                //Set random tree radius between 0.3 and 0.6
                tree_radius[i] = (double) (random.nextInt(4) + 3) / 10;

                //Get random negative.
                //We loop through random and wait till random is or -1 and 1.
                //Because 0 is a possible value we use the while loop to check if not 0
                //We are aware that this is a bit of a cheaty work around
                //But we have no better solution at the moment.
                int negative = 0;
                while (negative == 0) {
                    negative = random.nextInt(3) - 1;
                }

                int negative2 = 0;
                while (negative2 == 0) {
                    negative2 = random.nextInt(3) - 1;
                }
                
                //Use random negative to get random location at the corners of the map
                tree_x[i] = negative * (random.nextInt(25) + 15);
                tree_y[i] = negative2 * (random.nextInt(25) + 15);

            }
            
            //Set treesCalculated to true
            treesCalculated = true;

        }

        //Draw trees
        for (int i = 0; i < AMOUNT_TREES; i++) {

            drawTree(gl, glut, tree_radius[i], tree_heights[i], tree_x[i], tree_y[i], heightAt(tree_x[i], tree_y[i]));
        }

        // Draw the gray transparent surface
        setMaterial(Material.BLACK, gl);

        //Create 1d terain texture
        texture = new Texture1D(gl, textureColors);

        // Bind the terrain texture
        texture.bind(gl);
        // execute the draw texture function
        drawTexture(gl);
        // Unbind the terrain texture
        gl.glBindTexture(GL_TEXTURE_1D, 0);
        
        

        // Create the water texture
        setMaterial(Material.WATER, gl);
        gl.glBegin(GL_QUADS);
        gl.glVertex3d(-40, -40, 0);
        gl.glVertex3d(40, -40, 0);
        gl.glVertex3d(40, 40, 0);
        gl.glVertex3d(-40, 40, 0);
        gl.glEnd();

        //Draw the sky
        gl.glPushMatrix();
        // Draw the gray transparent surface
        setMaterial(Material.SKY, gl);
        sky.enable(gl);
        sky.bind(gl);
        
        //Draw first block of quads
        gl.glBegin(GL_QUADS);
        gl.glTexCoord2f(0, 0);
        gl.glVertex3d(40, -40, -5);
        gl.glTexCoord2f(1, 0);
        gl.glVertex3d(40, -40, 40);
        gl.glTexCoord2f(1, 1);
        gl.glVertex3d(40, 40, 40);
        gl.glTexCoord2f(0, 1);
        gl.glVertex3d(40, 40, -5);
        gl.glEnd();

        //Draw second bloc of quads
        gl.glBegin(GL_QUADS);
        gl.glTexCoord2f(0, 0);
        gl.glVertex3d(-40, 40, -5);
        gl.glTexCoord2f(1, 0);
        gl.glVertex3d(-40, 40, 40);
        gl.glTexCoord2f(1, 1);
        gl.glVertex3d(40, 40, 40);
        gl.glTexCoord2f(0, 1);
        gl.glVertex3d(40, 40, -5);
        gl.glEnd();
        
        
        //Draw third block of quads
        gl.glBegin(GL_QUADS);
        gl.glTexCoord2f(0, 0);
        gl.glVertex3d(40, -40, -5);
        gl.glTexCoord2f(1, 0);
        gl.glVertex3d(40, -40, 40);
        gl.glTexCoord2f(1, 1);
        gl.glVertex3d(-40, -40, 40);
        gl.glTexCoord2f(0, 1);
        gl.glVertex3d(-40, -40, -5);
        gl.glEnd();

        //Draw fourth block of quads
        gl.glBegin(GL_QUADS);
        gl.glTexCoord2f(0, 0);
        gl.glVertex3d(-40, -40, -5);
        gl.glTexCoord2f(1, 0);
        gl.glVertex3d(-40, -40, 40);
        gl.glTexCoord2f(1, 1);
        gl.glVertex3d(-40, 40, 40);
        gl.glTexCoord2f(0, 1);
        gl.glVertex3d(-40, 40, -5);
        gl.glEnd();
        
        //Draw the ceiling.
        gl.glBegin(GL_QUADS);
        gl.glTexCoord2f(0, 0);
        gl.glVertex3d(-xSize, -ySize, 40);
        gl.glTexCoord2f(1, 0);
        gl.glVertex3d(-xSize, ySize, 40);
        gl.glTexCoord2f(1, 1);
        gl.glVertex3d(xSize, ySize, 40);
        gl.glTexCoord2f(0, 1);
        gl.glVertex3d(xSize, -ySize, 40);
        gl.glEnd();
        gl.glPopMatrix();
        sky.disable(gl);
        // Draw the gray transparent surface
        setMaterial(Material.BLACK, gl);
    }

    //Draw the 1D texture
    public void drawTexture(GL2 gl) {
        gl.glBegin(GL_TRIANGLES);
        for (int xi = -SEGMENTS; xi < SEGMENTS; xi++) {
            // Calculate the two x's
            double x1 = xBegin + xSize * xi / ((double) SEGMENTS);
            double x2 = xBegin + xSize * (xi + 1) / ((double) SEGMENTS);
            for (int yi = -SEGMENTS; yi < SEGMENTS; yi++) {
                // Calculate the two y's
                double y1 = yBegin + ySize * yi / ((double) SEGMENTS);
                double y2 = yBegin + ySize * (yi + 1) / ((double) SEGMENTS);
                // Calculate the heights of the terrain
                double f11 = heightAt(x1, y1);
                double f12 = heightAt(x1, y2);
                double f21 = heightAt(x2, y1);
                double f22 = heightAt(x2, y2);

                // Draw the first triangle between (x1, y1), (x1, y2) and (x2, y2)
                {
                    // Create vector objects for points
                    Vector point1 = new Vector(x1, y1, f11);
                    Vector point2 = new Vector(x1, y2, f12);
                    Vector point3 = new Vector(x2, y2, f22);
                    // Create normal vector
                    Vector normal = point2.subtract(point1).cross(point3.subtract(point1)).scale(-1);
                    // Set normal vector
                    gl.glNormal3d(normal.x(), normal.y(), normal.z());
                    // Draw 3 vertices with texture colors
                    gl.glTexCoord1d(getTextureCoordinateFromHeight(point1.z()));
                    gl.glVertex3d(point1.x(), point1.y(), point1.z());
                    gl.glTexCoord1d(getTextureCoordinateFromHeight(point2.z()));
                    gl.glVertex3d(point2.x(), point2.y(), point2.z());
                    gl.glTexCoord1d(getTextureCoordinateFromHeight(point3.z()));
                    gl.glVertex3d(point3.x(), point3.y(), point3.z());
                }
                // Draw the first triangle between (x1, y1), (x2, y1) and (x2, y2)
                {
                    // Create vector objects for points
                    Vector point1 = new Vector(x1, y1, f11);
                    Vector point2 = new Vector(x2, y1, f21);
                    Vector point3 = new Vector(x2, y2, f22);
                    // Create normal vector
                    Vector normal = point2.subtract(point1).cross(point3.subtract(point1));
                    // Set normal vector
                    gl.glNormal3d(normal.x(), normal.y(), normal.z());
                    // Draw 3 vertices with texture colors
                    gl.glTexCoord1d(getTextureCoordinateFromHeight(point1.z()));
                    gl.glVertex3d(point1.x(), point1.y(), point1.z());
                    gl.glTexCoord1d(getTextureCoordinateFromHeight(point2.z()));
                    gl.glVertex3d(point2.x(), point2.y(), point2.z());
                    gl.glTexCoord1d(getTextureCoordinateFromHeight(point3.z()));
                    gl.glVertex3d(point3.x(), point3.y(), point3.z());
                }
            }
        }
        // Finish the triangle list
        gl.glEnd();
    }
    
    //Get  correct color from height
    public double getTextureCoordinateFromHeight(double height) {
        //The value of the texture color
        double textureCoordinate;

        //if height smaller then 0 then return blue value
        if (height < 0) {
            textureCoordinate = 0.25; //Blue texture
        } 
        //if height bigger then 0.75 then return green value
        else if (height > 0.75) {
            textureCoordinate = 0.75; //Green texture
        } 
        //if height smaller then 0 then return yellow value
        else {
            textureCoordinate = 0.5; //Yellow texture
        }

        //Return the correct value in color spectrum
        return textureCoordinate;
    }

    /**
     * function to draw a tree
     *
     * @param gl
     * @param glut
     * @param radius
     * @param height
     * @param x
     * @param y
     * @param z
     */
    public void drawTree(GL2 gl, GLUT glut, double radius, int height, double x, double y, double z) {
        //Initialize matrix
        gl.glPushMatrix();
        //Translate over x,y,z
        gl.glTranslated(x, y, z);

        //Set material to wood for base of tree
        setMaterial(Material.WOOD, gl);
        //Draw tree trunk
        glut.glutSolidCylinder(radius, height - 0.75, 30, 30);

        //Set Material to green
        setMaterial(Material.GREEN, gl);
        for (int i = 0; i < 3; i++) {
            //Translate over tree trunk
            gl.glTranslated(0, 0, height / 3 - height / 12);

            //Draw tree part
            glut.glutSolidCone(radius * 4, height / 3, 30, 30);

            //Initial code for christmas balls. Not used yet...
//            if (i == 1)
//            {
//                gl.glPushMatrix();
//                 setMaterial(Material.BLUE, gl);
//                gl.glTranslated(radius * 2, radius * 2, -0.1);
//                glut.glutSolidSphere(0.5, 30,30);
//                 setMaterial(Material.GREEN, gl);
//                gl.glPopMatrix();
//            }
        }

        gl.glPopMatrix();
    }
//    

    private void setMaterial(Material material, GL2 gl) {
        //Set material determined by given robot type
        gl.glMaterialf(GL_FRONT, GL_SHININESS, material.shininess);
        gl.glMaterialfv(GL_FRONT, GL_DIFFUSE, material.diffuse, 0);
        gl.glMaterialfv(GL_FRONT, GL_SPECULAR, material.specular, 0);
    }

}
