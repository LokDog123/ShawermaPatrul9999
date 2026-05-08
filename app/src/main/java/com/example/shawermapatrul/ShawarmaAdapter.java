package com.example.shawermapatrul;

import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class ShawarmaAdapter extends RecyclerView.Adapter<ShawarmaAdapter.ViewHolder> {

    private List<Shawerma> shawarmaList;
    private OnItemClickListener onItemClickListener;
    private OnItemLongClickListener onItemLongClickListener;

    public interface OnItemClickListener {
        void onItemClick(Shawerma shawarma);
    }

    public interface OnItemLongClickListener {
        void onItemLongClick(Shawerma shawarma);
    }

    public ShawarmaAdapter(List<Shawerma> shawarmaList,
                           OnItemClickListener clickListener,
                           OnItemLongClickListener longClickListener) {
        this.shawarmaList = shawarmaList;
        this.onItemClickListener = clickListener;
        this.onItemLongClickListener = longClickListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        CardView cardView = (CardView) LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_shawarma, parent, false);
        return new ViewHolder(cardView);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Shawerma shawarma = shawarmaList.get(position);

        holder.tvName.setText("🌯 " + shawarma.getName());
        holder.tvPlaceName.setText("📍 " + shawarma.getPlaceName());
        holder.tvRating.setText(getRatingStars(shawarma.getRating()) + " (" + shawarma.getRating() + "/10)");
        holder.tvPrice.setText("💰 " + shawarma.getPrice() + " BYN");//	 • р. • руб. • Br -
                                                        // значки, которые можно подставить
        holder.tvAddress.setText("🏠 " + shawarma.getAddress());
        holder.tvComment.setText("📝 " + shawarma.getCommet());

        holder.cardView.setOnClickListener(v -> onItemClickListener.onItemClick(shawarma));
        holder.cardView.setOnLongClickListener(v -> {
            onItemLongClickListener.onItemLongClick(shawarma);
            return true;
        });
    }

    private String getRatingStars(int rating) {
        int fullStars = rating / 2;
        StringBuilder stars = new StringBuilder();
        for (int i = 0; i < fullStars; i++) {
            stars.append("⭐");
        }
        return stars.toString();
    }

    @Override
    public int getItemCount() {
        return shawarmaList.size();
    }

    public void updateList(List<Shawerma> newList) {
        this.shawarmaList = newList;
        notifyDataSetChanged();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        CardView cardView;
        TextView tvName, tvPlaceName, tvRating, tvPrice, tvAddress, tvComment;

        public ViewHolder(@NonNull CardView cardView) {
            super(cardView);
            this.cardView = cardView;
            tvName = cardView.findViewById(R.id.tv_name);
            tvPlaceName = cardView.findViewById(R.id.tv_place_name);
            tvRating = cardView.findViewById(R.id.tv_rating);
            tvPrice = cardView.findViewById(R.id.tv_price);
            tvAddress = cardView.findViewById(R.id.tv_address);
            tvComment = cardView.findViewById(R.id.tv_comment);
        }
    }
}