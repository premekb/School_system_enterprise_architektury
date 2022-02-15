package cz.cvut.kbss.ear.project.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;

@Entity
@NamedQueries({
        @NamedQuery(name = "Classroom.findByName", query = "SELECT c FROM Classroom c WHERE c.name = :name")
})
public class Classroom extends AbstractEntity {

    @Column(unique = true)
    private String name;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
