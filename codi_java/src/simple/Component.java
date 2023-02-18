package simple;

import java.util.ArrayList;
import java.util.List;

public class Component extends Circuit {
	private List<Circuit> circuits = new ArrayList<Circuit>();

	public void addCircuit(Circuit circ) {
		circuits.add(circ);
	}

	public Component(String name, int numInputs, int numOutputs) {
		super(name);
		assert numInputs >= 1 : "invalid number of inputs for a Component";
		assert numOutputs >= 1 : "invalid number of outputs for a Component";

		for (int i = 1; i <= numInputs; i++) {
			addInput(new Pin("input " + i));
		}
		for (int i = 1; i <= numOutputs; i++) {
			addOutput(new Pin("output " + i));
		}
	}

	public void process() {
		for (Circuit circ : circuits) {
			circ.process();
		}
	}
}
