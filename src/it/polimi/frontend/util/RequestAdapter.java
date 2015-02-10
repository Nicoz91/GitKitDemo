package it.polimi.frontend.util;

import it.polimi.appengine.entity.manager.model.Request;
import it.polimi.frontend.activity.HttpUtils;
import it.polimi.frontend.activity.R;

import java.io.IOException;
import java.net.URL;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import com.squareup.picasso.Picasso;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class RequestAdapter extends ArrayAdapter<Request>{

	private Context context;
	private List<Request> reqs;
	//private View rowView;
	public final static int ALL_REQUEST=0;
	public final static int OWNER_REQUEST=1;
	public final static int JOINED_REQUEST=2;
	private int listMode=0;

	public RequestAdapter(Context context, int resource, List<Request> objects, int listMode) {
		super(context, R.layout.row_layout_request, objects);
		this.context=context;
		this.reqs=objects;
		this.listMode=listMode;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View rowView;
		if (convertView==null){
			rowView = inflater.inflate(R.layout.row_layout_request, parent, false);
		} else {
			rowView = convertView;
		}
		//		System.out.println("Position: "+position);
		try{

			TextView textView = (TextView) rowView.findViewById(R.id.txtTitle);
			TextView textView2 = (TextView) rowView.findViewById(R.id.subString);
			switch (listMode) {
			case OWNER_REQUEST:
				textView.setText(reqs.get(position).getTitle());
				((ImageView) rowView.findViewById(R.id.imgIcon)).setVisibility(View.GONE);
				if (reqs.get(position).getStart()!=null && reqs.get(position).getEnd()!=null){
					//Start date
					Calendar start = Calendar.getInstance();
					start.setTimeInMillis(reqs.get(position).getStart().getValue());
					String dataInizio = start.get(Calendar.DAY_OF_MONTH)+" "+
							start.getDisplayName(Calendar.MONTH, Calendar.SHORT, Locale.ITALIAN)
							+" "+start.get(Calendar.YEAR);
					//Duration
					long durationMs = reqs.get(position).getStart().getValue()-reqs.get(position).getEnd().getValue();
					long duration = durationMs / 1000;
					long h = duration / 3600;
					long m = (duration - h * 3600) / 60;
					long s = duration - (h * 3600 + m * 60);
					String durationValue;
					if (h == 0)
						durationValue = m+"m:"+s+"s";
					else 
						durationValue = h+"h:"+m+"m:"+s+"s";
					textView2.setText("Data: "+dataInizio+", Durata: "+durationValue);
				} else 
					textView2.setText("Tempi non specificati.");
				break;
			default: //ALL_REQUEST e JOINED REQUEST al momento
				textView.setText(reqs.get(position).getOwner().getName()+" "+
						reqs.get(position).getOwner().getSurname()+" ");
				textView2.setText(reqs.get(position).getTitle());
				ImageView iV = (ImageView) rowView.findViewById(R.id.imgIcon);
				//TODO task profile image
				String photoUrl=reqs.get(position).getOwner().getPhotoURL();
				if (photoUrl!=null){
					Picasso.with(context).load(photoUrl).into(iV);
					//new ProfileImageTask().execute(photoUrl);
				}
				break;
			}


		}catch(Exception e){
			System.out.println("Trovata eccezione: Probabilmente uno dei campi null");
			e.printStackTrace();
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
			View rowView=null; //TODO tolta temporaneamente var globale statica
			if (bitmap != null) {
				ImageView pictureView = (ImageView) rowView.findViewById(R.id.imgIcon);
				pictureView.setImageBitmap(bitmap);
			}
		}
	}

	private static class ViewHolder {
		ImageView imageView;
		Bitmap bitmap;
		String imageURL;
	}


	private class DownloadAsyncTask extends AsyncTask<ViewHolder, Void, ViewHolder> {
		@Override
		protected ViewHolder doInBackground(ViewHolder... params) {
			//load image directly
			ViewHolder viewHolder = params[0];
			try {
				URL imageURL = new URL(viewHolder.imageURL);
				viewHolder.bitmap = BitmapFactory.decodeStream(imageURL.openStream());
			} catch (IOException e) {
				Log.e("error", "Downloading Image Failed");
				viewHolder.bitmap = null;
			}

			return viewHolder;
		}

		@Override
		protected void onPostExecute(ViewHolder result) {
			if (result.bitmap != null)
				result.imageView.setImageBitmap(result.bitmap);
		}
	}
}


