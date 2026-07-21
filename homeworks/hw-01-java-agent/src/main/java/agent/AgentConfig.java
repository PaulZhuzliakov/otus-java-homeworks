package agent;

import org.w3c.dom.Document;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilderFactory;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

// Настройки агента из xml
public class AgentConfig {

    private final List<String> methods;
    private final long durationMillis;

    public AgentConfig(List<String> methods, long durationMillis) {
        this.methods = methods;
        this.durationMillis = durationMillis;
    }

    public static AgentConfig load(String path) throws Exception {
        Document doc = DocumentBuilderFactory.newInstance()
                .newDocumentBuilder()
                .parse(new File(path));

        long seconds = Long.parseLong(
                doc.getElementsByTagName("duration").item(0).getTextContent().trim());

        NodeList nodes = doc.getElementsByTagName("method");
        List<String> methods = new ArrayList<>();
        for (int i = 0; i < nodes.getLength(); i++) {
            methods.add(nodes.item(i).getTextContent().trim());
        }
        return new AgentConfig(methods, seconds * 1000);
    }

    public List<String> getMethods() {
        return methods;
    }

    public long getDurationMillis() {
        return durationMillis;
    }
}
