package br.edu.cidadesesg.repository;

import br.edu.cidadesesg.model.Categoria;
import br.edu.cidadesesg.model.Iniciativa;
import br.edu.cidadesesg.model.StatusIniciativa;
import org.springframework.data.jpa.repository.JpaRepository;

public interface IniciativaRepository extends JpaRepository<Iniciativa, Long> {
    long countByCategoria(Categoria categoria);
    long countByStatus(StatusIniciativa status);
}
