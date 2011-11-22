package de.jojahn.campus.floor;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.PointF;
import android.graphics.RectF;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.GestureDetector;
import android.view.GestureDetector.OnGestureListener;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewFlipper;
import de.jojahn.campus.R;
import de.jojahn.campus.search.SearchResultActivity;
import de.jojahn.campus.tools.JSONParser;

public class FloorActivity extends Activity implements OnGestureListener {
	private static final String TAG = "ZoomActivity";
	
	private Context mContext;
	
	private GestureDetector mGestureDetector;
	
	private ProgressDialog mProgressDialog;
	
	// Network loading and parsing 
	private JSONParser mJSONParser;	
	
	private int mHeaderOffset;
	private int mCurrentFloor = 1;	
	
	private float[] mMatrixValues = new float[9];
	
	private float mDensity;	
	private float mZoomBase = 1.0f;
	private float mZoomFactor = 0.3f;
	private float mMaxZoom;
	private float mMinZoom;
	private float mContentHeight;
	private float mContentWidth;
	
	private RectF mDisplayRect;	
	private PointF mStart = new PointF();
	
	public Matrix mMatrix = new Matrix();	
	public Matrix mSavedMatrix = new Matrix();	
	
	private ArrayList<FloorItem> mFloors = new ArrayList<FloorItem>();
	
	private ViewFlipper mViewFlipper;	
	private Bitmap[] mBitmaps;
	
