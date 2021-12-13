import edu.uw.bothell.css.dsl.MASS.MASS;
import edu.uw.bothell.css.dsl.MASS.Agent;
import java.util.*;


public class ClosestPairAgent extends Agent{
    public static final int INIT = 0;
    public static final int CHECK = 1;

    ArrayList<Point> points;

    public ClosestPairAgent( Object o){

        // System.out.println("Agent ka constructor hu me");
        ArgsToAgents args2agents = (ArgsToAgents) o;

        // System.out.println(x.points);
        points = args2agents.points;
    }

    public Object callMethod(int method, Object o) {
        switch (method) {
		
		    case INIT:
				    return roamAround(o);
            case CHECK:
                    return findMin(o);
		    default:
			    return new String("Unknown Method Number: " + method);
		}
    }
    Map<Integer, Point> agentToPoint = new HashMap<Integer, Point>();
    public static int counter = 0;
    public Object roamAround(Object o){
        agentToPoint.put(new Integer(getAgentId()), points.get(counter++));
        return null;
    }
    
    // to store min distance calculated by each agent
    public static List<Double> agentToMin = new ArrayList<Double>();
    public List<Double> findMin(Object o){
        Double min = Double.MAX_VALUE;
        Point curr = agentToPoint.get(getAgentId());
        for( Point p : points){
            if(! curr.equals(p)){

                Double dis = Math.pow(curr.getX() - p.getX(), 2) + Math.pow(curr.getY() - p.getY(), 2);
                min = Math.min(dis, min);
            }
        }
        agentToMin.add( Math.sqrt(min));
        // System.out.println(Math.sqrt(min));

        return agentToMin;
    }
}