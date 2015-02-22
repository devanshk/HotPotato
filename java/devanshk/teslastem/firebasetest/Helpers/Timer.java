package devanshk.teslastem.firebasetest.Helpers;

import android.app.Activity;
import android.os.AsyncTask;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;

import java.util.*;

import devanshk.teslastem.firebasetest.MainActivity;
import devanshk.teslastem.firebasetest.R;

/**
 * Created by Jay on 2/14/2015.
 */
public class Timer extends AsyncTask<Void,Integer,Void> {
    private final String TAG = "Async_Schedule_Updater";
    private static TextView textView;
    private static Activity parent;
    public static boolean doAgain;
    private static Date startDate;
    private static Date endDate;
    private static double elapsedMilliseconds;
    private static double timeleft;
    private static double millisecondsleft;
    private static Firebase firebase;
    public static double roundTime;
    public static long roundNumber;
    private static boolean receivedData = false;

    public Timer(Activity p, TextView tv, Firebase f)
    {
        textView = tv;
        parent = p;
        firebase = f;


    }
    @Override
    protected void onPreExecute(){
        super.onPreExecute();
        receivedData = false;
        //Initialize your first date here
        startDate = new Date();
        doAgain = true;
        Log.d("Timer","Start Time");
        firebase.child("round").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                roundNumber = (long)dataSnapshot.getValue();
                //roundTime = Math.round(5.0/(Math.pow((double)roundNumber, 1.0/3)));
                if (roundNumber < 10)
                {
                    roundTime = 5.0;
                }
                else if (roundNumber < 20)
                {
                    roundTime = 4.0;
                }
                else if(roundNumber < 30)
                {
                    roundTime = 3.0;
                }
                else if (roundNumber < 60)
                {
                    roundTime = 2.0;
                }
                else {
                    roundTime = 1.0;
                }
                System.out.println("roundTime = "+roundTime);
                textView.setText("" + String.valueOf(roundTime));
                receivedData = true;
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });
    }
    @Override
    protected Void doInBackground(Void... params) {
        while(!receivedData){}
        Log.e(TAG, "Started Updating Time");
        while(doAgain){
            if (MainActivity.numberOfPlayers==1 && !MainActivity.isOnMenu){
                doAgain = false;
                parent.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        firebase.child("players").addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot snapshot) {
                                firebase.child("players").setValue((long) snapshot.getValue() - 1);
                                MainActivity.playerID = -1;
                                MainActivity.hasDecrement = true;
                            }

                            @Override public void onCancelled(FirebaseError firebaseError) {}
                        });
                        firebase.child("round").addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                int numberOfRounds = Math.round((long)dataSnapshot.getValue());
                                MainActivity.PlaceholderFragment.roundView.setText("You lasted\n" + numberOfRounds + "\nrounds\n\n");
                            }

                            @Override public void onCancelled(FirebaseError firebaseError) {}
                        });
                        int averageMilliseconds = getAverage(MainActivity.timesList);
                        MainActivity.PlaceholderFragment.averageTimeView.setText("Your average response time was\n"+averageMilliseconds/1000+"."+averageMilliseconds%1000/10+"\nseconds");
                        try{Thread.sleep(500);}catch(Exception e){e.printStackTrace();}
                        MainActivity.PlaceholderFragment.resultView.setText("Winner");
                        MainActivity.PlaceholderFragment.backLinearLayout.setBackgroundColor(parent.getResources().getColor(R.color.victory_color));
                        MainActivity.PlaceholderFragment.backLinearLayout.setVisibility(View.VISIBLE);
                        firebase.child("round").setValue(1);
                        YoYo.with(Techniques.SlideInUp).duration(1000).playOn(MainActivity.PlaceholderFragment.backLinearLayout);
                    }
                });
            }
            //Do your algorithm stuff here
            //Initialize the second date over and over again here
            endDate = new Date();
            elapsedMilliseconds = endDate.getTime() - startDate.getTime();
            timeleft = roundTime - (elapsedMilliseconds /1000);
            timeleft = Math.round(timeleft * 100.0) / 100.0;
            //millisecondsleft = 99-(elapsedMilliseconds%1000)/10;
            if (timeleft <= 0 && millisecondsleft<=0)
            {
                doAgain = false;
            }
            parent.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    textView.setText("" + timeleft);

                }
            });
            try{
                Thread.sleep(10);} catch(Exception e){e.printStackTrace();} //10 is the delay in millisconds before it rusn again.
        }
        return null;
    }

    @Override
    protected void onPostExecute(Void v){
        Log.d("Timer","Stop Time");
        if (timeleft == 0 && millisecondsleft==0)
        {
            firebase.child("hot_player").setValue(MainActivity.randomPlayer);
            firebase.child("removedPlayer").setValue(MainActivity.playerID);
            firebase.child("players").addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot snapshot) {
                    firebase.child("players").setValue((long) snapshot.getValue() - 1);
                    MainActivity.playerID = -1;
                    MainActivity.hasDecrement = true;
                }

                @Override public void onCancelled(FirebaseError firebaseError) {}
            });
            firebase.child("round").addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    int numberOfRounds = Math.round((long)dataSnapshot.getValue());
                    MainActivity.PlaceholderFragment.roundView.setText("You lasted\n" + numberOfRounds + "\nrounds\n\n");
                }

                @Override public void onCancelled(FirebaseError firebaseError) {}
            });
            int averageMilliseconds = getAverage(MainActivity.timesList);
            MainActivity.PlaceholderFragment.averageTimeView.setText("Your average response time was\n"+averageMilliseconds/1000+"."+averageMilliseconds%1000/10+"\nseconds");
            try{Thread.sleep(500);} catch(Exception e){}
            MainActivity.PlaceholderFragment.backLinearLayout.setVisibility(View.VISIBLE);
            YoYo.with(Techniques.SlideInUp).duration(1000).playOn(MainActivity.PlaceholderFragment.backLinearLayout);
            MainActivity.isOnMenu = true;
        }
        else{
            MainActivity.timesList.add((int)elapsedMilliseconds);
        }
    }

    private static Integer getAverage(ArrayList<Integer>party){
        if (party.size()==0)
            return 0;
        System.out.println("partySize = "+party.size());
        int sum = 0;
        for (int x : party)
            sum+=x;
        return sum/party.size();
    }
}
