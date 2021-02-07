
package Physics;

import java.util.Arrays;
import java.util.Random;

/**
 * Column vector for doing physics and maths calculations
 * 
 * @author cjcode975
 */
public class Vector {
    
    private final double vals[];
    private final int dim;
    
    /**
     * Create a Vector from a list of input values
     * @param input_vals elements of vector
     */
    public Vector(double input_vals[]){
        dim = input_vals.length;
        vals = input_vals.clone();
    }
    
    /**
     * Create a new vector with all entries equal to 0
     * @param len dim of vector
     */
    public Vector(int len){
        dim = len;
        vals = new double[dim];
    }
    
    /**
     * Create a vector filled with the same value
     * @param len dim of vector
     * @param input_val value of each element
     */
    public Vector(int len, double input_val){
        dim = len;
        vals = new double[dim];
        Arrays.fill(vals, input_val);
    }
    
    /**
     * Construct a vector filled with random values in the range [min,max)
     * @param len dim of vector
     * @param rand random number generator
     * @param min lowest end of range
     * @param max highest end of range
     */
    public Vector(int len, Random rand, double min, double max){
        dim = len;
        vals = new double[dim];
        for(int i=0; i<dim; i++){
            vals[i] = (max-min)*rand.nextDouble()+min;
        }
    }
    
    /**
     * Return dim of vector
     * @return dim of vector
     */
    public int getDim(){
        return dim;
    }
    
    /**
     * Get the ith component of the vector
     * @param i position
     * @return value of vals[i]
     */
    public double get(int i){
        return vals[i];
    }
    
    /**
     * Vector displays as a list of its elements as a string
     * @return String of list of elements
     */
    @Override
    public String toString(){
        return Arrays.toString(vals);
    }
    
    /**
     * Change the value of element at position i to j
     * @param i position in vector to change
     * @param j new value
     */
    public void set(int i, double j){
        vals[i] = j;
    }
    
    /**
     * Add two vectors
     * @param additive vector to be added to current
     * @return new vector of result of this+additive
     */
    public Vector add(Vector additive){
        if(dim != additive.getDim()){
            throw new IllegalArgumentException("Vector lengths do not match: "+dim+" vs "+additive.getDim());
        }
        double temp_vals[] = vals.clone();
        for(int i=0; i<dim; i++){ temp_vals[i] += additive.get(i); }
        return new Vector(temp_vals);
    }
    
    /**
     * Subtract two vectors
     * @param subtractive vector to be subtracted from current
     * @return new vector of result of this-subtractive
     */
    public Vector sub(Vector subtractive){
        if(dim != subtractive.getDim()){
            throw new IllegalArgumentException("Vector lengths do not match: "+dim+" vs "+subtractive.getDim());
        }
        double temp_vals[] = vals.clone();
        for(int i=0; i<dim; i++){ temp_vals[i] -= subtractive.get(i); }
        return new Vector(temp_vals);
    }
    
    /**
     * Multiply a vector by a constant
     * @param multiplier amount to scale vector by
     * @return new vector of result of this*multiplier
     */
    public Vector scale(double multiplier){
        double temp_vals[] = vals.clone();
        for(int i=0; i<dim; i++){ temp_vals[i] *= multiplier; }
        return new Vector(temp_vals);
    }
    
    /**
     * Calculate the dot product this.multiplier
     * @param multiplier multiplying vector
     * @return dot product
     */
    public double dot(Vector multiplier){
        if (dim != multiplier.getDim()){
            throw new IllegalArgumentException("Vector lengths do not match: "+dim+" vs "+multiplier.getDim());
        }
        double output = 0;
        for(int i=0; i<dim; i++){
            output += vals[i]*multiplier.get(i);
        }
        return output;
    }
    
    /**
     * If the Vectors are 2d vectors in 3d space, compute the cross product
     * this x multiplier
     * @param multiplier right hand vector in multiplication
     * @return cross product
     */
    public Vector cross(Vector multiplier){
        if(dim!=3 || multiplier.getDim()!=3){
            throw new IllegalArgumentException("Vector lengths must both be 3: "+dim+", "+multiplier.getDim());
        }
        double output[] = {vals[1]*multiplier.get(2)-vals[2]*multiplier.get(1),
                            vals[2]*multiplier.get(0)-vals[0]*multiplier.get(0),
                            vals[0]*multiplier.get(1)-vals[1]*multiplier.get(0)};
        return new Vector(output);
    }
    
    /**
     * Calculate the magnitude of a vector
     * @return magnitude of a vector
     */
    public double magnitude(){           
        return Math.sqrt(this.dot(this));       
    }
    
    /**
     * Calculate the corresponding unit vector
     * @return 
     */
    public Vector unit(){
        return this.scale(1/this.magnitude());
    }
    
    /**
     * Unit test the vector methods
     * @param args 
     */
    public static void main(String args[]){
        Random rand = new Random(System.currentTimeMillis());
        
        Vector a = new Vector(4,rand,-1,1);
        Vector b = new Vector(4,3.2);
        double vals[] = {1.1,2.2,3.3};
        Vector c = new Vector(vals);
        
        System.out.println(a.toString()+" "+b.toString()+" "+c.toString());
        
        System.out.println(a.add(b).toString()+a.toString()+b.toString());
        System.out.println(b.sub(a).toString()+a.toString()+b.toString());
        System.out.println(c.scale(2).toString()+c.toString());
        System.out.println(a.dot(b));
        System.out.println(c.cross(new Vector(3,1)).toString());
        System.out.println(a.magnitude());
        System.out.println(a.unit().toString());
        
    }
    
    
}

