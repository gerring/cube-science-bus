package org.jax.cube.event.db;


import java.util.List;

import org.jax.cube.event.domain.Instance;
import org.springframework.data.jpa.repository.JpaRepository;

public interface InstanceRepository extends JpaRepository<Instance, Long> {

	
	List<Instance> findByConfigurationId(Long engineId);
	
}
