package collisionsim;

import Boundaries.Circle;
import Boundaries.Dispersive;
import Boundaries.Ellipse;
import Boundaries.Mushroom;
import Boundaries.Rectangle;
import Boundaries.Sinai;
import Boundaries.Stadium;
import std.StdDraw;

/**
 * Simulate a number of balls bouncing in a box for a chosen shape of box
 * 
 * @author cjcode975
 */
public class CollisionSim {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        Rectangle rect = new Rectangle(20,20);
        Circle circ = new Circle(12);
        Sinai sin = new Sinai(8,24);
        Dispersive disp = new Dispersive(16);
        Stadium stad = new Stadium(12,8);
        Mushroom mush = new Mushroom(10,8,10);
        BallSim bb = new BallSim(50,stad);
        stad.Draw();
        StdDraw.show();
        bb.Set_Radii(0.25, 0.5);
        bb.Set_Mass(false, 1);
        bb.Set_Locations();
        bb.Set_Speed(1.5);
        bb.Set_DT(0.05);
        
        bb.Draw();
        bb.Simulate(5000);
    }
    
}
