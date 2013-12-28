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
import org.andengine.opengl.font.Font;
import org.andengine.opengl.font.FontFactory;
import org.andengine.opengl.texture.ITexture;
import org.andengine.opengl.texture.bitmap.BitmapTexture;
import org.andengine.opengl.texture.region.TextureRegion;
import org.andengine.opengl.texture.region.TextureRegionFactory;
import org.andengine.ui.activity.SimpleBaseGameActivity;
import org.andengine.util.adt.io.in.IInputStreamOpener;
import org.andengine.util.debug.Debug;

public class About extends SimpleBaseGameActivity{
	
	private static int CAMERA_WIDTH = 480;
	private static int CAMERA_HEIGHT = 800;
	private TextureRegion TRtitle, TRback;
	private Font litho;
	private String text; 
	
	@Override
	public EngineOptions onCreateEngineOptions() {
		final Camera camera = new Camera(0, 0, CAMERA_WIDTH, CAMERA_HEIGHT);
		return new EngineOptions(true, ScreenOrientation.PORTRAIT_FIXED, 
		    new RatioResolutionPolicy(CAMERA_WIDTH, CAMERA_HEIGHT), camera);
	}
	@Override
	protected void onCreateResources() {
		    this.TRtitle = TextureRegionFactory.extractFromTexture(loadSprite("gfx/title.png"));
		    this.TRback = TextureRegionFactory.extractFromTexture(loadSprite("gfx/back.png"));
		    text = getResources().getString(R.string.main);
		    litho = FontFactory.createFromAsset(this.getFontManager(), this.getTextureManager(), 512, 512, this.getAssets(),
		    	    "fonts/lithospro.otf", 30, true, android.graphics.Color.parseColor("#eeeeee"));
		    litho.load();
	}
	@Override
	protected Scene onCreateScene() {
		final Scene aScene = new Scene();
		Rectangle backgroundSprite = makeColoredRectangle(0, 0, CAMERA_WIDTH, CAMERA_HEIGHT, 0.278f, 0.278f, 0.278f);
		aScene.attachChild(backgroundSprite);
		Sprite title = new Sprite(62, 50, this.TRtitle, getVertexBufferObjectManager()){
			@Override
			public boolean onAreaTouched(TouchEvent pSceneTouchEvent, float pTouchAreaLocalX, float pTouchAreaLocalY) {
		        if (pSceneTouchEvent.getAction() == TouchEvent.ACTION_UP) {
		        	About.this.finish();
		        }
		        return true;
		    }};
	    Sprite back = new Sprite(388, 710, this.TRback, getVertexBufferObjectManager()){
			@Override
			public boolean onAreaTouched(TouchEvent pSceneTouchEvent, float pTouchAreaLocalX, float pTouchAreaLocalY) {
		        if (pSceneTouchEvent.getAction() == TouchEvent.ACTION_UP) {
		        	About.this.finish();
		        }
		        return true;
			    }};
		Text desc = new Text(30, 150, litho, text, getVertexBufferObjectManager());
		aScene.attachChild(desc);
	    aScene.attachChild(title);
	    aScene.attachChild(back);
	    aScene.registerTouchArea(title);
	    aScene.registerTouchArea(back);
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
