package org.jax.cube.event.domain;

import java.net.URI;
import java.sql.Date;
import java.util.Objects;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

/**
 * Each Engine type in the Engine table may have multiple active instances.
 * If a type has no instances active, it is deamed not to be available for use.
 * 
 * @author gerrim
 *
 */
@Entity
public class Instance {

	@Id
    @GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;

	/**
	 * URI to submit (publish) a bean of the same JSON object type as submitTemplate
	 * Usually this will be a 0MQ publish.
	 */
	private URI submit;
	
	/**
	 * The results URI to which we will subscribe
	 * usually using 0MQ subscription
	 */
	private URI response;
	
	/**
	 * The complete URI to which we can subscribe to get the complete
	 * results. The results will be transmitted as a binary stream or a 
	 * JSON object which contains file paths. (Preferred binary stream.)
	 */
	private URI results;
	
	@ManyToOne
	@JoinColumn(name="configuration_id", nullable=false)
	private Configuration configuration;
	
	/**
	 * URI to check if this instance is still active.
	 */
	private URI active;

	/**
	 * When this instance was started.
	 */
	private Date started;
	
	/**
	 * Beyond which this instance will expire. 
	 * It is allwoed not to have an expiry. The active URI
	 * may be checked periodically if information about the 
	 * instance being available is required.
	 */
	private Date expiry;

	
	/**
	 * No args for reflection/bean
	 */
	public Instance() {
		
	}
	
	/**
	 * @param submit
	 * @param response
	 * @param results
	 * @param id
	 * @param engine
	 * @param active
	 * @param started
	 * @param expiry
	 */
	public Instance(URI submit, URI response, 
					URI results, Long id, 
					Configuration configuration, 
					URI active, 
					Date started,
					Date expiry) {
		super();
		this.submit = submit;
		this.response = response;
		this.results = results;
		this.id = id;
		this.configuration = configuration;
		this.active = active;
		this.started = started;
		this.expiry = expiry;
	}



	/**
	 * @return the id
	 */
	public Long getId() {
		return id;
	}

	/**
	 * @param id the id to set
	 */
	public void setId(Long id) {
		this.id = id;
	}

	/**
	 * @return the active
	 */
	public URI getActive() {
		return active;
	}

	/**
	 * @param active the active to set
	 */
	public void setActive(URI active) {
		this.active = active;
	}

	/**
	 * @return the started
	 */
	public Date getStarted() {
		return started;
	}

	/**
	 * @param started the started to set
	 */
	public void setStarted(Date started) {
		this.started = started;
	}

	/**
	 * @return the expiry
	 */
	public Date getExpiry() {
		return expiry;
	}

	/**
	 * @param expiry the expiry to set
	 */
	public void setExpiry(Date expiry) {
		this.expiry = expiry;
	}

	@Override
	public int hashCode() {
		return Objects.hash(active, configuration, expiry, id, response, results, started, submit);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!(obj instanceof Instance))
			return false;
		Instance other = (Instance) obj;
		return Objects.equals(active, other.active) && Objects.equals(configuration, other.configuration)
				&& Objects.equals(expiry, other.expiry) && Objects.equals(id, other.id)
				&& Objects.equals(response, other.response) && Objects.equals(results, other.results)
				&& Objects.equals(started, other.started) && Objects.equals(submit, other.submit);
	}

	/**
	 * @return the submit
	 */
	public URI getSubmit() {
		return submit;
	}

	/**
	 * @param submit the submit to set
	 */
	public void setSubmit(URI submit) {
		this.submit = submit;
	}

	/**
	 * @return the response
	 */
	public URI getResponse() {
		return response;
	}

	/**
	 * @param response the response to set
	 */
	public void setResponse(URI response) {
		this.response = response;
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

	/**
	 * @return the configuration
	 */
	public Configuration getConfiguration() {
		return configuration;
	}

	/**
	 * @param configuration the configuration to set
	 */
	public void setConfiguration(Configuration configuration) {
		this.configuration = configuration;
	}
}
