package com.gabrieldsrod.gestao_ct.service;

import com.gabrieldsrod.gestao_ct.enums.MetodoPagamento;
import com.gabrieldsrod.gestao_ct.enums.TipoTransacao;
import com.gabrieldsrod.gestao_ct.model.Aluno;
import com.gabrieldsrod.gestao_ct.model.AlunoPagamento;
import com.gabrieldsrod.gestao_ct.model.Categoria;
import com.gabrieldsrod.gestao_ct.model.Transacao;
import com.gabrieldsrod.gestao_ct.repository.AlunoPagamentoRepository;
import com.gabrieldsrod.gestao_ct.repository.CategoriaRepository;
import com.gabrieldsrod.gestao_ct.repository.TransacaoRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
public class PagamentoService {

    private final AlunoPagamentoRepository pagamentoRepo;
    private final TransacaoRepository transacaoRepo;
    private final CategoriaRepository categoriaRepo;

    public PagamentoService(AlunoPagamentoRepository pagamentoRepo, TransacaoRepository transacaoRepo, CategoriaRepository categoriaRepo) {
        this.pagamentoRepo = pagamentoRepo;
        this.transacaoRepo = transacaoRepo;
        this.categoriaRepo = categoriaRepo;
    }

    public AlunoPagamento gerarCobranca(Aluno aluno, LocalDate dataVencimento) {
        if (!aluno.getAtivo() || aluno.getPlano() == null) {
            return null; // Ignora se o aluno está inativo ou sem plano
        }

        AlunoPagamento pagamento = new AlunoPagamento();
        pagamento.setAluno(aluno);

        LocalDate proximoMes = dataVencimento.plusMonths(1).withDayOfMonth(1);
        int diaVencimento = Math.min(aluno.getDiaPreferenciaPagamento(), proximoMes.lengthOfMonth());
        pagamento.setDataVencimento(proximoMes.withDayOfMonth(diaVencimento));

        pagamento.setValorCobrado(aluno.getPlano().getValorMensalidade());
        pagamento.setDataPagamento(null);
        pagamento.setValorPago(null);
        pagamento.setTransacao(null);
        return pagamentoRepo.save(pagamento);
    }

    @Transactional
    public AlunoPagamento registrarPagamento(Long pagamentoId, MetodoPagamento metodoPagamento) {
        AlunoPagamento pagamento = pagamentoRepo.findById(pagamentoId)
                .orElseThrow(() -> new RuntimeException("Pagamento não encontrado"));

        if (pagamento.getDataPagamento() != null) {
            throw new RuntimeException("Pagamento já registrado");
        }

        Transacao entrada = new Transacao();
        entrada.setDescricao("Mensalidade - " + pagamento.getAluno().getNome());
        entrada.setValor(pagamento.getValorCobrado());
        entrada.setMetodoPagamento(metodoPagamento);
        entrada.setTipo(TipoTransacao.RECEITA);
        entrada.setDataMovimento(LocalDate.now());

        Categoria categoria = categoriaRepo.findByNome("Mensalidade")
                .orElseThrow(() -> new RuntimeException("Categoria 'Mensalidade' não encontrada"));
        entrada.setCategoria(categoria);

        transacaoRepo.save(entrada);

        pagamento.setDataPagamento(LocalDate.now());
        pagamento.setValorPago(pagamento.getValorCobrado());
        pagamento.setTransacao(entrada);

        gerarCobranca(pagamento.getAluno(), pagamento.getDataVencimento());

        return pagamentoRepo.save(pagamento);
    }
}
