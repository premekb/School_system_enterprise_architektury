package cz.cvut.kbss.ear.project.kosapi.util;

import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.Namespace;
import org.jdom2.input.SAXBuilder;

import org.xml.sax.SAXException;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.*;

@Deprecated // First attempt to parse the api response values manually, replaced by JacksonXML mapper
public class AtomConverter {
    public static List<HashMap<String, String>> getAtomContent(String apiResponse) throws IOException, SAXException, JDOMException {
        SAXBuilder saxBuilder = new SAXBuilder();
        Document document = saxBuilder.build(new ByteArrayInputStream((apiResponse.getBytes())));
        Element root = document.getRootElement();
        List<Element> content_descendants = getContentDescendants(root);
        ArrayList<HashMap<String, String>> result = new ArrayList<>();

        for (int i = 0; i < content_descendants.size(); i++) {
            Element child = content_descendants.get(i);
            result.add(atomContentToHashmap(child));
        }
        return result;
    }

    private static List<Element> getContentDescendants(Element root){
        List<Element> content_descendants = root.getChildren("content", Namespace.getNamespace("http://www.w3.org/2005/Atom"));
        if (content_descendants.size() == 0){
            content_descendants = new ArrayList<>();
            List<Element> entries = root.getChildren("entry", Namespace.getNamespace("http://www.w3.org/2005/Atom"));
            for (Element entry : entries){
                Element content = entry.getChild("content", Namespace.getNamespace("http://www.w3.org/2005/Atom"));
                content_descendants.add(content);
            }
        }

        return content_descendants;
    }

    private static HashMap<String, String> atomContentToHashmap(Element node){
        HashMap<String, String> result = new HashMap<>();
        List<Element> childNodes = node.getChildren();
        for (int i = 0; i < childNodes.size(); i++){
            Element childNode = childNodes.get(i);
            result.put(childNode.getName(), childNode.getValue());
        }

        return result;
    }
}
