package clone;

public class And extends Circuit {
	public And() { this("and"); }

	public And(String name) {
		this(name, 2);
	}

	public And(String name, int numInputs) {
		super(name);
		assert numInputs >= 2 : "invalid number of inputs for an And";
		for (int i = 1; i <= numInputs; i++) {
			addInput(new Pin(name + " input " + i));
		}
		addOutput(new Pin(name + " output"));
	}


	public void process() {
		boolean result = true ;
		for (Pin pinInput : inputs) {
			result = result && pinInput.isState();
		}
		setOutput(result);
	}

	@Override
	public And clone() {
		return new And(name, this.inputs.size());
	}
}
