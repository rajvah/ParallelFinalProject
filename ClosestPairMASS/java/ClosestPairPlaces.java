
import edu.uw.bothell.css.dsl.MASS.MASS;
import edu.uw.bothell.css.dsl.MASS.Place;
import java.util.*;

public class ClosestPairPlaces extends Place {
	public static final int INIT = 0;

    /**
	 * This constructor will be called upon instantiation by MASS
	 * The Object supplied MAY be the same object supplied when Places was created
	 * @param obj
	 */
	public ClosestPairPlaces(Object obj) { }

    /**
	 * This method is called when "callAll" is invoked from the master node
	 */
	public Object callMethod(int method, Object o) {
		switch (method) {
		
			case INIT:
				return init(o);
		default:
			return new String("Unknown Method Number: " + method);
		
		}
		
	}

	public double x, y;
	public Point p;
	public Object init ( Object o){
		x = getIndex()[0];
		y = getIndex()[1];

		System.out.println(x +", "+ y);

		return null;
	}
}