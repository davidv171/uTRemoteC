package com.example.davidvuckovic7.utorrentclient;

import android.widget.ImageView;

/**
 * Created by davidvuckovic7 on 25.10.2016.
 */
public class torrent {
    private double upSpeed;
    private double downSpeed;
    private String name;
    private String stanje;
    private ImageView zbriši;
    private double progress;
    public torrent(){

    }
    public torrent(double upSpeed,double downSpeed,String name,String stanje, ImageView zbriši, double progress){
        this.upSpeed=upSpeed;
        this.downSpeed=downSpeed;
        this.name = name;
        this.stanje= stanje;
        this.zbriši=zbriši;
        this.progress=progress;
    }

    public double getUpSpeed() {
        return upSpeed;
    }

    public void setUpSpeed(double upSpeed) {
        this.upSpeed = upSpeed;
    }

    public double getDownSpeed() {
        return downSpeed;
    }

    public void setDownSpeed(double downSpeed) {
        this.downSpeed = downSpeed;
    }

    public String getStanje() {
        return stanje;
    }

    public void setStanje(String stanje) {
        this.stanje = stanje;
    }

    public ImageView getZbriši() {
        return zbriši;
    }

    public void setZbriši(ImageView zbriši) {
        this.zbriši = zbriši;
    }

    public double getProgress() {
        return progress;
    }

    public void setProgress(double progress) {
        this.progress = progress;
    }
    public String getName(){
        return this.name;
    }
    public void setName(String name){
        this.name=name;
    }



}
