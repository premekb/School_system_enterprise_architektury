package cz.cvut.kbss.ear.project.model;

import com.sun.istack.NotNull;
import cz.cvut.kbss.ear.project.model.enums.CourseCompletionType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import java.util.Objects;

@Entity
@NamedQueries({
    @NamedQuery(name = "Course.findByCode", query = "SELECT c FROM Course c WHERE c.code = :code")
})
public class Course extends AbstractEntity {

    @NotNull
    private String name;

    @NotNull
    private Integer credits;

    private String description;

    @NotNull
    @Column(unique = true)
    private String code;

    private Integer recommendedSemester;

    @Enumerated(EnumType.STRING)
    private CourseCompletionType completionType;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getCredits() {
        return credits;
    }

    public void setCredits(Integer credits) {
        this.credits = credits;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public Integer getRecommendedSemester() {
        return recommendedSemester;
    }

    public void setRecommendedSemester(Integer recommendedSemester) {
        this.recommendedSemester = recommendedSemester;
    }

    public CourseCompletionType getCompletionType() {
        return completionType;
    }

    public void setCompletionType(CourseCompletionType completionType) {
        this.completionType = completionType;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Course course = (Course) o;
        return Objects.equals(name, course.name) && Objects.equals(credits, course.credits) && Objects.equals(description, course.description) && Objects.equals(code, course.code) && Objects.equals(recommendedSemester, course.recommendedSemester) && completionType == course.completionType;
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, credits, description, code, recommendedSemester, completionType);
    }

    @Override
    public String toString() {
        return "Course{" +
            "name='" + name + '\'' +
            ", credits=" + credits +
            ", description='" + description + '\'' +
            ", code='" + code + '\'' +
            ", recommendedSemester=" + recommendedSemester +
            ", completionType=" + completionType +
            '}';
    }
}
