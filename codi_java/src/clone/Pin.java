package clone;

import java.util.Observable;
import java.util.Observer;

public class Pin extends Observable implements Observer {
    private String name;
    private boolean state;

    public Pin(String name) {
        this.name = name;
        clearChanged(); // from Observable : cleans this flag
    }

    // A destination pin updates itself copying the state of the origin pin to which it is connected
    public void update(Observable arg0, Object arg1) {
        boolean passedState = ((Pin) arg0).isState();
        //System.out.println("pin " + name + " passed state " + state);
        setState(passedState);
    }

    public boolean isState() {
        return state;
    }

    public void setState(boolean state) {
        //System.out.println("pin " + name + " " + state);
        this.state = state;
        setChanged();
        notifyObservers(this);
        //System.out.println(this.countObservers() + " observers of " + name + " notified");
    }
}
