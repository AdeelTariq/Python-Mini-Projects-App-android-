package com.adeel.pythonmini;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
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
import org.andengine.opengl.vbo.VertexBufferObjectManager;
import org.andengine.ui.activity.SimpleBaseGameActivity;
import org.andengine.util.adt.io.in.IInputStreamOpener;
import org.andengine.util.debug.Debug;

import android.content.Intent;

public class Memory extends SimpleBaseGameActivity{
	
	private static int CAMERA_WIDTH = 480;
	private static int CAMERA_HEIGHT = 800;
	private int CARD_WIDTH = 71, CARD_HEIGHT = 96;
	private TextureRegion TRtitle, TRback, TRrestart, TRhelp, TRcover;
	private ITexture Texturedeck;
	private IFont litho;
	private Text TXTturns;
	private Scene aScene;
	private int state = 0, turns = 0;
	private int exposed2, exposed1;
	private ArrayList<TextureRegion> TRdeck;
	private ArrayList<Cards> SPdeck;
	private ArrayList<Sprite> covers;
	
	@Override
	public EngineOptions onCreateEngineOptions() {
		 final Camera camera = new Camera(0, 0, CAMERA_WIDTH, CAMERA_HEIGHT);
		 return new EngineOptions(true, ScreenOrientation.PORTRAIT_FIXED, 
		    new RatioResolutionPolicy(CAMERA_WIDTH, CAMERA_HEIGHT), camera);
	}
	@Override
	protected void onCreateResources() {
		    this.TRtitle = TextureRegionFactory.extractFromTexture(loadSprite("red/mtitle.png"));
		    this.TRback = TextureRegionFactory.extractFromTexture(loadSprite("red/mback.png"));
		    this.TRhelp = TextureRegionFactory.extractFromTexture(loadSprite("red/mhelp.png"));
		    this.TRrestart = TextureRegionFactory.extractFromTexture(loadSprite("red/restart.png"));
		    this.TRcover = TextureRegionFactory.extractFromTexture(loadSprite("black/cover.png"));
		    this.Texturedeck = loadSprite("black/cards1.png");
		    this.TRdeck = new ArrayList<TextureRegion>();
		    for (int i=0;i<8;i++){
		    	this.TRdeck.add(TextureRegionFactory.extractFromTexture(this.Texturedeck,0,0+(CARD_HEIGHT*i),CARD_WIDTH, CARD_HEIGHT));
		    }
		    litho = FontFactory.createFromAsset(this.getFontManager(), this.getTextureManager(), 256, 256, this.getAssets(),
		    	    "fonts/lithospro.otf", 40, true, android.graphics.Color.parseColor("#f15c6a"));
		    litho.load();
	}
	
