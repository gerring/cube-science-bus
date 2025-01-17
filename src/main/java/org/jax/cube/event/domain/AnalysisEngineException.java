package org.jax.cube.event.domain;

public class AnalysisEngineException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 3238312318683023962L;

	/**
	 * 
	 */
	public AnalysisEngineException() {
		super();
	}

	/**
	 * @param message
	 * @param cause
	 * @param enableSuppression
	 * @param writableStackTrace
	 */
	public AnalysisEngineException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

	/**
	 * @param message
	 * @param cause
	 */
	public AnalysisEngineException(String message, Throwable cause) {
		super(message, cause);
	}

	/**
	 * @param message
	 */
	public AnalysisEngineException(String message) {
		super(message);
	}

	/**
	 * @param cause
	 */
	public AnalysisEngineException(Throwable cause) {
		super(cause);
	}

}
