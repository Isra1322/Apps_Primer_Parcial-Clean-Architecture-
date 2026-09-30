package fisei.uta.edu.ec.twittersearches.domain.model;

/**
 * Modelo de dominio que representa una búsqueda guardada.
 * Inmutable por diseño (Clean Code: objetos de valor).
 */
public class Search {
    private final String tag;
    private final String query;

    public Search(String tag, String query) {
        this.tag = tag;
        this.query = query;
    }

    public String getTag() {
        return tag;
    }

    public String getQuery() {
        return query;
    }
}
