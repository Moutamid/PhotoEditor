package app.com.photoeditor;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;


import java.util.List;

public class ImageAdapter extends RecyclerView.Adapter<ImageAdapter.View_Holder> {
    private ImageAdapter.OnitemClickListener mListener;

    public interface OnitemClickListener {
        void onAccept(int position);

        void onReject(int position);

    }

    public void setOnItemClick(ImageAdapter.OnitemClickListener listener) {
        mListener = listener;
    }

    LayoutInflater layoutInflater;
    List<ImageModel> users;


    public ImageAdapter(Context ctx, List<ImageModel> users) {
        this.layoutInflater = LayoutInflater.from(ctx);
        this.users = users;
    }

    @NonNull
    @Override
    public View_Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = layoutInflater.inflate(R.layout.layout_images, parent, false);
        return new View_Holder(view, mListener);
    }

    @Override
    public void onBindViewHolder(@NonNull View_Holder holder, int position) {
        ImageModel currentItem = users.get(position);
        holder.fName.setText(users.get(position).getFileName());
        holder.image.setImageBitmap(users.get(position).getBitmap());



    }

    @Override
    public int getItemCount() {
        return users.size();
    }

    class View_Holder extends RecyclerView.ViewHolder {
        TextView fName;
        ImageView image;
        public View_Holder(@NonNull View itemView, final ImageAdapter.OnitemClickListener listener) {
            super(itemView);
            fName = (TextView) itemView.findViewById(R.id.tvFileName);
            image=itemView.findViewById(R.id.img);


        }
    }
}


