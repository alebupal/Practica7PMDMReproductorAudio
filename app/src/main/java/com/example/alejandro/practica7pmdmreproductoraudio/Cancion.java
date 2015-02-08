package com.example.alejandro.practica7pmdmreproductoraudio;

import java.io.FileDescriptor;

import android.content.ContentUris;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Parcel;
import android.os.ParcelFileDescriptor;
import android.os.Parcelable;

public class Cancion implements Parcelable {
	String ruta;
    int id;
    String artista;
    String album;
    int duracion;
	String titulo;
    String idAlbum;


    public Cancion() {
    }


    public Cancion(String ruta, int id, String artista, String album, int duracion, String titulo, String idAlbum) {
        this.ruta = ruta;
        this.id = id;
        this.artista = artista;
        this.album = album;
        this.duracion = duracion;
        this.titulo = titulo;
        this.idAlbum = idAlbum;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getRuta() {
        return ruta;
    }

    public void setRuta(String ruta) {
        this.ruta = ruta;
    }

    public String getArtista() {
        return artista;
    }

    public void setArtista(String artista) {
        this.artista = artista;
    }

    public String getAlbum() {
        return album;
    }

    public void setAlbum(String album) {
        this.album = album;
    }

    public int getDuracion() {
        return duracion;
    }

    public void setDuracion(int duracion) {
        this.duracion = duracion;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getIdAlbum() {
        return idAlbum;
    }

    public void setIdAlbum(String idAlbum) {
        this.idAlbum = idAlbum;
    }

    // ==================== Parcelable ====================
    public int describeContents() {
        return 0;
    }


    public void writeToParcel(Parcel parcel, int flags) {

        parcel.writeString(this.ruta);
        parcel.writeInt(this.id);
        parcel.writeString(this.artista);
        parcel.writeString(this.album);
        parcel.writeInt(this.duracion);
        parcel.writeString(this.titulo);
        parcel.writeString(this.idAlbum);
    }

    private Cancion(Parcel in) {
        this.ruta = in.readString();
        this.id = in.readInt();
        this.artista = in.readString();
        this.album = in.readString();
        this.duracion = in.readInt();
        this.titulo = in.readString();
        this.idAlbum = in.readString();
    }

    public static final Parcelable.Creator<Cancion> CREATOR = new Parcelable.Creator<Cancion>() {
        public Cancion createFromParcel(Parcel in) {
            return new Cancion(in);
        }

        public Cancion[] newArray(int size) {
            return new Cancion[size];
        }
    };
	
	
}
