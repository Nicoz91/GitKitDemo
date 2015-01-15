package it.polimi.frontend.task;

import it.polimi.frontend.activity.HttpUtils;

import java.io.IOException;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;

public class ProfileImageTask extends AsyncTask<String, Void, Bitmap>{
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
			//pictureView.setImageBitmap(bitmap);
		}
	}
}
