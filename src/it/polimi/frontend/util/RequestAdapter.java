package it.polimi.frontend.util;

import it.polimi.appengine.entity.manager.model.Request;
import it.polimi.frontend.activity.R;

import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import com.squareup.picasso.Picasso;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class RequestAdapter extends ArrayAdapter<Request>{

	private Context context;
	private List<Request> reqs;
	public final static int ALL_REQUEST=0;
	public final static int OWNER_REQUEST=1;
	public final static int JOINED_REQUEST=2;
	private int listMode=0;
	private String NON_SPECIFICATO;

	public RequestAdapter(Context context, int resource, List<Request> objects, int listMode) {
		super(context, R.layout.row_layout_request, objects);
		this.context=context;
		this.reqs=objects;
		this.listMode=listMode;
		NON_SPECIFICATO=context.getString(R.string.not_specified);
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View rowView;
		if (convertView==null)
			rowView = inflater.inflate(R.layout.row_layout_request, parent, false);
		else 
			rowView = convertView;
		TextView textView = (TextView) rowView.findViewById(R.id.txtTitle);
		TextView textView2 = (TextView) rowView.findViewById(R.id.subString);
		switch (listMode) {
		case OWNER_REQUEST:
			//Upper: Titolo
			if(reqs.get(position).getTitle()!=null)
				textView.setText(reqs.get(position).getTitle());
			else
				textView.setText(NON_SPECIFICATO);
			//ProfileImage nascosta
			((ImageView) rowView.findViewById(R.id.imgIcon)).setVisibility(View.GONE);

			//Lower: Data, durata
			String lower=context.getString(R.string.startDateLabel);
			//Data
			if (reqs.get(position).getStart()!=null){
				Calendar start = Calendar.getInstance();
				start.setTimeInMillis(reqs.get(position).getStart().getValue());
				String dataInizio = start.get(Calendar.DAY_OF_MONTH)+" "+
						start.getDisplayName(Calendar.MONTH, Calendar.SHORT, Locale.ITALIAN)
						+" "+start.get(Calendar.YEAR)+" "+start.get(Calendar.HOUR_OF_DAY)+":"
								+start.get(Calendar.MINUTE);
				lower+=dataInizio+", "+context.getString(R.string.durationLabel);
			} else
				lower+=NON_SPECIFICATO+", "+context.getString(R.string.durationLabel);
			//Durata
			if(reqs.get(position).getEnd()!=null){
				long durationMs = reqs.get(position).getEnd().getValue()-reqs.get(position).getStart().getValue();
				long duration = durationMs / 1000;
				long h = duration / 3600;
				long m = (duration - h * 3600) / 60;
				String durationValue;
				if (h == 0)
					durationValue = m+"m";
				else 
					durationValue = h+"h:"+m+"m";
				lower+=durationValue;
			} else
				lower+=NON_SPECIFICATO;
			textView2.setText(lower);
			break;
		default: //ALL_REQUEST e JOINED REQUEST al momento
			if (reqs.get(position).getOwner()!=null) {
				//Nome Cognome
				String upper="";
				if (reqs.get(position).getOwner().getName()!=null) 
					upper+=reqs.get(position).getOwner().getName()+" ";
				else
					upper+=NON_SPECIFICATO+" ";
				if (reqs.get(position).getOwner().getSurname()!=null) 
					upper+=reqs.get(position).getOwner().getSurname();
				else
					upper+=NON_SPECIFICATO;
				textView.setText(upper);
				//Titolo
				if(reqs.get(position).getTitle()!=null)
					textView2.setText(reqs.get(position).getTitle());
				else
					textView2.setText(NON_SPECIFICATO);
				//Photo
				String photoUrl = reqs.get(position).getOwner().getPhotoURL();
				if (photoUrl != null){
					ImageView iV = (ImageView) rowView.findViewById(R.id.imgIcon);
					Picasso.with(context).load(photoUrl).into(iV);
				}
			} else {
				//Upper
				textView.setText(NON_SPECIFICATO);
				//Lower
				if(reqs.get(position).getTitle()!=null)
					textView2.setText(reqs.get(position).getTitle());
				else
					textView2.setText(NON_SPECIFICATO);
			}
			break;
		}
		return rowView;
	}
}


