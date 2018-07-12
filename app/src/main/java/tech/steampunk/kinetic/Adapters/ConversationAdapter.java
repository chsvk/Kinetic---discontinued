package tech.steampunk.kinetic.Adapters;

import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import pl.droidsonroids.gif.GifImageView;
import tech.steampunk.kinetic.R;
import tech.steampunk.kinetic.data.Message;

import static android.content.Context.MODE_PRIVATE;
import static android.content.Context.POWER_SERVICE;

/**
 * Created by Vamshi on 9/9/2017.
 */

public class ConversationAdapter extends RecyclerView.Adapter<ConversationAdapter.MyViewHolder> {

    private List<Message> conversation;
    Context context;

    public ConversationAdapter(List<Message> conversation, Context context) {
        this.conversation = conversation;
        this.context = context;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        private TextView actual_message, message_time_stamp, message_image_time_stamp;
        private CardView total_Message;
        private CardView image_message, message_card;
        private ImageView image;
        private CircleImageView contains_mood;


        public MyViewHolder(View itemView) {
            super(itemView);
            actual_message = itemView.findViewById(R.id.actual_message);
            message_time_stamp = itemView.findViewById(R.id.message_time_stamp);
            total_Message = itemView.findViewById(R.id.single_message_card);
            image_message = itemView.findViewById(R.id.single_image_card);
            message_image_time_stamp = itemView.findViewById(R.id.message_image_time_stamp);
            image = itemView.findViewById(R.id.actual_image);
            contains_mood = itemView.findViewById(R.id.contains_mood);
            message_card = itemView.findViewById(R.id.message_time_card);
        }
    }

    @Override
    public ConversationAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.single_message, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(ConversationAdapter.MyViewHolder holder, int position) {
        Message m = conversation.get(position);
        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.WRAP_CONTENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT);
        SharedPreferences preferences = context.getSharedPreferences("AUTH", MODE_PRIVATE);
        if(m.getType().equals("Message")){
            if(m.getNumber().equals(preferences.getString("Number",""))){
                holder.image_message.setVisibility(View.GONE);
                layoutParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
                holder.actual_message.setText(m.getMessage());
                holder.message_time_stamp.setText(m.getTime());
                holder.total_Message.setCardBackgroundColor(ContextCompat.getColor(context, R.color.MessageBlue));
                holder.actual_message.setTextColor(Color.WHITE);
                try{
                    if(m.getMood().equals("Neutral")){
                        holder.contains_mood.setVisibility(View.GONE);
                    }else {
                        holder.contains_mood.setVisibility(View.VISIBLE);
                    }
                }catch (Exception e){

                }
            }else if(m.getNumber()==null){
                holder.image_message.setVisibility(View.GONE);
                layoutParams.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
                holder.actual_message.setText(m.getMessage());
                holder.message_time_stamp.setText(m.getTime());
                holder.total_Message.setCardBackgroundColor(ContextCompat.getColor(context, R.color.RecievedBlue));
                holder.actual_message.setTextColor(Color.BLACK);
                try{
                    if(m.getMood().equals("Neutral")){
                        holder.contains_mood.setVisibility(View.GONE);
                    }else {
                        holder.contains_mood.setVisibility(View.VISIBLE);
                    }
                }catch (Exception e){

                }
            }else {
                holder.image_message.setVisibility(View.GONE);
                holder.actual_message.setText(m.getMessage());
                holder.message_time_stamp.setText(m.getTime());
                holder.total_Message.setCardBackgroundColor(ContextCompat.getColor(context, R.color.RecievedBlue));
                layoutParams.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
               try{
                   if(m.getMood().equals("Neutral")){
                       holder.contains_mood.setVisibility(View.GONE);
                   }else {
                       holder.contains_mood.setVisibility(View.VISIBLE);
                   }
               }catch (Exception e){

               }
            }
        }else{
            if(m.getNumber().equals(preferences.getString("Number",""))){
                holder.total_Message.setVisibility(View.GONE);
                holder.image_message.setCardBackgroundColor(ContextCompat.getColor(context, R.color.MessageBlue));
                layoutParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
                Glide.with(context.getApplicationContext()).load(m.getUrl()).into(holder.image);
                holder.message_image_time_stamp.setText(m.getTime());

            }else if(m.getNumber()==null){
                holder.total_Message.setVisibility(View.GONE);
                Glide.with(context.getApplicationContext()).load(m.getUrl()).into(holder.image);
                holder.message_image_time_stamp.setText(m.getTime());
                layoutParams.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
            }else {
                holder.total_Message.setVisibility(View.GONE);
                Glide.with(context.getApplicationContext()).load(m.getUrl()).into(holder.image);
                holder.message_image_time_stamp.setText(m.getTime());
                layoutParams.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
            }
        }

        holder.total_Message.setLayoutParams(layoutParams);
        holder.total_Message.setOnLongClickListener(view -> {
            Dialog dialog = new Dialog(context);
            dialog.setContentView(R.layout.message_mood);
            TextView actual_message_mood = dialog.findViewById(R.id.message_mood_content);
            GifImageView mood_message_gif = dialog.findViewById(R.id.message_mood_animation);
            actual_message_mood.setText(m.getMessage());
           try{
               if(!m.getMood().equals("Neutral")){
                   if(m.getMood().equals("Shades")) {
                       mood_message_gif.setBackgroundResource(R.drawable.emoji_20);
                   }
                   if(m.getMood().equals("Confused")) {
                       mood_message_gif.setBackgroundResource(R.drawable.emoji_5);
                   }
                   if(m.getMood().equals("Happy")) {
                       mood_message_gif.setBackgroundResource(R.drawable.emoji_18);
                   }
                   if(m.getMood().equals("Tensed")) {
                       mood_message_gif.setBackgroundResource(R.drawable.emoji_28);
                   }
                   if(m.getMood().equals("Laughing")) {
                       mood_message_gif.setBackgroundResource(R.drawable.emoji_23);
                   }

               }else {
                   mood_message_gif.setBackgroundResource(R.drawable.sunshine);
               }
           }catch (Exception e){

           }
            dialog.show();
              return false;  }
        
        );
        holder.total_Message.setOnClickListener(view ->{
            if(holder.message_card.getVisibility() == View.VISIBLE){
                holder.message_card.setVisibility(View.GONE);
            }else{
                holder.message_card.setVisibility(View.VISIBLE);
            }
        });

    }

    @Override
    public int getItemCount() {
        return conversation.size();
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }
}
