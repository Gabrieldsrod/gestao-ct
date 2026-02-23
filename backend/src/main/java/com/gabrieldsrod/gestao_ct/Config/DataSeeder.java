package com.gabrieldsrod.gestao_ct.Config;

import com.gabrieldsrod.gestao_ct.Model.Category;
import com.gabrieldsrod.gestao_ct.Model.Plan;
import com.gabrieldsrod.gestao_ct.Enums.TransactionType;
import com.gabrieldsrod.gestao_ct.Repository.CategoryRepository;
import com.gabrieldsrod.gestao_ct.Repository.PlanRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Configuration;

import java.math.BigDecimal;

@Configuration
public class DataSeeder implements CommandLineRunner {

    private final PlanRepository planRepository;
    private final CategoryRepository categoryRepository;

    public DataSeeder(PlanRepository planRepository, CategoryRepository categoryRepository) {
        this.planRepository = planRepository;
        this.categoryRepository = categoryRepository;
    }

    @Override
    public void run(String... args) throws Exception {
        // 1. Criar Planos se não existirem
        if (planRepository.count() == 0) {
            Plan p1 = new Plan();
            p1.setName("Plano Básico");
            p1.setPrice(new BigDecimal("99.90"));
            planRepository.save(p1);

            Plan p2 = new Plan();
            p2.setName("Plano Premium");
            p2.setPrice(new BigDecimal("129.90"));
            planRepository.save(p2);
        }

        // 2. Criar Categoria Obrigatória para o Service funcionar
        if (categoryRepository.findByName("Mensalidades").isEmpty()) {
            Category cat = new Category();
            cat.setName("Mensalidades");
            cat.setType(TransactionType.INCOME);
            categoryRepository.save(cat);
        }
    }
}
