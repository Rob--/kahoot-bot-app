package io.github.rob__.kahootbot.Menu;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import io.github.rob__.kahootbot.R;

public class SimpleItem extends DrawerItem<SimpleItem.ViewHolder> {

    private int selectedTextHint;
    private int selectedIconHint;

    private int iconHint;
    private int textHint;

    private Drawable icon;
    private String title;

    public SimpleItem(Drawable icon, String title, Context context) {
        this.icon = icon;
        this.title = title;

        this.iconHint = ContextCompat.getColor(context, R.color.textColorPrimary);
        this.textHint = ContextCompat.getColor(context, R.color.textColorPrimary);

        this.selectedTextHint = ContextCompat.getColor(context, R.color.textColorHighlighted);
        this.selectedIconHint = ContextCompat.getColor(context, R.color.textColorHighlighted);
    }

    @Override
    public ViewHolder createViewHolder(ViewGroup parent) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View v = inflater.inflate(R.layout.item_option, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void bindViewHolder(ViewHolder holder) {
        holder.title.setText(title);
        holder.icon.setImageDrawable(icon);

        holder.title.setTextColor(isChecked ? selectedIconHint : textHint);
        holder.icon.setColorFilter(isChecked ? selectedTextHint : iconHint);
    }

    static class ViewHolder extends DrawerAdapter.ViewHolder {

        private ImageView icon;
        private TextView title;

        public ViewHolder(View itemView) {
            super(itemView);
            icon = (ImageView) itemView.findViewById(R.id.icon);
            title = (TextView) itemView.findViewById(R.id.title);
        }
    }
}