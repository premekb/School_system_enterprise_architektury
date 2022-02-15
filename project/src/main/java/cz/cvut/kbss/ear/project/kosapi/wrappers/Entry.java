package cz.cvut.kbss.ear.project.kosapi.wrappers;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import cz.cvut.kbss.ear.project.kosapi.links.AtomLink;

import java.io.Serializable;

/**
 * Wraps the actual atom:content resource.
 *
 * @param <T>
 */
@JacksonXmlRootElement(localName = "entry", namespace = "atom")
public class Entry<T> implements Serializable {
    @JacksonXmlElementWrapper(localName = "content", namespace = "atom", useWrapping = false)
    private T content;

    @JacksonXmlProperty(localName = "link", namespace = "atom")
    private AtomLink link;

    public T getContent() {
        return content;
    }

    public AtomLink getLink() {
        return link;
    }
}
