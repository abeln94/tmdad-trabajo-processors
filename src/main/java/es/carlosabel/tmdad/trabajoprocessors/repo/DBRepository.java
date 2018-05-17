package es.carlosabel.tmdad.trabajoprocessors.repo;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DBRepository extends CrudRepository<DBTableRow, Integer> {

}
