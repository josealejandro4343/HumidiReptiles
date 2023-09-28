package com.example.humidireptiles;

import androidx.fragment.app.Fragment;

/**
 * A simple {@link //Fragment} subclass.
 * Use the {@link //ChooseTypeofPetFrag#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ChooseTypeofPetFrag extends Fragment {/*
    RecyclerView newrecyclerView;
    ChosenPetAdapter ChosenPetAdapter;

    RelativeLayout addOtherpet;
    Button addOtherpetbtn;
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public ChooseTypeofPetFrag() {
    }

    public static ChooseTypeofPetFrag newInstance(String param1, String param2) {
        ChooseTypeofPetFrag fragment = new ChooseTypeofPetFrag();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view=inflater.inflate(R.layout.fragment_choose_typeof_pet, container, false);
        newrecyclerView = view.findViewById(R.id.recyclerView_newfr);
        newrecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        FirebaseRecyclerOptions<ChosenPet> options =
                new FirebaseRecyclerOptions.Builder<ChosenPet>()
                        .setQuery(FirebaseDatabase.getInstance().getReference().child("ChosenPet"), ChosenPet.class)
                        .build();

        ChosenPetAdapter = new ChosenPetAdapter(options);
        newrecyclerView.setAdapter(ChosenPetAdapter);

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        addOtherpet.postDelayed(new Runnable() {
            public void run() {
                addOtherpetbtn.setVisibility(View.VISIBLE);
            }
        }, 2000);
        ChosenPetAdapter.startListening();
    }

    @Override
    public void onStop() {
        super.onStop();
        ChosenPetAdapter.stopListening();
    }*/
}