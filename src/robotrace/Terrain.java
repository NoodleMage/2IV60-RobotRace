package robotrace;

import com.jogamp.opengl.util.gl2.GLUT;
import com.jogamp.opengl.util.texture.Texture;
import java.awt.Color;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import static javax.media.opengl.GL.GL_FRONT;
import static javax.media.opengl.GL.GL_LINEAR;
import static javax.media.opengl.GL.GL_RGBA;
import static javax.media.opengl.GL.GL_RGBA8;
import static javax.media.opengl.GL.GL_TEXTURE_2D;
import static javax.media.opengl.GL.GL_TEXTURE_MAG_FILTER;
import static javax.media.opengl.GL.GL_TEXTURE_MIN_FILTER;
import static javax.media.opengl.GL.GL_TRIANGLES;
import static javax.media.opengl.GL.GL_UNSIGNED_BYTE;
import static javax.media.opengl.GL2.GL_COMPILE;
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
     * Whether the display list for the terrain has been set up.
     */
    private boolean displayListTerrainSetUp = false;

    /**
     * Number of segments to be used to draw the terrain (per dimension per
     * direction).
     */
    private int SEGMENTS = 50;

    /**
     * First x of the terrain.
     */
    private int xBegin = 0;

    /**
     * Size in x of the terrain.
     */
    private int xSize = 40;

    /**
     * First y of the terrain.
     */
    private int yBegin = 0;

    /**
     * Size in y of the terrain.
     */
    private int ySize = 40;

    /**
     * Returns the height of the terrain at a specific x and y.
     */
    private double getTerrainHeight(double x, double y) {
        return (-2 * Math.cos(0.3 * x + 0.2 * y) + 0.4 * Math.cos(x - 0.5 * y)) - 2;
    }

    /**
     * The colors for the 1D texture
     */
    private Color[] textureColors = new Color[]{
        Color.BLUE,
        Color.YELLOW,
        Color.GREEN
    };

    /**
     * The texid for the 1D texture
     */
    private int texture;

    /**
     * Constructs the terrain. Terrain is in [-40,40], looks much better in
     * camera scale.
     */
    public Terrain() {
    }

    /**
     * Draws the terrain.
     */
    public void draw(GL2 gl, GLUT glut, Texture sky) {
        // If the display list has not been set up yet, create it
        if (!displayListTerrainSetUp) {
            // Create the texture
            texture = create1DTexture(gl, textureColors);

            // Draw the gray transparent surface
            setMaterial(Material.WATER, gl);
            gl.glBegin(GL_QUADS);
            gl.glVertex3d(-40, -40, 2);
            gl.glVertex3d(40, -40, 2);
            gl.glVertex3d(40, 40, 2);
            gl.glVertex3d(-40, 40, 2);
            gl.glEnd();
            // Finish compiling the display list
            //       gl.glEndList();
            // Set set up boolean to true
            displayListTerrainSetUp = true;
        }
        // Bind the terrain texture
        gl.glBindTexture(GL_TEXTURE_1D, texture);
        // Execute the display list for the terrain
        drawTexture(gl);
        // Unbind the terrain texture
        gl.glBindTexture(GL_TEXTURE_1D, 0);

        gl.glPushMatrix();
        sky.enable(gl);
        sky.bind(gl);
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
        gl.glPopMatrix();
        sky.disable(gl);
    }

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
                double f11 = getTerrainHeight(x1, y1);
                double f12 = getTerrainHeight(x1, y2);
                double f21 = getTerrainHeight(x2, y1);
                double f22 = getTerrainHeight(x2, y2);
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

    public double getTextureCoordinateFromHeight(double height) {
        height = (height + 1) / 4 + 0.25;
        height = (height < 0.25) ? 0.25 : height;
        height = (height > 0.75) ? 0.75 : height;
        return height;
    }

    /**
     * Creates a new 1D - texture.
     *
     * @param gl
     * @param colors
     * @return the texture ID for the generated texture.
     */
    public int create1DTexture(GL2 gl, Color[] colors) {
        gl.glDisable(GL_TEXTURE_2D);
        gl.glEnable(GL_TEXTURE_1D);
        int[] texid = new int[]{-1};
        gl.glGenTextures(1, texid, 0);
        ByteBuffer bb = ByteBuffer.allocateDirect(colors.length * 4).order(ByteOrder.nativeOrder());
        for (Color color : colors) {
            int pixel = color.getRGB();
            bb.put((byte) ((pixel >> 16) & 0xFF)); // Red component
            bb.put((byte) ((pixel >> 8) & 0xFF));  // Green component
            bb.put((byte) (pixel & 0xFF));         // Blue component
            bb.put((byte) ((pixel >> 24) & 0xFF)); // Alpha component
        }
        bb.flip();
        gl.glBindTexture(GL_TEXTURE_1D, texid[0]);
        gl.glTexImage1D(GL_TEXTURE_1D, 0, GL_RGBA8, colors.length, 0, GL_RGBA, GL_UNSIGNED_BYTE, bb);
        gl.glTexParameteri(GL_TEXTURE_1D, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
        gl.glTexParameteri(GL_TEXTURE_1D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);
        gl.glBindTexture(GL_TEXTURE_1D, 0);
        return texid[0];
    }

    private void setMaterial(Material material, GL2 gl) {
        //Set material determined by given robot type
        gl.glMaterialf(GL_FRONT, GL_SHININESS, material.shininess);
        gl.glMaterialfv(GL_FRONT, GL_DIFFUSE, material.diffuse, 0);
        gl.glMaterialfv(GL_FRONT, GL_SPECULAR, material.specular, 0);
    }

}
