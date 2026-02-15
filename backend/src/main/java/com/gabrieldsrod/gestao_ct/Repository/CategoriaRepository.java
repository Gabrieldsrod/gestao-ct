package com.gabrieldsrod.gestao_ct.repository;

import com.gabrieldsrod.gestao_ct.enums.TipoTransacao;
import com.gabrieldsrod.gestao_ct.model.Categoria;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CategoriaRepository extends JpaRepository<Categoria, Long> {

    List<Categoria> findByTipo(TipoTransacao tipo);

    Optional<Categoria> findByNome(String nome);
}
