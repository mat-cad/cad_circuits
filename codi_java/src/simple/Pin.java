package simple;

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
        setState(((Pin) arg0).isState());
    }

    public boolean isState() {
        return state;
    }

    public void setState(boolean state) {
        this.state = state;
        setChanged();
        notifyObservers(this);
    }
}
