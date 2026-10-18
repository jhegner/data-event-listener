package br.com.app.lab.evento;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MensagemRecebidaRepository extends JpaRepository<MensagemRecebida, Long> {
    List<MensagemRecebida> findTop100ByOrderByRecebidoEmDesc();
}
