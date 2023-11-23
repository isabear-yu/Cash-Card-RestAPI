package example.cashcard;
import org.springframework.data.annotation.Id;
//@Id annotation to specify the primary key of an entity.
public record CashCard(@Id Long id, Double amount, String owner) {
}