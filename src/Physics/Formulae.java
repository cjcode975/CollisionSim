
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
    public static ArrayList<Double> quadraticRealRoots(double a, double b, double c){
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
    
    /**
     * Given two moving circles which are overlapping, identify how long ago
     * they first intersected. Positive radii assumes that one ball doesn't lie
     * within the other (exterior collision). Flipping the sign 
     * on one of the radii will calculate an internal collision, where the 
     * the smaller ball lies inside the larger ball
     * @param loc_1 centrepoint of circle 1
     * @param loc_2 centrepoint of circle 2
     * @param vel_1 velocity of circle 1
     * @param vel_2 velocity of circle 2
     * @param r1 radius of circle 1
     * @param r2 radius of circle 2
     * @return time first intersection occurred (positive)
     */
    public static double whenCirclesIntersected(Vector loc_1, Vector loc_2, Vector vel_1, Vector vel_2, double r1, double r2){
        
        Vector xdiff = loc_1.sub(loc_2);
        Vector vdiff = vel_1.sub(vel_2);

        double a = vdiff.dot(vdiff);
        double b = -2*xdiff.dot(vdiff);
        double c = xdiff.dot(xdiff)-Math.pow(r1+r2, 2);

        ArrayList<Double> t_roots = Formulae.quadraticRealRoots(a, b, c);
        double t_correc = 0;
        if(t_roots.size()==1){
            t_correc = t_roots.get(0);
        }
        else{
            if(t_roots.get(0)>0 && t_roots.get(1)>0){
                t_correc = Math.min(t_roots.get(0), t_roots.get(1));
            }
            else{
                t_correc = Math.max(t_roots.get(0), t_roots.get(1));
            }
        }
        if(t_correc<0){
            throw new IllegalStateException("Correction time calculated as negative");
        }

        return t_correc;
                    
    }
    
}
