package cz.cvut.kbss.ear.project.kosapi.links;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;

import java.io.Serializable;
import java.util.Objects;

public class AtomLink implements Comparable<AtomLink>, Serializable {
    @JacksonXmlProperty(isAttribute = true, localName = "href", namespace = "xlink")
    protected String url;

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AtomLink that = (AtomLink) o;
        return Objects.equals(url, that.url);
    }

    @Override
    public int hashCode() {
        return Objects.hash(url);
    }

    @Override
    public String toString() {
        return "AtomLink{" +
                "url='" + url + '\'' +
                '}';
    }

    @Override
    public int compareTo(AtomLink o) {
        return this.url.equals(o.url) ? 0 : 1;
    }
}
