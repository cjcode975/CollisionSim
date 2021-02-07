
package Physics;

import java.util.Arrays;
import java.util.Random;

/**
 * Class mimmicing a Matrix for doing physics and maths calculations
 * 
 * @author cjcode975
 */
public class Matrix {
    
    private final double vals[][];
    private final int nrows, ncols;
    
    /**
     * Create a new zero Matrix of size N_Rows x N_Cols
     * @param N_Rows number of rows
     * @param N_Cols number of columns
     */
    public Matrix(int N_Rows, int N_Cols){
        nrows = N_Rows;
        ncols = N_Cols;
        
        vals = new double[nrows][ncols];
    }
    
    /**
     * Create a new Matrix with all the same entry
     * @param N_Rows number of rows
     * @param N_Cols number of columns
     * @param val constant value for the entries
     */
    public Matrix(int N_Rows, int N_Cols, double val){
        nrows = N_Rows;
        ncols = N_Cols;
        
        vals = new double[nrows][ncols];
        for(double[] row:vals){
            Arrays.fill(row, val);
        }
    }
    
    /**
     * Create a new Matrix filled with random values
     * @param N_Rows
     * @param N_Cols
     * @param rand 
     */
    public Matrix(int N_Rows, int N_Cols, Random rand, double max, double min){
        nrows = N_Rows;
        ncols = N_Cols;
        
        vals = new double[nrows][ncols];
        for(int i=0; i<nrows; i++){
            for(int j=0; j<ncols; j++){
                vals[i][j] = min + (max-min)*rand.nextDouble();
            }
        }
    }
    
    /**
     * Create a Matrix from a 2d array
     * @param input_vals 2d array of entries
     */
    public Matrix(double input_vals[][]){
        vals = input_vals.clone();
        nrows = vals.length;
        ncols = vals[0].length;
    }
    
    /**
     * Get the number of rows in the matrix
     * @return row dimension
     */
    public int nRows(){
        return nrows;
    }
    
    /**
     * Get the number of columns in the matrix
     * @return 
     */
    public int nCols(){
        return ncols;
    }
    
    /**
     * Get the dimension of the Matrix as a string
     * @return "(nrows,ncols)"
     */
    public String dimString(){
        return "("+nrows+","+ncols+")";
    }
    
    /**
     * Matrix is represented as a list of its elements when taken as a String
     * @return String representation of Matrix by its elements
     */
    @Override
    public String toString(){
        return Arrays.deepToString(vals);
    }
    
    /**
     * Get the element in position i, j
     * @param i row position
     * @param j column position
     * @return element at i,j
     */
    public double get(int i, int j){
        return vals[i][j];
    }
    
    /**
     * Get the ith row of the Matrix as a Vector
     * @param i row to be copied
     * @return row as a Vector
     */
    public Vector row(int i){
        return new Vector(vals[i]);
    }
    
    /**
     * Get the jth column of the Matrix as a Vector
     * @param j column to be copied
     * @return column as Vector
     */
    public Vector column(int j){
        double output[] = new double[nrows];
        for(int i=0; i<nrows; i++){
            output[i] = vals[i][j];
        }
        return new Vector(output);
    }
    
    /**
     * Set the element at position i,j to a new value
     * @param i row position
     * @param j column position
     * @param new_val value to change to
     */
    public void set(int i, int j, double new_val){
        vals[i][j] = new_val;
    }
    
    /**
     * Set the values in the ith row to new values
     * @param i row position
     * @param new_vals values to replace old row
     */
    public void setRow(int i, Vector new_vals){
        for(int j=0; j<ncols; j++){
            vals[i][j] = new_vals.get(j);
        }
    }
    
    /**
     * Set the values in the jth column to new values
     * @param j column position
     * @param new_vals values to replace old column
     */
    public void setCol(int j, Vector new_vals){
        for(int i=0; i<nrows; i++){
            vals[i][j] = new_vals.get(i);
        }
    }
    
