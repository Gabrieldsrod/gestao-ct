package com.gabrieldsrod.gestao_ct.Controller;

import com.gabrieldsrod.gestao_ct.DTO.request.NovaTransacaoDTO;
import com.gabrieldsrod.gestao_ct.DTO.response.CaixaDTO;
import com.gabrieldsrod.gestao_ct.DTO.response.TransacaoDTO;
import com.gabrieldsrod.gestao_ct.Enums.TipoTransacao;
import com.gabrieldsrod.gestao_ct.Model.Categoria;
import com.gabrieldsrod.gestao_ct.Model.Transacao;
import com.gabrieldsrod.gestao_ct.Repository.CategoriaRepository;
import com.gabrieldsrod.gestao_ct.Repository.TransacaoRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/transacoes")
public class TransacaoController {

    private final TransacaoRepository transacaoRepo;

    private final CategoriaRepository categoriaRepo;

    public TransacaoController(TransacaoRepository transacaoRepo, CategoriaRepository categoriaRepo) {
        this.transacaoRepo = transacaoRepo;
        this.categoriaRepo = categoriaRepo;
    }

    @PostMapping
    public ResponseEntity<?> criarTransacao(@RequestBody NovaTransacaoDTO dados) {
        Categoria categoria = categoriaRepo.findById(dados.categoriaId())
                .orElseThrow(() -> new RuntimeException("Categoria não encontrada"));

        Transacao novaTransacao = new Transacao();
        novaTransacao.setDescricao(dados.descricao());
        novaTransacao.setValor(dados.valor());
        novaTransacao.setTipo(dados.tipoTransacao());
        novaTransacao.setMetodoPagamento(dados.metodoPagamento());
        novaTransacao.setCategoria(categoria);
        novaTransacao.setDataMovimento(LocalDate.now());

        transacaoRepo.save(novaTransacao);

        return ResponseEntity.ok().body(Map.of(
                "id", novaTransacao.getId(),
                "descricao", novaTransacao.getDescricao(),
                "categoria", categoria.getNome(),
                "valor", novaTransacao.getValor()
        ));
    }

    @GetMapping
    public ResponseEntity<List<TransacaoDTO>> listarTransacoes() {
        try {
            List<TransacaoDTO> transacoes = transacaoRepo.findAllByOrderByDataMovimentoDesc().stream()
                    .map(TransacaoDTO::new)
                    .toList();
            return ResponseEntity.ok(transacoes);
        }
        catch (Exception e) {
            return ResponseEntity.status(500).body(null);
        }
    }

    @GetMapping("/caixa")
    public ResponseEntity<?> obterResumoCaixa() {

        List<Transacao> transacoes = transacaoRepo.findAll();

        BigDecimal totalEntradas = transacoes.stream()
                .filter(t -> t.getTipo() == TipoTransacao.RECEITA)
                .map(Transacao::getValor)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal totalSaidas = transacoes.stream()
                .filter(t -> t.getTipo() == TipoTransacao.DESPESA)
                .map(Transacao::getValor)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal saldoFinal = totalEntradas.subtract(totalSaidas);

        List<TransacaoDTO> listaTransacoesDto = transacoes.stream()
                .map(TransacaoDTO::new)
                .toList();

        CaixaDTO resumo = new CaixaDTO(totalEntradas, totalSaidas, saldoFinal, listaTransacoesDto);

        return ResponseEntity.ok(resumo);
    }
}
