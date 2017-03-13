package com.flycode.jasonfit.adapter.viewHolder;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.flycode.jasonfit.R;
import com.flycode.jasonfit.adapter.OnItemClickListener;
import com.flycode.jasonfit.model.Workout;
import com.flycode.jasonfit.util.ImageUtil;

import org.zakariya.stickyheaders.SectioningAdapter;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created - Schumakher on  3/9/17.
 */

public class WorkoutItemViewHolder extends SectioningAdapter.ItemViewHolder {
    @BindView(R.id.title) TextView titleTextView;
    @BindView(R.id.image) ImageView imageView;

    private OnItemClickListener listener;

    public static WorkoutItemViewHolder initialize(ViewGroup parent, OnItemClickListener listener) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_workout, parent, false);
        return new WorkoutItemViewHolder(view, listener);
    }

    private WorkoutItemViewHolder(View itemView, OnItemClickListener listener) {
        super(itemView);

        ButterKnife.bind(this, itemView);

        this.listener = listener;
    }

    @OnClick(R.id.item_workout)
    public void onClick() {
        listener.onItemClick(this);
    }

    public void setupItem(Workout workout) {
        titleTextView.setText(workout.getName());
        imageView.setImageBitmap(ImageUtil.getImageBitmap(imageView.getContext(), workout));
    }
}
