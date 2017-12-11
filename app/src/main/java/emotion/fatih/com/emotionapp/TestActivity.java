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
import android.widget.Toast;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

public class TestActivity extends AppCompatActivity {

    private TextView startTimeTV,endTimeTV;
    private MediaPlayer mediaPlayer;
    private ImageView playBtn;
    private ImageView nextBtn;
    private ImageView prevBtn;
    private SeekBar songSB;
    private ListView songsLV;
    private TextView moodTV;

    private String mood;

    int myPosition=0;
    int mySongsSize=0;
    private double currentTime=0;
    private double durationTime=0;

    ArrayList<String> mySongsName;
    ArrayList<String> mySongsArtistName;
    ArrayList<String> mySongsDurationTime;
    ArrayList<Integer> mySongsLoc;

    ArrayList<MusicModel> mySongModel;


    private Handler myHandler=new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);


        playBtn =(ImageView)findViewById(R.id.playBtn);
        nextBtn =(ImageView)findViewById(R.id.nextBtn);
        prevBtn =(ImageView)findViewById(R.id.prevBtn);
        startTimeTV   =(TextView)findViewById(R.id.startTimeTV);
        endTimeTV =(TextView)findViewById(R.id.endTimeTV);
        songSB=(SeekBar)findViewById(R.id.songSB);
        songsLV=(ListView)findViewById(R.id.songLV);
        moodTV=(TextView)findViewById(R.id.moodTV);

        mySongsName=new ArrayList<>();
        mySongsArtistName=new ArrayList<>();
        mySongsDurationTime=new ArrayList<>();
        mySongsLoc=new ArrayList<>();

        mySongModel=new ArrayList<MusicModel>();

        Intent intent=getIntent();

        mood=intent.getStringExtra("Deger");

        moodTV.setText("Modunuz : "+mood);


        setMusics();

        CustomAdapter adapter = new CustomAdapter(this,mySongModel);

        songsLV.setAdapter(adapter);

      //  Uri uri =Uri.parse(mySongsLoc.get(myPosition).toString());
        prevBtn.setClickable(false);
        mediaPlayer=MediaPlayer.create(getApplicationContext(),mySongsLoc.get(myPosition));


        songsLV.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {

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
           // Uri uri2 =Uri.parse(mySongsLoc.get(myPosition).toString());
            mediaPlayer=MediaPlayer.create(getApplicationContext(),mySongsLoc.get(myPosition));

            mediaPlayer.start();
            setTimes();
        }else{

            mediaPlayer=MediaPlayer.create(getApplicationContext(),mySongsLoc.get(myPosition));

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

    public void setMusics (){

        if(mood.equals("Mutlu")){

            setLists("Sen Olsan Bari","Aleyna Tilki",R.raw.senolsan,"189000",
                    "Shape Of You","Ed Sheeran",R.raw.shapeof,"263000");

        }
        else if(mood.equals("Nötr")){

            setLists("Lost On You","LP",R.raw.lostonyou,"270000",
                    "Yalan","Manuş Baba",R.raw.yalan,"199000");
        }
        else if(mood.equals("Üzgün")){
            setLists("Unutamam Seni","Koray Avcı",R.raw.unutamamseni,"253000",
                    "Yanımda Sen Olmayınca","Koray Avcı",R.raw.yanimdasen,"241000");
        }else if(mood.equals("Öfkeli")){
            setLists("HardWired","Metallica",R.raw.hardwired,"194000",
                    "Du Hast","Rammstein",R.raw.duhast,"235000");
        }else {

            setLists("Lost On You","LP",R.raw.lostonyou,"270000",
                    "Yalan","Manuş Baba",R.raw.yalan,"199000");
        }


    }

    public void setLists(String sarki1,String sarkici1,int sarkiLoc,String sarki1Dur,
                         String sarki2,String sarkici2,int sarkiLoc2,String sarki2Dur){

        String songName=sarki1;
        String artistName=sarkici1;
        int songLoc=sarkiLoc;
        String songDur=sarki1Dur;

        mySongsName.add(songName);
        mySongsArtistName.add(artistName);
        mySongsLoc.add(songLoc);
        mySongsDurationTime.add(Convertor(songDur));

        mySongModel.add(new MusicModel(songName,artistName,Convertor(songDur),songLoc));

        String songName2=sarki2;
        String artistName2=sarkici2;
        int songLoc2=sarkiLoc2;
        String songDur2=sarki2Dur;

        mySongsName.add(songName2);
        mySongsArtistName.add(artistName2);
        mySongsLoc.add(songLoc2);
        mySongsDurationTime.add(Convertor(songDur2));

        mySongModel.add(new MusicModel(songName2,artistName2,Convertor(songDur2),songLoc2));


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
