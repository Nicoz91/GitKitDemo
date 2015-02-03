package it.polimi.frontend.util;

import it.polimi.appengine.entity.manager.model.Feedback;
import it.polimi.frontend.activity.HttpUtils;
import it.polimi.frontend.activity.R;

import java.io.IOException;
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
import android.widget.RatingBar;
import android.widget.TextView;

public class FeedbackAdapter extends ArrayAdapter<Feedback>{

	private Context context;
	private List<Feedback> feedbacks;
	private View rowView;

	public FeedbackAdapter(Context context, int resource, List<Feedback> objects) {
		super(context, R.layout.row_layout_feedback, objects);
		this.context=context;
		this.feedbacks=objects;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		if (convertView==null){
			rowView = inflater.inflate(R.layout.row_layout_feedback, parent, false);
		} else {
			rowView = convertView;
		}
//		System.out.println("Position: "+position);
		try{
			TextView reviewer = (TextView) rowView.findViewById(R.id.reviewer);
			reviewer.setText(feedbacks.get(position).getFrom().getName()+" "+
								feedbacks.get(position).getFrom().getSurname()+" ");
			TextView comment = (TextView) rowView.findViewById(R.id.comment);
			comment.setText(feedbacks.get(position).getDescription());

			RatingBar valutazione = (RatingBar) rowView.findViewById(R.id.valutazione);
			valutazione.setRating(feedbacks.get(position).getEvaluation());
			//TODO task profile image
			String photoUrl=feedbacks.get(position).getFrom().getPhotoURL();
			if (photoUrl!=null){
				new ProfileImageTask().execute(photoUrl);
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
			if (bitmap != null) {
				ImageView pictureView = (ImageView) rowView.findViewById(R.id.imgIcon);
				pictureView.setImageBitmap(bitmap);
			}
		}
	}

}
