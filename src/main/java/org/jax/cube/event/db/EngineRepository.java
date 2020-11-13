package org.jax.cube.event.db;


import java.util.Map;
import java.util.UUID;

import org.jax.cube.event.domain.AnalysisEngineException;
import org.jax.cube.event.domain.Engine;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EngineRepository extends JpaRepository<Engine, UUID> {



}
