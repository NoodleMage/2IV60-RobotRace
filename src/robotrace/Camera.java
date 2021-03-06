package robotrace;

import java.util.Random;

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
    
    private int cameraCount = 0;
    private int caseCode = 0;

    /**
     * Updates the camera viewpoint and direction based on the
     * selected camera mode.
     */
    //public void update(GlobalState gs, Robot focus) {
    public void update(GlobalState gs, Robot focusBest, Robot focusWorst, Vector motorPosition) {

        switch (gs.camMode) {
            
            // Helicopter mode
            case 1:
                setHelicopterMode(gs, focusWorst);
                break;
                
            // Motor cycle mode    
            case 2:
                setMotorCycleMode(gs, focusBest,motorPosition);
                break;
                
            // First person mode    
            case 3:
                setFirstPersonMode(gs, focusWorst);
                break;
                
            // Auto mode    
            case 4:
                setAutoMode(gs, focusBest,focusWorst,motorPosition);
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
        //Calcualte eye point
        Vector V = new Vector(
                gs.vDist * Math.cos(gs.theta) * Math.cos(gs.phi),
                gs.vDist * Math.sin(gs.theta) * Math.cos(gs.phi),
                gs.vDist * Math.sin(gs.phi)
            );
            
            //eye point E = C + V
            this.eye = gs.cnt.add(V);
            
            //get center point from globalstate
            this.center = gs.cnt;   
            this.up = Vector.Z;
    }

    /**
     * Computes eye, center, and up, based on the helicopter mode.
     * The camera should focus on the robot.
     */
    private void setHelicopterMode(GlobalState gs, Robot focus) {
        //System.out.println(focus.position.x + " x");
        //Set up axis equal to direction of robot
        this.up = focus.direction;
        //Set center equal to position of robot
        this.center = focus.position;
        //Set eye equal to position of robot with view distance added on up z-axis (hover view).
        this.eye = focus.position.add(new Vector(0.00001,0.00001,gs.vDist));
        
    }

    /**
     * Computes eye, center, and up, based on the motorcycle mode.
     * The camera should focus on the robot.
     */
    private void setMotorCycleMode(GlobalState gs, Robot focus, Vector motorPosition) {
        this.up = Vector.Z;
        this.center = focus.position;
        
        //Add z unit vector for better view
        this.eye = motorPosition.add(new Vector(0,0,2));
    }

    /**
     * Computes eye, center, and up, based on the first person mode.
     * The camera should view from the perspective of the robot.
     */
    private void setFirstPersonMode(GlobalState gs, Robot focus) {
       this.up = Vector.Z;
       
       //Determine eye by postition and robot height
       double robot_height = 1; //TODO
       this.eye = focus.position.add(new Vector(0,0,robot_height + 0.2));
       
       //Use postion and add direction scaled by vDist to determine center point
       this.center = this.eye.add(focus.direction.normalized().scale(gs.vDist));
    }
    
    /**
     * Computes eye, center, and up, based on the auto mode.
     * The above modes are alternated.
     */
    private void setAutoMode(GlobalState gs, Robot focusBest, Robot focusWorst, Vector motorPosition) {
        
        //Get random between 0 and 3
        if (cameraCount % 100 == 0)
        {
            cameraCount = 0;
             Random rand = new Random();
             caseCode = rand.nextInt(4);
        }
        
        //Use random to determine camera mode
        switch (caseCode) {
            case 0:
                setDefaultMode(gs);
                break;
            case 1:
                setHelicopterMode(gs,focusBest);
                break;
            case 2:
                setMotorCycleMode(gs,focusBest,motorPosition);
                break;
            case 3:
                setFirstPersonMode(gs,focusWorst);
                break;
            default:
                setDefaultMode(gs);
                break;
        }
        //Increase cameracount
        cameraCount++;
    }

}
