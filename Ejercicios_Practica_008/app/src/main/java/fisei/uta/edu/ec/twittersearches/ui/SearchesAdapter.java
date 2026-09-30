package fisei.uta.edu.ec.twittersearches.ui;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import fisei.uta.edu.ec.twittersearches.R;
import java.util.List;

/**
 * Adaptador para la lista de búsquedas guardadas.
 *
 * Mejoras Clean Code / SOLID aplicadas:
 * - ISP: interfaz propia OnSearchInteractionListener en lugar de listeners genéricos de Android.
 * - SRP: el ViewHolder solo enlaza datos; la Activity maneja la lógica de negocio.
 * - Encapsulamiento: textView es package-private; el binding ocurre en bind().
 * - Rendimiento: notifyItemInserted/Removed en lugar de notifyDataSetChanged.
 */
public class SearchesAdapter extends RecyclerView.Adapter<SearchesAdapter.ViewHolder> {

    /**
     * Interfaz segregada (ISP) para interacciones con cada ítem de la lista.
     */
    public interface OnSearchInteractionListener {
        void onSearchClick(String tag);
        void onSearchLongClick(String tag);
    }

    private final List<String> tags;
    private final OnSearchInteractionListener listener;

    public SearchesAdapter(List<String> tags, OnSearchInteractionListener listener) {
        this.tags = tags;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.bind(tags.get(position), listener);
    }

    @Override
    public int getItemCount() {
        return tags.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView textView;

        ViewHolder(View itemView) {
            super(itemView);
            textView = itemView.findViewById(R.id.textView);
        }

        void bind(String tag, OnSearchInteractionListener listener) {
            textView.setText(tag);
            itemView.setOnClickListener(v -> listener.onSearchClick(tag));
            itemView.setOnLongClickListener(v -> {
                listener.onSearchLongClick(tag);
                return true;
            });
        }
    }
}
