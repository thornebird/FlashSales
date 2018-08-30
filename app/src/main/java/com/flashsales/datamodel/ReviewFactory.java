package com.flashsales.datamodel;

import java.util.ArrayList;

import com.flashsales.R;

public class ReviewFactory {
    private static ReviewFactory mInstance;
    private ArrayList<Review> reviewArrayList;

    public static synchronized ReviewFactory getInstance(){
        if(mInstance == null){
            mInstance = new ReviewFactory();
        }
        return mInstance;
    }


    public void createReviews(){
        if(reviewArrayList == null)
            reviewArrayList = new ArrayList<>();

        //reviewArrayList.add(new Review("Shaun","Quality is good and awesome",3.2, R.drawable.shaun));
      //  reviewArrayList.add(new Review("David","Items came on time",4.5,R.drawable.david));
      //  reviewArrayList.add(new Review("Francis","Exactly as advertised. Looks good",4.0,R.drawable.francis));
        reviewArrayList.add(new Review("Sharon","Its an amazing watch. I recommend it. (:",4.1,R.drawable.sharon));
        reviewArrayList.add(new Review("Nathalie"," I love it",3.5,R.drawable.nathalie));
        reviewArrayList.add(new Review("Jean"," Thanks",3.5,R.drawable.jean));
        reviewArrayList.add(new Review("Gugu"," the shipping it’s crazy fast woww thank you",4.5,R.drawable.gugu));
        reviewArrayList.add(new Review("Edna"," I'm very happ with this",4.2,R.drawable.edna));
       // reviewArrayList.add(new Review("Steve"," Shipping is okay. this is a great product",4.9,R.drawable.steve));
        reviewArrayList.add(new Review("Marica","This was so cheap im glad i bought 5",3.5,R.drawable.marica));
        reviewArrayList.add(new Review("Ntoko"," It fit just right,",4.3,R.drawable.ntoko));
        reviewArrayList.add(new Review("Noah","You guys are the best I just got my mine",5.0,R.drawable.noah));
        //reviewArrayList.add(new Review("Nathan","Looks just like the picture and the colour change is really good",3.8,R.drawable.nathan));
        reviewArrayList.add(new Review("Mia","It’s awesome and I love it so much",4.3,R.drawable.mia));
       // reviewArrayList.add(new Review("Marcus","Yess!!!! Im the 21 st visitor I got a free prize",4.8,R.drawable.marcus));
        reviewArrayList.add(new Review("Beth","Their stuff are so much fun ive never seen anything like it",4.0,R.drawable.beth));
        reviewArrayList.add(new Review("Sophie","it is beautiful",4.5,R.drawable.sophie));
        reviewArrayList.add(new Review("Emily"," its so freakin cute",3.8,R.drawable.emily));
        reviewArrayList.add(new Review("Daisy"," How long for the free shipping I just one",3.8,R.drawable.daisy));
        reviewArrayList.add(new Review("Sam","Nothin here is that great",2.5,R.drawable.sam));
        reviewArrayList.add(new Review("Libby","Wow this is some cheap stuff",4.5,R.drawable.libby));
      //  reviewArrayList.add(new Review("Paul","Pretty interesting stuff on this flash sales",4.0,R.drawable.paul));
        reviewArrayList.add(new Review("Amber","Super soft exactly like pictured",4.0,R.drawable.amber));
       // reviewArrayList.add(new Review("Ellie","Great came a month early but the bear isn’t squishy like I though",3.5,R.drawable.ellie));
      //  reviewArrayList.add(new Review("Ryan","Who else won a free prize for being the 21 user?",4.5,R.drawable.ryan));
      //  reviewArrayList.add(new Review("Dean","This app is so cheap!!!",4.1,R.drawable.dean));
        reviewArrayList.add(new Review("Samantha","Flash sales are great I bought 6 of these",5.0,R.drawable.samantha));
        reviewArrayList.add(new Review("Georgina","cute :):):)",4.0,R.drawable.georgina));
      //  reviewArrayList.add(new Review("Thomas","nice!",4.0,R.drawable.thomas));
        reviewArrayList.add(new Review("Leora","very fun shopping",4.3,R.drawable.leora));
        reviewArrayList.add(new Review("Annette","Good i like it:)",4.1,R.drawable.annette));
       // reviewArrayList.add(new Review("Frank","Guys i just wont another free prize. Keep loggin in and out every 21 user gets a free prize",5.0,R.drawable.frank));
    }

    public ArrayList<Review> getReviewArrayList() {
        return reviewArrayList;
    }
}
