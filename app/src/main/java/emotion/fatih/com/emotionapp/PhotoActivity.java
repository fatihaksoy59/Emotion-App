package emotion.fatih.com.emotionapp;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.microsoft.projectoxford.emotion.EmotionServiceClient;
import com.microsoft.projectoxford.emotion.EmotionServiceRestClient;
import com.microsoft.projectoxford.emotion.contract.RecognizeResult;
import com.microsoft.projectoxford.emotion.contract.Scores;
import com.microsoft.projectoxford.emotion.rest.EmotionServiceException;


import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class PhotoActivity extends AppCompatActivity {

    EmotionServiceClient client;

    int TAKE_PIC_FROM_GALLERY_CODE=100,REQUEST_PERMISSION_CODE=101,TAKE_PIC_FROM_CAMERA=102;

    Bitmap mBitmap;


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        if(requestCode== REQUEST_PERMISSION_CODE){
            if(grantResults[0]==PackageManager.PERMISSION_GRANTED)
                Toast.makeText(this,"İzin Verildi",Toast.LENGTH_SHORT).show();
            else
                Toast.makeText(this,"İzin Verilmedi",Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo);

        client=new EmotionServiceRestClient(getString(R.string.subscription_key));

        if(checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE )!= PackageManager.PERMISSION_GRANTED&&
                checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) !=PackageManager.PERMISSION_DENIED)
        {
            requestPermissions(new String[]{
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    Manifest.permission.INTERNET
            },REQUEST_PERMISSION_CODE);
        }
        takePhoto();


    }


    public void takePhoto(){
        Intent intent=new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if(intent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(intent,TAKE_PIC_FROM_CAMERA);
        }
    }
    public void takePicFromGallery(){

        Intent intent=new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent,TAKE_PIC_FROM_GALLERY_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode==TAKE_PIC_FROM_GALLERY_CODE){

            Uri selecterdImageUri=data.getData();
            InputStream in=null;

            try {
                in=getContentResolver().openInputStream(selecterdImageUri);
            }catch (FileNotFoundException e){
                e.printStackTrace();
            }

            mBitmap= BitmapFactory.decodeStream(in);
            procesImage();

        }else if(requestCode==TAKE_PIC_FROM_CAMERA){
            Bundle extras=data.getExtras();
            mBitmap=(Bitmap)extras.get("data");
            procesImage();

        }
    }

    public void procesImage(){
        //Image stream e dönüştürüldü
        ByteArrayOutputStream outputStream=new ByteArrayOutputStream();
        mBitmap.compress(Bitmap.CompressFormat.JPEG,100,outputStream);
        final ByteArrayInputStream inputStream =new ByteArrayInputStream(outputStream.toByteArray());

        // Görüntü işleme için asenkron görev yapıldı.


        final  AsyncTask<InputStream,String,List<RecognizeResult>> processAsync=new AsyncTask<InputStream, String, List<RecognizeResult>>() {

            ProgressDialog mDialog=new ProgressDialog(PhotoActivity.this);

            @Override
            protected void onPreExecute() {
                mDialog.show();
            }

            @Override
            protected void onProgressUpdate(String... values) {
                mDialog.setMessage(values[0]);
            }

            @Override
            protected void onPostExecute(List<RecognizeResult> recognizeResults) {
                mDialog.dismiss();
                for(RecognizeResult res: recognizeResults){
                    String status= getEmotion(res);
                    Intent intent=new Intent(getApplicationContext(),TestActivity.class);
                    intent.putExtra("Deger",status);
                    startActivity(intent);

                }
            }

            @Override
            protected List<RecognizeResult> doInBackground(InputStream... inputStreams) {
                publishProgress("Lütfen Bekleyiniz");
                List<RecognizeResult> results=null;

                try {
                    results = client.recognizeImage(inputStreams[0]);
                }catch (EmotionServiceException e){
                    e.printStackTrace();
                }catch (IOException e){
                    e.printStackTrace();
                }
                return results;


            }
        };

        processAsync.execute(inputStream);

    }

    private String getEmotion(RecognizeResult res){
        List<Double> list=new ArrayList<>();
        Scores scores=res.scores;

        list.add(scores.anger);
        list.add(scores.contempt);
        list.add(scores.disgust);
        list.add(scores.happiness);
        list.add(scores.fear);
        list.add(scores.neutral);
        list.add(scores.sadness);
        list.add(scores.surprise);


        //Sort List

        Collections.sort(list);

        double maxNum=list.get(list.size()-1);



        if(maxNum==scores.anger) {
            return "Öfkeli";
        }else if(maxNum==scores.happiness) {
            return "Mutlu";
        }else  if(maxNum==scores.contempt) {
            return "Aşağılık";
        }else if(maxNum==scores.disgust) {
            return "İğrenmiş";
        }else if(maxNum==scores.fear) {
            return "Korkmuş";
        }else if(maxNum==scores.neutral) {
            return "Nötr";
        }else if(maxNum==scores.sadness) {
            return "Üzgün";
        }else if(maxNum==scores.surprise);
        {
            return "Şaşırmış";
        }
    }
}
