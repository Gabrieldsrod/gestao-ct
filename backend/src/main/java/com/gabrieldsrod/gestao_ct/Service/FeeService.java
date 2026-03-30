package com.gabrieldsrod.gestao_ct.Service;

import com.gabrieldsrod.gestao_ct.Enums.PaymentMethod;
import com.gabrieldsrod.gestao_ct.Model.PaymentFee;
import com.gabrieldsrod.gestao_ct.Repository.FeeRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Optional;

@Service
public class FeeService {

    private final FeeRepository feeRepository;
    public FeeService(FeeRepository feeRepository) {
        this.feeRepository = feeRepository;
    }

    public BigDecimal calculateFee(PaymentMethod paymentMethod, BigDecimal grossAmount) {
        PaymentFee feeConfig = findPaymentFeeByPaymentMethod(paymentMethod);

        BigDecimal percentageFeeAmount = grossAmount
                .multiply(feeConfig.getPercentageFee())
                .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);
        return percentageFeeAmount.add(feeConfig.getFixedFee());
    }

    private PaymentFee findPaymentFeeByPaymentMethod(PaymentMethod paymentMethod) {
        Optional<PaymentFee> paymentFee = feeRepository.findPaymentFeeByPaymentMethod(paymentMethod);
        if (paymentFee.isPresent()) {
            return paymentFee.get();
        } else {
            throw new RuntimeException("Configuração de taxa para o método de pagamento " + paymentMethod + " não encontrada.");
        }
    }
}
