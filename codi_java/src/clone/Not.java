package clone;

public class Not extends Circuit {
	public Not() { this("not"); }

	public Not(String name) {
		super(name);
		addInput(new Pin("not input"));
		addOutput(new Pin("not output"));
	}

	protected boolean isStateInput() {
		return this.getPinInput(0).isState();
	}

	public void setStateInput(boolean state) {
		this.getPinInput(0).setState(state);
	}

	public void process() {
		setOutput(!isStateInput());
	}

	@Override
	public Not clone() {
		return new Not(name);
	}
}
