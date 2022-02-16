package kr.co.ecoletree.common.exception;

public class ETRuntimeException extends RuntimeException {
    public ETRuntimeException() {
        super();
    }

    public ETRuntimeException(final String msg) {
        super(msg);
    }

    public ETRuntimeException(final Throwable t) {
        super(t);
    }

    public ETRuntimeException(final String msg, final Throwable t) {
        super(msg, t);
    }
}
