
package collisionsim;

import Boundaries.Boundary;
import Physics.Formulae;
import std.StdDraw;
import Physics.Matrix;
import Physics.Vector;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Random;

/**
 * Simulate balls in a box experiencing elastic collisions
 * 
 * TO DO: 
 * -Improve collision detection (binary space partition) 
 * -Update Draw to feed buffered images to a separate animation class
 * 
 * @author cjcode975
 */
public class BallSim {
    
    //Primary simulation variables
    private final int n_balls;
    
    private Matrix loc, vel;
    
    private double[] radius, mass;
    private double max_radius;   
    
    private double dt;
    
    //Description of the boundary of billiard    
    private Boundary bounds;
    private double bounding_box[];
    
    //Variables used to describe the partition of the billiard into subspaces to
    //speed up collision detection
    private ArrayList<Integer>[][] partitionBoxes;
    private int nbox_x, nbox_y;
    private Vector partitionCorner = new Vector(2);    
    
    //Information needed for saving data
    private String baseFileName;
    
    //Momentum distribution information
    private double maxMomDist,momDistBoxWidth;
    private int nMomBoxes = 75;
    
    //Radial velocity information
    private int nRadBoxes = 75;
    
    private Random rand = new Random(System.currentTimeMillis());
    
    /**
     * Create new simulation of balls bouncing in a box
     * @param N_Balls number of balls
     * @param Max_X width of box
     * @param Max_Y height of box
     */
    public BallSim(int N_Balls, Boundary boundary){
        n_balls = N_Balls;
        bounds = boundary;
        
        bounding_box = bounds.getBounds();
        
        StdDraw.enableDoubleBuffering();
        StdDraw.setCanvasSize(20*(int)(bounding_box[1]-bounding_box[0]),20*(int)(bounding_box[3]-bounding_box[2]));
        StdDraw.setXscale(bounding_box[0]-1, bounding_box[1]+1);
        StdDraw.setYscale(bounding_box[2]-1, bounding_box[3]+1);
    }
    
    /**
     * Set the radii of the balls from a list of their values
     * @param vals list of the radius of each ball
     */
    public void Set_Radii(double vals[]){
        radius = vals.clone();
        
        max_radius = radius[0];
        
        for(int i=1; i<n_balls; i++){
            if(radius[i]>max_radius){ max_radius=radius[i]; }            
        }
        
        CollisionBoxes();
    }
    
    /**
     * Set the radii of the balls to be a constant value
     * @param radius_val constant radius
     */
    public void Set_Radii(double radius_val){
        radius = new double[n_balls];
        Arrays.fill(radius, radius_val);
        
        max_radius = radius_val;
        CollisionBoxes();
    }   
    
    
    /**
     * Set radii of the balls, drawn uniformly from the range [min,max)
     * @param min minimum possible radii
     * @param max maximum possible radii
     */
    public void Set_Radii(double min, double max){
        radius = new double[n_balls];
        max_radius = 0;
        for(int i=0; i<n_balls; i++){
            radius[i] = min+(max-min)*rand.nextDouble();
            if(radius[i]>max_radius){
                max_radius = radius[i];
            }
        }        
        
        CollisionBoxes();
    }
    
    /**
     * Setup the binary space partition used to detect ball-to-ball collisions
     */
    private void CollisionBoxes(){
        
        max_radius = 2.25*max_radius;
        
        int zero_box_x = (int)(Math.ceil(-1*(bounding_box[0]-1)/max_radius));
        int zero_box_y = (int)(Math.ceil(-1*(bounding_box[2]-1)/max_radius));
        
        nbox_x = (int)(Math.ceil((bounding_box[1]+1)/max_radius))+zero_box_x;
        nbox_y = (int)(Math.ceil((bounding_box[3]+1)/max_radius))+zero_box_y;
        
        partitionCorner.set(0, -1*max_radius*zero_box_x);
        partitionCorner.set(1, -1*max_radius*zero_box_y);
        
        partitionBoxes = new ArrayList[nbox_x][nbox_y];        
        
        for(int i=0; i<nbox_x; i++){
            for(int j=0; j<nbox_y; j++){
                partitionBoxes[i][j] = new ArrayList<Integer>();
            }
        }       
    }
    
    /**
     * Set the masses of the balls from a list of values
     * @param vals list of value of the mass of each ball
     */
    public void Set_Mass(double vals[]){
        mass = vals.clone();
    }
    
    /**
     * Set the masses of the balls, either as a constant mass, or based on a 
     * density and their volume 
     * @param Same_Mass boolean true if all balls have same mass, false to
     * calculate based on density
     * @param mass_val either mass of each ball, or density
     */
    public void Set_Mass(boolean Same_Mass, double mass_val){
        mass = new double[n_balls];
        if(Same_Mass){
            Arrays.fill(mass,mass_val);
            return;
        }
        for(int i=0; i<n_balls; i++){
            mass[i] = 4.0*Math.PI*Math.pow(radius[i], 3)*mass_val/3.0;
        }
    }
    
