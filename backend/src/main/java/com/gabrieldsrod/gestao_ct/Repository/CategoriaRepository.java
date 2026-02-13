package com.gabrieldsrod.gestao_ct.Repository;

import com.gabrieldsrod.gestao_ct.Enums.TipoTransacao;
import com.gabrieldsrod.gestao_ct.Model.Categoria;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CategoriaRepository extends JpaRepository<Categoria, Long> {

    List<Categoria> findByTipo(TipoTransacao tipo);
}
