package com.example.alejandro.practica7pmdmreproductoraudio;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.AudioManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.ParcelFileDescriptor;
import android.provider.MediaStore;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.GestureDetector;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.io.FileDescriptor;
import java.util.ArrayList;


public class Reproductor extends Activity {

    private SeekBar sbVolumen,sbProgreso;
    private ImageButton  ibPause, ibPlay;
    private ImageView ivCaratulaReproductor;
    private TextView tvArtistaReproductor,tvAlbumReproductor,tvTituloReproductor,tvFinalReproductor,tvInicioReproductor;
    private ArrayList<Cancion> canciones;

    private Cancion cancionActual;
    private AudioManager audioManager = null;

    private int id;
    private boolean pausado=false;

    private final String PROGRESO = "progreso";


    private static final int SWIPE_MIN_DISTANCE = 120;
    private static final int SWIPE_MAX_OFF_PATH = 250;
    private static final int SWIPE_THRESHOLD_VELOCITY = 200;
    private GestureDetector gestureDetector;
    View.OnTouchListener gestureListener;





    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reproductor);

        capturar();
        recibirDatos();
        asignarValores();
        volumen();
        movimiento();
        add();
        barraProgreso();

    }



    private void recibirDatos(){
        Bundle b = getIntent().getExtras();
        id = Integer.parseInt(b.getString("id"));
        canciones = b.getParcelableArrayList("canciones");
    }

    private void capturar(){
        sbVolumen= (SeekBar)findViewById(R.id.sbVolumen);
        sbProgreso= (SeekBar)findViewById(R.id.sbProgreso);
        tvArtistaReproductor = (TextView)findViewById(R.id.tvArtistaReproductor);
        tvAlbumReproductor = (TextView)findViewById(R.id.tvAlbumReproductor);
        tvTituloReproductor = (TextView)findViewById(R.id.tvTituloReproductor);
        tvFinalReproductor = (TextView)findViewById(R.id.tvFinalReproductor);
        tvInicioReproductor = (TextView)findViewById(R.id.tvInicioReproductor);
        ivCaratulaReproductor = (ImageView)findViewById(R.id.ivCaratulaReproductor);
        ibPause = (ImageButton)findViewById(R.id.ibPause);
        ibPlay = (ImageButton)findViewById(R.id.ibPlay);

    }

    private void asignarValores(){

        cancionActual =  buscarPorID(id,canciones);

        tvArtistaReproductor.setText(cancionActual.getArtista());
        tvAlbumReproductor.setText(cancionActual.getAlbum());
        tvTituloReproductor.setText(cancionActual.getTitulo());

        if(cancionActual.getDuracion()+""==null){
            tvFinalReproductor.setText("00:00");
        }else {
            tvFinalReproductor.setText(darTiempo(cancionActual.getDuracion()));
        }

        if (cancionActual.getIdAlbum() == null) {
            Picasso.with(getApplicationContext()).load(R.drawable.music).into(ivCaratulaReproductor);
        } else {
            Picasso.with(getApplicationContext()).load(getImageUri(getApplicationContext(), getAlbumart(Long.parseLong(cancionActual.getIdAlbum()), getApplicationContext()))).into(ivCaratulaReproductor);
        }
    }
    public Bitmap getAlbumart(Long album_id, Context context) {
        Bitmap bm = null;
        try {
            final Uri sArtworkUri = Uri.parse("content://media/external/audio/albumart");
            Uri uri = ContentUris.withAppendedId(sArtworkUri, album_id);
            ParcelFileDescriptor pfd = context.getContentResolver().openFileDescriptor(uri, "r");
            if (pfd != null) {
                FileDescriptor fd = pfd.getFileDescriptor();
                bm = BitmapFactory.decodeFileDescriptor(fd);
            }
        } catch (Exception e) {
        }
        return bm;
    }

    private void volumen(){
        try{
            audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
            sbVolumen.setMax(audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC));
            sbVolumen.setProgress(audioManager.getStreamVolume(AudioManager.STREAM_MUSIC));
            sbVolumen.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener(){
                @Override
                public void onStopTrackingTouch(SeekBar arg0){
                }
                @Override
                public void onStartTrackingTouch(SeekBar arg0){
                }
                @Override
                public void onProgressChanged(SeekBar arg0, int progress, boolean arg2){
                    audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, progress, 0);
                }
            });
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public void atras(View v){
        Intent i = new Intent(getApplicationContext(), ListaCanciones.class);
        startActivity(i);
    }
    @Override
    public void onBackPressed() {
        Intent i = new Intent(getApplicationContext(), ListaCanciones.class);
        startActivity(i);

    }

    public void grabar(View v){
        Intent i = new Intent(getApplicationContext(), Grabar.class);
        startActivity(i);
    }




    public void add(){
        Intent intent = new Intent(this, Servicio.class);
        intent.putExtra("cancion",cancionActual.getRuta());
        Log.v("ruta",cancionActual.getRuta()+"");
        intent.setAction(Servicio.ADD);
        startService(intent);
    }

    public void play(View v){

        ibPlay.setVisibility(View.INVISIBLE);
        ibPause.setVisibility(View.VISIBLE);

        if(pausado=true){

            Intent intent = new Intent(Reproductor.this, Servicio.class);
            intent.setAction(Servicio.PLAY);
            startService(intent);
        }else{
            Intent intent = new Intent(Reproductor.this, Servicio.class);
            intent.setAction(Servicio.STOP);
            startService(intent);
            add();
            intent = new Intent(Reproductor.this, Servicio.class);
            intent.setAction(Servicio.PLAY);
            startService(intent);
        }



        sbProgreso.setProgress(0);
        sbProgreso.setMax(cancionActual.getDuracion());
    }

    public void stop(View v){

        ibPause.setVisibility(View.INVISIBLE);
        ibPlay.setVisibility(View.VISIBLE);
        Intent intent = new Intent(this, Servicio.class);
        intent.setAction(Servicio.STOP);
        startService(intent);
    }

    public void pause(View v){
        pausado=true;
        ibPause.setVisibility(View.INVISIBLE);
        ibPlay.setVisibility(View.VISIBLE);
        Intent intent = new Intent(this, Servicio.class);
        intent.setAction(Servicio.PAUSE);
        startService(intent);
    }
    public void btSiguiente(View v){
        siguiente();
        asignarValores();
        add();

        ibPlay.setVisibility(View.INVISIBLE);
        ibPause.setVisibility(View.VISIBLE);
        Intent intent = new Intent(this, Servicio.class);
        intent.setAction(Servicio.PLAY);
        startService(intent);
    }
    public void btAnterior(View v){
        anterior();
        asignarValores();
        add();

        ibPlay.setVisibility(View.INVISIBLE);
        ibPause.setVisibility(View.VISIBLE);
        Intent intent = new Intent(this, Servicio.class);
        intent.setAction(Servicio.PLAY);
        startService(intent);
    }



    public String darTiempo(int milisegundos){
        int segundos=milisegundos/1000;
        int horas = 0;
        int minutos = 0;
        while(segundos>60){
            segundos= segundos-60;
            minutos++;
        }
        while(minutos>60){
            minutos= minutos-60;
            horas++;
        }

        //MAS DE UNA CIFRA EL TIEMPO
        String finalMinutos = "",finalSegundos = "";
        if(minutos<10)finalMinutos= "0"+ minutos;
        else finalMinutos = minutos+"";
        if(segundos<10)finalSegundos = "0"+ segundos;
        else finalSegundos = segundos+"";
        //SI HAY HORAS SALEN
        if(horas>0)
            return horas+":"+minutos+":"+segundos;
        else
            return finalMinutos+":"+finalSegundos;
    }

    public Uri getImageUri(Context inContext, Bitmap inImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(), inImage, "Title", null);
        return Uri.parse(path);
    }

    public void movimiento(){
        // Gesture detection
        gestureDetector = new GestureDetector(this, new MyGestureDetector());
        gestureListener = new View.OnTouchListener() {
            public boolean onTouch(View v, MotionEvent event) {
                return gestureDetector.onTouchEvent(event);
            }
        };
        ivCaratulaReproductor.setOnTouchListener(gestureListener);
    }

    class MyGestureDetector extends GestureDetector.SimpleOnGestureListener {
        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            try {
                if (Math.abs(e1.getY() - e2.getY()) > SWIPE_MAX_OFF_PATH)
                    return false;
                // right to left swipe
                if(e1.getX() - e2.getX() > SWIPE_MIN_DISTANCE && Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY) {
                    Log.v("donde", "izquierda");
                    siguiente();

                    asignarValores();
                    add();

                }  else if (e2.getX() - e1.getX() > SWIPE_MIN_DISTANCE && Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY) {
                    Log.v("donde","derecha");
                    anterior();

                    asignarValores();
                    add();
                }

            } catch (Exception e) {
                // nothing
            }
            return false;
        }

        @Override
        public boolean onDown(MotionEvent e) {
            return true;
        }
    }

    public void siguiente(){
        ibPlay.setVisibility(View.INVISIBLE);
        ibPause.setVisibility(View.VISIBLE);

        int posicion=posicionPorID(id,canciones);
        posicion++;
        Log.v("posicion",posicion+"");
        Log.v("tamano",canciones.size()+"");
        if(posicion>canciones.size()-1){
           posicion=canciones.size()-1;
        }
        id=canciones.get(posicion).getId();

        tvArtistaReproductor.setText(canciones.get(posicion).getArtista());
        tvAlbumReproductor.setText(canciones.get(posicion).getAlbum());
        tvTituloReproductor.setText(canciones.get(posicion).getTitulo());

        if(canciones.get(posicion).getDuracion()+""==null){
            tvFinalReproductor.setText("00:00:00");
        }else {
            tvFinalReproductor.setText(darTiempo(canciones.get(posicion).getDuracion()));
        }

        if (canciones.get(posicion).getIdAlbum() == null) {
            Picasso.with(getApplicationContext()).load(R.drawable.music).into(ivCaratulaReproductor);
        } else {
            Picasso.with(getApplicationContext()).load(getImageUri(getApplicationContext(), getAlbumart(Long.parseLong(canciones.get(posicion).getIdAlbum()),getApplicationContext()))).into(ivCaratulaReproductor);

        }

    }

    public void anterior(){
        ibPlay.setVisibility(View.INVISIBLE);
        ibPause.setVisibility(View.VISIBLE);
        
        int posicion=posicionPorID(id,canciones);
        posicion--;
        if(posicion<0){
            posicion=0;
        }
        id=canciones.get(posicion).getId();

        tvArtistaReproductor.setText(canciones.get(posicion).getArtista());
        tvAlbumReproductor.setText(canciones.get(posicion).getAlbum());
        tvTituloReproductor.setText(canciones.get(posicion).getTitulo());

        if(canciones.get(posicion).getDuracion()+""==null){
            tvFinalReproductor.setText("00:00:00");
        }else {
            tvFinalReproductor.setText(darTiempo(canciones.get(posicion).getDuracion()));
        }

        if (canciones.get(posicion).getIdAlbum() == null) {
            Picasso.with(getApplicationContext()).load(R.drawable.music).into(ivCaratulaReproductor);
        } else {
            Picasso.with(getApplicationContext()).load(getImageUri(getApplicationContext(), getAlbumart(Long.parseLong(canciones.get(posicion).getIdAlbum()), getApplicationContext()))).into(ivCaratulaReproductor);

        }

    }

    public Cancion buscarPorID(int id, ArrayList<Cancion> datos){
        Cancion encontrada= new Cancion();
        for (int i = 0; i <datos.size() ; i++) {
            if(datos.get(i).getId()==id){
                encontrada=datos.get(i);
            }
        }
        return encontrada;
    }

    public int posicionPorID(int id, ArrayList<Cancion> datos){
        int posicion=0;
        for (int i = 0; i <datos.size() ; i++) {
            if(datos.get(i).getId()==id){
                posicion=i;
            }
        }
        return posicion;
    }
    protected void onDestroy() {
        super.onDestroy();
        Servicio.servicioActivo=false;
        unregisterReceiver(receptor);
        stopService(new Intent(this, Servicio.class));
    }

    public void barraProgreso(){
        sbProgreso.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                if(Servicio.servicioActivo){
                    Intent intent = new Intent(Reproductor.this, Servicio.class);
                    intent.putExtra("posicionNueva", seekBar.getProgress());
                    intent.setAction(Servicio.POSICION);
                    startService(intent);
                }
            }
        });
    }


    private BroadcastReceiver receptor= new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            Bundle bundle = intent.getExtras();
            int posicionActual = bundle.getInt("posicionActual");
            if(Servicio.servicioActivo) {
                sbProgreso.setProgress(posicionActual);
                sbProgreso.setMax(cancionActual.getDuracion());
                tvInicioReproductor.setText(darTiempo(posicionActual));
            }
            Log.v("posicionActual",posicionActual+"");
            Log.v("duracion",cancionActual.getDuracion()+"");

            if(posicionActual == cancionActual.getDuracion()) {
                siguiente();
                asignarValores();
                add();
            }
        }
    };
    @Override
    protected void onResume() {
        super.onResume();
        Servicio.servicioActivo = true;
        registerReceiver(receptor, new IntentFilter(PROGRESO));
    }





}