    /**
     * Set the locations of the balls according to a given list of values
     * @param vals list of ball positions
     */
    public void Set_Locations(Matrix vals){
        loc = vals.clone();
    }
    
    /**
     * Randomly draw starting locations for the balls, guaranteeing that they
     * don't overlap with each other or the walls
     */
    public void Set_Locations(){
        loc = new Matrix(2,n_balls);
        
        for(int i=0; i<n_balls; i++){            
            do{
                loc.set(0, i, bounding_box[0]+(bounding_box[1]-bounding_box[0])*rand.nextDouble());
                loc.set(1, i, bounding_box[2]+(bounding_box[3]-bounding_box[2])*rand.nextDouble());
            }while(bounds.OutOfBounds(loc.column(i), radius[i]) || OverlapsPrevious(i));
        }
    }
    
    /**
     * Check if a suggested ball position overlaps with a previously placed ball
     * @param i number of ball being placed
     * @return if there is an overlap
     */
    private boolean OverlapsPrevious(int i){
        for(int j=0; j<i; j++){
            if(loc.column(j).sub(loc.column(i)).magnitude()<=radius[i]+radius[j]){
                return true;
            }
        }
        return false;
    }
    
    /**
     * Set the velocities of the balls according to a list of their values
     * @param vals list of the ball velocities
     */
    public void Set_Velocities(Matrix vals){
        vel = vals.clone();
        setMomDistMax();     
    }
        
    /**
     * Give all balls the same speed, in a random direction
     * @param speed speed for all balls
     */
    public void Set_Speed(double speed){
        vel = new Matrix(2,n_balls);
        
        for(int i=0; i<n_balls; i++){
            double angle = 2*Math.PI*rand.nextDouble();
            vel.set(0,i, speed*Math.cos(angle));
            vel.set(1,i,speed*Math.sin(angle));
        }
        
        setMomDistMax();
    }
    
    /**
     * Give all balls a constant momentum in a random direction
     * @param momentum 
     */
    public void Set_Momentum(double momentum){
        vel = new Matrix(2,n_balls);
        
        for(int i=0; i<n_balls; i++){
            double angle = 2*Math.PI*rand.nextDouble();
            vel.set(0,i, momentum*Math.cos(angle)/mass[i]);
            vel.set(1,i,momentum*Math.sin(angle)/mass[i]);
        }
        
        setMomDistMax();
    }
    
    private void setMomDistMax(){
        double totMom = 0;
        for(int i=0; i<n_balls; i++){
            totMom += vel.column(i).magnitude()*mass[i];
        }
        maxMomDist = 3*totMom/n_balls;
        momDistBoxWidth = maxMomDist/(double)nMomBoxes;
    }
    
    /**
     * Set the timestep for the simulation
     * @param time_step 
     */
    public void Set_DT(double time_step){
        dt = time_step;
    }
    
    /**
     * Calculate the next stage of the simulation after a timestep
     */
    public void Step_Time(){
        loc = loc.add(vel.scale(dt));
    }
    
    /**
     * Calculate and set the new velocities, and corrected positions for two
     * balls which have collided
     * @param i index of first ball
     * @param j index of second ball
     */
    private void Collide(int i, int j){        
        //Calculate when the balls actually collided
        double tcorrec = 0;
        try{
            tcorrec = Formulae.whenCirclesIntersected(loc.column(i),loc.column(j),vel.column(i),vel.column(j),radius[i],radius[j]);
        }
        catch(Exception e){
            return;
        }
        //Unwind time to the collision
        loc.setCol(i, loc.column(i).sub(vel.column(i).scale(tcorrec)));
        loc.setCol(j, loc.column(j).sub(vel.column(j).scale(tcorrec)));

        //Calculate the effect of the collision on the velocities
        Vector xdiff = loc.column(i).sub(loc.column(j));
        Vector vdiff = vel.column(i).sub(vel.column(j));

        Vector inc = xdiff.scale(2*vdiff.dot(xdiff)/((mass[i]+mass[j])*xdiff.dot(xdiff)));

        vel.setCol(i, vel.column(i).sub(inc.scale(mass[j])));
        vel.setCol(j, vel.column(j).add(inc.scale(mass[i])));

        //Evolve back to the end of the timestep
        loc.setCol(i, loc.column(i).add(vel.column(i).scale(tcorrec)));
        loc.setCol(j, loc.column(j).add(vel.column(j).scale(tcorrec)));
    }
    
