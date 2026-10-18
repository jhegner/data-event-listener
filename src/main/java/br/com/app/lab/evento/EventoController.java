package br.com.app.lab.evento;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api")
public class EventoController {

    private final EventoPersistenciaRepository eventoRepository;
    private final MensagemRecebidaRepository mensagemRepository;

    public EventoController(EventoPersistenciaRepository eventoRepository,
                            MensagemRecebidaRepository mensagemRepository) {
        this.eventoRepository = eventoRepository;
        this.mensagemRepository = mensagemRepository;
    }

    @GetMapping("/eventos")
    public List<EventoPersistencia> listarEventos() {
        return eventoRepository.findTop100ByOrderByOcorridoEmDesc();
    }

    @GetMapping("/mensagens")
    public List<MensagemRecebida> listarMensagens() {
        return mensagemRepository.findTop100ByOrderByRecebidoEmDesc();
    }
}
