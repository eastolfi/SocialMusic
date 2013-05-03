package es.edu.android.socialmusic.entities;

import com.echonest.api.v4.Song;


public class MySong {
	public String artist;
	public String song;
	public String id;
	
	public MySong(String artist, String song, String id) {
		this.artist = artist;
		this.song = song;
		this.id = id;
	}
	public MySong(Song enSong) {
		this.artist = enSong.getArtistName();
		this.song = enSong.getTitle();
		this.id = enSong.getID();
	}

}
