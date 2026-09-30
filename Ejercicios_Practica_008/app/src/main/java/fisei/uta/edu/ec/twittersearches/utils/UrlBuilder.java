package fisei.uta.edu.ec.twittersearches.utils;

import android.net.Uri;

/**
 * Utilidad para construir URLs de búsqueda.
 * Elimina la duplicación de construcción de URL (DRY).
 */
public final class UrlBuilder {

    private UrlBuilder() {
        // Clase utilitaria — no instanciar
    }

    public static String buildSearchUrl(String baseUrl, String query) {
        return baseUrl + Uri.encode(query, "UTF-8");
    }
}
