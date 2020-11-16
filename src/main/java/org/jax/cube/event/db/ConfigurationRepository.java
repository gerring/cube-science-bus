package org.jax.cube.event.db;


import org.jax.cube.event.domain.Configuration;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ConfigurationRepository extends JpaRepository<Configuration, Long> {



}
