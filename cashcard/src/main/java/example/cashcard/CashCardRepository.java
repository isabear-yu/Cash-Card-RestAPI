package example.cashcard;

import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

/**
 * CrudRepository is an interface supplied by Spring Data.
 * When we extend it (or other sub-Interfaces of Spring Data's Repository),
 * Spring Boot and Spring Data work together to automatically generate the CRUD methods that we need to interact with a database.
 */
public interface CashCardRepository extends CrudRepository<CashCard, Long>, PagingAndSortingRepository<CashCard, Long> {
    // filter by owner when finding both a single CashCard or a list of CashCards, Spring Data will take care of the actual implementations (writing the SQL queries) for us.
    boolean existsByIdAndOwner(Long id, String owner);
    CashCard findByIdAndOwner(Long id, String owner);
    Page<CashCard> findByOwner(String owner, PageRequest pageRequest);

}
