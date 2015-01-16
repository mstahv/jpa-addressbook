package org.example.backend;

import javax.persistence.Entity;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;

/**
 *
 * @author Matti Tahvonen
 */
@Entity
@NamedQueries({
    @NamedQuery(name = "Group.byName", query = "SELECT e FROM PhoneBookGroup AS e WHERE LOWER(e.name) LIKE :filter")
})
public class PhoneBookGroup extends AbstractEntity {

    private String name;

    public PhoneBookGroup(String name) {
        this.name = name;
    }

    public PhoneBookGroup() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "Group{" + "name=" + name + '}';
    }

}
