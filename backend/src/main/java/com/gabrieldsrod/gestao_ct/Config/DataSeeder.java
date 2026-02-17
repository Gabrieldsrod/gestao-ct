package com.gabrieldsrod.gestao_ct.Config;

import com.gabrieldsrod.gestao_ct.Model.Categoria;
import com.gabrieldsrod.gestao_ct.Model.Plano;
import com.gabrieldsrod.gestao_ct.Enums.TipoTransacao;
import com.gabrieldsrod.gestao_ct.Repository.CategoriaRepository;
import com.gabrieldsrod.gestao_ct.Repository.PlanoRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Configuration;

import java.math.BigDecimal;

@Configuration
public class DataSeeder implements CommandLineRunner {

    private final PlanoRepository planoRepository;
    private final CategoriaRepository categoriaRepository;

    public DataSeeder(PlanoRepository planoRepository, CategoriaRepository categoriaRepository) {
        this.planoRepository = planoRepository;
        this.categoriaRepository = categoriaRepository;
    }

    @Override
    public void run(String... args) throws Exception {
        // 1. Criar Planos se não existirem
        if (planoRepository.count() == 0) {
            Plano p1 = new Plano();
            p1.setNome("Plano Básico");
            p1.setValorMensalidade(new BigDecimal("99.90"));
            planoRepository.save(p1);

            Plano p2 = new Plano();
            p2.setNome("Plano Premium");
            p2.setValorMensalidade(new BigDecimal("129.90"));
            planoRepository.save(p2);
        }

        // 2. Criar Categoria Obrigatória para o Service funcionar
        if (categoriaRepository.findByNome("Mensalidades").isEmpty()) {
            Categoria cat = new Categoria();
            cat.setNome("Mensalidades");
            cat.setTipo(TipoTransacao.RECEITA);
            categoriaRepository.save(cat);
        }
    }
}
