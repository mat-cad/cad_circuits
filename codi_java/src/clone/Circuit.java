package clone;
import java.util.ArrayList;
import java.util.List;

public abstract class Circuit implements Cloneable {
  protected String name;
  protected List<Pin> inputs = new ArrayList<Pin>();
  protected List<Pin> outputs = new ArrayList<Pin>();

  public Circuit(String name) {
    this.name = name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getName() {
    return name;
  }

  protected void addInput(Pin pin) { inputs.add(pin); }

  protected void addOutput(Pin pin) {
    outputs.add(pin);
  }

  public void setInput(int numInput, boolean state) {
    getPinInput(numInput).setState(state);
  }

  public boolean isInput(int numPin) {
    return getPinInput(numPin).isState();
  }

  public void setOutput(boolean state) {
    setOutput(0, state);
  }

  public void setOutput(int numInput, boolean state) {
    getPinOutput(numInput).setState(state);
  }

  public boolean isOutput() { // of first an only output pin
    return isOutput(0);
  }

  public boolean isOutput(int numPin) {
    return getPinOutput(numPin).isState();
  }

  public abstract void process();

  public Pin getPinInput(int numPin) {
    return inputs.get(numPin);
  }

  public Pin getPinOutput(int numPin) {
    return outputs.get(numPin);
  }

  public int numPinsInput() {
    return inputs.size();
  }

  public int numPinsOutput() {
    return outputs.size();
  }

  @Override
  public abstract Circuit clone();
}