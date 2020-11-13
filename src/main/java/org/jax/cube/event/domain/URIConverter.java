package org.jax.cube.event.domain;

import java.net.URI;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

/**
 * @see https://stackoverflow.com/questions/25738569/how-to-map-a-map-json-column-to-java-object-with-jpa
 * @author gerrim
 *
 */
@Converter(autoApply = true)
public class URIConverter implements AttributeConverter<URI, String> {

	@Override
	public String convertToDatabaseColumn(URI meta) {
		
		if (meta==null) return null;
		return ((URI)meta).toString();
	}
	
	@Override
	public URI convertToEntityAttribute(String dbData) {
		
		if (dbData == null) return null;
		return URI.create(dbData.trim());
	}

}