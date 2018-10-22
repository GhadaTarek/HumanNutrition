package com.amg.humannutrition.Fragment;

import android.animation.TypeConverter;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.amg.humannutrition.Activity.MainActivity;
import com.amg.humannutrition.R;

import org.json.JSONObject;

import java.util.Objects;

import static android.content.Context.MODE_PRIVATE;
import static java.lang.String.format;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link HomeFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link HomeFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class HomeFragment extends Fragment {
//    // TODO: Rename parameter arguments, choose names that match
//    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
//    private static final String ARG_PARAM1 = "param1";
//    private static final String ARG_PARAM2 = "param2";
//
//    // TODO: Rename and change types of parameters
//    private String mParam1;
//    private String mParam2;

    private OnFragmentInteractionListener mListener;
    private CardView bmiCardView;
    private TextView bmi, bmiType;
    private TextView freeCalories, allCalories, burnedCalories;
    private TextView freeCarbs, allCarbs, burnedCarbs;
    private TextView freeProtein, allProtein, burnedProtein;
    private TextView freeFat, allFat, burnedFat;

    public HomeFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment HomeFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static HomeFragment newInstance(String param1, String param2) {
        HomeFragment fragment = new HomeFragment();
        Bundle args = new Bundle();
//        args.putString(ARG_PARAM1, param1);
//        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        if (getArguments() != null) {
//            mParam1 = getArguments().getString(ARG_PARAM1);
//            mParam2 = getArguments().getString(ARG_PARAM2);
//        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {

        bmiCardView = view.findViewById(R.id.bmiHome_card);

        bmi = view.findViewById(R.id.bmiFreeHome_text);
        bmiType = view.findViewById(R.id.bmiTypeHome_text);


        freeCalories = view.findViewById(R.id.caloreisFreeHome_text);
        allCalories = view.findViewById(R.id.caloreisAllHome_text);
        burnedCalories = view.findViewById(R.id.caloreisBurnedHome_text);

        freeCarbs = view.findViewById(R.id.carbsFreeHome_text);
        allCarbs = view.findViewById(R.id.carbsAllHome_text);
        burnedCarbs = view.findViewById(R.id.carbsBurnedHome_text);

        freeProtein = view.findViewById(R.id.proteinFreeHome_text);
        allProtein = view.findViewById(R.id.proteinAllHome_text);
        burnedProtein = view.findViewById(R.id.proteinBurnedHome_text);

        freeFat = view.findViewById(R.id.fatFreeHome_text);
        allFat = view.findViewById(R.id.fatAllHome_text);
        burnedFat = view.findViewById(R.id.fatBurnedHome_text);

        setUserData();

    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    private void setUserData() {
        String userJsonData = Get_UserData();
        try {
            JSONObject jsonObject = new JSONObject(userJsonData);
            bmi.setText(jsonObject.getString("BMI"));
            double bmiDouble = Double.parseDouble(bmi.getText().toString());
            if ( bmiDouble<18.5)
            {
                bmiCardView.setCardBackgroundColor(Color.parseColor("#F9F9F9"));
                bmiType.setText("Underweight");
            }
            else if( bmiDouble>18.5&& bmiDouble<24.9)
            {
                bmiCardView.setCardBackgroundColor(Color.parseColor("#A1D663"));
                bmiType.setText("Healthy Weight");
            }else if( bmiDouble>25&& bmiDouble<29.9)
            {
                bmiCardView.setCardBackgroundColor(Color.parseColor("#FFFB00"));
                bmiType.setText("Overweight");
            }else if( bmiDouble>30&& bmiDouble<34.9)
            {
                bmiCardView.setCardBackgroundColor(Color.parseColor("#FFD300"));
                bmiType.setText("Obese");
            }else if( bmiDouble>35&& bmiDouble<39.9)
            {
                bmiCardView.setCardBackgroundColor(Color.parseColor("#FF2600"));
                bmiType.setText("Severely Obese");
            }else
            {
                bmiCardView.setCardBackgroundColor(Color.parseColor("#8449B0"));
                bmiType.setText("Morbidly Obese");
            }
            allCalories.setText(jsonObject.getString("Calories"));
            freeCalories.setText(jsonObject.getString("Calories"));
            allCarbs.setText(String.format("%s g", jsonObject.getString("Carbs")));
            freeCarbs.setText(String.format("%s g", jsonObject.getString("Carbs")));
            allProtein.setText(String.format("%s g", jsonObject.getString("Protien")));
            freeProtein.setText(String.format("%s g", jsonObject.getString("Protien")));
            allFat.setText(String.format("%s g", jsonObject.getString("Fats")));
            freeFat.setText(String.format("%s g", jsonObject.getString("Fats")));
        } catch (Exception e) {
            Log.e(getString(R.string.TAG),e.toString());
        }

    }

    String Get_UserData() {
        SharedPreferences sharedPref = getContext().getSharedPreferences("UserData", MODE_PRIVATE);
        String restoredText = sharedPref.getString("JsonUserData", null);
        if (restoredText != null) {
//            String Name = sharedPref.getString("name", "No name defined");//"No name defined" is the default value.
//            String Email = sharedPref.getString("name", "No name defined");//"No name defined" is the default value.
            return restoredText;
        }
        return "null";
    }

//    @Override
//    public void onAttach(Context context) {
//        super.onAttach(context);
//
//    }


//
//    @Override
//    public void onDetach() {
//        super.onDetach();
//        mListener = null;
//    }

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
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}
