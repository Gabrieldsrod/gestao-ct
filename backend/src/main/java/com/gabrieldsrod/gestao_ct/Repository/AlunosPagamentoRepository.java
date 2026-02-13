package com.gabrieldsrod.gestao_ct.Repository;

import com.gabrieldsrod.gestao_ct.Model.Aluno;
import com.gabrieldsrod.gestao_ct.Model.AlunoPagamento;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface AlunosPagamentoRepository extends JpaRepository<AlunoPagamento, Long> {

    // LISTA DE INADIMPLENTES / A RECEBER
    // Traz tudo onde a Data de Pagamento está vazia (NULL)
    List<AlunoPagamento> findByDataPagamentoIsNull();

    // LISTA DE PAGOS
    // Traz tudo onde a Data de Pagamento NÃO está vazia
    List<AlunoPagamento> findByDataPagamentoIsNotNull();

    // Histórico financeiro de um aluno específico
    List<AlunoPagamento> findByAluno(Aluno aluno);

    // Busca pagamentos que vencem em um mês específico (para gerar boletos)
    // Ex: Todos os vencimentos entre 01/02 e 28/02
    List<AlunoPagamento> findByDataVencimentoBetween(LocalDate inicio, LocalDate fim);
}
