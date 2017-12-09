package emotion.fatih.com.emotionapp;

        import android.app.Activity;
        import android.content.Context;
        import android.view.LayoutInflater;
        import android.view.View;
        import android.view.ViewGroup;
        import android.widget.BaseAdapter;
        import android.widget.TextView;

        import java.util.List;

/**
 * Created by A on 12/8/2017.
 */

public class CustomAdapter extends BaseAdapter {
    private LayoutInflater myLayoutInflater;
    private List<MusicModel> mMusicModel;



    public CustomAdapter(Activity activity, List<MusicModel> music){
        myLayoutInflater = (LayoutInflater) activity.getSystemService(
                Context.LAYOUT_INFLATER_SERVICE);
        mMusicModel=music;
    }

    @Override
    public int getCount() {
        return mMusicModel.size();
    }

    @Override
    public Object getItem(int position) {
        return mMusicModel.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View lineView;
        lineView=myLayoutInflater.inflate(R.layout.line_layout,null);
        TextView songTextV= (TextView) lineView.findViewById(R.id.txtSong);
        TextView artistTextV= (TextView) lineView.findViewById(R.id.txtArtist);
        TextView durationTextV= (TextView) lineView.findViewById(R.id.txtDuration);
        MusicModel musicModel=mMusicModel.get(position);
        songTextV.setText(musicModel.getSong());
        artistTextV.setText(musicModel.getArtist());
        durationTextV.setText( musicModel.getDurationTime());

        return lineView;
    }
}