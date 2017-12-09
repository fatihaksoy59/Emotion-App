package emotion.fatih.com.emotionapp;


/**
 * Created by A on 12/8/2017.
 */
public class MusicModel {
    private String song;
    private String artist;
    private String durationTime;



    private String mySongLocation;

    public MusicModel(String song,String artist,String durationTime,String mySongLocation){
        super();
        this.song=song;
        this.artist=artist;
        this.durationTime=durationTime;
        this.mySongLocation=mySongLocation;
    }

    public String getSong() {
        return song;
    }

    public void setSong(String song) {
        this.song = song;
    }

    public String getArtist() {
        if(artist==null){
            return null;}
        return artist;
    }

    public void setArtist(String artist) {
        this.artist = artist;

    }

    public String getDurationTime() {
        return durationTime;
    }

    public void setDurationTime(String durationTime) {
        this.durationTime = durationTime;
    }
    public String getMySongLocation() {
        return mySongLocation;
    }

    public void setMySongLocation(String mySongLocation) {
        this.mySongLocation = mySongLocation;
    }

}