package com.adeel.pythonmini;

import java.io.IOException;
import java.io.InputStream;
import org.andengine.engine.camera.Camera;
import org.andengine.engine.options.EngineOptions;
import org.andengine.engine.options.ScreenOrientation;
import org.andengine.engine.options.resolutionpolicy.RatioResolutionPolicy;
import org.andengine.entity.primitive.Rectangle;
import org.andengine.entity.scene.Scene;
import org.andengine.entity.sprite.Sprite;
import org.andengine.entity.text.Text;
import org.andengine.input.touch.TouchEvent;
import org.andengine.opengl.font.FontFactory;
import org.andengine.opengl.font.IFont;
import org.andengine.opengl.texture.ITexture;
import org.andengine.opengl.texture.bitmap.BitmapTexture;
import org.andengine.opengl.texture.region.TextureRegion;
import org.andengine.opengl.texture.region.TextureRegionFactory;
import org.andengine.ui.activity.SimpleBaseGameActivity;
import org.andengine.util.adt.io.in.IInputStreamOpener;
import org.andengine.util.debug.Debug;

import android.content.Intent;
import android.os.SystemClock;

public class Stopwatch extends SimpleBaseGameActivity{
	
	private static int CAMERA_WIDTH = 800;
	private static int CAMERA_HEIGHT = 480;
	private TextureRegion TRtitle, TRback;
	private IFont litho, lithoSmall;
	private TextureRegion TRhelp;
	private TextureRegion TRreset;
	private int nScore = 0, time = 0; 
	private Text score, stopwatch;
	private boolean stopped = true; 
	