    /**
     * Check for collisions between balls and walls and each other. Update
     * ball velocities according to elastic collision rules when they occur
     */
    public void Collisions(){        
        //Check for collisions between pairs of balls
        //TO DO: update detection based on binary space partition for speed
        for(int i=0; i<n_balls; i++){
            for(int j=i+1; j<n_balls; j++){
                if(loc.column(i).sub(loc.column(j)).magnitude()<radius[i]+radius[j]){                    
                    Collide(i,j);
                }
            }
        }
        
        //Check for collisions with the walls
        for(int i=0; i<n_balls; i++){
            if(bounds.OutOfBounds(loc.column(i), radius[i])){
                try{
                    Matrix temp = bounds.Bounce(loc.column(i), vel.column(i), radius[i]);
                    loc.setCol(i, temp.column(0));
                    vel.setCol(i, temp.column(1));
                }catch(Exception e){}
            }
        }
    }
    
    /**
     * Check for collisions between balls and walls and each other. Update
 ball velocities according to elastic collision rules when they occur
 
 Uses a binary space partition for calculations - the full billiard is
 split into partitionBoxes slightly larger than a ball. This means balls can only 
 collide with those in adjacent partitionBoxes, meaning that the length of 
 needed nested loop to check for collisions is much shorter
     */
    public void CollisionsBSP(){        
        
        //Clear all the lists of which balls are in each bounding box
        for(int i=0; i<nbox_x; i++){
            for(int j=0; j<nbox_y; j++){
                partitionBoxes[i][j].clear();
            }
        }
        
        //Calculate which bounding box balls are inside
        for(int i=0; i<n_balls; i++){
            Vector rel_pos_to_corner = loc.column(i).sub(partitionCorner);
            int box_x = (int)(rel_pos_to_corner.get(0)/max_radius);
            int box_y = (int)(rel_pos_to_corner.get(1)/max_radius);
            partitionBoxes[box_x][box_y].add(i);
        }
             
        //Loop over the partitionBoxes to start looking for collisions
        for(int i=0; i<nbox_x; i++){
            for(int j=0; j<nbox_y; j++){
                
                if(partitionBoxes[i][j].isEmpty()){
                    continue;
                }
                                
                for(int p=0; p<partitionBoxes[i][j].size(); p++){ //loop over particles in the current box
                    
                    int ball_p = partitionBoxes[i][j].get(p);
                
                    for(int m=-1; m<=1; m++){ //loop over adjacent partitionBoxes
                        for(int n=-1; n<=1; n++){
                            
                            if(i+m<0 || j+n<0 || i+m>=nbox_x || j+n>=nbox_y){
                                continue;
                            }
                            
                            for(int q=0; q<partitionBoxes[i+m][j+n].size(); q++){ //loop over balls in adjacent partitionBoxes
                                
                                int ball_q = partitionBoxes[i+m][j+n].get(q);
                                
                                //Order collisions by ball number to prevent repeat calculations and check for collision
                                if(ball_q>=ball_p || loc.column(ball_p).sub(loc.column(ball_q)).magnitude() > radius[ball_p]+radius[ball_q]){
                                    continue;
                                }
                                
                                Collide(ball_p,ball_q);
                                
                            }
                        }
                    }
                }
            }
        }
                
        //Check for collisions with the walls
        for(int i=0; i<n_balls; i++){
            if(bounds.OutOfBounds(loc.column(i), radius[i])){
                try{
                    Matrix temp = bounds.Bounce(loc.column(i), vel.column(i), radius[i]);
                    loc.setCol(i, temp.column(0));
                    vel.setCol(i, temp.column(1));
                }catch(Exception e){}
            }
        }
    } 
   
    /**
     * Run the simulation for the given length of time
     * @param nSteps number of steps to simulate
     */
    public void Simulate(int nSteps){
        for(int i=0; i<nSteps; i++){
            Step_Time();
            CollisionsBSP();
            Draw();
        }
    }
    
    /**
     * Get the distribution of the momenta of the balls
     * @return histogram of the momentum distribution
     */
    public int[] momentumSplit(){
        int hist [] = new int[nMomBoxes];
        for(int i=0; i<n_balls; i++){
            hist[(int)((vel.column(i).magnitude()*mass[i])/momDistBoxWidth)] ++;
        }
        return hist;
    }
    
    /**
     * Measure how rotational the particle velocities are. 1 is entirely rotational
     * and 0 is entirely radial
     * @return 
     */
    public int[] radialVelocityDist(){
        int hist[] = new int[nRadBoxes];
        for(int i=0; i<n_balls; i++){
            hist[(int)((1-Math.abs(loc.column(i).unit().dot(vel.column(i).unit())))*nRadBoxes)]++;
        }
        return hist;
    }
    
    
    
