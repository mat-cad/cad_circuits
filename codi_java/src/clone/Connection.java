package clone;

public class Connection {
	private Pin pinFrom;
	private Pin pinTo;
	public Connection (Pin pinFrom, Pin pinTo) {
		this.pinFrom = pinFrom;
		this.pinTo = pinTo;
		pinFrom.addObserver(pinTo);
	}

	public Pin getPinFrom() { return pinFrom; }
	public Pin getPinTo() {
		return pinTo;
	}
}
