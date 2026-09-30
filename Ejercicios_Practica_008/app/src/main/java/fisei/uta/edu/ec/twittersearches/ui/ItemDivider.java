package fisei.uta.edu.ec.twittersearches.ui;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.view.View;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

/**
 * Decorador de ítem para el RecyclerView que dibuja el divisor del tema del sistema.
 * Movido a la capa ui junto con los demás componentes de presentación.
 */
public class ItemDivider extends RecyclerView.ItemDecoration {
    private final Drawable divider;

    public ItemDivider(Context context) {
        int[] attrs = {android.R.attr.listDivider};
        TypedArray typedArray = context.obtainStyledAttributes(attrs);
        divider = typedArray.getDrawable(0);
        typedArray.recycle();
    }

    @Override
    public void onDrawOver(@NonNull Canvas c, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
        super.onDrawOver(c, parent, state);

        int left = parent.getPaddingLeft();
        int right = parent.getWidth() - parent.getPaddingRight();

        for (int i = 0; i < parent.getChildCount() - 1; ++i) {
            View item = parent.getChildAt(i);
            RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) item.getLayoutParams();

            int top = item.getBottom() + params.bottomMargin;
            int bottom = top + divider.getIntrinsicHeight();

            divider.setBounds(left, top, right, bottom);
            divider.draw(c);
        }
    }
}