    /**
     * Get the locations of all balls
     * @return loc
     */
    public Matrix getLoc(){
        return loc;
    }
    
    /**
     * Get the velocities of all balls
     * @return vel
     */
    public Matrix getVel(){
        return vel;
    }
    
    private int counter = 0;
    /**
     * Draw the current state to screen
     * TO DO: Replace with method which sends bufferedimage to a seperate 
     * plotting class, to run in a seperate thread. Stops computation lag 
     * changing animation speed
     */
    public void Draw(){
        StdDraw.clear();
        StdDraw.setPenColor();
        bounds.Draw();
        for(int i=0; i<n_balls; i++){
            StdDraw.filledCircle(loc.get(0,i), loc.get(1,i), radius[i]);
        }
        //StdDraw.text(3, 1, Integer.toString(counter));
        counter++;
                
        StdDraw.show();
        StdDraw.pause(20);
    }
    
    /**
     * Set the file name used for saving data
     * @param fName 
     */
    public void SetFileName(String fName){
        baseFileName = fName;
    }
        
    /**
     * Save the initialisation details of the simulation to a textfile for later
     * referencing.
     * @param fName file name. "_INIT" will be appended. Any further files 
     * created for the simulation will also use fName with an appropriate added
     * suffix to save to to keep data collated
     * @throws IOException 
     */
    public void printInitialisation(String fName) throws IOException{
        baseFileName = fName;
        BufferedWriter bw = new BufferedWriter(new FileWriter(fName+"_INIT.txt"));
        bw.write("Boundary: "+bounds.toString()+"\n");
        bw.write("Num Balls: "+n_balls+"\n");
        bw.write("Timestep: "+dt+"\n");
        bw.write("Masses: "+Arrays.toString(mass)+"\n");
        bw.write("Radii: "+Arrays.toString(radius)+"\n");
        bw.write("Positions: "+loc.toString()+"\n");
        bw.write("Velocities: "+vel.toString()+"\n");
        bw.close();
    }
    
    /**
     * Read an initialisation file for a simulation and recreate the simulation
     * either as it started, or as it finished
     * @param fName Location where the initialisation is stored
     * @param atStart true if the start of the origonal simulation is to be used,
     * false if the new simulation should pick up at the end of the previous
     * @return
     * @throws FileNotFoundException
     * @throws IOException 
     */
    public static BallSim readSimulation(String fName,boolean atStart) throws FileNotFoundException, IOException{
        //Read data from the initialisation file
        BufferedReader br = new BufferedReader(new FileReader(fName+"_INIT.txt"));
        Boundary b = Boundary.parseBoundary(br.readLine().split(": ")[1]);
        int n = Integer.parseInt(br.readLine().split(": ")[1]);
        BallSim bs = new BallSim(n,b);
        bs.Set_DT(Double.parseDouble(br.readLine().split(": ")[1]));
        bs.Set_Mass(parseList(br.readLine().split(": ")[1]));
        bs.Set_Radii(parseList(br.readLine().split(": ")[1]));
        bs.Set_Locations(Matrix.parseMatrix(br.readLine().split(": ")[1]));
        bs.Set_Velocities(Matrix.parseMatrix(br.readLine().split(": ")[1]));
        br.close();
        
        /* If picking up at the end of the previous simulation then get the
        * last positions and velocities of the balls
        */
        if(!atStart){
            br = new BufferedReader(new FileReader(fName+"_LOC.txt"));
            String line = null, currLine;
            while((currLine=br.readLine())!=null){ line = currLine; }
            bs.Set_Locations(Matrix.parseMatrix(line));
            br.close();
            
            br = new BufferedReader(new FileReader(fName+"_VEL.txt"));
            while((currLine=br.readLine())!=null){ line = currLine; }
            bs.Set_Velocities(Matrix.parseMatrix(line));
            br.close();
        }
        
        return bs;
    }
    
    /**
     * Print the data about the simulation to file
     * @throws IOException 
     */
    public void printSimData() throws IOException{
        BufferedWriter bw = new BufferedWriter(new FileWriter(baseFileName+"_LOC.txt",true));
        bw.write(loc.toString()+"\n");
        bw.close();
        bw = new BufferedWriter(new FileWriter(baseFileName+"_VEL.txt",true));
        bw.write(vel.toString()+"\n");
        bw.close();
    }
    
    /**
     * Given a string representation of a 1D array of doubles, parse it to a 
     * 1D array of doubles
     * @param s string rep of a 1D array of doubles
     * @return parse list
     */
    private static double[] parseList(String s){
        String vals[] = s.substring(1, s.length()-1).split(", ");
        double output[] = new double[vals.length];
        for(int i=0; i<output.length; i++){
            output[i] = Double.parseDouble(vals[i]);
        }
        return output;
    }
}
