package simple;

public class And extends Circuit {
	public And() { this("and"); }

	public And(String name) {
		this(name, 2);
	}

	// Constructor with >= 1 inputs
	public And(String name, int numInputs) {
		super(name);
		for (int i = 1; i <= numInputs; i++) {
			addInput(new Pin("input " + i));
		}
		addOutput(new Pin("output"));
	}

	public void process() {
		boolean result = true ;
		for (Pin pinInput : inputs) {
			result = result && pinInput.isState();
		}
		setOutput(result);
	}
}
