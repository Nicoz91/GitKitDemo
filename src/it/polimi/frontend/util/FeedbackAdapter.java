package it.polimi.frontend.util;

import it.polimi.appengine.entity.manager.model.Feedback;
import it.polimi.frontend.activity.R;

import java.util.List;

import com.squareup.picasso.Picasso;

import android.content.Context;
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
	private String NON_SPECIFICATO;

	public FeedbackAdapter(Context context, int resource, List<Feedback> objects) {
		super(context, R.layout.row_layout_feedback, objects);
		this.context=context;
		this.feedbacks=objects;
		NON_SPECIFICATO=context.getString(R.string.not_specified);
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View rowView;
		if (convertView==null){
			rowView = inflater.inflate(R.layout.row_layout_feedback, parent, false);
		} else {
			rowView = convertView;
		}
		TextView reviewer = (TextView) rowView.findViewById(R.id.reviewer);
		TextView comment = (TextView) rowView.findViewById(R.id.comment);
		RatingBar valutazione = (RatingBar) rowView.findViewById(R.id.valutazione);
		if (feedbacks.get(position).getFrom()!=null){
			//REVIWER nome e cognome
			String upper="";
			if (feedbacks.get(position).getFrom().getName()!=null)
				upper+=feedbacks.get(position).getFrom().getName()+" ";
			else
				upper+=NON_SPECIFICATO+" ";
			if(feedbacks.get(position).getFrom().getSurname()!=null)
				upper+=feedbacks.get(position).getFrom().getSurname();
			else
				upper+=NON_SPECIFICATO;
			reviewer.setText(upper);
			//COMMENT
			if (feedbacks.get(position).getDescription()!=null)
				comment.setText(feedbacks.get(position).getDescription());
			else 
				comment.setText(NON_SPECIFICATO);
			//VALUTAZIONE
			if (feedbacks.get(position).getEvaluation()!=null)
				valutazione.setRating(feedbacks.get(position).getEvaluation());
			else 
				valutazione.setRating(0);
			//IMMAGINE
			String photoUrl=feedbacks.get(position).getFrom().getPhotoURL();
			if (photoUrl!=null){
				ImageView iV = (ImageView) rowView.findViewById(R.id.imgIcon);
				Picasso.with(context).load(photoUrl).placeholder(R.drawable.default_photo).into(iV);
			}
		} else {
			reviewer.setText(NON_SPECIFICATO+" "+NON_SPECIFICATO);
			comment.setText(NON_SPECIFICATO);
			valutazione.setRating(0);
		}
		return rowView;
	}

}
