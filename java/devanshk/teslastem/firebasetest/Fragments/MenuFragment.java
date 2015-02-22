package devanshk.teslastem.firebasetest.Fragments;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import java.util.concurrent.Executors;

import at.markushi.ui.CircleButton;
import devanshk.teslastem.firebasetest.Helpers.CircleMenu;
import devanshk.teslastem.firebasetest.MainActivity;
import devanshk.teslastem.firebasetest.R;

/**
 * Created by devanshk on 2/14/15.
 */
public class MenuFragment extends Fragment {
    private static CircleButton menuButton;
    public static RelativeLayout circlesLayout;
    private static CircleMenu menuAsynch;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_menu,null);
        menuButton = (CircleButton)rootView.findViewById(R.id.menu_join);
        circlesLayout = (RelativeLayout)rootView.findViewById(R.id.circles_layout);
        menuButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MainActivity.isOnMenu = false;
                getActivity().getFragmentManager().beginTransaction().replace(R.id.container, MainActivity.frag).commit();
            }
        });
        /*if (menuAsynch == null) {
            menuAsynch = new CircleMenu(getActivity());
            menuAsynch.executeOnExecutor(Executors.newSingleThreadExecutor());
        }*/
        return rootView;
    }
}
