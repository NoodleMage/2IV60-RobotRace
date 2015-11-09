package robotrace;

/**
 * Implementation of a camera with a position and orientation. 
 */
class Camera {

    /** The position of the camera. */
    public Vector eye = new Vector(3f, 6f, 5f);

    /** The point to which the camera is looking. */
    public Vector center = Vector.O;

    /** The up vector. */
    public Vector up = Vector.Z;

    /**
     * Updates the camera viewpoint and direction based on the
     * selected camera mode.
     */
    public void update(GlobalState gs, Robot focus) {

        switch (gs.camMode) {
            
            // Helicopter mode
            case 1:
                setHelicopterMode(gs, focus);
                break;
                
            // Motor cycle mode    
            case 2:
                setMotorCycleMode(gs, focus);
                break;
                
            // First person mode    
            case 3:
                setFirstPersonMode(gs, focus);
                break;
                
            // Auto mode    
            case 4:
                setAutoMode(gs, focus);
                break;
                
            // Default mode    
            default:
                setDefaultMode(gs);
        }
    }

    /**
     * Computes eye, center, and up, based on the camera's default mode.
     */
    private void setDefaultMode(GlobalState gs) {
        // code goes here ...
    }

    /**
     * Computes eye, center, and up, based on the helicopter mode.
     * The camera should focus on the robot.
     */
    private void setHelicopterMode(GlobalState gs, Robot focus) {
        // code goes here ...
    }

    /**
     * Computes eye, center, and up, based on the motorcycle mode.
     * The camera should focus on the robot.
     */
    private void setMotorCycleMode(GlobalState gs, Robot focus) {
        // code goes here ...
    }

    /**
     * Computes eye, center, and up, based on the first person mode.
     * The camera should view from the perspective of the robot.
     */
    private void setFirstPersonMode(GlobalState gs, Robot focus) {
        // code goes here ...
    }
    
    /**
     * Computes eye, center, and up, based on the auto mode.
     * The above modes are alternated.
     */
    private void setAutoMode(GlobalState gs, Robot focus) {
        // code goes here ...
    }

}
