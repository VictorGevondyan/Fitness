package com.flycode.jasonfit.activity.adapter.viewHolder;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.flycode.jasonfit.R;
import com.flycode.jasonfit.activity.adapter.OnItemClickListener;
import com.flycode.jasonfit.activity.model.Workout;

import org.zakariya.stickyheaders.SectioningAdapter;

import java.io.IOException;
import java.io.InputStream;

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

    @OnClick(R.id.workout_card_view)
    public void onClick() {
        listener.onItemClick(this);
    }

    public void setupItem(Workout workout) {
        titleTextView.setText(workout.getName());

        try {
            InputStream inputStream = imageView.getContext().getAssets().open(workout.getPicture());
            Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
            imageView.setImageBitmap(bitmap);
        }
        catch(IOException ex) {
            return;
        }
    }
}
