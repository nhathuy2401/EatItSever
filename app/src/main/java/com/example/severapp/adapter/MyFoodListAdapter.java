package com.example.severapp.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.severapp.Common.Common;
import com.example.severapp.Model.FoodModel;
import com.example.severapp.R;
import com.example.severapp.callback.IRecyclerClickListener;

import org.greenrobot.eventbus.EventBus;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class MyFoodListAdapter  extends RecyclerView.Adapter<MyFoodListAdapter.MyViewHolder> {
    private Context context ;
    private List<FoodModel> foodModelList ;

    public MyFoodListAdapter(Context context, List<FoodModel> foodModelList) {
        this.context = context;
        this.foodModelList = foodModelList;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new MyViewHolder( LayoutInflater.from( context )
                .inflate( R.layout.layout_food_item,parent,false));

    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {

        Glide.with( context ).load( foodModelList.get( position ).getImage() ).into( holder.img_food_image );
        holder.txt_food_price.setText( new StringBuilder( "$" )
                .append( foodModelList.get( position ).getPrice() ));
        holder.txt_food_name.setText( new StringBuilder(  )
                .append( foodModelList.get( position ).getName() ));

        holder.setListener( (view, pos) -> {
            Common.selectedFood = foodModelList.get( pos );


        } );




    }

    @Override
    public int getItemCount() {
        return foodModelList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private Unbinder unbinder ;
        @BindView( R.id.txt_food_name )
        TextView txt_food_name;
        @BindView( R.id.txt_food_price )
        TextView txt_food_price;
        @BindView( R.id.img_food_image )
        ImageView img_food_image ;


        IRecyclerClickListener listener ;

        public void setListener(IRecyclerClickListener listener) {
            this.listener = listener;
        }

        public MyViewHolder(@NonNull View itemView) {
            super( itemView );
            unbinder = ButterKnife.bind( this,itemView );
            itemView.setOnClickListener(this );
        }

        @Override
        public void onClick(View view) {
            listener.onItemClickListener( view,getAdapterPosition() );

        }
    }
}

