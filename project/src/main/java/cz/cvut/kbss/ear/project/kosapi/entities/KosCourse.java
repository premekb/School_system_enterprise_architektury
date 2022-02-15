package cz.cvut.kbss.ear.project.kosapi.entities;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Objects;

public class KosCourse implements Serializable {
    @JacksonXmlProperty
    private String credits;

    @JacksonXmlProperty
    private String[] name;

    @JacksonXmlProperty
    private String code;

    @JacksonXmlProperty
    private String completion;

    @JacksonXmlElementWrapper(localName = "instance", useWrapping = false)
    private KosCourseInstance instance;

    public String getCredits() {
        return credits;
    }

    public void setCredits(String credits) {
        this.credits = credits;
    }

    public String getName() {
        return name[1];
    }

    public void setName(String[] name) {
        this.name = name;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getCompletion() {
        return completion;
    }

    public void setCompletion(String completion) {
        this.completion = completion;
    }

    public KosCourseInstance getInstance() {
        return instance;
    }

    public void setInstance(KosCourseInstance instance) {
        this.instance = instance;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        KosCourse kosCourse = (KosCourse) o;
        return Objects.equals(credits, kosCourse.credits) && Arrays.equals(name, kosCourse.name) && Objects.equals(code, kosCourse.code) && Objects.equals(completion, kosCourse.completion);
    }

    @Override
    public int hashCode() {
        int result = Objects.hash(credits, code, completion);
        result = 31 * result + Arrays.hashCode(name);
        return result;
    }

    @Override
    public String toString() {
        return "KosCourse{" +
                "credits='" + credits + '\'' +
                ", name=" + Arrays.toString(name) +
                ", code='" + code + '\'' +
                ", completion='" + completion + '\'' +
                '}';
    }
}
