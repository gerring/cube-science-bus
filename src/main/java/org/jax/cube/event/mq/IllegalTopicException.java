package org.jax.cube.event.mq;

public class IllegalTopicException extends RuntimeException {

	/**
	 * 
	 */
	public IllegalTopicException() {
		super();
	}

	/**
	 * @param message
	 * @param cause
	 * @param enableSuppression
	 * @param writableStackTrace
	 */
	public IllegalTopicException(String message, Throwable cause, boolean enableSuppression,
			boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

	/**
	 * @param message
	 * @param cause
	 */
	public IllegalTopicException(String message, Throwable cause) {
		super(message, cause);
	}

	/**
	 * @param message
	 */
	public IllegalTopicException(String message) {
		super(message);
	}

	/**
	 * @param cause
	 */
	public IllegalTopicException(Throwable cause) {
		super(cause);
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = -6701212728456262556L;

}
