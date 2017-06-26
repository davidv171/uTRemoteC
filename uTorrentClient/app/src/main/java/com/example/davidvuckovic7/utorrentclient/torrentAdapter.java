package com.example.davidvuckovic7.utorrentclient;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

/**
 * Created by davidvuckovic7 on 25.10.2016.
 */
public class torrentAdapter extends RecyclerView.Adapter<torrentAdapter.MyViewHolder> {

    private List<torrent> seznamTorrentov;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView upSpeed,downSpeed,progress,name;
        public ImageView delete,stanje;

        public MyViewHolder(View view) {
            super(view);
            name = (TextView) view.findViewById(R.id.name);
            upSpeed = (TextView) view.findViewById(R.id.upSpeed);
            downSpeed = (TextView) view.findViewById(R.id.downSpeed);
            progress =(TextView)view.findViewById(R.id.progress);
            delete = (ImageView)view.findViewById(R.id.zbrisi);
            stanje = (ImageView)view.findViewById(R.id.stanje);
        }
    }


    public torrentAdapter(List<torrent> seznamTorrentov) {
        this.seznamTorrentov = seznamTorrentov;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.torrenti_vrsta, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        torrent torrent = seznamTorrentov.get(position);

        holder.name.setText(torrent.getName());
        holder.progress.setText(String.valueOf(torrent.getProgress() + "%"));
        //TUKAJ ZAOKROŽUJEMO VREDNOSTI
        //TRENUTNO NI PODPORE ZA GIGABITNE PRENOSE
        int sizeD=1;
        int sizeU = 1;
        if(torrent.getDownSpeed()>1000){
            sizeD = 1024;
        }
        if(torrent.getDownSpeed()>1000000){
            sizeD = 1024*1024;
        }
        if(torrent.getUpSpeed()>1000){
            sizeU = 1024;
        }
        if(torrent.getUpSpeed()>1000000){
            sizeU = 1024*1024;
        }

        holder.downSpeed.setText(String.valueOf(Math.round(torrent.getDownSpeed())/sizeD)+"KB/s");
        holder.upSpeed.setText(String.valueOf(Math.round(torrent.getUpSpeed()/sizeU) + "KB/s"));
        //TODO: ZRIHTAJ,DA SE POJAVIJO SLIKE
        if(torrent.getStanje().contains("Finished")){
            holder.stanje.setImageResource((R.drawable.check));


        }
        if(torrent.getStanje().contains("Stopped")||torrent.getStanje().contains("Paused")){
            holder.stanje.setImageResource((R.drawable.stop));
        }
        if(torrent.getStanje().contains("Downloading")){
            holder.stanje.setImageResource(R.drawable.down);


        }
        if(torrent.getStanje().contains("Seeding"))
        {
            holder.stanje.setImageResource(R.drawable.up);
        }

        holder.delete.setImageResource(R.drawable.delete);
    }

    @Override
    public int getItemCount() {
        return seznamTorrentov.size();
    }
}