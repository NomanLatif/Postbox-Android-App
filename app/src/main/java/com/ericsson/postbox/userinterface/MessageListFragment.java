package com.ericsson.postbox.userinterface;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.TextView;

import com.ericsson.postbox.adapter.PostMessagesAdapter;
import com.ericsson.postbox.entity.Messages;
import com.ericsson.postbox.library.DBTools;
import com.ericsson.postbox.userinterface.fragmentutil.FragmentCommunicator;

import static com.ericsson.postbox.shared.Constants.MESSAGE_RECEIVED_BROADCASTER;

public class MessageListFragment extends Fragment implements AbsListView.OnItemClickListener
{
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;
    private String mParam2;

    private FragmentCommunicator mListener;
    private AbsListView mListView;
    private ListAdapter mAdapter;
    private DBTools myDb;
    private PostMessagesAdapter myAdapter;
    private BroadcastReceiver myReceiver;
    private TextView myEmptyText;

    public static MessageListFragment newInstance(String param1, String param2)
    {
        MessageListFragment fragment = new MessageListFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    public MessageListFragment()
    {
    }

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        if (getArguments() != null)
        {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }

        myDb = new DBTools(getActivity().getApplicationContext());
        myAdapter = new PostMessagesAdapter(getActivity(), myDb.getMessages());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        createUserGroupsBroadcastListener();
        View view = inflater.inflate(R.layout.fragment_message, container, false);

        mListView = (AbsListView) view.findViewById(android.R.id.list);
        mListView.setAdapter(myAdapter);

        myEmptyText = (TextView) view.findViewById(R.id.empty_list_text);
        mListView.setOnItemClickListener(this);

        setEmptyTextVisibily();

        return view;
    }

    private void createUserGroupsBroadcastListener()
    {
        myReceiver = new BroadcastReceiver()
        {
            @Override
            public void onReceive(Context context, Intent intent)
            {
                loadGroupsFromSqlite();
            }
        };
    }

    public void setEmptyTextVisibily()
    {
        if (mListView.getCount() > 0)
        {
            mListView.setVisibility(View.VISIBLE);
            myEmptyText.setVisibility(View.GONE);
        }
        else
        {
            mListView.setVisibility(View.GONE);
            myEmptyText.setVisibility(View.VISIBLE);
        }
    }

    private void loadGroupsFromSqlite()
    {
        Messages messages = myDb.getMessages();
        refreshList(messages);
    }

    private void refreshList(Messages messages)
    {
        if (myAdapter == null)
        {
            myAdapter = new PostMessagesAdapter(getActivity(), messages);
        }
        else
        {
            myAdapter.update(messages);
        }
        myAdapter.notifyDataSetChanged();
        setEmptyTextVisibily();
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try
        {
            mListener = (FragmentCommunicator) activity;
        } catch (ClassCastException e)
        {
            throw new ClassCastException(activity.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onResume()
    {
        super.onResume();
        LocalBroadcastManager.getInstance(getActivity()).registerReceiver((myReceiver), new IntentFilter(MESSAGE_RECEIVED_BROADCASTER));
    }

    @Override
    public void onDetach()
    {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onPause()
    {
        LocalBroadcastManager.getInstance(getActivity()).unregisterReceiver(myReceiver);
        super.onPause();
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id)
    {
        if (null != mListener)
        {
            mListener.onFragmentInteraction(null);
        }
    }
}
