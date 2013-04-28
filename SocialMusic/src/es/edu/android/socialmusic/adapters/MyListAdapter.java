package es.edu.android.socialmusic.adapters;

import java.util.ArrayList;
import java.util.List;

import com.echonest.api.v4.Song;

import es.edu.android.socialmusic.R;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class MyListAdapter extends ArrayAdapter<Song> {
	Context mContext;
	int mLayoutResourceId;
	List<Song> data = null;

	public MyListAdapter(Context context, int layoutResourceId, List<Song> lst) {
		super(context, layoutResourceId, lst);
		mContext = context;
		mLayoutResourceId = layoutResourceId;
		data = lst;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View row = convertView;
		SongHolder holder = null;
		
		if (row == null) {
			LayoutInflater inflater = ((Activity) mContext).getLayoutInflater();
			row = inflater.inflate(mLayoutResourceId, parent, false);
			
			holder = new SongHolder();
			holder.txtSong = (TextView) row.findViewById(R.id.txtSongTitle);
			holder.txtArtist = (TextView) row.findViewById(R.id.txtArtistTitle);
			
			row.setTag(holder);
		}
		else {
			holder = (SongHolder) row.getTag();
		}
		
		Song song = data.get(position);
		holder.txtSong.setText(song.getTitle());
		holder.txtArtist.setText(song.getArtistName());
		
		return row;
	}

	
	static class SongHolder {
		TextView txtSong;
		TextView txtArtist;
	}
	
}
