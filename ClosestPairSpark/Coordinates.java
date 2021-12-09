import java.io.Serializable;
import java.util.*;

public class Coordinates implements Serializable{

    Double x;
    Double y;

    Coordinates( Double x, Double y){
        this.x = x;
        this.y = y;
    }

    public Double EuclideanDistance(Coordinates c1, Coordinates c2){

        return Math.sqrt(
            Math.pow( c1.x - c2.x, 2) + Math.pow( c1.y - c2.y, 2)
        );

    }
}