package clone;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

// Classe grup del composite
public class Component extends Circuit {
	private List<Connection> connections = new ArrayList<>();
	public List<Circuit> circuits = new ArrayList<Circuit>();

	public Component(int numInputs, int numOutputs) {
		this("component", numInputs, numOutputs);
	}

	public Component(String name, int numInputs, int numOutputs) {
		super(name);
		assert numInputs >= 1 : "invalid number of inputs for a Component";
		assert numOutputs >= 1 : "invalid number of outputs for a Component";

		for (int i = 1; i <= numInputs; i++) {
			addInput(new Pin(name + " input " + i));
		}
		for (int i = 1; i <= numOutputs; i++) {
			addOutput(new Pin(name + " output " + i));
		}
	}

	public void addConnection(Connection c) {
		connections.add(c);
	}

	public void addCircuit(Circuit circ) {
		circuits.add(circ);
	}

	public void process() {
		for (Circuit circ : circuits) {
			circ.process();
		}
	}

	private class TripletPin {
		public int idxCircuit;
		public boolean isInput;
		public int idxPin;
		public TripletPin(int i, boolean b, int j) {
			idxCircuit = i;
			isInput = b;
			idxPin = j;
		}
	}

	// find the index of the circuit a certain pin belongs to, whether it is
	// and input or an output pin, and the index in the corresponding list of
	// input/output pins of that circuit
	private TripletPin findPin(Pin pin) {
		int idxCirc = 0;
		boolean pinIsInput = false;
		int idxPin = 0;
		boolean found = false;
		int i = 0;
		//System.out.println("pin to be found " + pin);
		List<Circuit> allCircuits = new ArrayList<Circuit>();
		allCircuits.addAll(circuits);
		allCircuits.add(this);
		while (i < allCircuits.size() && !found) {
			Circuit circuit = allCircuits.get(i);
			//System.out.println("i = " + i + ", circuit " + circuit.getName());
			int numPinsInput = circuit.numPinsInput();
			for (int j = 0; j < numPinsInput; j++) {
				//System.out.println("pin input compared " + circuit.getPinInput(j));
				if (circuit.getPinInput(j) == pin) {
					idxCirc = i;
					pinIsInput = true;
					idxPin = j;
					found = true;
					//System.out.println("found");
					break;
				}
			}
			if (!found) {
				int numPinsOutput = circuit.numPinsOutput();
				for (int j = 0; j < numPinsOutput; j++) {
					//System.out.println("pin output compared " + circuit.getPinOutput(j));
					if (circuit.getPinOutput(j) == pin) {
						idxCirc = i;
						pinIsInput = false;
						idxPin = j;
						found = true;
						//System.out.println("found");
						break;
					}
				}
			}
			i++;
		}
		assert found : "pin not found";
		return new TripletPin(idxCirc, pinIsInput, idxPin);
	}

	@Override
	public Component clone() {
		Component cloned = new Component(this.name, this.inputs.size(), this.outputs.size());
		for (Circuit circuit : circuits) {
			cloned.addCircuit(circuit.clone());
			// *contained* within the component but not the component itself
			// this is recursive if the circuit is a component that has components=groups
		}
		// clone connections also
		for (Connection c : connections) {
			Pin pinFrom = c.getPinFrom();
			TripletPin tripletPinFrom = findPin(pinFrom);
			Circuit clonedCircuitFrom;
			//System.out.println("tripletPinFrom.idxCircuit " + tripletPinFrom.idxCircuit + "circuits.size() " + circuits.size());
			if (tripletPinFrom.idxCircuit == circuits.size()) {
				clonedCircuitFrom = cloned;
			} else {
				clonedCircuitFrom = cloned.circuits.get(tripletPinFrom.idxCircuit);
			}
			Pin clonedPinFrom;
			if (tripletPinFrom.isInput) {
				clonedPinFrom = clonedCircuitFrom.getPinInput(tripletPinFrom.idxPin);
			} else {
				clonedPinFrom = clonedCircuitFrom.getPinOutput(tripletPinFrom.idxPin);
			}

			Pin pinTo = c.getPinTo();
			TripletPin tripletPinTo = findPin(pinTo);
			Circuit clonedCircuitTo;
			//System.out.println("tripletPinTo.idxCircuit " + tripletPinTo.idxCircuit + "circuits.size() " + circuits.size());
			if (tripletPinTo.idxCircuit == circuits.size()) {
				clonedCircuitTo = cloned;
			} else {
				clonedCircuitTo = cloned.circuits.get(tripletPinTo.idxCircuit);
			}
			Pin clonedPinTo;
			if (tripletPinTo.isInput) {
				clonedPinTo = clonedCircuitTo.getPinInput(tripletPinTo.idxPin);
			} else {
				clonedPinTo = clonedCircuitTo.getPinOutput(tripletPinTo.idxPin);
			}
			cloned.addConnection(new Connection(clonedPinFrom, clonedPinTo));
		}
		return cloned;
	}
}
