package robotrace;

/**
* Materials that can be used for the robots.
*/
public enum Material {
    
//diffuse should be darker than specular
    /** 
     * Gold material properties.
     * Modify the default values to make it look like gold.
     */
    GOLD (
        new float[] {RGBToFloat(191), RGBToFloat(155), RGBToFloat(48), 1.0f},
        new float[] {RGBToFloat(255), RGBToFloat(191), RGBToFloat(0), 1.0f},     
        100f),

    /**
     * Silver material properties.
     * Modify the default values to make it look like silver.
     */
    SILVER (
        new float[] {RGBToFloat(158), RGBToFloat(158), RGBToFloat(158), 1.0f},
        new float[] {RGBToFloat(189), RGBToFloat(189), RGBToFloat(189), 1.0f},     
      128),

    /** 
     * Wood material properties.
     * Modify the default values to make it look like wood.
     */
    WOOD (
        new float[] {RGBToFloat(121), RGBToFloat(85), RGBToFloat(72), 1.0f},
        new float[] {RGBToFloat(141), RGBToFloat(110), RGBToFloat(99), 1.0f},     
      1f),

    /**
     * Orange material properties.
     * Modify the default values to make it look like orange.
     */
    ORANGE (
        new float[] {RGBToFloat(255), RGBToFloat(152), RGBToFloat(0), 1.0f},
        new float[] {RGBToFloat(255), RGBToFloat(167), RGBToFloat(38), 1.0f},     
            128f),
    
     /**
     * Orange material properties.
     * Modify the default values to make it look like orange.
     */
    RED (
        new float[] {RGBToFloat(244), RGBToFloat(67), RGBToFloat(54), 1.0f},
        new float[] {RGBToFloat(239), RGBToFloat(83), RGBToFloat(80), 1.0f},     
        100f),
    
    /**
     * Orange material properties.
     * Modify the default values to make it look like orange.
     */
    GREEN (
        new float[] {RGBToFloat(76), RGBToFloat(175), RGBToFloat(80), 1.0f},
        new float[] {RGBToFloat(102), RGBToFloat(187), RGBToFloat(106), 1.0f},     
        100f),
    
    /**
     * Orange material properties.
     * Modify the default values to make it look like orange.
     */
     BLUE (
         new float[] {RGBToFloat(33), RGBToFloat(150), RGBToFloat(243), 1.0f},
        new float[] {RGBToFloat(66), RGBToFloat(165), RGBToFloat(245), 1.0f},     
       100f),
     
      /**
     * Orange material properties.
     * Modify the default values to make it look like orange.
     */
     YELLOW (
        new float[] {RGBToFloat(255), RGBToFloat(235), RGBToFloat(59), 1.0f},
        new float[] {RGBToFloat(255), RGBToFloat(238), RGBToFloat(88), 1.0f},     
      100f),
     
       /**
     * Orange material properties.
     * Modify the default values to make it look like orange.
     */
     BLACK (
        new float[] {RGBToFloat(0), RGBToFloat(0), RGBToFloat(0), 1.0f},
        new float[] {RGBToFloat(0), RGBToFloat(0), RGBToFloat(0), 1.0f},     
      100f),
     
      /** 
     * Android original material properties.
     * Easter Egg
     */
    ANDROID (
         new float[] {RGBToFloat(151), RGBToFloat(192), RGBToFloat(61), 1.0f},
        new float[] {RGBToFloat(151), RGBToFloat(192), RGBToFloat(161), 1.0f},     
      100f);

    /** The diffuse RGBA reflectance of the material. */
    float[] diffuse;

    /** The specular RGBA reflectance of the material. */
    float[] specular;
    
    /** The specular exponent of the material. */
    float shininess;

    /**
     * Constructs a new material with diffuse and specular properties.
     */
    private Material(float[] diffuse, float[] specular, float shininess) {
        this.diffuse = diffuse;
        this.specular = specular;
        this.shininess = shininess;
    }
    
    private static float RGBToFloat(int rgb)
    {
        return rgb/255f;
    }
}
