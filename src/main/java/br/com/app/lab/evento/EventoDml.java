package br.com.app.lab.evento;

import java.time.Instant;

public record EventoDml(
        String operacao,
        String entidade,
        String entidadeId,
        String dados,
        Instant ocorridoEm) {
}
