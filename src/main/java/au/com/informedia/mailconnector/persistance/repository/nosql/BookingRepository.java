package au.com.informedia.mailconnector.persistance.repository.nosql;

import au.com.informedia.mailconnector.persistance.domain.document.BookingDocument;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BookingRepository extends MongoRepository<BookingDocument, String> {

}