    /**
     * Return new Matrix that is the result of this+additive
     * @param additive Matrix to be added
     * @return addition result
     */
    public Matrix add(Matrix additive){
        
        if(nrows!=additive.nRows() || ncols!=additive.nCols()){
            throw new IllegalArgumentException("Matrix dimensions do not match: " + this.dimString() + " vs " + additive.dimString());
        }
        
        double output[][] = new double[nrows][ncols];
        for(int i=0; i<nrows; i++){
            for(int j=0; j<ncols; j++){
                output[i][j] = vals[i][j] + additive.get(i,j);
            }
        }        
        return new Matrix(output);
    }
    
    /**
     * Return new Matrix that is the result of this-additive
     * @param subtractive Matrix to be subtracted
     * @return subtraction result
     */
    public Matrix sub(Matrix subtractive){
        
        if(nrows!=subtractive.nRows() || ncols!=subtractive.nCols()){
            throw new IllegalArgumentException("Matrix dimensions do not match: " + this.dimString() + " vs " + subtractive.dimString());
        }
        
        double output[][] = new double[nrows][ncols];
        for(int i=0; i<nrows; i++){
            for(int j=0; j<ncols; j++){
                output[i][j] = vals[i][j] - subtractive.get(i,j);
            }
        }        
        return new Matrix(output);
    }
    
    /**
     * Multiply the Matrix by a scalar, multiplier*this
     * @param multiplier scalar multiplier
     * @return multiplier*this
     */
    public Matrix scale(double multiplier){
        double output[][] = new double[nrows][ncols];
        for(int i=0; i<nrows; i++){
            for(int j=0; j<ncols; j++){
                output[i][j] = vals[i][j] * multiplier;
            }
        }
        
        return new Matrix(output);
    }
    
    /**
     * Get the result of right multiplying by another Matrix, doing 
     * this*multiplier
     * @param multiplier matrix multiplying on the right
     * @return result of this*multiplier
     */
    public Matrix multiply(Matrix multiplier){
        
        if(ncols!=multiplier.nRows()){
            throw new IllegalArgumentException("Matrix inner dimensions do not match: " + this.dimString() + " vs " + multiplier.dimString());
        }
        
        double output[][] = new double[nrows][multiplier.nCols()];
        for(int i=0; i<nrows; i++){
            for(int j=0; j<multiplier.nCols(); j++){
                for(int k=0; k<ncols; k++){
                    output[i][j] += vals[i][k]*multiplier.get(k, j);
                }
            }
        }
        
        return new Matrix(output);
    }
    
    /**
     * Right multiply the Matrix by a Vector, calculating this*multiplier
     * @param multiplier Vector to right multiply by
     * @return result of this*multiplier
     */
    public Vector multiply(Vector multiplier){
        
        if(ncols!=multiplier.getDim()){
            throw new IllegalArgumentException("Number of columns in Matrix does not match the Vector dimension: "+ncols+" vs "+multiplier.getDim());
        }
        
        double output[] = new double[nrows];
        for(int i=0; i<nrows; i++){
            for(int j=0; j<ncols; j++){
                output[i] += vals[i][j]*multiplier.get(j);
            }
        }
        
        return new Vector(output);
    }
    
    /**
     * Unit test the Matrix functions
     * @param args 
     */
    public static void main(String args[]){
        Random rand = new Random(System.currentTimeMillis());
        
        Matrix a = new Matrix(2,2,rand,-1,1);
        Matrix b = new Matrix(2,2,1.5);
        double input[][] = {{11,12,13},{21,22,23}};
        Matrix c = new Matrix(input);
        
        System.out.println(a.toString()+b.toString()+c.toString());
        System.out.println(a.add(b).toString());
        System.out.println(a.sub(b).toString());
        System.out.println(a.multiply(b).toString());
        System.out.println(a.multiply(c).toString());
        
        System.out.println(a.row(0).toString()+a.column(0).toString());
        b.setRow(0, a.column(0));
        b.setCol(1, a.row(0));
        System.out.println(b);
        
        //Expect to throw errors
        System.out.println(a.add(c).toString());
        System.out.println(a.sub(c).toString());
        System.out.println(c.multiply(a).toString());
    }
    
}
