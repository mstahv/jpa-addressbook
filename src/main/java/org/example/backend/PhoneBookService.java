package org.example.backend;

import java.util.List;
import java.util.Random;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.EntityGraph;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Subgraph;

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
        return entryRepo.findByNameLikeIgnoreCase("%" + filter + "%");
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

    public List getGroups() {
        return em.createQuery("SELECT e from PhoneBookGroup e",
                PhoneBookGroup.class).getResultList();
    }

    public PhoneBookEntry refreshEntry(PhoneBookEntry entry) {
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
    private static final String[] names = new String[]{"Younker Patel", "Zollicoffer Robinson", "Zeh Haugen", "Yager Johansen", "Zickefoose Macdonald", "Yerkes Karlsson", "Yerby Gustavsson", "Zimple Svensson", "Youmans Stewart", "Zahn Davis", "Zenz Davis", "Zamastil Jackson", "Zamastil Gustavsson", "Zucchero Walker", "Zielke Martin", "Zabowski Carlsson", "Yoes Hansson", "Zuczek Smith", "Zeidler Watson", "Yingling Harris", "Zahn Karlsen", "Zimmermann Olsson", "Zerkey Martin", "Zatovich Andersson", "Yurky Andersson", "Yeary Carlsson", "Yeary Olsen", "Zabowski Olsen", "Zuber Jackson", "Zeim Nilsen"};
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

}
