package it.polimi.frontend.util;

import java.io.IOException;
import java.util.List;

import it.polimi.appengine.entity.manager.model.Request;
import it.polimi.frontend.activity.HttpUtils;
import it.polimi.frontend.activity.R;
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

public class RequestAdapter extends ArrayAdapter<Request>{

	private Context context;
	private List<Request> reqs;
	private View rowView;

	public RequestAdapter(Context context, int resource, List<Request> objects) {
		super(context, R.layout.row_layout, objects);
		this.context=context;
		this.reqs=objects;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		if (convertView==null){
			rowView = inflater.inflate(R.layout.row_layout, parent, false);
		} else {
			rowView = convertView;
		}
		System.out.println("Position: "+position);
		try{
			TextView textView = (TextView) rowView.findViewById(R.id.txtTitle);
			textView.setText(reqs.get(position).getOwner().getName()+" "+
								reqs.get(position).getOwner().getSurname()+" ");
			TextView textView2 = (TextView) rowView.findViewById(R.id.subString);
			textView2.setText(reqs.get(position).getTitle());

			//TODO task profile image
			String photoUrl=reqs.get(position).getOwner().getPhotoURL();
			if (photoUrl!=null){
				new ProfileImageTask().execute(photoUrl);
			}

		}catch(Exception e){
			System.out.println("Trovata eccezione cerco di recuperare");
		}
		return rowView;
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
