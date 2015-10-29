package com.star.criminalintent;


import android.app.Activity;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;

import java.util.Date;
import java.util.UUID;


public class CrimeFragment extends Fragment {

    private static final String ARG_CRIME_ID = "crime_id";
    private static final String DIALOG_DATE = "DialogDate";
    private static final String DIALOG_TIME = "DialogTime";

    public static final String EXTRA_DATE = "date";

    private static final int REQUEST_CODE = 0;

    private Crime mCrime;
    private EditText mTitleField;

    private Button mDateButton;
    private Button mTimeButton;
    private CheckBox mSolvedCheckBox;

    public static CrimeFragment newInstance(UUID crimeId) {
        Bundle args = new Bundle();
        args.putSerializable(ARG_CRIME_ID, crimeId);

        CrimeFragment crimeFragment = new CrimeFragment();
        crimeFragment.setArguments(args);

        return crimeFragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

        UUID crimeId = (UUID) getArguments().getSerializable(ARG_CRIME_ID);

        mCrime = CrimeLab.getInstance(getContext()).getCrime(crimeId);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_crime, container, false);

        mTitleField = (EditText) view.findViewById(R.id.crime_title);
        mTitleField.setText(mCrime.getTitle());
        mTitleField.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                mCrime.setTitle(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        mDateButton = (Button) view.findViewById(R.id.crime_date);
        updateDate();
        mDateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (getResources().getConfiguration().orientation
                        == Configuration.ORIENTATION_LANDSCAPE) {
                    FragmentManager fragmentManager = getFragmentManager();
                    DatePickerFragment datePickerFragment =
                            DatePickerFragment.newInstance(mCrime.getDate());
                    datePickerFragment.setTargetFragment(CrimeFragment.this, REQUEST_CODE);
                    datePickerFragment.show(fragmentManager, DIALOG_DATE);
                } else if (getResources().getConfiguration().orientation
                        == Configuration.ORIENTATION_PORTRAIT) {
                    Intent intent = new Intent(CrimeFragment.this.getActivity(),
                            DatePickerActivity.class);
                    intent.putExtra(EXTRA_DATE, mCrime.getDate());
                    startActivityForResult(intent, REQUEST_CODE);
                }

            }
        });

        mTimeButton = (Button) view.findViewById(R.id.crime_time);
        updateTime();
        mTimeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (getResources().getConfiguration().orientation
                        == Configuration.ORIENTATION_LANDSCAPE) {
                    FragmentManager fragmentManager = getFragmentManager();
                    TimePickerFragment timePickerFragment =
                            TimePickerFragment.newInstance(mCrime.getDate());
                    timePickerFragment.setTargetFragment(CrimeFragment.this, REQUEST_CODE);
                    timePickerFragment.show(fragmentManager, DIALOG_TIME);
                } else if (getResources().getConfiguration().orientation
                        == Configuration.ORIENTATION_PORTRAIT) {
                    Intent intent = new Intent(CrimeFragment.this.getActivity(),
                            TimePickerActivity.class);
                    intent.putExtra(EXTRA_DATE, mCrime.getDate());
                    startActivityForResult(intent, REQUEST_CODE);
                }
            }
        });

        mSolvedCheckBox = (CheckBox) view.findViewById(R.id.crime_solved);
        mSolvedCheckBox.setChecked(mCrime.isSolved());
        mSolvedCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                mCrime.setSolved(isChecked);
            }
        });

        return  view;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            Date date = (Date) data.getSerializableExtra(PickerFragment.EXTRA_DATE);
            mCrime.setDate(date);
            updateDate();
            updateTime();
        }
    }

    private void updateDate() {
        mDateButton.setText(mCrime.getFormattedDate());
    }

    private void updateTime() {
        mTimeButton.setText(mCrime.getFormattedTime());
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);

        inflater.inflate(R.menu.fragment_crime, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_item_delete_crime:
                if (mCrime != null) {
                    CrimeLab.getInstance(getContext()).removeCrime(mCrime);
                    getActivity().finish();
                }
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}