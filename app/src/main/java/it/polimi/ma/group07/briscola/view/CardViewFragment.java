package it.polimi.ma.group07.briscola.view;


import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;

import it.polimi.ma.group07.briscola.R;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link CardViewFragment.OnCardSelectedListener} interface
 * to handle interaction events.
 * Use the {@link CardViewFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class CardViewFragment extends Fragment {

    // TODO: Rename and change types of parameters
    private int mImageId;
    private ImageView imageView;
    private OnCardSelectedListener mListener;

    public CardViewFragment() {
        Log.i("CardViewFragment","fragment created");
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param mImageId id of the source image
     * @return A new instance of fragment CardViewFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static CardViewFragment newInstance(int mImageId) {
        CardViewFragment fragment = new CardViewFragment();
        Bundle args = new Bundle();
        args.putInt("mImageId", mImageId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mImageId = getArguments().getInt("mImageId");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Load the saved state (the list of images and list index) if there is one
        if(savedInstanceState != null) {
            mImageId = savedInstanceState.getInt("mImageId");
        }
        Log.i("CardViewFragment","mImageId:"+mImageId);
        // Inflate the layout for this fragment
        final View rootView = inflater.inflate(R.layout.fragment_card_view, container, false);
        imageView = (ImageView) rootView.findViewById(R.id.card_image);
        // If a list of image ids exists, set the image resource to the correct item in that list
        // Otherwise, create a Log statement that indicates that the list was not found
        if(mImageId != 0){
            // Set the image resource to the list item at the stored index
            imageView.setImageResource(mImageId);

            // Set a click listener on the image view
            imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    onCardPressed(CardViewFragment.this);
                }
            });

        }
        return rootView;
    }
    // Setter methods for keeping track of the list images this fragment can display and which image
    // in the list is currently being displayed

    public void setImageId(int mImageId) {
        this.mImageId = mImageId;
    }

    public void setOnCardSelectedListener(OnCardSelectedListener listener) {
        this.mListener = listener;
    }
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
//This is the NoSaveStateFrameLayout - force it to not clip
        ViewGroup frameLayout = (ViewGroup) view.getParent();
        frameLayout.setClipChildren(false);
        frameLayout.setClipToPadding(false);
    }
    /**
     * Save the current state of this fragment
     */
    @Override
    public void onSaveInstanceState(Bundle currentState) {
        if(mImageId!=0)
            currentState.putInt("mImageId", mImageId);
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onCardPressed(CardViewFragment card) {
        if (mListener != null) {
            mListener.onCardSelected(card);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (mListener instanceof OnCardSelectedListener ||mListener==null) {

        } else {
            Log.i("Card View Exception","Implement Listener");
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public int getImageId() {
        return mImageId;
    }
     public void changeImageResource(int resourceId) {
         mImageId=resourceId;
         if(imageView!=null)
             imageView.setImageResource(resourceId);
     }
    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnCardSelectedListener {

        void onCardSelected(CardViewFragment card);
    }
}
