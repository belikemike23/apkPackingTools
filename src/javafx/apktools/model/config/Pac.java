package javafx.apktools.model.config;

/**
 * Created by mike.wang on 2016/10/10.
 */
public class Pac {
    public String name;
    public String mark;

    public Pac() {
    }

    public Pac(String name, String mark) {
        this.name = name;
        this.mark = mark;
    }

    @Override
    public int hashCode() {
        return name.hashCode() + mark.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if(obj == null){
            return false;
        }
        if(obj instanceof Pac){
            Pac c = (Pac) obj;
            if(c.name == null || c.mark == null){
                return false;
            }
            if(c.name.equals(name) && c.mark.equals(mark)){
                return true;
            }
        }
        return false;
    }

    @Override
    public String toString() {
        return name +"("+mark+")" ;
    }
    public String getMark(){
        return mark;
    }
    public String getName(){
        return name;
    }
}