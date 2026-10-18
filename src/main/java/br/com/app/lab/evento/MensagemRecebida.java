package br.com.app.lab.evento;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.Instant;

@Entity
@Table(name = "mensagens_recebidas")
public class MensagemRecebida {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 20)
    private String operacao;

    @Column(nullable = false, length = 120)
    private String entidade;

    @Column(nullable = false, length = 80)
    private String entidadeId;

    @Column(nullable = false, columnDefinition = "JSON")
    private String dados;

    @Column(nullable = false)
    private Instant ocorridoEm;

    @Column(nullable = false)
    private Instant recebidoEm;

    protected MensagemRecebida() {
    }

    public MensagemRecebida(String operacao, String entidade, String entidadeId, String dados,
                            Instant ocorridoEm, Instant recebidoEm) {
        this.operacao = operacao;
        this.entidade = entidade;
        this.entidadeId = entidadeId;
        this.dados = dados;
        this.ocorridoEm = ocorridoEm;
        this.recebidoEm = recebidoEm;
    }

    public Long getId() {
        return id;
    }

    public String getOperacao() {
        return operacao;
    }

    public String getEntidade() {
        return entidade;
    }

    public String getEntidadeId() {
        return entidadeId;
    }

    public String getDados() {
        return dados;
    }

    public Instant getOcorridoEm() {
        return ocorridoEm;
    }

    public Instant getRecebidoEm() {
        return recebidoEm;
    }
}
