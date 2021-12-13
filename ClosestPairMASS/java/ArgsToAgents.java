import java.io.*;
import java.util.*;
import edu.uw.bothell.css.dsl.MASS.*;

public class ArgsToAgents implements Serializable{
    
    public ArgsToAgents( ArrayList<Point> a ) {
	    points = a;
    }
    public ArrayList<Point> points = new ArrayList<Point>();
}