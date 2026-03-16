package com.gabrieldsrod.gestao_ct.Repository;

import com.gabrieldsrod.gestao_ct.Enums.TransactionType;
import com.gabrieldsrod.gestao_ct.Model.Transaction;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDate;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long> {

    @Query("SELECT COALESCE(SUM(t.amount), 0) FROM Transaction t WHERE t.type = :type AND t.transactionDate BETWEEN :startDate AND :endDate")
    BigDecimal sumAmountByPeriodAndType(
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate,
            @Param("type") TransactionType type // Passa o Enum aqui!
    );

    @Query("SELECT COALESCE(SUM(t.amount), 0) FROM Transaction t WHERE t.type = :type")
    BigDecimal sumTotalAmountByType(@Param("type") TransactionType type);

    @Query("SELECT t FROM Transaction t WHERE " +
            "t.transactionDate BETWEEN :start AND :end AND " +
            "(:type IS NULL OR t.type = :type) AND " +
            "(:categoryId IS NULL OR t.category.id = :categoryId)")
    Page<Transaction> findTransactionsWithFilters(
            @Param("start") LocalDate start,
            @Param("end") LocalDate end,
            @Param("type") TransactionType type,
            @Param("categoryId") Long categoryId,
            Pageable pageable);
}