package org.jax.cube.event.domain;

import java.net.URI;
import java.util.Objects;

/**
 * Totally fake example of bean which will be sent during a test run.
 * 
 * @author gerrim
 *
 */
public class TResponseBean {

	private String status;
	private double complete;
	private URI results;
	/**
	 * @return the status
	 */
	public String getStatus() {
		return status;
	}
	/**
	 * @param status the status to set
	 */
	public void setStatus(String status) {
		this.status = status;
	}
	/**
	 * @return the complete
	 */
	public double getComplete() {
		return complete;
	}
	/**
	 * @param complete the complete to set
	 */
	public void setComplete(double complete) {
		this.complete = complete;
	}
	/**
	 * @return the results
	 */
	public URI getResults() {
		return results;
	}
	/**
	 * @param results the results to set
	 */
	public void setResults(URI results) {
		this.results = results;
	}
	@Override
	public int hashCode() {
		return Objects.hash(complete, results, status);
	}
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!(obj instanceof TResponseBean))
			return false;
		TResponseBean other = (TResponseBean) obj;
		return Double.doubleToLongBits(complete) == Double.doubleToLongBits(other.complete)
				&& Objects.equals(results, other.results) && Objects.equals(status, other.status);
	}
}
