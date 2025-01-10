package ord.descartes.geoma.engine;

import java.util.Map;

public interface OSMEntityClassifier {
    OSMEntityClass classify(Map<String, String> tags);
}
