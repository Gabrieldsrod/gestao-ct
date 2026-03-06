package com.gabrieldsrod.gestao_ct.Repository;

import com.gabrieldsrod.gestao_ct.Model.Transaction;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long> {

    Page<Transaction> findByTransactionDateBetween(LocalDate start, LocalDate end, Pageable pageable);

    @Query("SELECT COALESCE(SUM(t.amount), 0) FROM Transaction t WHERE t.type = 'INCOME'")
    BigDecimal sumTotalIncomes();

    @Query("SELECT COALESCE(SUM(t.amount), 0) FROM Transaction t WHERE t.type = 'EXPENSE'")
    BigDecimal sumTotalExpenses();

    @Query("SELECT COALESCE(SUM(t.amount), 0) FROM Transaction t WHERE t.type = 'INCOME' AND t.transactionDate BETWEEN :startDate AND :endDate")
    BigDecimal sumIncomesBetween(@Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);
}