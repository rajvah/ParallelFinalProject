import edu.uw.bothell.css.dsl.MASS.Agents;
import edu.uw.bothell.css.dsl.MASS.MASS;
import edu.uw.bothell.css.dsl.MASS.Places;
import edu.uw.bothell.css.dsl.MASS.logging.LogLevel;

import java.util.Date;
import java.util.ArrayList;
import java.util.*;
import java.io.*;


public class ComputeClosestPair{
    private static final String NODE_FILE = "nodes.xml";

    public static void main(String args[]) throws IOException{

        String fileName = args[0];

        long startTime = new Date().getTime();
        
        ArrayList<Point> inputPoints = new ArrayList<>();  
        // inputPoints.add(new Point(2.87, 9.09));
        try{
            BufferedReader in = new BufferedReader(new FileReader("src/main/resources/" + fileName));
            String line = null;
            while((line = in.readLine()) != null) {
                String[] items = line.split("\\s+");
                double[] coordinates = new double[items.length];
                for (int i = 0; i < items.length; i++) {
                    coordinates[i] = Double.parseDouble(items[i]);
                }
   
                Point p = new Point(coordinates[0], coordinates[1]);
                
                inputPoints.add(p);
            }
        } catch( Exception e){
            System.out.println(e);
            throw e;
        }
        
        // new InputStreamReader(getClass().getResourceAsStream ("/DBTextFiles/Administrator.txt"))
            

        MASS.setNodeFilePath( NODE_FILE );
		MASS.setLoggingLevel( LogLevel.DEBUG );

        MASS.init();
        MASS.getLogger().debug("MASS started");

        int size = inputPoints.size();

        Places places = new Places( 1, ClosestPairPlaces.class.getName(), null, 100, 100);  

        places.callAll(ClosestPairPlaces.INIT);

        ArgsToAgents args2agents = new ArgsToAgents( inputPoints );

        Agents agents = new Agents(2, ClosestPairAgent.class.getName(), (Object) args2agents, places, size);

        agents.callAll(ClosestPairAgent.INIT);
        Object resp = agents.callAll(ClosestPairAgent.CHECK, null);

        Double min = Double.MAX_VALUE;

        for (Object ob : (Object[]) resp) {

            if (ob != null) {
                List<Double> mins = (List< Double>) ob;

                for( Double x : mins){
                     min = Double.min(min, x);
                }
            }
        }

        long endTime = System.currentTimeMillis();
        System.out.println(min);

        System.out.println("Closest Pair of Points : "+ Long.toString(endTime - startTime) + " milli seconds");

        MASS.finish();

    }
}