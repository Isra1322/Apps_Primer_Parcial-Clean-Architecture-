package fisei.uta.edu.ec.twittersearches.domain.repository;

import fisei.uta.edu.ec.twittersearches.domain.model.Search;
import java.util.List;

/**
 * Contrato de acceso a datos para búsquedas guardadas.
 * Principio DIP: las capas superiores dependen de esta abstracción,
 * no de la implementación concreta (SharedPreferences, Room, etc.).
 */
public interface SearchRepository {
    void saveSearch(Search search);
    void deleteSearch(String tag);
    String getQuery(String tag);
    List<String> getAllTags();
}
