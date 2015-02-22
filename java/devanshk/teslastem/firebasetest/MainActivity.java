package devanshk.teslastem.firebasetest;

import android.app.Activity;
import android.app.Fragment;
import android.graphics.Point;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Vibrator;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;

import java.util.ArrayList;

import devanshk.teslastem.firebasetest.Fragments.MenuFragment;
import devanshk.teslastem.firebasetest.Helpers.Timer;

import java.util.Random;
import java.util.concurrent.Executors;


public class MainActivity extends Activity {
    public static Firebase firebase;
    public static long playerID;
    public static long numberOfPlayers = 0;
    public static long currentPlayer = 0;
    public static PlaceholderFragment frag = new PlaceholderFragment();
    public static MenuFragment menuFrag = new MenuFragment();
    public static String playerNumbersString="";
    public static MediaPlayer mediaPlayer, pushSound;
    public static long randomPlayer;
    public static Timer myTimer;
    public static Random random = new Random();
    public static ArrayList<Integer> timesList = new ArrayList<Integer>();
    public static boolean hasDecrement = false;
    public static boolean isOnMenu = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Firebase.setAndroidContext(this);
        firebase =  new Firebase("https://ohiknowwhatthisis.firebaseio.com");
        setContentView(R.layout.activity_main);
        if (savedInstanceState == null) {
            getFragmentManager().beginTransaction()
                    .add(R.id.container, menuFrag)
                    .commit();
        }
        //getActionBar().hide();
        mediaPlayer = MediaPlayer.create(this,R.raw.hot_potato);
        //mediaPlayer.setLooping(true);
        //mediaPlayer.start();
        pushSound = MediaPlayer.create(this,R.raw.dry_fire_gun);
    }

    @Override
    public void onResume(){
        super.onResume();
        final Firebase playerReference = firebase.child("players");

        playerReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                playerReference.setValue((long)snapshot.getValue()+1);
                if (playerID==0)
                    playerID = (long)snapshot.getValue()+1;
                System.out.println("PlayerID: "+playerID);
                frag.updateHot();
            }
            @Override
            public void onCancelled(FirebaseError firebaseError) {
            }
        });
    }

    @Override
    public void onPause() {
        final Firebase playerReference = firebase.child("players");
        if (!hasDecrement) {
            firebase.child("removedPlayer").setValue(playerID);
            playerID = 0;
            playerReference.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot snapshot) {
                    playerReference.setValue((long) snapshot.getValue() - 1);
                }

                @Override
                public void onCancelled(FirebaseError firebaseError) {
                }
            });

        }
        super.onPause();
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {
        public static Button button;
        public static TextView infoView;
        public static TextView timeText;
        public static TextView playerView,roundView,averageTimeView, resultView;
        public static View backLinearLayout;
        public int seekSpot = 0;

        public PlaceholderFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_main, container, false);
            button = (Button)rootView.findViewById(R.id.backgroundBlue);
            infoView = (TextView)rootView.findViewById(R.id.infoView);
            timeText = (TextView)rootView.findViewById(R.id.timeText);
            playerView = (TextView)rootView.findViewById(R.id.playersView);
            roundView = (TextView)rootView.findViewById(R.id.roundsView);
            averageTimeView = (TextView)rootView.findViewById(R.id.averageTimeView);
            backLinearLayout = rootView.findViewById(R.id.game_over_vayout);
            resultView = (TextView)rootView.findViewById(R.id.result_view);

            timeText.setText("5");
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    System.out.println("playerId = "+playerID+" and currentPlayer = "+currentPlayer);
                    if (playerID == currentPlayer) {
                        if (pushSound.isPlaying()) {
                            pushSound.pause();
                            pushSound.seekTo(0);
                            pushSound.start();
                        }
                        else
                            pushSound.start();
                        Vibrator vab = (Vibrator)getActivity().getSystemService(VIBRATOR_SERVICE);
                        vab.vibrate(200);
                        firebase.child("hot_player").setValue(randomPlayer);
                        Timer.doAgain=false;
                        firebase.child("round").addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                firebase.child("round").setValue((long)dataSnapshot.getValue()+1);
                            }

                            @Override public void onCancelled(FirebaseError firebaseError) {}
                        });
                    }
                }
            });

            firebase.child("players").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    long newPlayers = (long)dataSnapshot.getValue();
                    if (numberOfPlayers>newPlayers) {
                        firebase.child("removedPlayer").addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot snapshot) {
                                long removedPlayerID = (long)snapshot.getValue();
                                //System.out.println("playerID = "+playerID+", and removedPlayerId = "+removedPlayerID);
                                if (playerID>removedPlayerID)
                                    playerID--;
                                //System.out.println("playerID = "+playerID+", and removedPlayerId = "+removedPlayerID);

                                //button.setText(""+playerID);
                                infoView.setText(numberOfPlayers+" players connected.\nYou are player "+playerID+".");
                            }
                            @Override
                            public void onCancelled(FirebaseError firebaseError) {
                            }
                        });
                    }
                    System.out.println("currentPlayer = "+currentPlayer+" and newPlayers = "+newPlayers);
                    if (currentPlayer>newPlayers) {
                        currentPlayer-=1;
                        firebase.child("hot_player").setValue(currentPlayer);
                        updateHot();
                    }

                    numberOfPlayers = (long)dataSnapshot.getValue();
                    //button.setText(""+playerID);

                    infoView.setText(numberOfPlayers+" players connected.\nYou are player "+playerID+".");
                    updateHot();
                    playerView.setText("\nThere are\n"+numberOfPlayers+"\nother players remaining\n\n");
                    randomPlayer = pickRandomPlayers();
                }

                @Override
                public void onCancelled(FirebaseError firebaseError) {

                }
            });

            firebase.child("hot_player").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    try{
                        long currentHotPlayer = (long)dataSnapshot.getValue();
                        currentPlayer = currentHotPlayer;
                        System.out.println("currentPlayer = "+currentHotPlayer+" and playerID = "+playerID);
                    if (currentHotPlayer == playerID) {
                        System.out.println("currentHotPlayer does equal playerID");
                        randomPlayer = pickRandomPlayers();

                        firebase.child("round").addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                System.out.println("round result = "+dataSnapshot.getValue());
                                button.setBackground(getResources().getDrawable(R.drawable.activated_circle));
                                repickLocationAndSize((long)dataSnapshot.getValue());
                                MainActivity.myTimer = new Timer(getActivity(), timeText, firebase);
                                MainActivity.myTimer.executeOnExecutor(Executors.newSingleThreadExecutor());
                            }

                            @Override public void onCancelled(FirebaseError firebaseError) {}
                        });

                    }
                    else
                        button.setBackground(getResources().getDrawable(R.drawable.deactivated_circle));
                    System.out.println("new Hot Player is "+currentHotPlayer);} catch(Exception e){e.printStackTrace();}
                }

                @Override
                public void onCancelled(FirebaseError firebaseError) {
                }
            });

            return rootView;
        }

        private void updateHot(){
            firebase.child("hot_player").addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot snapshot) {
                    try {
                        long currentHotPlayer = (long) snapshot.getValue();
                        currentPlayer = currentHotPlayer;
                        if (currentHotPlayer == playerID) {
                            isOnMenu = false;
                            button.setBackground(getResources().getDrawable(R.drawable.activated_circle));
                        }
                        else
                            button.setBackground(getResources().getDrawable(R.drawable.deactivated_circle));
                        System.out.println("new Hot Player is " + currentHotPlayer);
                        if (currentHotPlayer == 0)
                            firebase.child("hot_player").setValue(1);
                    }
                    catch(Exception e){e.printStackTrace();}
                }
                @Override
                public void onCancelled(FirebaseError firebaseError) {
                }
            });
        }

        public static ArrayList<Integer> parseIntegersFromString(String string){
            ArrayList<Integer> hopper=new ArrayList<Integer>();

            while (string.contains("-")){
                String sub = string.substring(0,string.indexOf("-"));
                hopper.add(Integer.parseInt(sub));
                string=string.substring(string.indexOf("-"+1));
            }
            hopper.add(Integer.parseInt(string));
            return hopper;
        }

        public static long pickRandomPlayers()
        {
            if (numberOfPlayers==1)
                return playerID;
            else if (numberOfPlayers<=0)
                return 1;
            Random rand = new Random();
            long hopper = playerID;
            while (playerID == hopper)
            {
                hopper = (long)rand.nextInt((int)numberOfPlayers) + 1;
            }
            return hopper;
        }
        public static Integer whatsMissingFrom(String after){
            String string = playerNumbersString;
            ArrayList<Integer> before=parseIntegersFromString(string);

            ArrayList<Integer> afterArray=parseIntegersFromString(after);

            for (Integer b: before){
                if (!afterArray.contains(b))
                    return b;
            }
            return 99999;
        }

        public void repickLocationAndSize(long round){
            int dimens = (int)((1 / (Math.pow((round * 1.0), (1.0 / 1.8)))) * 600);
            System.out.println("dimens = " + dimens);
            button.setLayoutParams(new RelativeLayout.LayoutParams(dimens,dimens));

            //int maxWidth = (screenWidth - 32) - (int)(dimens);
            //int maxHeight = (screenHeight - 32) - (int)(dimens);
            int positionX = random.nextInt(900 - 32 - (int)dimens) + 16;
            int positionY = random.nextInt(1200 - 32 - (int)dimens) + 16;
            System.out.println("Position x = " + positionX);
            System.out.println("Position y = " + positionY);
            //button.setLayoutParams(new RelativeLayout.LayoutParams((int)dimens, (int)dimens));
            button.setX(positionX);
            button.setY(positionY);
        }
    }
}
