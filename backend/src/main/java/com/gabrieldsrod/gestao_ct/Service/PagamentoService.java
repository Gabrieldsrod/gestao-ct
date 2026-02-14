package com.gabrieldsrod.gestao_ct.Service;

import com.gabrieldsrod.gestao_ct.Enums.MetodoPagamento;
import com.gabrieldsrod.gestao_ct.Enums.TipoTransacao;
import com.gabrieldsrod.gestao_ct.Model.Aluno;
import com.gabrieldsrod.gestao_ct.Model.AlunoPagamento;
import com.gabrieldsrod.gestao_ct.Model.Categoria;
import com.gabrieldsrod.gestao_ct.Model.Transacao;
import com.gabrieldsrod.gestao_ct.Repository.AlunoPagamentoRepository;
import com.gabrieldsrod.gestao_ct.Repository.CategoriaRepository;
import com.gabrieldsrod.gestao_ct.Repository.TransacaoRepository;
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
        AlunoPagamento pagamento = new AlunoPagamento();
        pagamento.setAluno(aluno);
        pagamento.setDataVencimento(dataVencimento);
        pagamento.setValorCobrado(aluno.getPlano().getValorMensalidade());

        pagamento.setDataPagamento(null); // Ainda não pago
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
        entrada.setValor(pagamento.getValorPago());
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

        return pagamentoRepo.save(pagamento);
    }
}
