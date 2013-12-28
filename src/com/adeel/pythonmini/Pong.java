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

import android.os.SystemClock;

public class Pong extends SimpleBaseGameActivity{
	
	private static int CAMERA_WIDTH = 800;
	private static int CAMERA_HEIGHT = 480;
	private TextureRegion TRtitle, TRback, TRrestart, TRhelp, TRball;
	private IFont litho, lithosmall;
	private Rectangle paddle1, paddle2;
	private Sprite ball;
	private Text TXTscore1, TXTscore2, TXTwin;
	private Scene aScene;
	private boolean pause = false;
	private int score1=0, score2=0;
	private float vel_x, vel_y, velocity = 5;
	
	@Override
	public EngineOptions onCreateEngineOptions() {
		 final Camera camera = new Camera(0, 0, CAMERA_WIDTH, CAMERA_HEIGHT);
		 return new EngineOptions(true, ScreenOrientation.LANDSCAPE_FIXED, 
		    new RatioResolutionPolicy(CAMERA_WIDTH, CAMERA_HEIGHT), camera);
	}
	@Override
	protected void onCreateResources() {
		    this.TRtitle = TextureRegionFactory.extractFromTexture(loadSprite("pong/wtitle.png"));
		    this.TRback = TextureRegionFactory.extractFromTexture(loadSprite("pong/wback.png"));
		    this.TRhelp = TextureRegionFactory.extractFromTexture(loadSprite("pong/whelp.png"));
		    this.TRrestart = TextureRegionFactory.extractFromTexture(loadSprite("pong/wrestart.png"));
		    this.TRball = TextureRegionFactory.extractFromTexture(loadSprite("pong/ball.png"));
		    litho = FontFactory.createFromAsset(this.getFontManager(), this.getTextureManager(), 512, 512, this.getAssets(),
		    	    "fonts/lithospro.otf", 100, true, android.graphics.Color.parseColor("#d9d9d9"));
		    litho.load();
		    lithosmall = FontFactory.createFromAsset(this.getFontManager(), this.getTextureManager(), 512, 512, this.getAssets(),
		    	    "fonts/lithospro.otf", 50, true, android.graphics.Color.parseColor("#d9d9d9"));
		    lithosmall.load();
	}
	@Override
	protected Scene onCreateScene() {
		aScene = new Scene(){
			@Override
			protected void onManagedUpdate(float pSecondsElapsed) {
			    //phone's ai code	
				if (vel_x < 0 && !pause){
			    		if (ball.getY()>paddle1.getY()+70){
			    			if (paddle1.getY()<CAMERA_HEIGHT-paddle1.getHeight()-velocity){
			    			paddle1.setPosition(paddle1.getX(), paddle1.getY()+velocity);
			    			}
			    		}else if (ball.getY()<paddle1.getY()+25){
			    			if (paddle1.getY()>velocity){
			    			paddle1.setPosition(paddle1.getX(), paddle1.getY()-velocity);
			    			}
			    		}
			    	}
				if (!pause){
			    ball.setPosition(ball.getX()+vel_x, ball.getY()+vel_y);
				}
				//collision check with gutters and paddels, left side
			    if (ball.getX() <= 50 - vel_x){
			        if (ball.getY()+ball.getWidth()/2> paddle1.getY() && ball.getY()+ball.getWidth()/2 < paddle1.getY()+100){
			            vel_x *= -1.1;
			            vel_y = (float) ((vel_y-0.5) + (Math.random() * (((vel_y+0.5) - (vel_y-0.5)) + 1)));
			        }else{
			        	score2 += 1;
			        	TXTscore2.setText(String.valueOf(score2));
			            spawn_ball(0);
			        }
			    }
			    //collision check with gutters and paddels right side        
			    else if (ball.getX()+ball.getWidth() >= CAMERA_WIDTH - 50 - vel_x){
			        if (ball.getY()+ball.getWidth()/2 > paddle2.getY() && ball.getY()+ball.getWidth()/2 < paddle2.getY()+100){
			            vel_x *= -1.1;
			            vel_y = (float) ((vel_y-0.5) + (Math.random() * (((vel_y+0.5) - (vel_y-0.5)) + 1)));
			        }else{
		                score1 += 1;
		                TXTscore1.setText(String.valueOf(score1));
			            spawn_ball(1);
			        }
			    }
				super.onManagedUpdate(pSecondsElapsed);
			}
		};
		Sprite title = new Sprite(335, 25, this.TRtitle, getVertexBufferObjectManager());
	    Sprite back = new Sprite(408, 400, this.TRback, getVertexBufferObjectManager()){
			@Override
			public boolean onAreaTouched(TouchEvent pSceneTouchEvent, float pTouchAreaLocalX, float pTouchAreaLocalY) {
		        if (pSceneTouchEvent.getAction() == TouchEvent.ACTION_UP) {
		        	Pong.this.finish();
		        }
		        return true;
			    }};
	    Sprite help = new Sprite(325, 400, this.TRhelp, getVertexBufferObjectManager()){
			@Override
			public boolean onAreaTouched(TouchEvent pSceneTouchEvent, float pTouchAreaLocalX, float pTouchAreaLocalY) {
		        if (pSceneTouchEvent.getAction() == TouchEvent.ACTION_UP) {

		        }
		        return true;
			    }};
		Sprite restart = new Sprite(240, 400, this.TRrestart, getVertexBufferObjectManager()){
			@Override
			public boolean onAreaTouched(TouchEvent pSceneTouchEvent, float pTouchAreaLocalX, float pTouchAreaLocalY) {
		        if (pSceneTouchEvent.getAction() == TouchEvent.ACTION_UP) {
		        	new_game();
		        }
		        return true;
			}};
		
		Rectangle board = makeColoredRectangle(0, 0, 800, 480, 0.1f, 0.1f, 0.1f);
		board.attachChild(makeColoredRectangle(0, 0, 2, 480, 0.85f, 0.85f, 0.85f));
		board.attachChild(makeColoredRectangle(50, 0, 2, 480, 0.85f, 0.85f, 0.85f));
		board.attachChild(makeColoredRectangle(748, 0, 2, 480, 0.85f, 0.85f, 0.85f));
		board.attachChild(makeColoredRectangle(898, 0, 2, 480, 0.85f, 0.85f, 0.85f));
		board.attachChild(makeColoredRectangle(400, 0, 2, 480, 0.85f, 0.85f, 0.85f));
		paddle1 = makeColoredRectangle(5, 0, 45, 100, 0.85f, 0.85f, 0.85f);
		paddle2 = makePlayerPaddle();
		board.attachChild(paddle1);
		board.attachChild(paddle2);
		aScene.registerTouchArea(paddle2);
		ball = new Sprite(0, 215, this.TRball, getVertexBufferObjectManager());
		board.attachChild(ball);
		aScene.attachChild(board);
		
	    TXTscore1 = new Text(200, 70f, litho, "999", getVertexBufferObjectManager());
	    TXTscore2 = new Text(500, 70f, litho, "999", getVertexBufferObjectManager());
	    TXTwin = new Text(200, 280f, lithosmall, "Your phone won!!", getVertexBufferObjectManager());
	    TXTscore1.setText(String.valueOf(0));
	    TXTscore2.setText(String.valueOf(0));
	    TXTwin.setText("");
	    restart.setAlpha(0.75f);
	    help.setAlpha(0.75f);
	    back.setAlpha(0.75f);
	    aScene.attachChild(restart);
	    aScene.attachChild(title);
	    aScene.attachChild(back);
	    aScene.attachChild(help);
	    aScene.attachChild(TXTscore1);
	    aScene.attachChild(TXTscore2);
	    aScene.attachChild(TXTwin);
	    aScene.registerTouchArea(back);
	    aScene.registerTouchArea(help);
	    aScene.registerTouchArea(restart);
		aScene.setTouchAreaBindingOnActionDownEnabled(true);
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				while(true){
					//check to bottom collision and control bouncing
					if (ball.getY()+ball.getWidth()/2 <= ball.getWidth()/2 - vel_y){
						vel_y *= -1;
					}else if (ball.getY()+ball.getWidth()/2 >= CAMERA_HEIGHT - ball.getWidth()/2 - vel_y){
				    	vel_y *= -1;
				    }
					//check the winner
					if (score1>=10){
						TXTwin.setText("Your Phone won!");
						pause = true;
					}else if (score2>=10){
						TXTwin.setText("You won!");
						pause = true;
					}
				SystemClock.sleep(17);
				}
			}
		}).start();
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
	    score1 = 0;
	    score2 = 0;
	    pause = false;
	    TXTscore1.setText(String.valueOf(score1));
	    TXTscore2.setText(String.valueOf(score2));
	    TXTwin.setText("");
	    paddle1.setPosition(paddle1.getX(), 220-50);
	    paddle2.setPosition(paddle2.getX(), 220-50);
	    spawn_ball((int) (0 + (Math.random() * ((1 - 0) + 1))));
	}
	private Rectangle makeColoredRectangle(final float pX, final float pY, final float pW, final float pH, final float pRed, final float pGreen, final float pBlue) {
        final Rectangle coloredRect = new Rectangle(pX, pY, pW, pH, this.getVertexBufferObjectManager());
        coloredRect.setColor(pRed, pGreen, pBlue);
        return coloredRect;
	}private Rectangle makePlayerPaddle() {
		Rectangle paddle = new Rectangle(750, 0, 50, 100, this.getVertexBufferObjectManager()){
			@Override
			public boolean onAreaTouched(TouchEvent pSceneTouchEvent, float pTouchAreaLocalX, float pTouchAreaLocalY) {
		        if (pSceneTouchEvent.getAction() == TouchEvent.ACTION_MOVE) {
		        	this.setPosition(this.getX(),pSceneTouchEvent.getY()-50);	
		        	if (this.getY()>CAMERA_HEIGHT-this.getHeight()){
		        		this.setPosition(this.getX(),CAMERA_HEIGHT-this.getHeight());
		        	}else if (this.getY()<0){
		        		this.setPosition(this.getX(),0);
		        		}
		        }
		        return true;
			}
		};
	    paddle.setColor(0.85f, 0.85f, 0.85f);
	    return paddle;
	}
	private void spawn_ball(int direction){
	    ball.setPosition(CAMERA_HEIGHT/2-ball.getWidth()/2, CAMERA_WIDTH/2-ball.getWidth()/2); 
	    if (direction==0){
	    	vel_x = (float) (2 + (Math.random() * ((4 - 2) + 1)));
	    	vel_y = (float) (1 + (Math.random() * ((3 - 1) + 1))) * -1;
	    }else if (direction==1){
	    	vel_x = (float) (2 + (Math.random() * ((4 - 2) + 1))) * -1;
    		vel_y = (float) (1 + (Math.random() * ((3 - 1) + 1))) * -1;
	    }
	}

}