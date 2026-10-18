package br.com.app.lab.evento;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface EventoPersistenciaRepository extends JpaRepository<EventoPersistencia, Long> {
    List<EventoPersistencia> findTop100ByOrderByOcorridoEmDesc();
}
