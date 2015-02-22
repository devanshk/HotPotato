package devanshk.teslastem.firebasetest.Fragments;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.firebase.client.Firebase;

import java.util.concurrent.Executors;

import at.markushi.ui.CircleButton;
import devanshk.teslastem.firebasetest.Helpers.CircleMenu;
import devanshk.teslastem.firebasetest.MainActivity;
import devanshk.teslastem.firebasetest.R;


/**
 * Created by Jay on 2/15/2015.
 */
public class GameOverFragment extends Fragment{
    public static Firebase firebase;
    public static long playersLeft;
    public static long numberOfTurns;

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        View rootView = inflater.inflate(R.layout.fragment_menu,null);
        return rootView;
    }
}
