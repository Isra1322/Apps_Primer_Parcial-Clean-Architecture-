package fisei.uta.edu.ec.twittersearches.ui;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputLayout;
import fisei.uta.edu.ec.twittersearches.R;
import fisei.uta.edu.ec.twittersearches.data.repository.SearchRepositoryImpl;
import fisei.uta.edu.ec.twittersearches.domain.model.Search;
import fisei.uta.edu.ec.twittersearches.domain.repository.SearchRepository;
import fisei.uta.edu.ec.twittersearches.utils.UrlBuilder;
import java.util.Collections;
import java.util.List;

/**
 * Actividad principal — capa de presentación (UI).
 *
 * Responsabilidades (SRP):
 *   - Inicializar y coordinar vistas.
 *   - Delegar persistencia a SearchRepository.
 *   - Implementar OnSearchInteractionListener para reaccionar a eventos de la lista.
 *
 * Principios aplicados:
 *   - SRP: lógica de datos fuera de la Activity.
 *   - DIP: depende de SearchRepository (interfaz), no de SharedPreferences.
 *   - DRY: construcción de URL delegada a UrlBuilder.
 */
public class MainActivity extends AppCompatActivity
        implements SearchesAdapter.OnSearchInteractionListener {

    private EditText queryEditText;
    private EditText tagEditText;
    private FloatingActionButton saveButton;

    private SearchRepository searchRepository;
    private List<String> tags;
    private SearchesAdapter adapter;

    // ─── Lifecycle ───────────────────────────────────────────────────────────

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        searchRepository = new SearchRepositoryImpl(this);
        tags = searchRepository.getAllTags();

        setupInputViews();
        setupRecyclerView();
        setupSaveButton();
    }

    // ─── Setup ───────────────────────────────────────────────────────────────

    private void setupInputViews() {
        TextInputLayout queryLayout = findViewById(R.id.queryTextInputLayout);
        TextInputLayout tagLayout = findViewById(R.id.tagTextInputLayout);
        queryEditText = queryLayout.getEditText();
        tagEditText = tagLayout.getEditText();

        TextWatcher watcher = createSaveButtonWatcher();
        queryEditText.addTextChangedListener(watcher);
        tagEditText.addTextChangedListener(watcher);
    }

    private void setupRecyclerView() {
        RecyclerView recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new SearchesAdapter(tags, this);
        recyclerView.setAdapter(adapter);
        recyclerView.addItemDecoration(new ItemDivider(this));
    }

    private void setupSaveButton() {
        saveButton = findViewById(R.id.fab);
        saveButton.setOnClickListener(v -> onSaveButtonClicked());
        updateSaveButtonVisibility();
    }

    // ─── Input helpers ────────────────────────────────────────────────────────

    private TextWatcher createSaveButtonWatcher() {
        return new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {
                updateSaveButtonVisibility();
            }
            @Override public void afterTextChanged(Editable s) {}
        };
    }

    private void updateSaveButtonVisibility() {
        boolean queryFilled = !queryEditText.getText().toString().trim().isEmpty();
        boolean tagFilled   = !tagEditText.getText().toString().trim().isEmpty();

        if (queryFilled && tagFilled) {
            saveButton.show();
        } else {
            saveButton.hide();
        }
    }

    // ─── Save flow ────────────────────────────────────────────────────────────

    private void onSaveButtonClicked() {
        String query = queryEditText.getText().toString().trim();
        String tag   = tagEditText.getText().toString().trim();

        if (query.isEmpty() || tag.isEmpty()) return;

        hideKeyboard();
        saveSearch(tag, query);
        clearInputFields();
    }

    private void saveSearch(String tag, String query) {
        searchRepository.saveSearch(new Search(tag, query));

        if (!tags.contains(tag)) {
            tags.add(tag);
            Collections.sort(tags, String.CASE_INSENSITIVE_ORDER);
            adapter.notifyItemInserted(tags.indexOf(tag));
        }
    }

    private void clearInputFields() {
        queryEditText.setText("");
        tagEditText.setText("");
        queryEditText.requestFocus();
    }

    private void hideKeyboard() {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm != null) {
            imm.hideSoftInputFromWindow(saveButton.getWindowToken(), 0);
        }
    }

    // ─── OnSearchInteractionListener ─────────────────────────────────────────

    @Override
    public void onSearchClick(String tag) {
        String url = buildUrl(tag);
        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(url)));
    }

    @Override
    public void onSearchLongClick(String tag) {
        showSearchOptionsDialog(tag);
    }

    // ─── Dialog ──────────────────────────────────────────────────────────────

    private void showSearchOptionsDialog(String tag) {
        new AlertDialog.Builder(this)
                .setTitle(getString(R.string.share_edit_delete_title, tag))
                .setItems(R.array.dialog_items, (dialog, which) -> handleDialogOption(which, tag))
                .setNegativeButton(getString(R.string.cancel), null)
                .show();
    }

    private void handleDialogOption(int option, String tag) {
        switch (option) {
            case 0: shareSearch(tag);          break;
            case 1: populateFieldsForEdit(tag); break;
            case 2: confirmDeleteSearch(tag);   break;
        }
    }

    // ─── Actions ─────────────────────────────────────────────────────────────

    private void shareSearch(String tag) {
        String url = buildUrl(tag);

        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("text/plain");
        intent.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.share_subject));
        intent.putExtra(Intent.EXTRA_TEXT, getString(R.string.share_message, url));

        startActivity(Intent.createChooser(intent, getString(R.string.share_search)));
    }

    private void populateFieldsForEdit(String tag) {
        tagEditText.setText(tag);
        queryEditText.setText(searchRepository.getQuery(tag));
    }

    private void confirmDeleteSearch(String tag) {
        new AlertDialog.Builder(this)
                .setMessage(getString(R.string.confirm_message, tag))
                .setNegativeButton(getString(R.string.cancel), null)
                .setPositiveButton(getString(R.string.delete), (dialog, id) -> deleteSearch(tag))
                .show();
    }

    private void deleteSearch(String tag) {
        int position = tags.indexOf(tag);
        tags.remove(tag);
        searchRepository.deleteSearch(tag);
        adapter.notifyItemRemoved(position);
    }

    // ─── Utility ─────────────────────────────────────────────────────────────

    private String buildUrl(String tag) {
        return UrlBuilder.buildSearchUrl(
                getString(R.string.search_URL),
                searchRepository.getQuery(tag)
        );
    }
}
