package simple;

import java.util.ArrayList;
import java.util.List;

public abstract class Circuit {
	protected final String name;
	protected List<Pin> inputs = new ArrayList<Pin>();
	protected List<Pin> outputs = new ArrayList<Pin>();

	public Circuit(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}

	protected void addInput(Pin pin) {
		inputs.add(pin);
	}

	public Pin getPinInput(int numPin) { return inputs.get(numPin); }

	public void setInput(int numPin, boolean state) {
		getPinInput(numPin).setState(state);
	}

	protected void addOutput(Pin pin) {
		outputs.add(pin);
	}

	public void setOutput(boolean state) {
		this.getPinOutput(0).setState(state);
	}

	public boolean isOutput() { return isOutput(0); }

	public boolean isOutput(int numPin) { return outputs.get(numPin).isState(); }

	public Pin getPinOutput(int numPin) { return outputs.get(numPin); }

	public abstract void process();

}