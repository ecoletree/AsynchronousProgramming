package kr.co.ecoletree.common.exception;

public class ETException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public ETException() {
		super();
	}
	
	public ETException(final String msg) {
		super(msg);
	}

	public ETException(final Throwable t) {
		super(t);
	}

	public ETException(final String msg, final Throwable t) {
		super(msg, t);
	}
}
