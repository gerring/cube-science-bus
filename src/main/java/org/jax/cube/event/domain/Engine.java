package org.jax.cube.event.domain;

import java.sql.Date;
import java.util.Objects;
import java.util.UUID;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;


/**
 * Rows of the Engine table for analysis engines.
 * 
 * NOTE the @column annotation is not required in this class
 * as the columns match the field names but we have used it anyway.
 * 
 * @author gerrim
 *
 */
@Entity
@Table(name = "ENGINE")
public class Engine {

	@Id
    @GeneratedValue(strategy = GenerationType.AUTO)
	private UUID id;

	private String description;
	
	/**
	 * An example of what the submit JSON should be.
	 */
	private String submitTemplate;
	/**
	 * The template which we will expect the results to be in.
	 */
	private String responseTemplate;

	/**
	 * The registration of the 
	 */
	private Date registered;

	/**
	 * No args for reflection/bean
	 */
	public Engine() {
		
	}

	/**
	 * @param id
	 * @param description
	 * @param submitTemplate
	 * @param responseTemplate
	 * @param registered
	 */
	public Engine(UUID id, String description, String submitTemplate, String responseTemplate, Date registered) {
		super();
		this.id = id;
		this.description = description;
		this.submitTemplate = submitTemplate;
		this.responseTemplate = responseTemplate;
		this.registered = registered;
	}

	/**
	 * @return the id
	 */
	public UUID getId() {
		return id;
	}

	/**
	 * @param id the id to set
	 */
	public void setId(UUID id) {
		this.id = id;
	}

	/**
	 * @return the description
	 */
	public String getDescription() {
		return description;
	}

	/**
	 * @param description the description to set
	 */
	public void setDescription(String description) {
		this.description = description;
	}

	/**
	 * @return the submitTemplate
	 */
	public Object getSubmitTemplate() {
		return submitTemplate;
	}

	/**
	 * @param submitTemplate the submitTemplate to set
	 */
	public void setSubmitTemplate(String submitTemplate) {
		this.submitTemplate = submitTemplate;
	}

	/**
	 * @return the responseTemplate
	 */
	public Object getResponseTemplate() {
		return responseTemplate;
	}

	/**
	 * @param responseTemplate the responseTemplate to set
	 */
	public void setResponseTemplate(String responseTemplate) {
		this.responseTemplate = responseTemplate;
	}

	/**
	 * @return the registered
	 */
	public Date getRegistered() {
		return registered;
	}

	/**
	 * @param registered the registered to set
	 */
	public void setRegistered(Date registered) {
		this.registered = registered;
	}

	@Override
	public int hashCode() {
		return Objects.hash(description, id, registered, responseTemplate, submitTemplate);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!(obj instanceof Engine))
			return false;
		Engine other = (Engine) obj;
		return Objects.equals(description, other.description) && Objects.equals(id, other.id)
				&& Objects.equals(registered, other.registered)
				&& Objects.equals(responseTemplate, other.responseTemplate)
				&& Objects.equals(submitTemplate, other.submitTemplate);
	}


	
}
