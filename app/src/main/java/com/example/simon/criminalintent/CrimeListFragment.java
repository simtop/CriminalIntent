package com.example.simon.criminalintent;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

public class CrimeListFragment extends Fragment {

    private RecyclerView mCrimeRecyclerView;
    private CrimeAdapter mAdapter;
    private int mClickedItemPosition;
    private boolean mSubtitleVisible;

    private TextView mEmptyTextView;
    private Button mAddCrimeButton;

    private Callbacks mCallbacks;

    private static final String SAVED_SUBTITLE_VISIBLE = "subtitle";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.fragment_crime_list,menu);

        MenuItem subtitleItem = menu.findItem(R.id.show_subtitle);
        if (mSubtitleVisible) {
            subtitleItem.setTitle(R.string.hide_subtitle);
        } else {
            subtitleItem.setTitle(R.string.show_subtitle);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.new_crime:
                createNewCrimeAndStart();
                return true;
            case R.id.show_subtitle:
                mSubtitleVisible = !mSubtitleVisible;
                getActivity().invalidateOptionsMenu();
                updateSubtitle();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void createNewCrimeAndStart() {
        Crime crime = new Crime();
        CrimeLab.get(getActivity()).addCrime(crime);
        //Intent intent = CrimePagerActivity
          //      .newIntent(getActivity(), crime.getId());
        //startActivity(intent);
        updateUI();
        mCallbacks.onCrimeSelected(crime);
    }

    private void updateSubtitle() {
        CrimeLab crimeLab = CrimeLab.get(getActivity());
        int crimeCount = crimeLab.getCrimes().size();

        String subtitle = getResources()
                .getQuantityString(R.plurals.subtitle_plural, crimeCount, crimeCount);

        if (!mSubtitleVisible) {
            subtitle = null;
        }

        AppCompatActivity activity = (AppCompatActivity) getActivity();
        activity.getSupportActionBar().setSubtitle(subtitle);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_crime_list,container,false);

        mCrimeRecyclerView = view.findViewById(R.id.crime_recycler_view);
        mCrimeRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        mEmptyTextView = view.findViewById(R.id.empty_text);
        mAddCrimeButton = view.findViewById(R.id.new_crime_button);
        mAddCrimeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                createNewCrimeAndStart();
            }
        });

        if (savedInstanceState != null) {
            mSubtitleVisible = savedInstanceState.getBoolean(SAVED_SUBTITLE_VISIBLE);
        }

        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(new ItemTouchHelper.
                                SimpleCallback(ItemTouchHelper.UP | ItemTouchHelper.DOWN,
                ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(RecyclerView recyclerView,
                                  RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
                if (direction == ItemTouchHelper.RIGHT) {
                    mAdapter.swipeToDelete(viewHolder.getAdapterPosition());
                    updateUI();
                }
            }
        });

        itemTouchHelper.attachToRecyclerView(mCrimeRecyclerView);


        updateUI();

        return view;
    }

    /**
     * Required interface for hosting activities
     */
    public interface Callbacks {
        void onCrimeSelected(Crime crime);
        void onCrimeDeleted(Crime crime);
    }




    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mCallbacks = (Callbacks) context;
    }


    @Override
    public void onDetach() {
        super.onDetach();
        mCallbacks = null;
    }

    @Override
    public void onResume() {
        super.onResume();
        updateUI();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean(SAVED_SUBTITLE_VISIBLE, mSubtitleVisible);
    }

    public void updateUI() {
        CrimeLab crimeLab = CrimeLab.get(getActivity());
        List<Crime> crimes = crimeLab.getCrimes();

        if (crimes.size() == 0) {
            mEmptyTextView.setVisibility(View.VISIBLE);
            mAddCrimeButton.setVisibility(View.VISIBLE);
        } else {
            mEmptyTextView.setVisibility(View.GONE);
            mAddCrimeButton.setVisibility(View.GONE);
        }

        if(mAdapter == null){
            mAdapter = new CrimeAdapter(crimes);
            mCrimeRecyclerView.setAdapter(mAdapter);
        }
        else {
            mAdapter.setCrimes(crimes);
            //Deleted because notifyItemChanged can't  work when the item is deleted
            //mAdapter.notifyItemChanged(mClickedItemPosition);
            mAdapter.notifyDataSetChanged();
        }
        updateSubtitle();
    }

    private class CrimeHolder extends RecyclerView.ViewHolder
            implements View.OnClickListener {

        private TextView mTitleTextView;
        private TextView mDateTextView;

        private Crime mCrime;

        private ImageView mSolvedImageView;

        public CrimeHolder(LayoutInflater inflater, ViewGroup parent) {
            super(inflater.inflate(R.layout.list_item_crime, parent, false));

            mTitleTextView = itemView.findViewById(R.id.crime_title);
            mDateTextView  = itemView.findViewById(R.id.crime_date);
            mSolvedImageView = itemView.findViewById(R.id.crime_solved);
            itemView.setOnClickListener(this);
        }

        public void bind (Crime crime){
            mCrime = crime;
            mTitleTextView.setText(crime.getTitle());
            mDateTextView.setText(getFormattedDate());
            mSolvedImageView.setVisibility(crime.isSolved() ? View.VISIBLE : View.GONE);
        }

        private String getFormattedDate() {
            Locale locale = getResources().getConfiguration().locale;
            Locale.setDefault(locale);
            SimpleDateFormat formatter = new SimpleDateFormat("EEEE, MMM d, yyyy");
            return formatter.format(mCrime.getDate());
        }

        private String getFormattedTime (){
            Locale locale = getResources().getConfiguration().locale;
            Locale.setDefault(locale);
            SimpleDateFormat formatter = new SimpleDateFormat("HH:mm");
            String crimeTime = formatter.format(mCrime.getTime());
            return crimeTime;
        }

        @Override
        public void onClick(View view) {
            mClickedItemPosition = getAdapterPosition();
            //Intent intent =  CrimePagerActivity.newIntent(getActivity(),mCrime.getId());
            //startActivity(intent);
            //TODO: Check if needed
            mCallbacks.onCrimeSelected(mCrime);

        }
    }

    private class CrimeAdapter extends RecyclerView.Adapter<CrimeHolder> {

        private List<Crime> mCrimes;

        public CrimeAdapter(List<Crime> crimes) {
            mCrimes = crimes;
        }

        @NonNull
        @Override
        public CrimeHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {
            LayoutInflater layoutInflater = LayoutInflater.from(getActivity());
            return new CrimeHolder(layoutInflater,parent);
        }

        @Override
        public void onBindViewHolder(@NonNull CrimeHolder holder, int position) {
            Crime crime = mCrimes.get(position);
            holder.bind(crime);
        }

        @Override
        public int getItemCount() {
            return mCrimes.size();
        }

        public void setCrimes(List<Crime> crimes) {
            mCrimes = crimes;
        }



        @Override
        public int getItemViewType(int position) {
            Crime crime = mCrimes.get(position);
            if (crime.isRequiresPolice()) {
                return R.layout.list_item_crime_police;
            } else {
                return R.layout.list_item_crime;
            }
        }

        public void swipeToDelete(int position) {
            CrimeLab crimeLab = CrimeLab.get(getActivity());
            Crime crime = mCrimes.get(position);
            crimeLab.removeCrime(crime);
            File photoFile = CrimeLab.get(getActivity()).getPhotoFile(crime);
            File file = new File(photoFile.getPath());
            if (file.exists()) {
                file.delete();
            }
            mAdapter.notifyItemRemoved(position);
            mAdapter.notifyItemRangeChanged(position, crimeLab.getCrimes().size());
            Toast.makeText(getContext(), R.string.toast_crime_deleted, Toast.LENGTH_SHORT).show();
            notShowCrimeFragment();


        }

        private void notShowCrimeFragment() {

            if (getActivity().findViewById(R.id.detail_fragment_container) == null) {
            } else {

                List<Crime> crimes = CrimeLab.get(getActivity()).getCrimes();
                if (!crimes.isEmpty()) {
                    mCallbacks.onCrimeDeleted(crimes.get(0));
                } else {
                    LinearLayout layout = (LinearLayout) getActivity()
                            .findViewById(R.id.fragment_crime_layout);
                    layout.setVisibility(View.GONE);
                }
            }
        }
    }
}
