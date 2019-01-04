package com.example.simon.criminalintent.database;

import android.database.Cursor;
import android.database.CursorWrapper;

import com.example.simon.criminalintent.Crime;
import com.example.simon.criminalintent.database.CrimeDbSchema.CrimeTable;

import java.util.Date;
import java.util.UUID;

public class CrimeCursorWrapper extends CursorWrapper {
    public CrimeCursorWrapper(Cursor cursor) {
        super(cursor);
    }

    public Crime getCrime() {
        String uuidString = getString(getColumnIndex(CrimeTable.Cols.UUID));
        String title = getString(getColumnIndex(CrimeTable.Cols.TITLE));
        long date = getLong(getColumnIndex(CrimeTable.Cols.DATE));
        long time = getLong(getColumnIndex(CrimeTable.Cols.TIME));
        int isSolved = getInt(getColumnIndex(CrimeTable.Cols.SOLVED));
        String suspect  = getString(getColumnIndex(CrimeTable.Cols.SUSPECT));
        String contact  = getString(getColumnIndex(CrimeTable.Cols.CONTACT));


        Crime crime = new Crime(UUID.fromString(uuidString));
        crime.setTitle(title);
        crime.setDate(new Date(date));
        crime.setTime(new Date(time));
        crime.setSolved(isSolved != 0);
        crime.setSuspect(suspect);
        crime.setContactNumber(contact);
        return crime;
    }
}
