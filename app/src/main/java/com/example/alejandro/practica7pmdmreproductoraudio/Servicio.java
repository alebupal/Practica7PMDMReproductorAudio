package com.example.alejandro.practica7pmdmreproductoraudio;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.os.IBinder;
import android.os.PowerManager;
import android.util.Log;

import java.io.IOException;
import java.util.List;

/**
 * Created by Alejandro on 08/02/2015.
 */

public class Servicio extends Service implements MediaPlayer.OnPreparedListener, MediaPlayer.OnCompletionListener, AudioManager.OnAudioFocusChangeListener{

    // aleatoria
    // bucle
    // seekbar
    // previous next

    private MediaPlayer mp;
    private enum Estados{
        idle,
        initialized,
        preparing,
        prepared,
        started,
        paused,
        completed,
        stopped,
        end,
        error
    };
    private Estados estado;
    public static final String PLAY="play";
    public static final String STOP="stop";
    public static final String ADD="add";
    public static final String PAUSE="pause";
    public static final String PROGRESO = "progreso";
    public static final String POSICION="posicion";
    public static boolean servicioActivo = true;


    private Progreso progreso;
    private String rutaCancion=null;
    private boolean reproducir;
    private String dato;

    /* ******************************************************* */
    // METODOS SOBREESCRITOS //
    /* ****************************************************** */

    @Override
    public void onCreate() {
        super.onCreate();
        mp = new MediaPlayer();
        mp.setOnPreparedListener(this);
        mp.setOnCompletionListener(this);
        AudioManager am = (AudioManager)getSystemService(Context.AUDIO_SERVICE);
        int r = am.requestAudioFocus(this, AudioManager.STREAM_MUSIC, AudioManager.AUDIOFOCUS_GAIN);
        if(r == AudioManager.AUDIOFOCUS_REQUEST_GRANTED){
        }
        estado = Estados.idle;

    }

    @Override
    public void onDestroy() {
        mp.release();
        mp = null;
        super.onDestroy();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        String action = intent.getAction();


        if(action.equals(PLAY)){
            play();
        }else if(action.equals(ADD)){
            if(intent.getStringExtra("cancion") != null){
                dato = intent.getStringExtra("cancion");
            }
            add(dato);
        }else if(action.equals(STOP)){
            stop();
        }else if(action.equals(PAUSE)) {
            pause();
        }else if(action.equals(POSICION)) {
            int pos = intent.getIntExtra("posicionNueva", 0);
            mp.seekTo(pos);
        }

        return super.onStartCommand(intent, flags, startId);
    }

    /* ******************************************************* */
    // INTERFAZ PREPARED LISTENER //
    /* ****************************************************** */

    @Override
    public void onPrepared(MediaPlayer mp) {
        estado = Estados.prepared;
        if(reproducir){
            if(progreso!=null){
                progreso.cancel(true);
            }
            mp.start();
            estado = Estados.started;
            progreso = new Progreso();
            progreso.execute();
        }
    }

    /* ******************************************************* */
    // INTERFAZ COMPLETED LISTENER //
    /* ****************************************************** */

    @Override
    public void onCompletion(MediaPlayer mp) {
        estado = Estados.completed;
    }

    /* ******************************************************* */
    // INTERFAZ AUDIO FOCUS CHANGED //
    /* ****************************************************** */

    @Override
    public void onAudioFocusChange(int focusChange) {
        switch (focusChange) {
            case AudioManager.AUDIOFOCUS_GAIN:
                play();
                mp.setVolume(1.0f, 1.0f);
                break;
            case AudioManager.AUDIOFOCUS_LOSS:
                pause();
                break;
            case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT:
                pause();
                break;
            case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK:
                mp.setVolume(0.1f, 0.1f);
                break;
        }
    }

    /* ******************************************************* */
    // METODOS DE AUDIO //
    /* ****************************************************** */

    private void play(){

        if(rutaCancion != null){
            if(estado.equals(Estados.error)){
                estado = Estados.idle;
            }
            if(estado.equals(Estados.idle)){
                reproducir = true;
                try {
                    mp.setDataSource(rutaCancion);
                    estado = Estados.initialized;
                } catch (IOException e) {
                    estado= Estados.error;
                }
            }
            if(estado.equals(Estados.initialized) || estado.equals(Estados.stopped)){
                reproducir = true;
                mp.prepareAsync();
                estado = Estados.preparing;
            } else if(estado.equals(Estados.preparing)) {
                reproducir = true;
            }
            if(estado.equals(Estados.prepared) || estado.equals(Estados.paused) || estado.equals(Estados.completed)) {
                mp.start();
                estado = Estados.started;
            }
        }
    }

    private void stop(){
        if(estado.equals(Estados.prepared) || estado.equals(Estados.started) || estado.equals(Estados.paused) || estado.equals(Estados.completed)){
            mp.seekTo(0); // Para volver al principio sino comentar para pause
            mp.stop();
            estado = Estados.stopped;
        }
        reproducir = false;
    }

    private void pause() {
        if(estado.equals(Estados.started)){
            mp.pause();
            estado = Estados.paused;
        } else if(estado.equals(Estados.paused)){
            mp.start();
            estado = Estados.started;
            progreso.cancel(true);
            progreso = new Progreso();
            progreso.execute();
        } else if(estado.equals(Estados.completed)) {
            mp.seekTo(0);
            mp.start();
            estado = Estados.started;
            progreso.cancel(true);
            progreso = new Progreso();
            progreso.execute();
        }
    }

    private void add(String cancion){

        stop();
        initComponents();
        this.rutaCancion = cancion;
        play();
    }

    public void initComponents(){
        AudioManager am = (AudioManager)getSystemService(Context.AUDIO_SERVICE);
        int r = am.requestAudioFocus(this, AudioManager.STREAM_MUSIC, AudioManager.AUDIOFOCUS_GAIN);
        if(r==AudioManager.AUDIOFOCUS_REQUEST_GRANTED){
            // normal
            mp = new MediaPlayer();
            mp.setOnPreparedListener(this);
            mp.setOnCompletionListener(this);
            mp.setWakeMode(this,PowerManager.PARTIAL_WAKE_LOCK);
            estado = Estados.idle;
        } else {
            stopSelf();
        }
    }


    private class Progreso extends AsyncTask<Void, Integer, Integer>{

        private void progreso(int p){
            Intent intent = new Intent(PROGRESO);
            intent.putExtra("posicionActual", p);
            sendBroadcast(intent);

            Log.v("estoy pasando esto",p+"");
        }

        @Override
        protected Integer doInBackground(Void... voids) {
            while(mp.getDuration() > mp.getCurrentPosition()){
                try {
                    Thread.sleep(300);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                if(estado.equals(Estados.started)&& !isCancelled()) {
                    Log.v("voy por", mp.getCurrentPosition()+"");
                    progreso(mp.getCurrentPosition());
                } else if(estado.equals(Estados.paused)) {
                    Log.v("voy por", mp.getCurrentPosition()+"");
                    progreso(mp.getCurrentPosition());
                }  else if(estado.equals(Estados.stopped)) {
                    progreso(0);
                }else {
                    if(estado.equals(Estados.completed)) {
                        return mp.getDuration();
                    }
                    return mp.getCurrentPosition();

                }
            }
            return mp.getDuration();
        }
        @Override
        protected void onPostExecute(Integer integer) {
            progreso(mp.getDuration());
        }

    }




}