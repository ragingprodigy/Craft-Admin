package ng.softcom.bespoke.craftadmin.adapters;

import android.content.Context;
import android.view.View;
import android.widget.TextView;

import ng.softcom.bespoke.craftadmin.R;
import ng.softcom.bespoke.craftadmin.models.CAArtisan;
import ng.softcom.bespoke.craftadmin.utils.Craft;

/**
 * Created by oladapo on 03/05/2016.
 * as part of ng.softcom.bespoke.craftadmin.adapters in Craft Admin
 */
public class CAArtisansListAdapter extends CABaseAdapter<CAArtisan, CAArtisansListAdapter.ViewHolder> {

    private int[] backgrounds = {
            R.drawable.oval_bg_red,
            R.drawable.oval_bg_primary,
            R.drawable.oval_bg_accent,
            R.drawable.oval_bg_blue
    };

    private Context context;

    @Override
    protected void bindView(ViewHolder holder, CAArtisan artisan, View rootView) {
        String title = artisan.getFirstName() + " " + artisan.getSurname();
        holder.name.setText(title);

        holder.description.setText(artisan.getBusinessName());

        // Set Backgrounds Here
        String[] parts = artisan.getBusinessName().isEmpty() ? artisan.getFullName().split(" ") : artisan.getBusinessName().split(" ");
        StringBuilder subTextBuilder = new StringBuilder(2);

        new Craft(getContext()).letterViewData(parts, subTextBuilder);

        holder.label.setText(subTextBuilder.toString().toUpperCase());
        holder.badge.setBackgroundResource(backgrounds[currentPosition % backgrounds.length]);
    }

    @Override
    protected ViewHolder createViewHolder(View rootView) {
        return new ViewHolder(rootView);
    }

    @Override
    protected int getLayoutResId() {
        return R.layout.artisans_list_item;
    }

    public Context getContext() {
        return context;
    }

    public void setContext(Context context) {
        this.context = context;
    }

    /**
     * ViewHolder for the List View Item
     */
    static class ViewHolder {
        TextView label, name, description;
        View badge;

        ViewHolder(View itemView) {
            label = (TextView) itemView.findViewById(R.id.label);
            name = (TextView) itemView.findViewById(R.id.title);
            description = (TextView) itemView.findViewById(R.id.description);

            badge = itemView.findViewById(R.id.badge);
        }
    }
}