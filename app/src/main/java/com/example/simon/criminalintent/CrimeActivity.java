package com.example.simon.criminalintent;

import android.content.Context;
import android.content.Intent;
import android.support.v4.app.Fragment;

import java.util.UUID;

public class CrimeActivity extends SingleFragmentActivity {

    public static final String EXTRA_CRIME_ID = "com.example.simon.criminalintent.crime_id";

    public static Intent newIntent(Context package_context, UUID crime_id){
        Intent intent = new Intent(package_context,CrimeActivity.class);
        intent.putExtra(EXTRA_CRIME_ID, crime_id);
        return intent;
    }

    @Override
    protected Fragment createFragment() {
        UUID crimeId = (UUID) getIntent().getSerializableExtra(EXTRA_CRIME_ID);
        return CrimeFragment.newInstance(crimeId);
    }
}
