package fisei.uta.edu.ec.twittersearches.data.repository;

import android.content.Context;
import android.content.SharedPreferences;
import fisei.uta.edu.ec.twittersearches.domain.model.Search;
import fisei.uta.edu.ec.twittersearches.domain.repository.SearchRepository;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Implementación concreta de SearchRepository usando SharedPreferences.
 * Principio OCP: si se necesita migrar a Room o a una API remota,
 * se crea otra implementación sin tocar el resto de la app.
 */
public class SearchRepositoryImpl implements SearchRepository {

    private static final String PREFS_NAME = "searches";
    private final SharedPreferences sharedPreferences;

    public SearchRepositoryImpl(Context context) {
        this.sharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
    }

    @Override
    public void saveSearch(Search search) {
        sharedPreferences.edit()
                .putString(search.getTag(), search.getQuery())
                .apply();
    }

    @Override
    public void deleteSearch(String tag) {
        sharedPreferences.edit()
                .remove(tag)
                .apply();
    }

    @Override
    public String getQuery(String tag) {
        return sharedPreferences.getString(tag, "");
    }

    @Override
    public List<String> getAllTags() {
        List<String> tags = new ArrayList<>(sharedPreferences.getAll().keySet());
        Collections.sort(tags, String.CASE_INSENSITIVE_ORDER);
        return tags;
    }
}
