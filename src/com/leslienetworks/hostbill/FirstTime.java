package com.leslienetworks.hostbill;
 
import android.app.Activity;
import android.database.DataSetObserver;
import android.graphics.Color;
import android.os.Bundle;
import android.view.*;
import android.view.ViewGroup.LayoutParams;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Gallery;
import android.widget.ImageView;
import android.widget.SpinnerAdapter;
import android.os.Handler;
import java.util.Timer;
import java.util.TimerTask;
import com.leslienetworks.hostbill.R;
 
public class FirstTime extends Activity {
 
    public int currentimageindex=0;
    Timer timer;
    TimerTask task;
    ImageView slidingimage;
 
    private int[] IMAGE_IDS = {
            R.drawable.slide1, R.drawable.slide2, R.drawable.slide3, R.drawable.slide4  };
 
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.firstview);
        Gallery gallery = (Gallery) findViewById(R.id.gallery);
        gallery.setAdapter(new SpinnerAdapter() {

			public int getCount() {
				return IMAGE_IDS.length;
			}

			public Object getItem(int arg0) {
				return arg0;
			}

			public long getItemId(int position) {
				// TODO Auto-generated method stub
				return position;
			}

			public int getItemViewType(int position) {
				// TODO Auto-generated method stub
				return position;
			}

			public View getView(int position, View convertView, ViewGroup parent) {
				ImageView imageView = new ImageView(getBaseContext());
				
				imageView.setImageResource(IMAGE_IDS[position]);
				imageView.setLayoutParams(new Gallery.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
				imageView.setScaleType(ImageView.ScaleType.FIT_XY);
				imageView.setBackgroundColor(Color.WHITE);
				
				return imageView;
			}

			public int getViewTypeCount() {
				// TODO Auto-generated method stub
				return 0;
			}

			public boolean hasStableIds() {
				// TODO Auto-generated method stub
				return false;
			}

			public boolean isEmpty() {
				// TODO Auto-generated method stub
				return false;
			}

			public void registerDataSetObserver(DataSetObserver observer) {
				// TODO Auto-generated method stub
				
			}

			public void unregisterDataSetObserver(DataSetObserver observer) {
				// TODO Auto-generated method stub
				
			}

			public View getDropDownView(int position, View convertView,
					ViewGroup parent) {
				// TODO Auto-generated method stub
				return null;
			}
        });

    }
 
}