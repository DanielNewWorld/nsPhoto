package net.in.nsfoto.nsfoto;

import android.support.v7.widget.CardView;
import android.widget.ImageView;

/**
 * Created by root on 04.08.15.
 */
class CardImage {
    //String imageName;
    String imageURL;
    String imageURLAndroid;
    String imageType;
    int imageID;
    int imageSave;
    CardView cardviewID;

    CardImage(String iurl, String iurlandroid, String itype, int iID, int isave, CardView ciID) {
        this.imageURL = iurl;
        this.imageURLAndroid = iurlandroid;
        this.imageType = itype;
        this.imageID = iID;
        this.imageSave = isave;
        this.cardviewID = ciID;
    }
}