	@Override
	protected Scene onCreateScene() {
		aScene = new Scene();
		Rectangle backgroundSprite = makeColoredRectangle(0, 0, CAMERA_WIDTH, CAMERA_HEIGHT, 0.278f, 0.278f, 0.278f);
		aScene.attachChild(backgroundSprite);
		Sprite title = new Sprite(122, 30, this.TRtitle, getVertexBufferObjectManager());
		SPdeck = new ArrayList<Cards>();
		covers = new ArrayList<Sprite>();
		for (int i=0;i<16;i++){
			SPdeck.add(new Cards(0, 0, TRdeck.get(i % TRdeck.size()), getVertexBufferObjectManager(), i % TRdeck.size()));
			covers.add(new Sprite(40 + (105 * (i % 4)), 280 + (105 * ((int)(i/4))), TRcover, getVertexBufferObjectManager()){
				@Override
				public boolean onAreaTouched(TouchEvent pSceneTouchEvent, float pTouchAreaLocalX, float pTouchAreaLocalY) {
			        if (pSceneTouchEvent.getAction() == TouchEvent.ACTION_UP) {
			        		if (this.isVisible()){
			                    if (state == 0){
			                        exposed1 = covers.indexOf(this);
			                        this.setVisible(false);
			                        state = 1;
			                    }else if (state == 1){
			                    	this.setVisible(false);
			                        exposed2 = covers.indexOf(this);
			                        state = 2;
			                        turns += 1;	//increment turns counter
			                        TXTturns.setText(String.valueOf(turns));
			                    }else{
			                        if (SPdeck.get(exposed1).getNumber()!=SPdeck.get(exposed2).getNumber()){
			                            covers.get(exposed1).setVisible(true);
			                            covers.get(exposed2).setVisible(true);
			                        }
			                        exposed1 = covers.indexOf(this);
			                        this.setVisible(false);
			                        state = 1;
			                    }
			        		}
			        	}
			        return true;
				    }
			});
			SPdeck.get(i).setZIndex(1);
			covers.get(i).setZIndex(2);
			aScene.attachChild(SPdeck.get(i));
			aScene.attachChild(covers.get(i));
			aScene.registerTouchArea(covers.get(i));
		}
	    Sprite back = new Sprite(388, 710, this.TRback, getVertexBufferObjectManager()){
			@Override
			public boolean onAreaTouched(TouchEvent pSceneTouchEvent, float pTouchAreaLocalX, float pTouchAreaLocalY) {
		        if (pSceneTouchEvent.getAction() == TouchEvent.ACTION_UP) {
		        	Memory.this.finish();
		        }
		        return true;
			    }};
	    Sprite help = new Sprite(0, 710, this.TRhelp, getVertexBufferObjectManager()){
			@Override
			public boolean onAreaTouched(TouchEvent pSceneTouchEvent, float pTouchAreaLocalX, float pTouchAreaLocalY) {
		        if (pSceneTouchEvent.getAction() == TouchEvent.ACTION_UP) {
		        	Intent i = new Intent(Memory.this,About.class);
		        	i.putExtra("help", "memory");
		        	startActivity(i);
		        }
		        return true;
		    }
		};
		Sprite restart = new Sprite(0, 110, this.TRrestart, getVertexBufferObjectManager()){
			@Override
			public boolean onAreaTouched(TouchEvent pSceneTouchEvent, float pTouchAreaLocalX, float pTouchAreaLocalY) {
		        if (pSceneTouchEvent.getAction() == TouchEvent.ACTION_UP) {
		        	new_game();
		        }
		        return true;
			}};
	    TXTturns = new Text(408, 208f, litho, "999", getVertexBufferObjectManager());
	    TXTturns.setText(String.valueOf(turns));
	    aScene.attachChild(restart);
	    aScene.attachChild(title);
	    aScene.attachChild(back);
	    aScene.attachChild(help);
	    aScene.attachChild(TXTturns);
	    aScene.registerTouchArea(back);
	    aScene.registerTouchArea(help);
	    aScene.registerTouchArea(restart);
		aScene.setTouchAreaBindingOnActionDownEnabled(true);
		new_game();
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
	private void new_game(){
		turns = 0;
		TXTturns.setText(String.valueOf(turns));
		state = 0;
		exposed1 = 0;
		exposed2 = 0;
		Collections.shuffle(SPdeck);
		for(int i=0; i<16;i++){
			SPdeck.get(i).setPosition(40 + (105 * (i % 4)), 280 + (105 * ((int)(i/4))));
			covers.get(i).setVisible(true);
		}
		aScene.sortChildren(false);
	}
	class Cards extends Sprite{
		private int number;
		public Cards(int i, int j, TextureRegion textureRegion,
				VertexBufferObjectManager vertexBufferObjectManager, int number) {
			super(i, j, textureRegion, vertexBufferObjectManager);
			this.number = number;
		}
		public int getNumber(){
			return number;
		}
	}
	private Rectangle makeColoredRectangle(final float pX, final float pY, final float pW, final float pH, final float pRed, final float pGreen, final float pBlue) {
	    final Rectangle coloredRect = new Rectangle(pX, pY, pW, pH, this.getVertexBufferObjectManager());
	    coloredRect.setColor(pRed, pGreen, pBlue);
	    return coloredRect;
	}
}