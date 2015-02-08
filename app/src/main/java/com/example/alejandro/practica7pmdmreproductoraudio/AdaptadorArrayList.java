package com.example.alejandro.practica7pmdmreproductoraudio;

import android.content.Context;
import android.graphics.Bitmap;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;

/**
 * Created by Alejandro on 21/01/2015.
 *//*
public class AdaptadorArrayList extends ArrayAdapter<Cancion> {


    private Context contexto;
    private ArrayList<Cancion> lista;
    private int recurso;
    private static LayoutInflater i;


    public static class ViewHolder{
        public TextView tvNombreLista, tvArtistaLista, tvDuracionLista;
        public ImageView ivCaratulaLista;
        public int posicion;
    }

    public AdaptadorArrayList(Context context, int resource, ArrayList<Cancion> objects) {
        super(context, resource, objects);
        this.contexto = context;
        this.lista=objects;
        this.recurso=resource;
        this.i=(LayoutInflater)contexto.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder vh=null;
        if(convertView==null){
            convertView=i.inflate(recurso,null);
            vh=new ViewHolder();
            vh.ivCaratulaLista = (ImageView) convertView.findViewById(R.id.ivCaratulaLista);
            vh.tvNombreLista = (TextView) convertView.findViewById(R.id.tvNombreLista);
            vh.tvArtistaLista = (TextView) convertView.findViewById(R.id.tvArtistaLista);
            vh.tvDuracionLista = (TextView) convertView.findViewById(R.id.tvDuracionLista);
            convertView.setTag(vh);
        }else{
            vh=(ViewHolder)convertView.getTag();
        }
        vh.posicion=position;

        if(lista.get(position).getCaratula()==null){
            Picasso.with(getContext()).load(R.drawable.music).into(vh.ivCaratulaLista);
        }else{
            Picasso.with(getContext()).load(getImageUri(getContext(),lista.get(position).getCaratula())).into(vh.ivCaratulaLista);
        }

        vh.tvNombreLista.setText(lista.get(position).getTitulo());
        vh.tvArtistaLista.setText(lista.get(position).getArtista());
        vh.tvDuracionLista.setText(darTiempo(lista.get(position).getDuracion()));

        return convertView;
    }
    public Uri getImageUri(Context inContext, Bitmap inImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(), inImage, "Title", null);
        return Uri.parse(path);
    }
    public String darTiempo(int milisegundos) {
        String tiempo;
        int segundos = (int) (milisegundos / 1000) % 60;
        int minutos = (int) ((milisegundos / (1000 * 60)) % 60);
        int hora = (int) ((milisegundos / (1000 * 60 * 60)) % 24);
        if (minutos < 10 && segundos < 10) {
            tiempo = hora + ":0" + minutos + ":0" + segundos;
            return tiempo;
        } else if (minutos < 10) {
            tiempo = hora + ":0" + minutos + ":" + segundos;
            return tiempo;
        } else if (segundos < 10) {
            tiempo = hora + ":" + minutos + ":0" + segundos;
            return tiempo;
        }
        tiempo = hora + ":" + minutos + ":" + segundos;
        return tiempo;
    }


}
*/