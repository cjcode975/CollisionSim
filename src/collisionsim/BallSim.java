
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
    
    private final int n_balls;
    
    private Matrix loc, vel;
    
    private double[] radius, mass;
    private double max_radius;
        
    private Boundary bounds;
    private double bounding_box[];
    
    private double dt;
    
    private String baseFileName;
    
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
    }
    
    /**
     * Set the radii of the balls to be a constant value
     * @param radius_val constant radius
     */
    public void Set_Radii(double radius_val){
        radius = new double[n_balls];
        Arrays.fill(radius, radius_val);
    }
    
    /**
     * Set radii of the balls, drawn uniformly from the range [min,max)
     * @param min minimum possible radii
     * @param max maximum possible radii
     */
    public void Set_Radii(double min, double max){
        radius = new double[n_balls];
        for(int i=0; i<n_balls; i++){
            radius[i] = min+(max-min)*rand.nextDouble();
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
            }while(bounds.OutOfBounds(loc.column(i), radius[i]));
        }
    }
    
    /**
     * Set the velocities of the balls according to a list of their values
     * @param vals list of the ball velocities
     */
    public void Set_Velocities(Matrix vals){
        vel = vals.clone();
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
     * Check for collisions between balls and walls and each other. Update
     * ball velocities according to elastic collision rules when they occur
     */
    public void Collisions(){        
        //Check for collisions between pairs of balls
        //TO DO: update detection based on binary space partition for speed
        for(int i=0; i<n_balls; i++){
            for(int j=i+1; j<n_balls; j++){
                if(loc.column(i).sub(loc.column(j)).magnitude()<radius[i]+radius[j]){
                    
                    //Clculte when the balls actually collided
                    double tcorrec = Formulae.whenCirclesIntersected(loc.column(i),loc.column(j),vel.column(i),vel.column(j),radius[i],radius[j]);
                    
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
            }
        }
        
        //Check for collisions with the walls
        for(int i=0; i<n_balls; i++){
            if(bounds.OutOfBounds(loc.column(i), radius[i])){
                Matrix temp = bounds.Bounce(loc.column(i), vel.column(i), radius[i]);
                loc.setCol(i, temp.column(0));
                vel.setCol(i, temp.column(1));
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
            Collisions();
            Draw();
        }
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
