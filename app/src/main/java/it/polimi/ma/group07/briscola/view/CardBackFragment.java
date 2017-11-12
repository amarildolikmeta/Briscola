package it.polimi.ma.group07.briscola.view;



import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import it.polimi.ma.group07.briscola.R;

/**
 * A simple {@link Fragment} subclass.
 * create an instance of this fragment.
 */
public class CardBackFragment extends Fragment {


    public CardBackFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_card_back, container, false);
    }

}