	@Override
	public EngineOptions onCreateEngineOptions() {
		final Camera camera = new Camera(0, 0, CAMERA_WIDTH, CAMERA_HEIGHT);
		return new EngineOptions(true, ScreenOrientation.LANDSCAPE_FIXED, 
		    new RatioResolutionPolicy(CAMERA_WIDTH, CAMERA_HEIGHT), camera);
	}
	@Override
	protected void onCreateResources() {
		    this.TRtitle = TextureRegionFactory.extractFromTexture(loadSprite("stop/stitle.png"));
		    this.TRback = TextureRegionFactory.extractFromTexture(loadSprite("stop/sback.png"));
		    this.TRhelp = TextureRegionFactory.extractFromTexture(loadSprite("stop/shelp.png"));
		    this.TRreset = TextureRegionFactory.extractFromTexture(loadSprite("stop/sreset.png"));
		    litho = FontFactory.createFromAsset(this.getFontManager(), this.getTextureManager(), 512, 512, this.getAssets(),
		    	    "fonts/lithospro.otf", 150, true, android.graphics.Color.parseColor("#eab166"));
		    lithoSmall = FontFactory.createFromAsset(this.getFontManager(), this.getTextureManager(), 256, 256, this.getAssets(),
		    	    "fonts/lithospro.otf", 40, true, android.graphics.Color.parseColor("#eab166"));
		    litho.load();
		    lithoSmall.load();
	}
	@Override
	protected Scene onCreateScene() {
		final Scene aScene = new Scene(){
			@Override
			public void onManagedUpdate(float pSeconds){
				score.setText(String.valueOf(nScore));
		}};
		Rectangle backgroundSprite = makeColoredRectangle(0, 0, CAMERA_WIDTH, CAMERA_HEIGHT, 0.278f, 0.278f, 0.278f);
		aScene.attachChild(backgroundSprite);
		Sprite title = new Sprite(245, 25, this.TRtitle, getVertexBufferObjectManager()){
			@Override
			public boolean onAreaTouched(TouchEvent pSceneTouchEvent, float pTouchAreaLocalX, float pTouchAreaLocalY) {
		        if (pSceneTouchEvent.getAction() == TouchEvent.ACTION_UP) {
		        	Intent i = new Intent(Stopwatch.this,About.class);
		        	i.putExtra("help", "stop");
		        	startActivity(i);
		        }
		        return true;
			}
		};
	    Sprite back = new Sprite(712, 380, this.TRback, getVertexBufferObjectManager()){
			@Override
			public boolean onAreaTouched(TouchEvent pSceneTouchEvent, float pTouchAreaLocalX, float pTouchAreaLocalY) {
		        if (pSceneTouchEvent.getAction() == TouchEvent.ACTION_UP) {
		        	Stopwatch.this.finish();
		        }
		        return true;
			    }};
	    Sprite help = new Sprite(0, 380, this.TRhelp, getVertexBufferObjectManager()){
			@Override
			public boolean onAreaTouched(TouchEvent pSceneTouchEvent, float pTouchAreaLocalX, float pTouchAreaLocalY) {
		        if (pSceneTouchEvent.getAction() == TouchEvent.ACTION_UP) {
		        	Intent i = new Intent(Stopwatch.this,About.class);
		        	i.putExtra("help", "stop");
		        	startActivity(i);
		        }
		        return true;
		    }
		};
	    Sprite reset = new Sprite(0, 73, this.TRreset, getVertexBufferObjectManager()){
			@Override
			public boolean onAreaTouched(TouchEvent pSceneTouchEvent, float pTouchAreaLocalX, float pTouchAreaLocalY) {
		        if (pSceneTouchEvent.getAction() == TouchEvent.ACTION_UP) {
		        	stopped = true;
        		    time = 0;
        		    nScore = 0;
        		    score.setText(String.valueOf(nScore));
        		    stopwatch.setText(format(time));
		        }
		        return true;
			}
		};
	    stopwatch = new Text(120f, 200f, litho, "00:00.00", getVertexBufferObjectManager()){
			@Override
			public boolean onAreaTouched(TouchEvent pSceneTouchEvent, float pTouchAreaLocalX, float pTouchAreaLocalY) {
		        if (pSceneTouchEvent.getAction() == TouchEvent.ACTION_DOWN) {
		        	if (!stopped){
		                if (time%10==0){
		                    nScore += 1;
		                }else{
		                	nScore -= 1;
		                }
		            score.setText(String.valueOf(nScore));
		            stopped = true;
		        	}else{
		        		stopped = false;
		        	}
		        }
		        return true;
		}};
	    score = new Text(725f, 95f, lithoSmall, "8.139" , getVertexBufferObjectManager());
	    score.setText(String.valueOf(nScore));
	    stopwatch.setText(format(time));
	    aScene.attachChild(stopwatch);
	    aScene.attachChild(score);
	    aScene.attachChild(title);
	    aScene.attachChild(back);
	    aScene.attachChild(help);
	    aScene.attachChild(reset);
	    aScene.registerTouchArea(back);
	    aScene.registerTouchArea(reset);
	    aScene.registerTouchArea(help);
	    aScene.registerTouchArea(stopwatch);
		aScene.setTouchAreaBindingOnActionDownEnabled(true);
		Thread timer = new Thread(new Runnable() {
			@Override
			public void run() {
				while(true){
					if (!stopped){
						time += 1;
						stopwatch.setText(format(time));
						if (format(time).equals("9:00.0")){
							stopped = true;
						}
					}
					SystemClock.sleep(100);
				}
			}
		});
		timer.start();
		return aScene;
	}
	
	private ITexture loadSprite(final String pathToFile){
		ITexture texture = null;
		try {
		    // 1 - Set up bitmap textures
		    texture = new BitmapTexture(this.getTextureManager(), new IInputStreamOpener() {
		        public InputStream open() throws IOException {
		            return getAssets().open(pathToFile);
		        }});
		    texture.load();
		} catch (IOException e) {
		    Debug.e(e);
		}
		return texture;
	}
	
	private String format(int t){
	    //Converts tenthOfSeconds into minutes
	    int minutes = (int)(((t / 60) / 10));
	    //Converts tenthOfSeconds into seconds
	    int seconds = (int)((t / 10) % 60);
	    String secStr = "";
	    if(seconds < 10){
	        secStr = "0"+ String.valueOf(seconds);
	    }else{
	    	secStr = String.valueOf(seconds);
	    }
	    int tenth_seconds = (int)(t % 10);

	    // formated string
	    String time = String.valueOf(minutes) +':'+ secStr +"."+ String.valueOf(tenth_seconds);
	    return time;
	}
	private Rectangle makeColoredRectangle(final float pX, final float pY, final float pW, final float pH, final float pRed, final float pGreen, final float pBlue) {
	    final Rectangle coloredRect = new Rectangle(pX, pY, pW, pH, this.getVertexBufferObjectManager());
	    coloredRect.setColor(pRed, pGreen, pBlue);
	    return coloredRect;
	}
}
