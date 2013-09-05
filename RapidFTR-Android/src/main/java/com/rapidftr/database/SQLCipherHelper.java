package com.rapidftr.database;

import android.content.Context;
import com.google.inject.Inject;
import com.rapidftr.database.migration.Migrations;
import com.rapidftr.model.User;
import lombok.Getter;
import net.sqlcipher.database.SQLiteDatabase;
import net.sqlcipher.database.SQLiteOpenHelper;

public class SQLCipherHelper extends SQLiteOpenHelper implements DatabaseHelper {

    public static final int DB_VERSION = 2;

    protected @Getter final DatabaseSession session;

    @Inject
    public SQLCipherHelper(User user, Context context) {
        super(context, user.getDbName(), null, DB_VERSION);
        SQLiteDatabase.loadLibs(context);

        this.session = new SQLCipherSession(getWritableDatabase(user.getDbKey()));
    }

    @Override
    public void onCreate(SQLiteDatabase database) {
        for (Migrations migration : Migrations.values()) {
            database.execSQL(migration.getSql());
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase database, int oldVersion, int newVersion) {
        for (Migrations migration : Migrations.forVersion(newVersion)) {
            database.execSQL(migration.getSql());
        }
    }

    @Override
    public void close() {
        super.close();
        SQLiteDatabase.releaseMemory();
    }

}
