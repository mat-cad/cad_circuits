package simple;

public class Not extends Circuit {
	public Not() { this("not"); }
	public Not(String name) {
		super(name);
		addInput(new Pin("input not"));
		addOutput(new Pin("output not"));
	}
	// Per fer mes llegible la funcio process(), donat que nomes hi ha una entrada sempre.
	protected boolean isStateInput() {
		return this.getPinInput(0).isState();
	}

	public void setStateInput(boolean state) {
		this.getPinInput(0).setState(state);
	}
		
	public void process() {
		setOutput(!isStateInput());
	}
}
