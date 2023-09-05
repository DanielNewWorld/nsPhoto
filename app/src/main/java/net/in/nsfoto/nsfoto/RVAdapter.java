package net.in.nsfoto.nsfoto;

/**
 * Created by root on 04.08.15.
 */
import android.app.FragmentTransaction;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import net.in.nsfoto.nsfoto.model.DBWallpaper;

import java.util.ArrayList;
import java.util.List;

public class RVAdapter extends RecyclerView.Adapter<RVAdapter.PersonViewHolder> {

    public interface OnCardClikedListener{
        public void onCardCliked (int imageID, String imageURL, String imageURLAndroid, String imageType);
    }

    public static class PersonViewHolder extends RecyclerView.ViewHolder {

        CardView cv;
        ImageView personPhoto;
        TextView txtSave;

        int imageID;
        String imageURL;
        String imageURLAndroid;
        String imageType;

        PersonViewHolder(final View itemView, int i) {
            super(itemView);
            cv = (CardView)itemView.findViewById(R.id.cv);
            personPhoto = (ImageView)itemView.findViewById(R.id.person_photo);
            txtSave = (TextView)itemView.findViewById(R.id.txtSave);

            cv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    OnCardClikedListener listner = (OnCardClikedListener) itemView.getContext();
                    if (listner != null) {
                        listner.onCardCliked(imageID, imageURL, imageURLAndroid, imageType); //передаем в Star
                    }
                }
            });
        }
    }

    List<CardImage> persons;

    RVAdapter(List<CardImage> persons){
        this.persons = persons;
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }

    @Override
    public PersonViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item, viewGroup, false);
        PersonViewHolder pvh = new PersonViewHolder(v, i);
        return pvh;
    }

    @Override
    public void onBindViewHolder(PersonViewHolder personViewHolder, int i) {
        //personViewHolder.personPhoto.setImageResource(persons.get(i).photoId);
        //String imageUrl="http://10.20.200.19/mini/photo-1429743433956-0e34951fcc67.jpeg";

        Picasso.with(personViewHolder.personPhoto.getContext()) //передаем контекст приложения
                .load(persons.get(i).imageURL) //адрес изображения
                .into(personViewHolder.personPhoto); //ссылка на ImageView

        //personViewHolder.personPhoto.setImageResource(android.R.drawable.stat_notify_sync);

        /*persons.set(i, new CardImage(persons.get(i).imageName,
                persons.get(i).imageType,
                persons.get(i).imageID,
                personViewHolder.cv));*/

        /*personViewHolder.txtInfo2.setText("i = " + String.valueOf(i) + "\n" +
        "card: " + String.valueOf(personViewHolder.cv) + "\n" +
        "person: " + String.valueOf(persons.get(i).cardImageID));*/
        personViewHolder.cv.setId(i);

        personViewHolder.imageID = persons.get(i).imageID;
        personViewHolder.imageURL = persons.get(i).imageURL;
        personViewHolder.imageURLAndroid = persons.get(i).imageURLAndroid;
        personViewHolder.imageType = persons.get(i).imageType;
        //personViewHolder.txtInfo2.setText("ID: " + String.valueOf(personViewHolder.cv.getId()));

        personViewHolder.txtSave.setText(String.valueOf(persons.get(i).imageSave));
    }

    @Override
    public int getItemCount() {
        return persons.size();
    }
}