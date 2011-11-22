package de.jojahn.campus.tools;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;
import de.jojahn.campus.R;

public class TutorialHelper {	
	private Context mContext;	
	private FrameLayout mContainer;
	private FrameLayout mTutorialView;
	
	private TextView mTextView;
	private ScrollView mScrollView;
	private ImageView mImageView;	
	
	// Constructor
	public TutorialHelper(Context context, FrameLayout view) {
		mContext = context;		
		mContainer = view;	
		
		mTutorialView = (FrameLayout) LayoutInflater.from(mContext).inflate(R.layout.tutorial, null);
		mTutorialView.setOnClickListener(new OnClickListener() {			
			@Override
			public void onClick(View arg0) {
				mTutorialView.setVisibility(View.GONE);			
			}
		});
		
		// Get Views from Tutorial FrameLayout
		mScrollView = (ScrollView) mTutorialView.getChildAt(0);
		mTextView = (TextView) mScrollView.getChildAt(0);
		mImageView = (ImageView) mTutorialView.getChildAt(1);
		
		mContainer.addView(mTutorialView);
	}	
	
	// Start tutorial 
	public void start (String text) {	
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(mContext);
		boolean isTutorial = prefs.getBoolean("tutorial_state", true);
		
		if (isTutorial) {
			mTextView.setText(text);
			
			Animation flingLeft = AnimationUtils.loadAnimation(mContext, R.anim.fling_left);
			Animation flingRight = AnimationUtils.loadAnimation(mContext, R.anim.fling_right);
			
			mTutorialView.setVisibility(View.VISIBLE);
			mScrollView.setAnimation(flingLeft);
			mImageView.setAnimation(flingRight);
		}
	}
}
