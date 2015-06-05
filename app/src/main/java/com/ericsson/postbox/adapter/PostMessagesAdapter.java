package com.ericsson.postbox.adapter;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.ericsson.postbox.entity.Messages;
import com.ericsson.postbox.entity.PostMessage;
import com.ericsson.postbox.entity.User;
import com.ericsson.postbox.entity.Users;
import com.ericsson.postbox.library.AsyncResponse;
import com.ericsson.postbox.library.DBTools;
import com.ericsson.postbox.userinterface.R;

import org.json.JSONArray;

/**
 * Created by root on 5/22/15.
 */
public class PostMessagesAdapter extends BaseAdapter
{
    private Activity myActivity;
    private Messages myMessages;
    private LayoutInflater myInflater;

    public PostMessagesAdapter(Activity activity, Messages messages)
    {
        myActivity = activity;
        myMessages = messages;
    }


    public void update(Messages messages)
    {
        myMessages = messages;
    }

    @Override
    public int getCount()
    {
        return myMessages.size();
    }

    @Override
    public Object getItem(int position)
    {
        return myMessages.get(position);
    }

    @Override
    public long getItemId(int position)
    {
        return myMessages.get(position).getId();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {
        if (myInflater == null)
        {
            myInflater = (LayoutInflater) myActivity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        if (convertView == null)
        {
            convertView = myInflater.inflate(R.layout.messages_row, null);
        }
        TextView messageId = (TextView) convertView.findViewById(R.id.message_id);
        TextView messageDateTime = (TextView) convertView.findViewById(R.id.message_date_time);
        TextView delete = (TextView) convertView.findViewById(R.id.delete);

        PostMessage message = myMessages.get(position);
        messageId.setText("" + message.getId());
        messageDateTime.setText(message.getDateTime());

        delete.setOnClickListener(new DeleteClickListener(message.getId()));

        return convertView;
    }

    private class DeleteClickListener implements View.OnClickListener
    {

        private final long myMessageId;

        public DeleteClickListener(long messageId)
        {
            myMessageId = messageId;
        }

        @Override
        public void onClick(View rejectTxtView)
        {
            DBTools db = new DBTools(myActivity.getApplicationContext());
            db.deleteMessage(myMessageId);
            myMessages = db.getMessages();
            notifyDataSetChanged();

//            FriendRequestFunctions friendRequestFunctions = new FriendRequestFunctions(myActivity.getApplicationContext(), this);
//            friendRequestFunctions.reject(mySenderId);
//            friendRequestFunctions.closeDb();
//            ((TextView) rejectTxtView).setText(R.string.friend_request_rejected);
//            myAcceptTxtView.setVisibility(View.GONE);
        }
    }
}
