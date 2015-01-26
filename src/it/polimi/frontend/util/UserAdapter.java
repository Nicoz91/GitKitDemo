package it.polimi.frontend.util;

import it.polimi.appengine.entity.manager.model.User;
import it.polimi.frontend.activity.HttpUtils;
import it.polimi.frontend.activity.R;

import java.io.IOException;
import java.util.Calendar;
import java.util.List;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class UserAdapter extends ArrayAdapter<User>{

	private Context context;
	private List<User> users;
	private View rowView;

	public UserAdapter(Context context, int resource, List<User> objects) {
		super(context, R.layout.row_layout_request, objects);
		this.context=context;
		this.users=objects;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		if (convertView==null){
			rowView = inflater.inflate(R.layout.row_layout_user, parent, false);
		} else {
			rowView = convertView;
		}
		System.out.println("Position: "+position);
		try{
			TextView textView = (TextView) rowView.findViewById(R.id.nomeCognome);
			textView.setText(users.get(position).getName()+" "+
								users.get(position).getSurname()+" ");
			TextView textView2 = (TextView) rowView.findViewById(R.id.sessoEta);
			//TODO controllare che il long tornato da getValue sia tempo corretto
			textView2.setText( (users.get(position).getGender() ? "M, " : "F, ") +
					getAge(users.get(position).getBday().getValue())+" anni");

			//TODO task profile image
			String photoUrl=users.get(position).getPhotoURL();
			if (photoUrl!=null){
				new ProfileImageTask().execute(photoUrl);
			}

		}catch(Exception e){
			System.out.println("Trovata eccezione cerco di recuperare");
		}
		return rowView;
	}
	
	private int getAge(long bDayInMillis){
		int age=0;
		Calendar bDay = Calendar.getInstance();
		bDay.setTimeInMillis(bDayInMillis);
		Calendar now = Calendar.getInstance();
		age = now.get(Calendar.YEAR)-bDay.get(Calendar.YEAR);
		if(now.get(Calendar.DAY_OF_YEAR)<=bDay.get(Calendar.DAY_OF_YEAR))
			age--;
		return age;
	}
	
	private class ProfileImageTask extends AsyncTask<String, Void, Bitmap>{
		@Override
		protected Bitmap doInBackground(String... arg) {
			try {
				byte[] result = HttpUtils.get(arg[0]);
				return BitmapFactory.decodeByteArray(result, 0, result.length);
			} catch (IOException e) {
				return null;
			}
		}

		@Override
		protected void onPostExecute(Bitmap bitmap) {
			if (bitmap != null) {
				ImageView pictureView = (ImageView) rowView.findViewById(R.id.imgIcon);
				pictureView.setImageBitmap(bitmap);
			}
		}
	}

}
