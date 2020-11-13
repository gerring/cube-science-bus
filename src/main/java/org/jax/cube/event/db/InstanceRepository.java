package org.jax.cube.event.db;


import java.util.Collection;
import java.util.UUID;

import org.jax.cube.event.domain.Instance;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface InstanceRepository extends JpaRepository<Instance, Long> {

	
	Collection<Instance> findByEngineId(UUID engineId);
	
}
