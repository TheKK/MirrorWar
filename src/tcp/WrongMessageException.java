package tcp;

public class WrongMessageException extends Exception {
	private static final long serialVersionUID = 1L;

	public WrongMessageException(String msg) {
		super(msg);
	}
}
