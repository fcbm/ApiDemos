package com.example.resourcesdemo;

import android.animation.Animator;
import android.animation.AnimatorInflater;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.annotation.TargetApi;
import android.app.Activity;
import android.graphics.drawable.AnimationDrawable;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.widget.Button;
import android.widget.ImageView;

public class MainActivity extends Activity {

	private final static String TAG = "MainActivity";
	private ImageView ivFrameByFrameAnimation;
	private Button btnTweenedViewAnimated;
	
	@TargetApi(11)
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		ivFrameByFrameAnimation = (ImageView) findViewById( R.id.ivFrameByFrameHost);
		ivFrameByFrameAnimation.setBackgroundResource( R.drawable.frame_by_frame_animation );
		
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB)
		{
			// Retrieve PropertyAnimation using ObjectAnimator
			final ObjectAnimator animator = (ObjectAnimator) AnimatorInflater.loadAnimator(this, R.animator.property_animation);
			setupAnimator( animator);
			Button btnPropAnimationXml = (Button) findViewById( R.id.btnPropertyAnimationXML );
			
			btnPropAnimationXml.setOnClickListener( new View.OnClickListener() {
				
				@Override
				public void onClick(View v) {
					animator.start();
				}
			});
			
			String propertyName = "textScaleX";
			float fromX = 1f;
			float toX = 0f;
			
			final Button btnPropAnimationCode = (Button) findViewById( R.id.btnPropertyAnimationCODE );
			
			final ObjectAnimator animX = ObjectAnimator.ofFloat(btnPropAnimationCode, propertyName, fromX, toX);
			setupAnimator(animX);
			animX.addListener( new Animator.AnimatorListener() {
				
				@Override
				public void onAnimationStart(Animator animation) {
				}
				
				@Override
				public void onAnimationRepeat(Animator animation) {
					String txt = btnPropAnimationCode.getText().toString();
					if (txt.charAt(0) >= 'A' && txt.charAt(0) <= 'Z' )
						btnPropAnimationCode.setText(btnPropAnimationCode.getText().toString().toLowerCase());
					else
						btnPropAnimationCode.setText(btnPropAnimationCode.getText().toString().toUpperCase());					
					
				}
				
				@Override
				public void onAnimationEnd(Animator animation) {
				}
				
				@Override
				public void onAnimationCancel(Animator animation) {
				}
			});

			btnPropAnimationCode.setOnClickListener( new View.OnClickListener() {
				
				@Override
				public void onClick(View v) {
					animX.start();
				}
			});
			
			final Button btnPropAnimationSet = (Button) findViewById( R.id.btnPropertyAnimationSet );
			
			String propScale = "scaleX";
			float fromScaleX = 1f, toScaleX = 0.5f;
			String propTransX = "translationX";
			float fromTransX = 1f, toTransX = 300f;
			String propRotX = "rotationX";
			float fromRotX = 0, toRotX = 360;
			String propRotY = "rotationY";
			float fromRotY = 0, toRotY = 360;
			
			final ObjectAnimator animScale = ObjectAnimator.ofFloat(btnPropAnimationSet, propScale, fromScaleX, toScaleX);
			setupAnimator(animScale);
			final ObjectAnimator animTransX = ObjectAnimator.ofFloat(btnPropAnimationSet, propTransX, fromTransX, toTransX);
			setupAnimator(animTransX);
			final ObjectAnimator animRotX = ObjectAnimator.ofFloat(btnPropAnimationSet, propRotX, fromRotX, toRotX);
			setupAnimator(animRotX);
			final ObjectAnimator animRotY = ObjectAnimator.ofFloat(btnPropAnimationSet, propRotY, fromRotY, toRotY);
			setupAnimator(animRotY);
			
			final AnimatorSet animSet = new AnimatorSet();
			animSet.play(animScale).before(animRotY);
			animSet.play(animRotY).with( animTransX);
			animSet.play(animRotX).after(animTransX);

			btnPropAnimationSet.setOnClickListener( new View.OnClickListener() {
				
				@Override
				public void onClick(View v) {
					animSet.start();
				}
			});
			
		}

		Button btnTweenedViewRotate = (Button) findViewById( R.id.btnTweenedViewRotate );
		final Animation animRotate = AnimationUtils.loadAnimation( this , R.anim.rotate_simple_animation );
		//btnTweenedViewRotate.setAnimation( animRotate );
		btnTweenedViewRotate.setOnClickListener( new View.OnClickListener( ) {
			
			@Override
			public void onClick(View v) {
				
				v.startAnimation( animRotate );
			}
		});
		
		btnTweenedViewAnimated = (Button) findViewById( R.id.btnTweenedViewAnimated );

		// This is the simplest and most efficient type of Animation
		final AnimationSet tweenedViewAnimation = (AnimationSet)AnimationUtils.loadAnimation(this, R.anim.shrink_animation);

		Log.i(TAG, "animations size " + tweenedViewAnimation.getAnimations().size());

		for (Animation a : tweenedViewAnimation.getAnimations())
		{
		a.setAnimationListener( new Animation.AnimationListener() {

			@Override
			public void onAnimationStart(Animation animation) {
				Log.d(TAG, "Animation Started ");
			}
			
			@Override
			public void onAnimationRepeat(Animation animation) {
				Log.d(TAG, "Animation Repeating ");
				String txt = btnTweenedViewAnimated.getText().toString();
				if (txt.charAt(0) >= 'A' && txt.charAt(0) <= 'Z' )
					btnTweenedViewAnimated.setText(btnTweenedViewAnimated.getText().toString().toLowerCase());
				else
					btnTweenedViewAnimated.setText(btnTweenedViewAnimated.getText().toString().toUpperCase());					
			}
			
			@Override
			public void onAnimationEnd(Animation animation) {
				Log.d(TAG, "Animation End ");			
			}
			
		} ); }
		
		View.OnClickListener btnListener = new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				//tweenedViewAnimation.reset();
				v.startAnimation( tweenedViewAnimation );
			}
		};
		
		btnTweenedViewAnimated.setOnClickListener( btnListener );

		final ViewGroup vgMain = (ViewGroup)findViewById( R.id.mainLayout );
		
		Button btnLayoutAnimation = (Button) findViewById( R.id.btnLayoutAnimation1 );
		final Animation layoutAnimation = AnimationUtils.loadAnimation( MainActivity.this, R.anim.rotate_animation);
		btnLayoutAnimation.setOnClickListener( new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				//vgMain.scheduleLayoutAnimation();
				vgMain.startAnimation( layoutAnimation);
			}
		});
	}
	
	@TargetApi(11)
	private void setupAnimator(ObjectAnimator anim)
	{
		anim.setDuration( 1000 );
		anim.setRepeatMode( ObjectAnimator.REVERSE );
		anim.setRepeatCount( 1 );
		anim.setInterpolator( new LinearInterpolator() );
	}
	
	@Override
	public void onWindowFocusChanged(boolean hasFocus)
	{
		super.onWindowFocusChanged(hasFocus);
		
		if (ivFrameByFrameAnimation != null)
		{
			// Retrieve FrameByFrameAnimation using AnimationDrawable
			AnimationDrawable ad = (AnimationDrawable) ivFrameByFrameAnimation.getBackground();
			
			if (hasFocus)
			{
				// Starts the animation, looping if necessary. This method has no effect if the animation is running. 
				// !! Do not call this in the onCreate(Bundle) method of your activity, because the AnimationDrawable 
				// is not yet fully attached to the window !! 
				// If you want to play the animation immediately, without requiring interaction, then you might want to 
				// call it from the onWindowFocusChanged(boolean) method in your activity, which will get called when 
				// Android brings your window into focus.			
				ad.start();
			}
			else
			{
				ad.stop();
			}
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

}
