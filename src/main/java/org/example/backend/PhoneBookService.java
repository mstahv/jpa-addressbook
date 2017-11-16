package org.example.backend;

import com.vaadin.data.provider.QuerySortOrder;
import com.vaadin.shared.data.sort.SortDirection;
import java.util.List;
import java.util.Random;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.EntityGraph;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import org.apache.deltaspike.data.api.QueryResult;

/**
 * EJB to hide JPA related stuff from the UI layer.
 */
@Stateless
public class PhoneBookService {

    @Inject
    PhoneBookEntryRepository entryRepo;

    @PersistenceContext(unitName = "customerdb")
    EntityManager em;

    public PhoneBookService() {
    }

    public void save(PhoneBookEntry entry) {
        entryRepo.save(entry);
    }

    public void save(PhoneBookGroup value) {
        if (value.getId() == null) {
            em.persist(value);
        } else {
            em.merge(value);
        }
    }

    public List<PhoneBookEntry> getEntries(String filter) {
        if (filter == null) {
            return entryRepo.findAll();
        }
        return entryRepo.findByNameLikeIgnoreCase("%" + filter + "%").
                getResultList();
    }

    public List<PhoneBookGroup> getGroups(String filter) {
        return em.createNamedQuery("Group.byName", PhoneBookGroup.class).
                setParameter(
                        "filter", "%" + filter.toLowerCase() + "%").
                getResultList();
    }

    public void delete(PhoneBookEntry value) {
        // Hibernate cannot remove detached, reattach...
        entryRepo.remove(entryRepo.findBy(value.getId()));
    }

    public void delete(PhoneBookGroup value) {
        // Hibernate cannot remove detached, reattach...
        value = em.find(PhoneBookGroup.class, value.getId());
        em.remove(value);
    }

    public List<PhoneBookGroup> getGroups() {
        return em.createQuery("SELECT e from PhoneBookGroup e",
                PhoneBookGroup.class).getResultList();
    }

    /**
     * Fetches an instance of given PhoneBookEntry with all lazy 
     * loaded properties loaded.
     * @param entry
     * @return the fully loaded instance
     */
    public PhoneBookEntry loadFully(PhoneBookEntry entry) {
        // To get lazy loaded fields initialized, you have couple of options,
        // all with pros and cons, 3 of them presented here.

        // 1) use an explicit join query (with EntityManager or @Query annotation
        //    in repository method.
        //    em.createQuery("select e from PhoneBookEntry e LEFT JOIN FETCH e.groups where e.id = :id", PhoneBookEntry.class);
        //    ...
        // 2) use EntityGraph's introduced in JPA 2.1, here constructed dynamically
        //    and passed via QueryResult object from DeltaSpike Data. You can 
        //    also use entity graphs with @Query annotation in repositories or
        //    with raw EntityManager API.
        EntityGraph<PhoneBookEntry> graph = this.em.createEntityGraph(
                PhoneBookEntry.class);
        graph.addSubgraph("groups");
        entry = entryRepo.findById(entry.getId())
                .hint("javax.persistence.loadgraph", graph)
                .getSingleResult();

        // 3) ..or use the infamous size() hack that all of us actually do :-)
        entry.getAddresses().size();

        return entry;
    }

