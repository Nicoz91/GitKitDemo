package it.polimi.frontend.util;

import it.polimi.appengine.entity.manager.model.User;
import it.polimi.frontend.activity.R;

import java.util.Calendar;
import java.util.List;

import com.squareup.picasso.Picasso;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class UserAdapter extends ArrayAdapter<User>{

	private Context context;
	private List<User> users;
	private String NON_SPECIFICATO;

	public UserAdapter(Context context, int resource, List<User> objects) {
		super(context, R.layout.row_layout_request, objects);
		this.context=context;
		this.users=objects;
		NON_SPECIFICATO=context.getString(R.string.not_specified);
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View rowView;
		if (convertView==null){
			rowView = inflater.inflate(R.layout.row_layout_user, parent, false);
		} else {
			rowView = convertView;
		}
		//UPPER: Nome e cognome
		TextView textView = (TextView) rowView.findViewById(R.id.nomeCognome);
		String upper="";
		if (users.get(position).getName()!=null)
			upper+=users.get(position).getName()+" ";
		else
			upper+=NON_SPECIFICATO+" ";
		if(users.get(position).getSurname()!=null)
			upper+=users.get(position).getSurname();
		else
			upper+=NON_SPECIFICATO;
		textView.setText(upper);
		//LOWER: sesso e età
		TextView textView2 = (TextView) rowView.findViewById(R.id.sessoEta);
		String subText="";
		if (users.get(position).getGender())
			subText+=context.getString(R.string.detailMale);
		else
			subText+=context.getString(R.string.detailFemale);
		if (users.get(position).getBday()!=null){
			subText+=getAge(users.get(position).getBday().getValue())+context.getString(R.string.detailYears);
		} else 
			subText+=NON_SPECIFICATO;
		textView2.setText(subText);

		String photoUrl=users.get(position).getPhotoURL();
		if (photoUrl!=null){
			ImageView iV = (ImageView)rowView.findViewById(R.id.imgIcon);
			Picasso.with(context).load(users.get(position).getPhotoURL()).placeholder(R.drawable.default_photo).into(iV);
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

}
