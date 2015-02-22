package devanshk.teslastem.firebasetest.Helpers;

import android.app.Activity;
import android.os.AsyncTask;
import android.util.Log;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import java.util.Random;

import devanshk.teslastem.firebasetest.Fragments.MenuFragment;
import devanshk.teslastem.firebasetest.R;

/**
 * Created by Jay on 2/14/2015.
 */
public class CircleMenu extends AsyncTask<Void,Integer,Void> {
    private final String TAG = "Async_Circle_Creator";
    public static boolean keepGoing = true;
    private static Activity activity;
    private static Random random = new Random();

    public CircleMenu(Activity a)
    {
        activity = a;
    }
    @Override
    protected void onPreExecute(){
        super.onPreExecute();
    }
    @Override
    protected Void doInBackground(Void... params) {
        while(keepGoing){
            activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    System.out.println("Creating view");
                    ImageView v = new ImageView(activity);
                    v.setImageDrawable(activity.getResources().getDrawable(R.drawable.activated_circle));
                    int value = random.nextInt(50)+150;
                    v.setLayoutParams(new ViewGroup.LayoutParams(value, value));
                    v.setX(random.nextInt(800) + 100);
                    v.setY(random.nextInt(1200) + 100);
                    MenuFragment.circlesLayout.addView(v);
                    Animation anim = AnimationUtils.loadAnimation(activity, R.anim.alpha_out);
                    v.startAnimation(anim);
                }
            });
            try{Thread.sleep(500);} catch(Exception e){e.printStackTrace();}
        }

        return null;
    }

    @Override
    protected void onPostExecute(Void v){
        Log.d("Async","Stopped working on menu");
    }
}
