
package Physics;

import java.util.ArrayList;

/**
 * Class storing common mathematical formulae
 * @author cjcode975
 */
public class Formulae {
    
    /**
     * Fid all real roots of a quadratic equation ax^2+bx+c=0
     * @param a x^2 coefficient
     * @param b x coefficient
     * @param c x^0 coefficinet
     * @return all real roots
     */
    public static ArrayList<Double> QuadraticRealRoots(double a, double b, double c){
        double disc = b*b-4*a*c;        
        if(disc<0){
            throw new IllegalArgumentException("No real solutions");
        }
        ArrayList<Double> roots = new ArrayList<Double>();
        //Repeated root up to machine precision
        if(disc<1e-10){
            roots.add(-b/(2*a));
        }
        else{
            disc = Math.sqrt(disc);
            roots.add((-b+disc)/(2*a));
            roots.add((-b-disc)/(2*a));
        }
        
        return roots;
    }
    
}
