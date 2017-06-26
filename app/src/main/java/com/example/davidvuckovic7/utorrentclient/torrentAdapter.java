package com.example.davidvuckovic7.utorrentclient;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

/**
 * Created by davidvuckovic7 on 25.10.2016.
 */
public class torrentAdapter extends RecyclerView.Adapter<torrentAdapter.MyViewHolder> {
    prikazPodatkov pp = new prikazPodatkov();
    private List<torrent> seznamTorrentov;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView upSpeed, downSpeed, progress, name;
        public ImageView delete, stanje;

        public MyViewHolder(View view) {
            super(view);
            name = (TextView) view.findViewById(R.id.name);
            upSpeed = (TextView) view.findViewById(R.id.upSpeed);
            downSpeed = (TextView) view.findViewById(R.id.downSpeed);
            progress = (TextView) view.findViewById(R.id.progress);
            delete = (ImageView) view.findViewById(R.id.zbrisi);
            stanje = (ImageView) view.findViewById(R.id.stanje);
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

    public void onBindViewHolder(MyViewHolder holder, final int position) {
        torrent torrent = seznamTorrentov.get(position);
        final String hash = torrent.getHash();

        //STATUS MORA BITI FINAL VREDNOST, ZATO MU NASTAVIMO VREDNOST Z UPORABO POMOŽNE SPREMENLJIVKE STATUS1
        //STATUS STATUS NATO PREVERI, ČE JE HITROST UPLOADA ALI DOWNLOADA NE ENAK NIČ, ČE SE POGOJ IZPOLNI, POMENI DA JE TORRENT AKTIVEN, TER MU DAMO USTREZNO VREDNOST
        //V METODI, KI JO KLIČEMO IZ RAZREDA PRIKAZ PODATKOV, SE PREVERI, ČE JE VREDNOST STATUSA AKTIVEN ALI NE, TER SE GLEDE NA TO IZVRŠI USTREZNA AKCIJA
        //ČE JE VREDNOST STRINGA AKTIVEN POMENI, DA NAD TORRENTOM IZVRŠIMO ACTION STOP
        //V OBRATNEM PRIMERU IZVRŠIMO AKCIJO START
        String status1;
        if (torrent.getDownSpeed()!=0|| torrent.getUpSpeed()!=0){
          status1 = "active";
        }
        else{
            status1 = "inactive";
        }
        final String status = status1;
        System.out.println();
        holder.name.setText(torrent.getName());

        holder.progress.setText(String.valueOf(torrent.getProgress() + "%"));
        //TUKAJ ZAOKROŽUJEMO VREDNOSTI
        //TRENUTNO NI PODPORE ZA GIGABITNE PRENOSE
        //S SPREMENLJIVKAMA sizeUname in Dname spreminjamo, ali napišemo na koncu hitrosti downloada MB/S ali KB/s
        int sizeD = 1024;
        int sizeU = 1024;
        String sizeUname = "KB/s";
        String sizeDname = "KB/s";
        if (torrent.getDownSpeed() > 1000000) {
            sizeD = 1024 * 1024;
            sizeDname = "MB/s";
        }
        if (torrent.getUpSpeed() > 1000000) {
            sizeU = 1024 * 1024;
            sizeUname = "MB/s";
        }

        holder.downSpeed.setText(String.valueOf(Math.round(torrent.getDownSpeed()) / sizeD) + sizeDname);
        holder.upSpeed.setText(String.valueOf(Math.round(torrent.getUpSpeed()) / sizeU) + sizeUname);
        //TODO: PROGRESS BAR
        if(torrent.getUpSpeed()!=0&&torrent.getDownSpeed()==0){
            holder.stanje.setImageResource(R.drawable.up);
        }
        if (torrent.getDownSpeed() == 0 && torrent.getUpSpeed() == 0) {
            holder.stanje.setImageResource(R.drawable.check);

        }
        if (torrent.getStanje().contains("Stopped") || torrent.getStanje().contains("Paused")  || torrent.getStanje().contains("Connecting")|| torrent.getStanje().contains("Checking")) {
            holder.stanje.setImageResource((R.drawable.stop));
        }
        if (torrent.getStanje().contains("Downloading")) {
            holder.stanje.setImageResource(R.drawable.down);


        }

        if(torrent.getStanje().contains("Seeding") && (torrent.getUpSpeed() > 1)){
            holder.stanje.setImageResource(R.drawable.up);
            System.out.println("TORRENT UPLOADA" + torrent.getUpSpeed());
        }


        holder.delete.setImageResource(R.drawable.delete);
        holder.delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //PO KLICU METODE ZA IZBRIS TORRENTA MORAMO POSKRBET, DA OB NASLEDNJEM OSVEŽEVANJU NE CRASHNEMO
                //TOREJ RECYCLERVIEW JE TREBA OBVESTITI O ODSTRANITVI ELEMENTA!
                pp.deleteTorrent(hash);
                seznamTorrentov.remove(position);
                notifyItemRemoved(position);
                notifyItemRangeChanged(position, seznamTorrentov.size());

            }
        });
        holder.stanje.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                System.out.println("PRITISNJEN STATUS GUMB");
               pp.changeStatus(hash,status);
                seznamTorrentov.remove(position);
                notifyItemRemoved(position);

                notifyItemChanged(position);

            }
        });

    }

    @Override
    public int getItemCount() {
        return seznamTorrentov.size();
    }
}