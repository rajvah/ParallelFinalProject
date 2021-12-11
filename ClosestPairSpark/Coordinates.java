import java.io.Serializable;

public class Coordinates implements Serializable{

    Double x;
    Double y;

    Coordinates( Double x, Double y){
        this.x = x;
        this.y = y;
    }
}