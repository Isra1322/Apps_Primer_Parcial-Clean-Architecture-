package fisei.uta.edu.ec.addressbook;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.view.View;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class ItemDivider extends RecyclerView.ItemDecoration {
    private final Drawable divider;

    @SuppressLint("ResourceType")
    public ItemDivider(Context context) {
        int[] attrs = {android.R.attr.listDivider};
        try (TypedArray a = context.obtainStyledAttributes(attrs)) {
            divider = a.getDrawable(0);
        }
    }

    @Override
    public void onDrawOver(@NonNull Canvas c, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
        int left = parent.getPaddingLeft();
        int right = parent.getWidth() - parent.getPaddingRight();

        for (int i = 0; i < parent.getChildCount() - 1; i++) {
            View child = parent.getChildAt(i);
            RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) child.getLayoutParams();
            int top = child.getBottom() + params.bottomMargin;
            int bottom = top + (divider != null ? divider.getIntrinsicHeight() : 0);
            if (divider != null) {
                divider.setBounds(left, top, right, bottom);
                divider.draw(c);
            }
        }
    }
}