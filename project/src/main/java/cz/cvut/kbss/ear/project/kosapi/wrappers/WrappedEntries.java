package cz.cvut.kbss.ear.project.kosapi.wrappers;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Used when fetching List of atom:entry. The root is an entry node containing list of atom:entry nodes.
 *
 * @param <T> resource type
 */
@JacksonXmlRootElement(localName = "entry", namespace = "atom")
public class WrappedEntries<T> implements Serializable {
    @JacksonXmlElementWrapper(localName = "entry", namespace = "atom", useWrapping = false)
    private Entry<T>[] entry;

    public ArrayList<T> getContentList(){
        ArrayList<T> result = new ArrayList<>();
        if (this.entry == null) return result;
        for (Entry<T> singleEntry : this.entry){
            result.add(singleEntry.getContent());
        }
        return result;
    }

    public List<Entry<T>> unwrap() {
        return Arrays.asList(entry);
    }
}
