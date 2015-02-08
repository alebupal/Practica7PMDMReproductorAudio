package com.example.alejandro.practica7pmdmreproductoraudio;

import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.ParcelFileDescriptor;
import android.provider.MediaStore;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Base64;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import java.io.ByteArrayOutputStream;
import java.io.FileDescriptor;
import java.util.ArrayList;


public class ListaCanciones extends ActionBarActivity {

    private int REPRODUCIR = 1;
    private AdaptadorCursor ad;

    private ArrayList<Cancion> canciones = new ArrayList<Cancion>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lista_canciones);






        Cursor cur = getContentResolver().query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,null, null,	null, null);

        cur.moveToFirst();
        for(int i=0;i<cur.getCount();i++){
            Cancion c = new Cancion();
            c.setAlbum(cur.getString(cur.getColumnIndex(MediaStore.Audio.Media.ALBUM)));
            c.setId(Integer.parseInt(cur.getString(cur.getColumnIndex(MediaStore.Audio.Media._ID))));
            if(getAlbumart(Long.parseLong(cur.getString(cur.getColumnIndex(MediaStore.Audio.Media.ALBUM_ID))), getApplicationContext())==null){
                c.setIdAlbum(null);
            }else{
                c.setIdAlbum(cur.getString(cur.getColumnIndex(MediaStore.Audio.Media.ALBUM_ID)));
            }

            c.setArtista(cur.getString(cur.getColumnIndex(MediaStore.Audio.Media.ARTIST)));
            c.setTitulo(cur.getString(cur.getColumnIndex(MediaStore.Audio.Media.TITLE)));
            if(cur.getString(cur.getColumnIndex(MediaStore.Audio.Media.DURATION))==null){
                c.setDuracion(0);
            }else {
                c.setDuracion(Integer.parseInt(cur.getString(cur.getColumnIndex(MediaStore.Audio.Media.DURATION))));
            }
            c.setRuta(cur.getString(cur.getColumnIndex(MediaStore.Audio.Media.DATA)));
            canciones.add(c);
            cur.moveToNext();
        }

        Cursor cu = getContentResolver().query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,null, null,	null, null);
        final ListView ls = (ListView) findViewById(R.id.listView);
        ad = new AdaptadorCursor(this, cu);
        ls.setAdapter(ad);
        registerForContextMenu(ls);
        ls.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Cursor c=(Cursor)ls.getItemAtPosition(i);
                Intent it = new Intent(getApplicationContext(), Reproductor.class);
                Bundle b = new Bundle();
                b.putString("id", c.getString(c.getColumnIndex(MediaStore.Audio.Media._ID)));
                b.putParcelableArrayList("canciones",canciones);
                it.putExtras(b);
                startActivityForResult(it,REPRODUCIR);

            }
        });

        cur.close();

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

}
