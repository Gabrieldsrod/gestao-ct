package com.gabrieldsrod.gestao_ct.service;

import com.gabrieldsrod.gestao_ct.DTO.request.FeeUpdateDTO;
import com.gabrieldsrod.gestao_ct.DTO.response.FeeResponseDTO;
import com.gabrieldsrod.gestao_ct.Enums.PaymentMethod;
import com.gabrieldsrod.gestao_ct.Model.PaymentFee;
import com.gabrieldsrod.gestao_ct.Repository.FeeRepository;
import com.gabrieldsrod.gestao_ct.Service.FeeService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Sort;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FeeServiceTest {

    @Mock
    private FeeRepository feeRepository;

    @InjectMocks
    private FeeService feeService;

    @Test
    @DisplayName("Deve retornar todas as taxas")
    void shouldGetAllFees() {
        // Arrange
        PaymentFee fee1 = new PaymentFee();
        fee1.setId(1L);
        fee1.setPaymentMethod(PaymentMethod.CREDIT_CARD);
        fee1.setPercentageFee(new BigDecimal("3.00"));
        fee1.setFixedFee(new BigDecimal("0.50"));
        fee1.setDaysToReceive(14);

        PaymentFee fee2 = new PaymentFee();
        fee2.setId(2L);
        fee2.setPaymentMethod(PaymentMethod.PIX);
        fee2.setPercentageFee(new BigDecimal("1.00"));
        fee2.setFixedFee(new BigDecimal("0.00"));
        fee2.setDaysToReceive(1);


        when(feeRepository.findAll(Sort.by(Sort.Direction.ASC, "id"))).thenReturn(List.of(fee1, fee2));

        // Act
        List<FeeResponseDTO> result = feeService.getAllFees();

        // Assert
        assertEquals(2, result.size());
        assertEquals(1L, result.get(0).id());
        assertEquals(2L, result.get(1).id());
        verify(feeRepository, times(1)).findAll(Sort.by(Sort.Direction.ASC, "id"));
    }

    @Test
    @DisplayName("Deve atualizar uma taxa com sucesso")
    void shouldUpdateFeeSuccessfully() {
        // Arrange
        Long feeId = 1L;
        FeeUpdateDTO feeUpdateDTO = new FeeUpdateDTO(new BigDecimal("3.50"), new BigDecimal("0.75"), 10);
        PaymentFee existingFee = new PaymentFee();
        existingFee.setId(feeId);
        existingFee.setPaymentMethod(PaymentMethod.CREDIT_CARD);
        existingFee.setPercentageFee(new BigDecimal("3.00"));
        existingFee.setFixedFee(new BigDecimal("0.50"));
        existingFee.setDaysToReceive(14);

        when(feeRepository.findById(feeId)).thenReturn(Optional.of(existingFee));

        // Act
        feeService.updateFee(feeId, feeUpdateDTO);

        // Assert
        verify(feeRepository, times(1)).findById(feeId);
        verify(feeRepository, times(1)).save(any(PaymentFee.class));
        assertEquals(new BigDecimal("3.50"), existingFee.getPercentageFee());
        assertEquals(new BigDecimal("0.75"), existingFee.getFixedFee());
        assertEquals(10, existingFee.getDaysToReceive());
    }

    @Test
    @DisplayName("Deve lançar exceção ao tentar atualizar taxa inexistente")
    void shouldThrowExceptionWhenUpdatingNonExistentFee() {
        // Arrange
        Long feeId = 99L;
        FeeUpdateDTO feeUpdateDTO = new FeeUpdateDTO(new BigDecimal("3.50"), new BigDecimal("0.75"), 10);

        when(feeRepository.findById(feeId)).thenReturn(Optional.empty());

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> feeService.updateFee(feeId, feeUpdateDTO));

        assertEquals("Configuração de taxa com ID " + feeId + " não encontrada.", exception.getMessage());
        verify(feeRepository, times(1)).findById(feeId);
        verify(feeRepository, never()).save(any());
    }

    @Test
    @DisplayName("Deve calcular a taxa para cartão de crédito")
    void shouldCalculateFeeForCreditCard() {
        // Arrange
        PaymentFee feeConfig = new PaymentFee();
        feeConfig.setPercentageFee(new BigDecimal("5.00")); // 5%
        feeConfig.setFixedFee(new BigDecimal("1.00"));

        when(feeRepository.findPaymentFeeByPaymentMethod(PaymentMethod.CREDIT_CARD)).thenReturn(Optional.of(feeConfig));

        BigDecimal grossAmount = new BigDecimal("100.00");

        // Act
        BigDecimal calculatedFee = feeService.calculateFee(PaymentMethod.CREDIT_CARD, grossAmount);

        // Assert
        // 5% of 100.00 is 5.00. 5.00 + 1.00 (fixed fee) = 6.00
        assertEquals(new BigDecimal("6.00"), calculatedFee);
        verify(feeRepository, times(1)).findPaymentFeeByPaymentMethod(PaymentMethod.CREDIT_CARD);
    }

    @Test
    @DisplayName("Deve calcular a taxa para PIX")
    void shouldCalculateFeeForPix() {
        // Arrange
        PaymentFee feeConfig = new PaymentFee();
        feeConfig.setPercentageFee(new BigDecimal("1.00")); // 1%
        feeConfig.setFixedFee(new BigDecimal("0.00"));

        when(feeRepository.findPaymentFeeByPaymentMethod(PaymentMethod.PIX)).thenReturn(Optional.of(feeConfig));

        BigDecimal grossAmount = new BigDecimal("150.00");

        // Act
        BigDecimal calculatedFee = feeService.calculateFee(PaymentMethod.PIX, grossAmount);

        // Assert
        // 1% of 150.00 is 1.50. 1.50 + 0.00 (fixed fee) = 1.50
        assertEquals(new BigDecimal("1.50"), calculatedFee);
        verify(feeRepository, times(1)).findPaymentFeeByPaymentMethod(PaymentMethod.PIX);
    }

    @Test
    @DisplayName("Deve lançar exceção ao calcular taxa para método de pagamento sem configuração")
    void shouldThrowExceptionWhenCalculatingFeeForUnconfiguredPaymentMethod() {
        // Arrange
        when(feeRepository.findPaymentFeeByPaymentMethod(PaymentMethod.DEBIT_CARD)).thenReturn(Optional.empty());

        BigDecimal grossAmount = new BigDecimal("100.00");

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> feeService.calculateFee(PaymentMethod.DEBIT_CARD, grossAmount));

        assertEquals("Configuração de taxa para o método de pagamento " + PaymentMethod.DEBIT_CARD + " não encontrada.", exception.getMessage());
        verify(feeRepository, times(1)).findPaymentFeeByPaymentMethod(PaymentMethod.DEBIT_CARD);
    }
}
