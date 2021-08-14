package adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.weconnect.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;

import java.util.List;

import model.Chat;

public class MessageAdapter extends  RecyclerView.Adapter<MessageAdapter.ViewHolder> {
  private Context mContext;
  private List<Chat>mChats;
  public static final int msg_type_left=0;
  public static final int msg_type_right=1;

FirebaseUser fUser;
    public MessageAdapter(Context mContext, List<Chat> mChats) {
        this.mContext = mContext;
        this.mChats = mChats;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if(viewType==msg_type_right){
        View view= LayoutInflater.from(mContext).inflate(R.layout.chat_item_right,parent,false);

        return new ViewHolder(view);
        }else{
            View view= LayoutInflater.from(mContext).inflate(R.layout.chat_item_left,parent,false);

            return new ViewHolder(view);
        }
    }


    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {

final Chat chat=mChats.get(position);
holder.show_message.setText(chat.getMessage());

holder.show_message.setOnLongClickListener(new View.OnLongClickListener() {
    @Override
    public boolean onLongClick(View v) {

        AlertDialog AD=new AlertDialog.Builder(mContext).create();
        AD.setTitle("Do you want to delete the Message");
        AD.setButton(AlertDialog.BUTTON_NEUTRAL, "No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        AD.setButton(AlertDialog.BUTTON_POSITIVE, "Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(final DialogInterface dialog, int which) {
                FirebaseDatabase.getInstance().getReference().child("Chats").child(chat.getId()).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()){
                            dialog.dismiss();
                            holder.show_message.setVisibility(View.GONE);
                            holder.show_message.setVisibility(View.VISIBLE);
                        }else {}
                    }
                });
            }
        });
        AD.show();



        return true;
    }
});

    }

    @Override
    public int getItemCount() {
        return mChats.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        public TextView show_message;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);

        show_message=itemView.findViewById(R.id.show_message);
        }
    }

    @Override
    public int getItemViewType(int position) {
        fUser= FirebaseAuth.getInstance().getCurrentUser();

        if(mChats.get(position).getSender().equals(fUser.getUid())){
            return msg_type_right;
        }else{
            return msg_type_left;

        }
    }
}





