package cc.cnplay.uhf;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class LoginDbHelper extends SQLiteOpenHelper {

	private Context context;

	// 数据库名称
	private static final String name = "cnplay.db";

	// 数据库名称
	private static final String table = "login";

	// 数据库版本
	private static final int version = 1;

	public LoginDbHelper(Context context) {
		super(context, name, null, version);
		this.context = context;
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		StringBuffer sb = new StringBuffer();
		sb.append("CREATE TABLE IF NOT EXISTS ");
		sb.append(table);
		sb.append(" (id integer primary key autoincrement, username varchar(50), ");
		sb.append("password varchar(50), hostname varchar(50), reftoken varchar(512), acctoken varchar(512))");
		db.execSQL(sb.toString());
		UIHelper.ToastMessage(context, "CREATE TABLE IF NOT EXISTS ");
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int arg1, int arg2) {
		// TODO Auto-generated method stub

	}

	public void insert(Login login) {
		SQLiteDatabase database = this.getWritableDatabase();
		ContentValues values = new ContentValues();
		values.put("username", login.getUsername());
		values.put("password", login.getPassword());
		values.put("hostname", login.getHostname());
		values.put("acctoken", login.getAcctoken());
		values.put("reftoken", login.getReftoken());
		database.insert(table, null, values);
	}

	public void update(Login login) {
		SQLiteDatabase database = this.getWritableDatabase();
		ContentValues values = new ContentValues();
		values.put("password", login.getPassword());
		values.put("hostname", login.getHostname());
		values.put("acctoken", login.getAcctoken());
		values.put("reftoken", login.getReftoken());
		database.update(table, values, "username = ?",
				new String[] { login.getUsername() });
	}

	public void delete(String username) {
		SQLiteDatabase database = this.getWritableDatabase();
		database.delete(table, "username = ?", new String[] { username });
	}

	public Login getByUsername(String username) {
		Login login = null;
		SQLiteDatabase db = this.getReadableDatabase();
		Cursor cursor = null;
		try {
			cursor = db
					.query(table,
							new String[] { "username,password,hostname,acctoken,reftoken" },
							"username = ?", new String[] { username }, null,
							null, null, null);
			if (cursor.getCount() > 0) {
				login = new Login();
				while (cursor.moveToNext()) {
					login.setUsername(cursor.getColumnName(cursor
							.getColumnIndex("username")));
					login.setPassword(cursor.getColumnName(cursor
							.getColumnIndex("password")));
					login.setHostname(cursor.getColumnName(cursor
							.getColumnIndex("hostname")));
					login.setAcctoken(cursor.getColumnName(cursor
							.getColumnIndex("acctoken")));
					login.setReftoken(cursor.getColumnName(cursor
							.getColumnIndex("reftoken")));
					return login;
				}
			}
		} catch (Throwable ex) {
			UIHelper.ToastMessage(context, ex.getMessage());
		} finally {
			if (cursor != null)
				cursor.close();
		}
		return login;
	}

	public Login findOne() {
		Login login = null;
		SQLiteDatabase db = this.getReadableDatabase();
		Cursor cursor = null;
		try {
			cursor = db
					.query(table,
							new String[] { "username,password,hostname,acctoken,reftoken" },
							null, null, null, null, null, null);
			if (cursor.getCount() > 0) {
				login = new Login();
				while (cursor.moveToNext()) {
					login.setUsername(cursor.getColumnName(cursor
							.getColumnIndex("username")));
					login.setPassword(cursor.getColumnName(cursor
							.getColumnIndex("password")));
					login.setHostname(cursor.getColumnName(cursor
							.getColumnIndex("hostname")));
					login.setAcctoken(cursor.getColumnName(cursor
							.getColumnIndex("acctoken")));
					login.setReftoken(cursor.getColumnName(cursor
							.getColumnIndex("reftoken")));
					return login;
				}
			}
		} catch (Throwable ex) {
			UIHelper.ToastMessage(context, ex.getMessage());
		} finally {
			if (cursor != null)
				cursor.close();
		}
		return login;
	}
}
