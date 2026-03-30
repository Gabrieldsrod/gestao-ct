package com.gabrieldsrod.gestao_ct.Service;

import com.gabrieldsrod.gestao_ct.DTO.request.FeeUpdateDTO;
import com.gabrieldsrod.gestao_ct.DTO.response.FeeResponseDTO;
import com.gabrieldsrod.gestao_ct.Enums.PaymentMethod;
import com.gabrieldsrod.gestao_ct.Model.PaymentFee;
import com.gabrieldsrod.gestao_ct.Repository.FeeRepository;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Optional;

@Service
public class FeeService {

    private final FeeRepository feeRepository;

    public FeeService(FeeRepository feeRepository) {
        this.feeRepository = feeRepository;
    }

    public List<FeeResponseDTO> getAllFees() {
        return feeRepository.findAll(Sort.by(Sort.Direction.ASC, "id")).stream().map(FeeResponseDTO::new).toList();
    }

    public void updateFee(Long id, FeeUpdateDTO data) {
        PaymentFee feeConfig = this.findById(id);
        feeConfig.setPercentageFee(data.percentageFee());
        feeConfig.setFixedFee(data.fixedFee());
        feeConfig.setDaysToReceive(data.daysToReceive());

        feeRepository.save(feeConfig);
    }

    public BigDecimal calculateFee(PaymentMethod paymentMethod, BigDecimal grossAmount) {
        PaymentFee feeConfig = this.findByPaymentMethod(paymentMethod);

        BigDecimal percentageFeeAmount = grossAmount
                .multiply(feeConfig.getPercentageFee())
                .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);
        return percentageFeeAmount.add(feeConfig.getFixedFee());
    }

    private PaymentFee findByPaymentMethod(PaymentMethod paymentMethod) {
        Optional<PaymentFee> paymentFee = feeRepository.findPaymentFeeByPaymentMethod(paymentMethod);
        if (paymentFee.isPresent()) {
            return paymentFee.get();
        } else {
            throw new RuntimeException("Configuração de taxa para o método de pagamento " + paymentMethod + " não encontrada.");
        }
    }

    private PaymentFee findById (Long id) {
        Optional<PaymentFee> feeOptional = feeRepository.findById(id);
        if (feeOptional.isPresent()) {
            return feeOptional.get();
        } else {
            throw new RuntimeException("Configuração de taxa com ID " + id + " não encontrada.");
        }
    }
}