	private ImageButton mZoomInButton;
	private ImageButton mZoomOutButton;
	private ImageButton mUpStairsButton;
	private ImageButton mDownStairsButton;
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.floor);
        
        mContext = getApplicationContext();
        
        mGestureDetector = new GestureDetector(this);
        
        // InfoText
		TextView textView = (TextView) findViewById(R.id.infotext_floor);
		textView.setText(getIntent().getExtras().getString("building"));
    
        mViewFlipper = (ViewFlipper) findViewById(R.id.viewFlipper);
                
        mZoomInButton = (ImageButton) findViewById(R.id.btn_floor_zoom_in);
    	mZoomOutButton = (ImageButton) findViewById(R.id.btn_floor_zoom_out);
    	mZoomOutButton.setEnabled(false);
    	
    	mUpStairsButton = (ImageButton) findViewById(R.id.btn_floor_upstairs);
    	mDownStairsButton = (ImageButton) findViewById(R.id.btn_floor_downstairs);
    	mDownStairsButton.setEnabled(false);
    	    	
        mDensity = getResources().getDisplayMetrics().density; 
              
        mHeaderOffset = (int) (mDensity * (25 - 48));         
        mDisplayRect = new RectF(0, 0, getResources().getDisplayMetrics().widthPixels, getResources().getDisplayMetrics().heightPixels);
                
        // Create and show ProgressDialog
		mProgressDialog = ProgressDialog.show(this, "", getString(R.string.progress_dialog_text), true);
	
        new DownloadFloorTask().execute("");           
    }	
    
    private class DownloadFloorTask extends AsyncTask<String, Void, String> {
    	
		@Override
		protected String doInBackground(String... arg0) {			
	        // Parsing network files
	        mJSONParser = new JSONParser(mContext, mContext.getString(R.string.type_floor));
	        mFloors = mJSONParser.parseFloor(getIntent().getExtras().getString("building"));    
	        
	        if (!mFloors.isEmpty()) {
		        mBitmaps = new Bitmap[mFloors.size()];
		        for (int i = 0; i < mFloors.size(); i++) {
		        	mBitmaps[i] = getImageBitmap(mFloors.get(i).getImageUrl());	        	
		        }
	        }
			return null;
		}

		@Override
		protected void onPostExecute(String result) {
			if (!mFloors.isEmpty()) {
				prepareFloors();					
			} else {				
				TextView empty = (TextView) findViewById(R.id.floor_empty);
				empty.setVisibility(View.VISIBLE);
				Toast.makeText(mContext, getString(R.string.progress_failed), Toast.LENGTH_LONG).show();
			}
			mProgressDialog.dismiss();
			super.onPostExecute(result); 
		}	
	}
    
    public void prepareFloors() {
    	
    	for (int i = 0; i < mFloors.size(); i++) {
    		ImageView view = (ImageView) LayoutInflater.from(this).inflate(R.layout.floor_imageview, null);
    		view.setImageBitmap(mBitmaps[i]);
    		mViewFlipper.addView(view);       
    	}
        	
        mContentWidth = ((ImageView) mViewFlipper.getChildAt(0)).getDrawable().getIntrinsicWidth();        
        mContentHeight = ((ImageView) mViewFlipper.getChildAt(0)).getDrawable().getIntrinsicHeight();        
                      
        mMaxZoom = 1.0f * mDensity;       
        mMinZoom = Math.min(mDisplayRect.width() / mContentWidth, mDisplayRect.height() / mContentHeight);
          
        mMatrix.postScale(mMinZoom, mMinZoom, mDisplayRect.centerX(), mDisplayRect.centerY());
        mSavedMatrix.set(mMatrix);        
                
        invalidate();            
               
        gotoFloor();
    }
    
    // Switch to current floor
    private void gotoFloor() {
    	int floor = getIntent().getExtras().getInt("floor") + 1;    	
    	Log.e(TAG, "floor: " + floor);    	
    	Log.e(TAG, "current floor: " + mCurrentFloor);   	
    	if (floor == 1) {  
    		return;
    	} else {
    		while (floor != mCurrentFloor) {    			
    			onUpStairsClicked(mUpStairsButton);
    		}
    	}
    }
    
    // Getting bitmap from URL
    private Bitmap getImageBitmap(String url) { 
        Bitmap bitmap = null; 
        
        try { 
            URL aURL = new URL(url); 
            URLConnection conn = aURL.openConnection();            
            conn.connect(); 
            
            InputStream is = conn.getInputStream(); 
            BufferedInputStream bis = new BufferedInputStream(is); 
            
            bitmap = BitmapFactory.decodeStream(bis); 
            
            bis.close(); 
            is.close(); 
            
       } catch (IOException e) { 
           Log.e(TAG, "Error getting bitmap", e); 
       } 
       return bitmap; 
    } 
    
	public void invalidate() {
		checkPanning();				
		((ImageView) mViewFlipper.getChildAt(mViewFlipper.getDisplayedChild())).setImageMatrix(mMatrix);	
	}
	
	private void checkPanning(){
        mMatrix.getValues(mMatrixValues);        
        float currentX = mMatrixValues[Matrix.MTRANS_X];
        float currentY = mMatrixValues[Matrix.MTRANS_Y];
        
        float currentScale = mMatrixValues[Matrix.MSCALE_X];
        float currentHeight = mContentHeight * currentScale;
        float currentWidth = mContentWidth * currentScale;
        
        float newX = currentX;
        float newY = currentY;     
        
        RectF drawingRect = new RectF(newX, newY, newX + currentWidth, newY + currentHeight);
        float diffUp = Math.min(mDisplayRect.bottom - drawingRect.bottom, mDisplayRect.top - drawingRect.top);
        float diffDown = Math.max(mDisplayRect.bottom - drawingRect.bottom, mDisplayRect.top - drawingRect.top);
        float diffLeft = Math.min(mDisplayRect.left - drawingRect.left, mDisplayRect.right - drawingRect.right);
        float diffRight = Math.max(mDisplayRect.left - drawingRect.left, mDisplayRect.right - drawingRect.right);
               
        float dx = 0, dy = 0;
        
        if (diffUp > 0) dy += diffUp;       
        if (diffDown < 0) dy += diffDown;    
        if (diffLeft > 0) dx += diffLeft;     
        if (diffRight < 0) dx += diffRight;
        
        if(currentWidth < mDisplayRect.width()) dx = -currentX + (mDisplayRect.width() - currentWidth) / 2;
        if(currentHeight < mDisplayRect.height()) dy = -currentY + (mDisplayRect.height() - currentHeight) / 2;
                 
        mMatrix.postTranslate(dx, dy);       
	}
	
	private void zoom(float factor) {
		mMatrix.getValues(mMatrixValues);
        float currentScale = mMatrixValues[Matrix.MSCALE_X];
        float scale = mZoomBase + factor;
        
        // Zoom limits
        if (scale * currentScale > mMaxZoom) {
        	scale = mMaxZoom / currentScale;
        	mZoomInButton.setEnabled(false);
        } else if (scale * currentScale < mMinZoom) {
        	scale = mMinZoom / currentScale;
        	mZoomOutButton.setEnabled(false);
        } else {
        	mZoomInButton.setEnabled(true);
        	mZoomOutButton.setEnabled(true);
        }
        mMatrix.postScale(scale, scale, mDisplayRect.centerX(), mDisplayRect.centerY());
               
        invalidate();
	}
	
	// ButtonClickEvent: ZoomIn
	public void onZoomIn(View view) {		
		zoom(mZoomFactor);		
	}
	
	// ButtonClickEvent: ZoomOut
	public void onZoomOut(View view) {
		zoom(-mZoomFactor);
	}
	
	// ButtonClickEvent: UpStairs
	public void onUpStairsClicked(View view) {		
		if (mCurrentFloor < mFloors.size()) {
			mCurrentFloor++;
			
			mViewFlipper.setInAnimation(AnimationUtils.loadAnimation(this, R.anim.push_left_in));
			mViewFlipper.setOutAnimation(AnimationUtils.loadAnimation(this, R.anim.push_left_out));    
			
			mViewFlipper.showNext();
			mMatrix.set(mSavedMatrix);
			
			mZoomInButton.setEnabled(true);
	    	mZoomOutButton.setEnabled(true);
	    	mDownStairsButton.setEnabled(true);
	    	
			invalidate();   
			
			if (mCurrentFloor == mFloors.size()) mUpStairsButton.setEnabled(false);
		}
	}
	
	// ButtonClickEvent: DownStairs
	public void onDownStairsClicked(View view) {
		if (mCurrentFloor > 1) {
			mCurrentFloor--;
			
			mViewFlipper.setInAnimation(AnimationUtils.loadAnimation(this, R.anim.push_right_in));
			mViewFlipper.setOutAnimation(AnimationUtils.loadAnimation(this, R.anim.push_right_out));  
			
			mViewFlipper.showPrevious();			
			mMatrix.set(mSavedMatrix);
			
			mZoomInButton.setEnabled(true);
	    	mZoomOutButton.setEnabled(true);
	    	mUpStairsButton.setEnabled(true);
	    	
			invalidate();	
			
			if (mCurrentFloor == 1) mDownStairsButton.setEnabled(false);
		}
	}

	@Override
	protected void onDestroy() {		
		super.onDestroy();
		
		// Unbind drawables to avoid memory leaks
		for (int i = 0; i < mFloors.size(); i++) {
			((ImageView) mViewFlipper.getChildAt(i)).getDrawable().setCallback(null);	
			mBitmaps[i].recycle();
		}				
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {		
		return mGestureDetector.onTouchEvent(event);		
	}	
	
	@Override
	public boolean onDown(MotionEvent arg0) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
		// Fling left	
		if(e1.getX() > e2.getX() + 100 && velocityX < -1000) {
			onUpStairsClicked(mUpStairsButton);
			return true;
		}
		
		// Fling right		
		if(e1.getX() < e2.getX() - 100 && velocityX > 1000) {
			onDownStairsClicked(mDownStairsButton);
			return true;
		}
		
		return false;
	}

	@Override
	public void onLongPress(MotionEvent arg0) {
		// TODO Auto-generated method stub
	}

	@Override
	public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {		
		mMatrix.postTranslate(-distanceX - mStart.x, -distanceY - mStart.y);
		checkPanning();
		invalidate();		
		return true;
	}

	@Override
	public void onShowPress(MotionEvent arg0) {
		// TODO Auto-generated method stub		
	}

	@Override
	public boolean onSingleTapUp(MotionEvent e1) {		
		for (Room r : mFloors.get(mCurrentFloor - 1).getRooms()) {
			
			PointF tap = new PointF(e1.getX(), e1.getY() + mHeaderOffset);
						
			RectF rect = r.getRect(mContentWidth, mContentHeight);		
			mMatrix.mapRect(rect);
						
			if (rect.contains(tap.x, tap.y)) {				
				Intent newIntent = new Intent(mContext, SearchResultActivity.class);
            	newIntent.putExtra("room_name", r.getName());
            	newIntent.putExtra("room_description", r.getDescription());
            	newIntent.putExtra("room_searched", false);
				startActivity(newIntent); 	
				//Toast.makeText(this, "" + r.getName(), Toast.LENGTH_SHORT).show();
			} 
		}		 
		return true;
	}
}
