package com.example.alejandro.practica7pmdmreproductoraudio;

import android.content.Intent;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Environment;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.IOException;
import java.util.Date;


public class Grabar extends ActionBarActivity {

    private ImageButton ibGrabando;
    private TextView tvGrabando;

    private MediaRecorder grabador;
    private File carpeta;
    private boolean guardar=false;
    private String nombre="";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_grabar);

        ibGrabando = (ImageButton)findViewById(R.id.ibGrabando);
        tvGrabando = (TextView)findViewById(R.id.tvGrabando);
        grabador = new MediaRecorder();

        carpeta = new File(Environment.getExternalStoragePublicDirectory("Grabaciones").toString());
        if(carpeta.exists()){
        }else{
            carpeta.mkdir();
        }

        ibGrabando.setOnTouchListener(new View.OnTouchListener() {
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        Log.v("prueba", "1");
                        grabar();

                        Picasso.with(getApplicationContext()).load(R.drawable.microfono2).into(ibGrabando);
                        tvGrabando.setText(getString(R.string.grabando));
                        break;
                    case MotionEvent.ACTION_UP:
                        Log.v("prueba", "2");
                        parar();

                        Picasso.with(getApplicationContext()).load(R.drawable.microfono1).into(ibGrabando);
                        tvGrabando.setText("");
                        break;
                }
                return true;
            }
        });
    }


    public void volver(View v){
        Intent i = new Intent(getApplicationContext(), ListaCanciones.class);
        startActivity(i);
    }
    @Override
    public void onBackPressed() {
        Intent i = new Intent(getApplicationContext(), ListaCanciones.class);
        startActivity(i);
    }

    public void grabar(){
        Date d=new Date();
        nombre=(d.getYear() + 1900) + "_" + (d.getMonth() + 1) + "_" + d.getDate() + "_" + d.getHours() + "_" + d.getMinutes() + "_" + d.getSeconds();
        grabador.setAudioSource(MediaRecorder.AudioSource.MIC);
        grabador.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
        grabador.setOutputFile(Environment.getExternalStoragePublicDirectory("Grabaciones") + "/" + nombre + ".mp3");
        grabador.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
        try {
            grabador.prepare();
            grabador.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public void parar(){
        grabador.stop();
        grabador.release();

        Intent intent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        File archivo = new File(Environment.getExternalStoragePublicDirectory("Grabaciones") + "/"+nombre+".mp3");
        Uri uri = Uri.fromFile(archivo);
        intent.setData(uri);
        this.sendBroadcast(intent);
    }


}
