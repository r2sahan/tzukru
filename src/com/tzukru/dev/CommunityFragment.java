package com.tzukru.dev;

import android.app.Fragment;
import android.os.Bundle;
import android.text.Editable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import com.tzukru.dev.model.ChannelListItem;
import com.tzukru.dev.util.ChannelManager;

import java.util.ArrayList;
import java.util.List;

public class CommunityFragment extends Fragment {
	
	public CommunityFragment(){}

    private ChannelManager channelManager;
    private ArrayAdapter<ChannelListItem> adapter;
    private List<ChannelListItem> channelList = new ArrayList<ChannelListItem>();

	@Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_community, container, false);

        channelManager = new ChannelManager(rootView.getContext());

        channelList.addAll(channelManager.getChannelList());

        final EditText txtChannel = (EditText) rootView.findViewById(R.id.txt_channel);
        ImageView addChannel = (ImageView) rootView.findViewById(R.id.add_channel);
        addChannel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Editable text = txtChannel.getText();
                String review = text != null ? text.toString() : "";
                if (!review.equals("")) {
                    String size = channelList.size() + "";
                    channelManager.addChannel(size, review);
                    txtChannel.setText("");
                    txtChannel.clearFocus();
                    channelList.add(new ChannelListItem(review, "0"));
                    adapter.notifyDataSetInvalidated();
                }
            }
        });

        adapter = new ArrayAdapter<ChannelListItem>(rootView.getContext(), android.R.layout.simple_list_item_1, channelList) {

            @Override
            public View getView(final int position, View convertView, ViewGroup parent) {
                View vi = convertView;
                if (vi == null)
                    vi = inflater.inflate(R.layout.channel_item, null);

                final ChannelListItem channelListItem = channelList.get(position);
                if (channelListItem == null)
                    return vi;

                final CheckBox channelCheck = (CheckBox) vi.findViewById(R.id.channel_check);
                channelCheck.setText(channelListItem.getText());
                channelCheck.setChecked(channelListItem.getChecked().equals("1"));
                channelCheck.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        channelManager.checkChannel(position, channelCheck.isChecked());
                    }
                });

                ImageView channelDelete = (ImageView) vi.findViewById(R.id.channel_delete);
                channelDelete.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        channelList.remove(channelListItem);
                        channelManager.deleteChannel(position);
                        adapter.notifyDataSetChanged();
                    }
                });

                return vi;
            }
        };

        ListView listView = (ListView) rootView.findViewById(R.id.list2);
        listView.setAdapter(adapter);

        return rootView;
    }

}
