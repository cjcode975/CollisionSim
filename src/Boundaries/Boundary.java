package Boundaries;

import Physics.Matrix;
import Physics.Vector;

/**
 * Class defining a general boundary for the simulation with methods to detect balls
 * hitting it, when they hit it, and how they bounce off. Specific boundary
 * shapes can be defined as subclasses.
 * 
 * @author cjcode975
 */
public abstract class Boundary {
    
    /**
     * Given a String describing a boundary, identify the boundary type and 
     * its specifications, and reconstruct the boundary
     * @param s Name of boundary and all dimensions
     * @return reconstructed boundary
     */
    public static Boundary parseBoundary(String s){
        String args[] = s.split(", ");
        switch(args[0]){
            case "Circle":
                if(args.length!=2){
                    throw new IllegalArgumentException("Wrong number of specifications made on boundary");
                }
                return new Circle(Double.parseDouble(args[1]));
            case "Dispersive":
                if(args.length!=2){
                    throw new IllegalArgumentException("Wrong number of specifications made on boundary");
                }
                return new Dispersive(Double.parseDouble(args[1]));
            case "Mushroom":
                if(args.length!=4){
                    throw new IllegalArgumentException("Wrong number of specifications made on boundary");
                }
                return new Mushroom(Double.parseDouble(args[1]),Double.parseDouble(args[2]),Double.parseDouble(args[3]));
            case "Rectangle":
                if(args.length!=3){
                    throw new IllegalArgumentException("Wrong number of specifications made on boundary");
                }
                return new Rectangle(Double.parseDouble(args[1]),Double.parseDouble(args[2]));
            case "Sinai":
                if(args.length!=3){
                    throw new IllegalArgumentException("Wrong number of specifications made on boundary");
                }
                return new Sinai(Double.parseDouble(args[1]),Double.parseDouble(args[2]));
            case "Stadium":
                if(args.length!=3){
                    throw new IllegalArgumentException("Wrong number of specifications made on boundary");
                }
                return new Stadium(Double.parseDouble(args[1]),Double.parseDouble(args[2]));
        }
        throw new IllegalArgumentException("Unsupported boundary type");
    }
    
    //Store a bounding box for the boundary - x_min, x_max, y_min, y_max
    double bounds[] = new double[4];
    
    /**
     * Get the bounding box of the boundary
     * @return 
     */
    public double[] getBounds(){
        return bounds;
    }
        
    /**
     * Get the Vector normal to the surface of the boundary at a point
     * @param loc position on boundary
     * @return normal vector for the surface
     */
    public abstract Vector Normal(Vector loc);
    
    /**
     * Check if a ball is outside bounds or in contact with the boundary wall 
     * @param loc centre of the ball
     * @param radius radius of the ball
     * @return if the boundary is breached
     */
    public abstract boolean OutOfBounds(Vector loc, double radius);
       
    
    public abstract double Rewind_Time(Vector loc, Vector vel, double radius);
    
    /**
     * Calculate the new velocity of a ball after hitting the boundary
     * @param loc centre point of the ball
     * @param vel velocity of the ball
     * @param radius radius of the ball
     * @return updated velocity
     */
    public Matrix Bounce(Vector loc, Vector vel, double radius){
        //Rewind time to the point where the ball hit the surface
        double dt = Rewind_Time(loc,vel,radius);
        if(dt<0){
            throw new IllegalStateException("Rewind time calculated as negative");
        }    
        
        Vector loc_prime = loc.sub(vel.scale(dt));
        
        //Calculate velocity after reflection
        Vector normal = Normal(loc_prime);
        Vector parallel = normal.scale(vel.dot(normal));       
        
        Vector vel_prime = vel.sub(parallel.scale(2));
        loc_prime = loc_prime.add(vel_prime.scale(dt));
        
        //Output data
        Matrix output = new Matrix(2,2);
        output.setCol(0, loc_prime);
        output.setCol(1, vel_prime);
        
        return output;
    }
    
    /**
     * Draw the boundary
     */
    public abstract void Draw();

}