    /* Demo data generation... */
    private static final String[] names = new String[]{"Carly Copeland","Angelica Mccarthy","Imani Craig","Adara Frost","Anastasia Ramsey","Yolanda Berry","Elton Nixon","Kiayada Dickson","Sydnee Haney","Yetta Miranda","Hayes Powell","Quincy Adkins","Sage Johns","Rogan Humphrey","Hannah Strong","Kane Ortiz","Gavin Dominguez","Aline Austin","Tyrone White","Cheyenne Bright","Michael Dunn","Ivana Hewitt","Hilel Paul","Pandora Whitaker","Colin Hobbs","Ocean Todd","Pandora Yang","Tad Warner","Daria Glover","Chaney Nash","Akeem Mclaughlin","Orson Lawson","Selma Jones","Ishmael Watts","Judah Petersen","Ivan Benjamin","Colin Small","Graham Willis","Tana Alford","Beverly Gilmore","Samantha Gray","Rogan House","Ila Tate","Sybill Short","Flavia Rocha","Roanna Osborn","Zachary Rowland","Molly Sexton","Ayanna Wooten","Susan Blackburn","Acton Page","Constance Herring","Kellie Meadows","Cameron Dorsey","Myles Mckenzie","Brady Strickland","Iliana Holt","Hadley Fernandez","Noel Oliver","Harrison Herring","Isabella Chavez","Kyla Gay","Joy Gibbs","Vielka Levine","Ora Flynn","Ursula Price","Xaviera Hicks","Cruz Faulkner","Mikayla Serrano","Aquila Reid","Raven Juarez","Mira Bentley","Mason Fleming","Quemby Sanford","Fleur Rivas","Beverly Frank","Regan Pittman","Damon Shaw","Marah Jefferson","Quyn Hoffman","Bo Blair","Cyrus Coleman","Destiny Guzman","Deborah Oneal","Alfonso Hardy","Callie Weiss","Ralph Jefferson","Jordan Fitzpatrick","Keely Brooks","Roary Mercer","Kaseem Holt","Lillith Dale","Silas Barton","Nehru Peck","Maya Beck","Boris Beck","Katelyn Middleton","Lareina Jennings","Destiny Massey","Aquila Paul","Howard Oneil","Hector Carlson","Elliott Singleton","Fulton Bentley","Laith Ramsey","Ivor Robbins","Rooney Waller","Forrest Bailey","Armand Graham","James Velez","Amery Barnett","William Mason","Herman Holmes","Kennedy Thornton","Carl Kim","Richard Weeks","Aquila Noble","Geoffrey Hale","Nehru Sims","Plato Vega","Philip Torres","Nasim Snow","Emerson Reed","Ciaran Gilmore","Fuller Hubbard","Kieran Odom","Griffith Baird","Nasim Farmer","Tad Bolton","Marsden Harper","Elliott Santiago","Kyle Carson","Denton Joyner","Alan Cantrell","Raja Callahan","Demetrius Charles","Richard Fuller","Sawyer Galloway","Abdul Irwin","Felix Cote","Jonah Moody","Stuart Black","Josiah Evans","Howard Solomon","Keane Mercer","Hamish Shaw","Driscoll Dalton","Yuli Walsh","Victor Richardson","Simon Goodwin","Randall Cole","Clarke Conley","Geoffrey Mooney","Kibo Wilkinson","Russell Gilmore","Walker Neal","Reuben Beck","Allen Jacobson","Myles Lucas","Levi Cruz","Walker Carney","Todd Bright","Cairo Davis","Hector Roth","Fletcher Osborne","Oliver Day","Samson Woodward","Xavier Pruitt","Walker Houston","Clinton Turner","Clayton Sharpe","Stephen Clark","Calvin Lowe","Tanner Mendoza","Oren Hoover","Ishmael Quinn","Leonard Castillo","Wesley Curry","Elmo Matthews","Zane Wilcox","Kareem Stark","Basil Cervantes","Linus Morrow","Kane Livingston","Maxwell Carey","Fuller Jacobs","Ali Cohen","Orson Burton","Bruce Austin","Raphael Carter","Cole Holden","Lamar Russo","Uriah Rose","Ezra Hunter","Erich Pearson","Oleg Gutierrez","Jamal Greene","Stewart Contreras","Lester Murray","Hakeem Anthony"};
    private static final String[] groupNames = new String[]{"Collegues", "Friends", "Family", "Students"};

    public void ensureDemoData() {
        if (getGroups().isEmpty()) {

            Random r = new Random(0);

            for (String name : groupNames) {
                em.persist(new PhoneBookGroup(name));
            }
            em.flush();

            List<PhoneBookGroup> groups = getGroups();

            for (String name : names) {
                String[] split = name.split(" ");
                final PhoneBookEntry phoneBookEntry = new PhoneBookEntry(name,
                        "+ 358 555 " + (100 + r.
                        nextInt(900)), split[0].toLowerCase() + "@" + split[1].
                        toLowerCase() + ".com");
                for (int i = r.nextInt(groups.size()); i < groups.size(); i = i + r.
                        nextInt(groups.size()) + 1) {
                    phoneBookEntry.getGroups().add(groups.get(i));
                }
                em.persist(phoneBookEntry);
                em.flush();
            }
        }
    }

    /**
     * Finds a set of entries from database with given filter, starting from
     * given row. The "page size" (aka max results limit passed for the query)
     * is 45.
     *
     * @param filter the filters string
     * @param firstRow the first row to be fetched
     * @param maxresults maximum number of results
     * @return
     */
    public List<PhoneBookEntry> getEntriesPaged(String filter, int firstRow, int maxresults, List<QuerySortOrder> sortOrder) {
        QueryResult<PhoneBookEntry> qr = entryRepo.findByNameLikeIgnoreCase("%" + filter + "%");
        for (QuerySortOrder qso : sortOrder) {
            if(qso.getDirection() == SortDirection.ASCENDING) {
                qr = qr.orderAsc(qso.getSorted());
            } else {
                qr = qr.orderDesc(qso.getSorted());
            }
        }
        return qr
                .firstResult(firstRow).maxResults(maxresults)
                .getResultList();
    }

    /**
     * Finds a number of entries from database with given filter.
     *
     * @param filter
     * @return
     */
    public int countEntries(String filter) {
        return (int) entryRepo.findByNameLikeIgnoreCase("%" + filter + "%").
                count();
    }

}
