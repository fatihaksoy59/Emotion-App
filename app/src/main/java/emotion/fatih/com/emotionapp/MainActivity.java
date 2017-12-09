package emotion.fatih.com.emotionapp;


import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity {

    private TextView startTimeTV,endTimeTV;
    private MediaPlayer mediaPlayer;
    private ImageView playBtn;
    private ImageView nextBtn;
    private ImageView prevBtn;
    private SeekBar songSB;
    private ListView songsLV;

    int myPosition=0;
    int mySongsSize=0;
    private double currentTime=0;
    private double durationTime=0;

    ArrayList<String> mySongsName;
    ArrayList<String> mySongsArtistName;
    ArrayList<String> mySongsDurationTime;
    ArrayList<String> mySongsLoc;

    ArrayList<MusicModel> mySongModel;


    private Handler myHandler=new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        playBtn =(ImageView)findViewById(R.id.playBtn);
        nextBtn =(ImageView)findViewById(R.id.nextBtn);
        prevBtn =(ImageView)findViewById(R.id.prevBtn);
        startTimeTV   =(TextView)findViewById(R.id.startTimeTV);
        endTimeTV =(TextView)findViewById(R.id.endTimeTV);
        songSB=(SeekBar)findViewById(R.id.songSB);
        songsLV=(ListView)findViewById(R.id.songLV);

        mySongsName=new ArrayList<>();
        mySongsArtistName=new ArrayList<>();
        mySongsDurationTime=new ArrayList<>();
        mySongsLoc=new ArrayList<>();

        mySongModel=new ArrayList<MusicModel>();

        getMusic();

        // ArrayAdapter<String> adp=new ArrayAdapter<>(this,R.layout.songs_layout,R.id.textView,mySongsName);
        // songsLV.setAdapter(adp);

        CustomAdapter adapter = new CustomAdapter(this,mySongModel);

        songsLV.setAdapter(adapter);

        Uri uri =Uri.parse(mySongsLoc.get(myPosition).toString());
        prevBtn.setClickable(false);
        mediaPlayer=MediaPlayer.create(getApplicationContext(),uri);

        songsLV.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                myPosition=position;

                buttonKontrol();
                play();
            }
        });

        songSB.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                mediaPlayer.seekTo(songSB.getProgress());
            }
        });



    }


    public void setTimes(){
        durationTime=mediaPlayer.getDuration();
        currentTime=mediaPlayer.getCurrentPosition();

        songSB.setMax((int) durationTime);
        endTimeTV.setText(String.format("%d min, %d sec",
                TimeUnit.MILLISECONDS.toMinutes((long) durationTime),
                TimeUnit.MILLISECONDS.toSeconds((long) durationTime) -
                        TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.
                                toMinutes((long) durationTime)))
        );

        startTimeTV.setText(String.format("%d min, %d sec",
                TimeUnit.MILLISECONDS.toMinutes((long) currentTime),
                TimeUnit.MILLISECONDS.toSeconds((long) currentTime) -
                        TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.
                                toMinutes((long) currentTime)))
        );

        songSB.setProgress((int)currentTime);
        myHandler.postDelayed(UpdateSongTime,100);
    }

    public void clickBtn(View view){

        try {
            if (!mediaPlayer.isPlaying()) {


                mediaPlayer.start();
                playBtn.setImageResource(R.drawable.pause);

                setTimes();


            } else {
                mediaPlayer.pause();

                playBtn.setImageResource(R.drawable.play);
            }
        }catch (Exception e){
            Log.e("hata",e.getMessage());
        }



    }

    public void play(){
        if(mediaPlayer.isPlaying()){
            mediaPlayer.stop();
            Uri uri2 =Uri.parse(mySongsLoc.get(myPosition).toString());
            mediaPlayer=MediaPlayer.create(getApplicationContext(),uri2);
            mediaPlayer.start();
            setTimes();
        }else{

            Uri uri2 =Uri.parse(mySongsLoc.get(myPosition).toString());
            mediaPlayer=MediaPlayer.create(getApplicationContext(),uri2);
            setTimes();
        }
    }

    public void buttonKontrol(){
        if(mySongsSize-1==myPosition){
            nextBtn.setClickable(false);
        }else{
            nextBtn.setClickable(true);
        }
        if(myPosition<=0){
            prevBtn.setClickable(false);
        }else{
            prevBtn.setClickable(true);
        }
    }

    public void prevBtnClick(View view){
        myPosition--;
        buttonKontrol();
        play();
    }

    public void nextBtnClick(View view){

        myPosition++;
        buttonKontrol();
        play();
    }


    public void getMusic (){
        ContentResolver contentResolver =getContentResolver();
        Uri songUri= MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        Cursor songCursor=contentResolver.query(songUri,null,null,null,null);

        if(songCursor!=null &&songCursor.moveToFirst()){
            int songTitle=songCursor.getColumnIndex(MediaStore.Audio.Media.TITLE);
            int songArtist=songCursor.getColumnIndex(MediaStore.Audio.Media.ARTIST);
            int songLocation=songCursor.getColumnIndex(MediaStore.Audio.Media.DATA);
            int songDuration=songCursor.getColumnIndex(MediaStore.Audio.Media.DURATION);
            do{
                String songLoc = songCursor.getString(songLocation);
                String songTit = songCursor.getString(songTitle);
                String songArt = songCursor.getString(songArtist);
                String songDur= songCursor.getString(songDuration);
                mySongsName.add(songTit);
                if(songArt.length()>30){
                    mySongsArtistName.add(songArt.substring(0,30));
                }else{
                    mySongsArtistName.add(songArt);
                }
                mySongsLoc.add(songLoc);
                mySongsDurationTime.add(Convertor(songDur));

                if(songArt.length()>30){
                    mySongModel.add(new MusicModel(songTit,songArt.substring(0,30),Convertor(songDur),songLoc));
                }else{
                    mySongModel.add(new MusicModel(songTit,songArt,Convertor(songDur),songLoc));
                }

            }while (songCursor.moveToNext());
        }
        mySongsSize=mySongsName.size();
    }

    public String Convertor(String sor){
        double value=Double.parseDouble(sor);
        String duration=  String.format("%d min, %d sec",
                TimeUnit.MILLISECONDS.toMinutes((long) value),
                TimeUnit.MILLISECONDS.toSeconds((long) value) -
                        TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.
                                toMinutes((long) value)));

        return duration;
    }

    private Runnable UpdateSongTime = new Runnable() {
        public void run() {
            if(!mediaPlayer.isPlaying()){

                playBtn.setImageResource(R.drawable.play);
            }
            currentTime = mediaPlayer.getCurrentPosition();
            startTimeTV.setText(String.format("%d min, %d sec",
                    TimeUnit.MILLISECONDS.toMinutes((long) currentTime),
                    TimeUnit.MILLISECONDS.toSeconds((long) currentTime) -
                            TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.
                                    toMinutes((long) currentTime)))
            );

            songSB.setProgress((int)currentTime);
            myHandler.postDelayed(this, 100);
        }
    };
}
