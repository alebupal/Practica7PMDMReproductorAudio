package com.example.alejandro.practica7pmdmreproductoraudio;

import android.content.ContentUris;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.ParcelFileDescriptor;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.io.FileDescriptor;


public class AdaptadorCursor extends CursorAdapter {

    private static LayoutInflater i;
    private TextView tvNombreLista, tvArtistaLista, tvDuracionLista;
    private ImageView ivCaratulaLista;


    public AdaptadorCursor(Context context, Cursor c) {
        super(context, c, true);
    }


    @Override
    public View newView(Context context, Cursor cursor, ViewGroup vg) {
        LayoutInflater i = LayoutInflater.from(vg.getContext());
        View v = i.inflate(R.layout.lista_detalle, vg, false);
        return v;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {

        ivCaratulaLista = (ImageView) view.findViewById(R.id.ivCaratulaLista);
        tvNombreLista = (TextView) view.findViewById(R.id.tvNombreLista);
        tvArtistaLista = (TextView) view.findViewById(R.id.tvArtistaLista);
        tvDuracionLista = (TextView) view.findViewById(R.id.tvDuracionLista);

       /* Long id = Long.parseLong(cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ALBUM_ID)));
        if (getAlbumart(id, context) == null) {*/
            Picasso.with(context).load(R.drawable.music).into(ivCaratulaLista);/*
        } else {
            Picasso.with(context).load(getImageUri(context, getAlbumart(id, context))).into(ivCaratulaLista);
        }*/
        tvNombreLista.setText(cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.TITLE)));
        tvArtistaLista.setText(cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ARTIST)));
        String duracion = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DURATION));
        if (duracion == null) {
            tvDuracionLista.setText("");
        } else {
            tvDuracionLista.setText(darTiempo(Integer.parseInt(duracion)));
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

    public Uri getImageUri(Context inContext, Bitmap inImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(), inImage, "Title", null);
        return Uri.parse(path);
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



}
