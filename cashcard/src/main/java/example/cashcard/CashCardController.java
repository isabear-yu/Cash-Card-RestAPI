package example.cashcard;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;
import org.springframework.data.repository.PagingAndSortingRepository;
import java.net.URI;
import java.util.*;
import java.security.Principal;



//tells Spring that this class is a Component of type RestController and capable of handling HTTP requests.
@RestController
//indicates which address requests must have to access this Controller.
@RequestMapping("/cashcards")
public class CashCardController {
    private CashCardRepository cashCardRepository;
    public CashCardController(CashCardRepository cashCardRepository) {
        this.cashCardRepository = cashCardRepository;
    }

    private CashCard findCashCard(Long requestedId, Principal principal) {
        return cashCardRepository.findByIdAndOwner(requestedId, principal.getName());
    }

    //The handler methoda, GET requests that match cashcards/{requestedID} will be handled by this method.
    @GetMapping("/{requestedId}")
    //@PathVariable makes Spring Web aware of the requestedId supplied in the HTTP request
    private ResponseEntity<CashCard> findById(@PathVariable Long requestedId, Principal principal) {
        /** calling CrudRepository.findById which returns an Optional. This smart object might or might not contain the CashCard for which we're searching. */
        //Optional<CashCard> cashCardOptional = cashCardRepository.findById(requestedId);

        /**The Principal holds our user's authenticated, authorized information. principal.getName() will return the username provided from Basic Auth.*/
        //Optional<CashCard> cashCardOptional = Optional.ofNullable(cashCardRepository.findByIdAndOwner(requestedId, principal.getName()));

        //CashCard cashCard = cashCardRepository.findByIdAndOwner(requestedId, principal.getName());
        CashCard cashCard = findCashCard(requestedId, principal);
        /**
         * If cashCardOptional.isPresent() is true then the repository successfully found the CashCard and we can retrieve it with cashCardOptional.get().
         * If not, the repository has not found the CashCard.
         */
//        if (cashCardOptional.isPresent()) {
//            return ResponseEntity.ok(cashCardOptional.get());
//        } else {
//            return ResponseEntity.notFound().build();
//        }

        if (cashCard != null) {
            return ResponseEntity.ok(cashCard);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping
    // the POST expects a request "body". This contains the data submitted to the API
    // UriComponentsBuilder ucb is a method argument to this POST handler method and it was automatically passed in. It was injected from Spring's IoC Container.
    private ResponseEntity createCashCard(@RequestBody CashCard newCashCardRequest, UriComponentsBuilder ucb, Principal principal) {

        /** use the provided Principal to ensure that the correct owner is saved with the new CashCard */
        CashCard cashCardWithOwner = new CashCard(null, newCashCardRequest.amount(), principal.getName());
        CashCard savedCashCard = cashCardRepository.save(cashCardWithOwner);

        //saves a new CashCard, and returns the saved object with a unique id provided by the database
        //CashCard savedCashCard = cashCardRepository.save(newCashCardRequest);

        /**
         * Spring Web will deserialize the data into a CashCard. This is constructing a URI to the newly created CashCard.
         * This is the URI that the caller can then use to GET the newly-created CashCard.
         * savedCashCard.id is used as the identifier, which matches the GET endpoint's specification of cashcards/<CashCard.id>.
         */
        URI locationofNewCashCard =ucb
                .path("cashcards/{id}")
                .buildAndExpand(savedCashCard.id())
                .toUri();
        /**
         * ResponseEntity.created(uriOfCashCard) method to create the above response.
         * This method requires you to specify the location, ensures the Location URI is well-formed (by using the URI class),
         * adds the Location header, and sets the Status Code for you.
         */

        return ResponseEntity.created(locationofNewCashCard).build();
    }

    /**
     * Use one of Spring Data's built-in implementations: CrudRepository.findAll().
     * CashCardRepository will automatically return all CashCard records from the database when findAll() is invoked.
     */
//    @GetMapping
//    public ResponseEntity<Iterable<CashCard>> findAll() {
//        return ResponseEntity.ok(cashCardRepository.findAll());
//    }

    @GetMapping
    /** Since we specified the URI parameters of page=0&size=1, pageable will contain the values we need.*/
    private ResponseEntity<List<CashCard>> findAll(Pageable pageable, Principal principal) {
        //Page<CashCard> page = cashCardRepository.findAll(
        Page<CashCard> page = cashCardRepository.findByOwner(principal.getName(),

                PageRequest.of(
                        pageable.getPageNumber(),
                        pageable.getPageSize(),
                        /** The getSort() method extracts the sort query parameter from the request URI. */
                        //pageable.getSort()
                        /**
                         * getSortOr() method provides default values for the page, size, and sort parameters.
                         * Spring provides the default page and size values (they are 0 and 20, respectively).
                         * A default of 20 for page size explains why all three of our Cash Cards were returned.
                         */
                        pageable.getSortOr(Sort.by(Sort.Direction.ASC, "amount"))
                )
        );
        return ResponseEntity.ok(page.getContent());
    }

    /**
     * The @PutMapping supports the PUT verb and supplies the target requestedId.
     * The @RequestBody contains the updated CashCard data.
     * added the Principal as a method argument, provided automatically by Spring Security
     * Return an HTTP 204 NO_CONTENT response code for now, just to get started.
     */
    @PutMapping("/{requestedId}")
    private ResponseEntity<Void> putCashCard(@PathVariable Long requestedId, @RequestBody CashCard cashCardUpdate, Principal principal) {
        /**
         * scope our retrieval of the CashCard to the submitted requestedId and Principal (provided by Spring Security)
         * to ensure only the authenticated, authorized owner may update this CashCard
         */
        //CashCard cashCard = cashCardRepository.findByIdAndOwner(requestedId, principal.getName());

        CashCard cashCard = findCashCard(requestedId, principal);

        /**
         * build a CashCard with updated values and save it.
         */
        if (cashCard != null) {
            CashCard updatedCashCard = new CashCard(cashCard.id(), cashCardUpdate.amount(), principal.getName());
            cashCardRepository.save(updatedCashCard);
            return ResponseEntity.noContent().build();
        }

        return ResponseEntity.notFound().build();
    }

    /**
     * use the @DeleteMapping with the "{id}" parameter, which Spring Web matches to the id method parameter.
     * CashCardRepository already has the method we need: deleteById() (it's inherited from CrudRepository).
     */
    @DeleteMapping("/{id}")
    private ResponseEntity<Void> deleteCashCard(@PathVariable Long id,  Principal principal) {


        if (cashCardRepository.existsByIdAndOwner(id, principal.getName())) {
            cashCardRepository.deleteById(id);
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }






}
