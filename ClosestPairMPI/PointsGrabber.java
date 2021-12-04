import java.util.*;
import java.io.*;

public class pointsGrabber implements Serializable{
    private double x, y;

    public pointsGrabber(){}

    pointsGrabber(double x, double y){
        this.x = x;
        this.y = y;
    }

    public void setX(Double number){
        this.x = number;
    }

    public void setY(Double number){
        this.y = number;
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

}