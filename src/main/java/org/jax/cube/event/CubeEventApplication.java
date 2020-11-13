package org.jax.cube.event;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * 
 * @author Matthew Gerring 
 * 
 * 
 * In local debug mode do:
-Dserver.port=3230
 */
@SpringBootApplication
@EnableScheduling
public class CubeEventApplication {
	
	/**
	 *  spring starts up the app
	 * @param args The arguments
	 */
	public static void main(String[] args) {	
		if (System.getProperty("server.port")==null) {
			System.setProperty("server.port", String.valueOf(3230));
		}
		SpringApplication.run(CubeEventApplication.class, args);
	}
	
}