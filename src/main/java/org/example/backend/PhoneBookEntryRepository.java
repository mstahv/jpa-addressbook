
package org.example.backend;

import java.util.List;
import org.apache.deltaspike.data.api.EntityRepository;
import org.apache.deltaspike.data.api.QueryResult;
import org.apache.deltaspike.data.api.Repository;

@Repository
public interface PhoneBookEntryRepository extends EntityRepository<PhoneBookEntry,Long> {

    // DeltaSpike Data automatically implements this method based on naming
    public List<PhoneBookEntry> findByNameLikeIgnoreCase(String string);

    // QueryResult is a special intermedieate result type in DeltaSpike Data,
    // handy for programmatically configuring limits, sorting and other dynamic 
    // query details.
    public QueryResult<PhoneBookEntry> findById(Long id);

}
