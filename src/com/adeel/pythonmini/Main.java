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
import org.andengine.input.touch.TouchEvent;
import org.andengine.opengl.texture.ITexture;
import org.andengine.opengl.texture.bitmap.BitmapTexture;
import org.andengine.opengl.texture.region.TextureRegion;
import org.andengine.opengl.texture.region.TextureRegionFactory;
import org.andengine.ui.activity.SimpleBaseGameActivity;
import org.andengine.util.adt.io.in.IInputStreamOpener;
import org.andengine.util.color.Color;
import org.andengine.util.debug.Debug;
import android.content.Intent;

public class Main extends SimpleBaseGameActivity{
	
	private static int CAMERA_WIDTH = 480;
	private static int CAMERA_HEIGHT = 800;
	private TextureRegion TRtitle, TRstopwatch, TRblackjack, TRmemory, TRpong, TRrice, TRexit;
	
	@Override
	public EngineOptions onCreateEngineOptions() {
		final Camera camera = new Camera(0, 0, CAMERA_WIDTH, CAMERA_HEIGHT);
		return new EngineOptions(true, ScreenOrientation.PORTRAIT_FIXED, 
		    new RatioResolutionPolicy(CAMERA_WIDTH, CAMERA_HEIGHT), camera);
	}

	@Override
	protected void onCreateResources() {
		    this.TRtitle = TextureRegionFactory.extractFromTexture(loadSprite("gfx/title.png"));
		    this.TRstopwatch = TextureRegionFactory.extractFromTexture(loadSprite("gfx/stopwatch.png"));
		    this.TRblackjack = TextureRegionFactory.extractFromTexture(loadSprite("gfx/blackjack.png"));
		    this.TRmemory = TextureRegionFactory.extractFromTexture(loadSprite("gfx/memory.png"));
		    this.TRpong = TextureRegionFactory.extractFromTexture(loadSprite("gfx/pong.png"));
		    this.TRrice = TextureRegionFactory.extractFromTexture(loadSprite("gfx/rice.png"));
		    this.TRexit = TextureRegionFactory.extractFromTexture(loadSprite("gfx/exit.png"));
	}

	@Override
	protected Scene onCreateScene() {
		final Scene aScene = new Scene();
		aScene.setColor(new Color(71, 71, 71));
		Rectangle backgroundSprite = makeColoredRectangle(0, 0, CAMERA_WIDTH, CAMERA_HEIGHT, 0.278f, 0.278f, 0.278f);
		aScene.attachChild(backgroundSprite);
		Sprite title = new Sprite(52, 50, this.TRtitle, getVertexBufferObjectManager()){
			@Override
			public boolean onAreaTouched(TouchEvent pSceneTouchEvent, float pTouchAreaLocalX, float pTouchAreaLocalY) {
		        if (pSceneTouchEvent.getAction() == TouchEvent.ACTION_UP) {
		        	startActivity(new Intent(Main.this, About.class));
		        }
		        return true;
		    }};
		    Sprite stopwatch = new Sprite(0, 140, this.TRstopwatch, getVertexBufferObjectManager()){
				@Override
				public boolean onAreaTouched(TouchEvent pSceneTouchEvent, float pTouchAreaLocalX, float pTouchAreaLocalY) {
			        if (pSceneTouchEvent.getAction() == TouchEvent.ACTION_UP) {
			        	startActivity(new Intent(Main.this, Stopwatch.class));
			        }
			        return true;
			    }};
		    Sprite blackjack = new Sprite(100, 252, this.TRblackjack, getVertexBufferObjectManager()){
				@Override
				public boolean onAreaTouched(TouchEvent pSceneTouchEvent, float pTouchAreaLocalX, float pTouchAreaLocalY) {
			        if (pSceneTouchEvent.getAction() == TouchEvent.ACTION_UP) {
			        	startActivity(new Intent(Main.this, Black.class));
			        }
			        return true;
			    }};
		    Sprite memory = new Sprite(0, 365, this.TRmemory, getVertexBufferObjectManager()){
				@Override
				public boolean onAreaTouched(TouchEvent pSceneTouchEvent, float pTouchAreaLocalX, float pTouchAreaLocalY) {
			        if (pSceneTouchEvent.getAction() == TouchEvent.ACTION_UP) {
			        	startActivity(new Intent(Main.this, Memory.class));
			        }
			        return true;
			    }};
		    Sprite pong = new Sprite(100, 485, this.TRpong, getVertexBufferObjectManager()){
				@Override
				public boolean onAreaTouched(TouchEvent pSceneTouchEvent, float pTouchAreaLocalX, float pTouchAreaLocalY) {
			        if (pSceneTouchEvent.getAction() == TouchEvent.ACTION_UP) {
			        	startActivity(new Intent(Main.this, Pong.class));
			        }
			        return true;
			    }};
		    Sprite rice = new Sprite(0, 602, this.TRrice, getVertexBufferObjectManager()){
				@Override
				public boolean onAreaTouched(TouchEvent pSceneTouchEvent, float pTouchAreaLocalX, float pTouchAreaLocalY) {
			        if (pSceneTouchEvent.getAction() == TouchEvent.ACTION_UP) {
			        	startActivity(new Intent(Main.this, Ricerocks.class));
			        }
			        return true;
			    }};
		    Sprite exit = new Sprite(388, 710, this.TRexit, getVertexBufferObjectManager()){
				@Override
				public boolean onAreaTouched(TouchEvent pSceneTouchEvent, float pTouchAreaLocalX, float pTouchAreaLocalY) {
			        if (pSceneTouchEvent.getAction() == TouchEvent.ACTION_UP) {
			        	Main.this.finish();
			        }
			        return true;
			    }};
	    aScene.attachChild(stopwatch);
	    aScene.attachChild(blackjack);
	    aScene.attachChild(memory);
	    aScene.attachChild(pong);
	    aScene.attachChild(title);
	    aScene.attachChild(rice);
	    aScene.attachChild(exit);
	    aScene.registerTouchArea(stopwatch);
	    aScene.registerTouchArea(blackjack);
	    aScene.registerTouchArea(memory);
	    aScene.registerTouchArea(pong);
	    aScene.registerTouchArea(title);
	    aScene.registerTouchArea(rice);
	    aScene.registerTouchArea(exit);
		aScene.setTouchAreaBindingOnActionDownEnabled(true);
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
	private Rectangle makeColoredRectangle(final float pX, final float pY, final float pW, final float pH, final float pRed, final float pGreen, final float pBlue) {
	    final Rectangle coloredRect = new Rectangle(pX, pY, pW, pH, this.getVertexBufferObjectManager());
	    coloredRect.setColor(pRed, pGreen, pBlue);
	    return coloredRect;
	}
}
