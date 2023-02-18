package simple;

public class Connection {
	public Connection(Pin pinFrom, Pin pinTo) {
		pinFrom.addObserver(pinTo);
	}
}
