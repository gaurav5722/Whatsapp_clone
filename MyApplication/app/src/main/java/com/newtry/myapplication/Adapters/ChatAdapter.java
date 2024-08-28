package com.newtry.myapplication.Adapters;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.newtry.myapplication.Models.MessageModel;
import com.newtry.myapplication.R;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

public class ChatAdapter extends RecyclerView.Adapter{
    ArrayList<MessageModel> messageModel;
   Context context;
   String receiverId;

int SENDER_VIEW_TYPE  =1;     //the user signin currently is sender
int RECEIVER_VIEW_TYPE = 2;    //the other users are receiver


    public ChatAdapter(ArrayList<MessageModel> messageModel, Context context,String receiverId) {
        this.messageModel = messageModel;
        this.context = context;
        this.receiverId = receiverId;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
      if(viewType==SENDER_VIEW_TYPE)
{
     View view= LayoutInflater.from(context).inflate(R.layout.sample_sender,parent,false);
    return new SenderViewHolder(view);
}else{

          View view= LayoutInflater.from(context).inflate(R.layout.sample_receiver,parent,false);
          return new ReceiverViewHolder(view);
      }

    }

    @Override
    public int getItemViewType(int position) {


      if((messageModel.get(position).getuId()).equals(FirebaseAuth.getInstance().getUid()))
{
   return SENDER_VIEW_TYPE;
}else
{
return RECEIVER_VIEW_TYPE;
}

    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
      MessageModel messageModeling= messageModel.get(position);

      holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
          @Override
          public boolean onLongClick(View view) {
              new AlertDialog.Builder(context)
                      .setTitle("Delete the message")
                      .setMessage("Are you sure you want to delete the message")
                      .setPositiveButton("yes", new DialogInterface.OnClickListener() {
                          @Override
                          public void onClick(DialogInterface dialogInterface, int i) {
                              FirebaseDatabase database = FirebaseDatabase.getInstance();
                              String senderRoom = FirebaseAuth.getInstance().getUid() +receiverId;

                              if(receiverId == null)
                              {
                                  Toast.makeText(context, "successfully deleted", Toast.LENGTH_SHORT).show();
                                  database.getReference().child("Group Chat")
                                          .child(messageModeling.getMessageId())
                                          .setValue(null);
                              }
//                              Toast.makeText(context, senderRoom, Toast.LENGTH_SHORT).show();
//                              Toast.makeText(context, messageModeling.getMessageId(), Toast.LENGTH_SHORT).show();
                              else {
                                  database.getReference().child("chats").child(senderRoom)
                                          .child(messageModeling.getMessageId())
                                          .setValue(null);
                              }
                          }
                      })
                      .setNegativeButton("No", new DialogInterface.OnClickListener() {
                          @Override
                          public void onClick(DialogInterface dialogInterface, int i) {
                             dialogInterface.dismiss();
                          }
                      }).show();

              return false;
          }
      });
        Calendar calendar = Calendar.getInstance();
        TimeZone tz = TimeZone.getDefault();
        calendar.add(Calendar.MILLISECOND, tz.getOffset(calendar.getTimeInMillis()));
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());


if(holder.getClass()== SenderViewHolder.class)
   {  ((SenderViewHolder)holder).senderMsg.setText(messageModeling.getMessage());
       java.util.Date currenTimeZone=new java.util.Date( messageModeling.getTimestamp());
       ((SenderViewHolder)holder).senderTime.setText(sdf.format(currenTimeZone));
    }else{
    ((ReceiverViewHolder)holder).receiverMsg.setText(messageModeling.getMessage());

    java.util.Date currenTimeZone=new java.util.Date( messageModeling.getTimestamp());
//    Toast.makeText(context, sdf.format(currenTimeZone), Toast.LENGTH_SHORT).show();
    ((ReceiverViewHolder)holder).receiverTime.setText(sdf.format(currenTimeZone));

}
}
    @Override
    public int getItemCount() {
        return messageModel.size();
    }





    public class ReceiverViewHolder extends RecyclerView.ViewHolder{

        TextView receiverMsg,receiverTime;
        public ReceiverViewHolder(@NonNull View itemView) {
            super(itemView);
            receiverMsg =itemView.findViewById(R.id.tvRvMessage);
            receiverTime = itemView.findViewById(R.id.tvRvtime);
        }
    }
    public class SenderViewHolder extends RecyclerView.ViewHolder{

        TextView senderMsg,senderTime;
        public SenderViewHolder(@NonNull View itemView) {
            super(itemView);
            senderMsg =itemView.findViewById(R.id.tvSenderMessage);
            senderTime = itemView.findViewById(R.id.tvStime);
        }
    }
}
