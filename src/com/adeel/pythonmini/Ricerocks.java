package com.adeel.pythonmini;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import org.andengine.engine.camera.Camera;
import org.andengine.engine.options.EngineOptions;
import org.andengine.engine.options.ScreenOrientation;
import org.andengine.engine.options.resolutionpolicy.RatioResolutionPolicy;
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
import org.andengine.opengl.vbo.VertexBufferObjectManager;
import org.andengine.ui.activity.SimpleBaseGameActivity;
import org.andengine.util.adt.io.in.IInputStreamOpener;
import org.andengine.util.debug.Debug;
import android.os.SystemClock;

public class Ricerocks extends SimpleBaseGameActivity{
	
	private static int CAMERA_WIDTH = 800;
	private static int CAMERA_HEIGHT = 480;
	private int score = 0, lives = 0;
	private boolean started = false;
	private TextureRegion TRbg, TRhud, TRthrust, TRidle, TRlaser, TRasteroids, TRright, TRleft, TRforward, TRfire, TRback, TRnew, TRsplash, TRhelp;
	private ArrayList<TextureRegion> TRexplo;
	private Set<Asteroid> allAsteroids = new HashSet<Ricerocks.Asteroid>(10);
	private Set<Laser> allMissiles = new HashSet<Ricerocks.Laser>(5);
	private Ship ship;
	private Sprite splash, New, back, help, fire, forward, left, right;
	private Scene aScene;
	private Font litho;
	private Text TXTscore, TXTlives;
	
	@Override
	public EngineOptions onCreateEngineOptions() {
		final Camera camera = new Camera(0, 0, CAMERA_WIDTH, CAMERA_HEIGHT);
		return new EngineOptions(true, ScreenOrientation.LANDSCAPE_FIXED, 
		    new RatioResolutionPolicy(CAMERA_WIDTH, CAMERA_HEIGHT), camera);
	}
	@Override
	protected void onCreateResources() {
		this.TRsplash = TextureRegionFactory.extractFromTexture(loadSprite("rice/splash.png"));
		this.TRback = TextureRegionFactory.extractFromTexture(loadSprite("rice/quit.png"));
		this.TRnew = TextureRegionFactory.extractFromTexture(loadSprite("rice/new.png"));
		this.TRhelp = TextureRegionFactory.extractFromTexture(loadSprite("rice/rhelp.png"));
		this.TRbg = TextureRegionFactory.extractFromTexture(loadSprite("rice/bg.png"));
	    this.TRhud = TextureRegionFactory.extractFromTexture(loadSprite("rice/hud.png"));
	    this.TRthrust = TextureRegionFactory.extractFromTexture(loadSprite("rice/thrustship.png"));
	    this.TRidle = TextureRegionFactory.extractFromTexture(loadSprite("rice/idleship.png"));
	    this.TRlaser = TextureRegionFactory.extractFromTexture(loadSprite("rice/laser.png"));
	    this.TRasteroids = TextureRegionFactory.extractFromTexture(loadSprite("rice/asteroids.png"));
	    this.TRright = TextureRegionFactory.extractFromTexture(loadSprite("rice/right.png"));
	    this.TRleft = TextureRegionFactory.extractFromTexture(loadSprite("rice/left.png"));
	    this.TRforward = TextureRegionFactory.extractFromTexture(loadSprite("rice/forward.png"));
	    this.TRfire = TextureRegionFactory.extractFromTexture(loadSprite("rice/fire.png"));
	    ITexture TexExplosion = loadSprite("rice/exp2.png");
	    this.TRexplo = new ArrayList<TextureRegion>();
	    int x=0,y = 0;
	    for (int i=0;i<16;i++){
	    	x = (i % 4);
	    	y = (int)(i/4);
	    	this.TRexplo.add(TextureRegionFactory.extractFromTexture(TexExplosion,0+(80*x),0+(80*y),80, 80));
	    }
	    litho = FontFactory.createFromAsset(this.getFontManager(), this.getTextureManager(), 256, 256, this.getAssets(),
	    	    "fonts/lithospro.otf", 40, true, android.graphics.Color.parseColor("#eeeeee"));
	    litho.load();
	}
	@Override
	protected Scene onCreateScene() {
		aScene = new Scene();
		Sprite bg = new Sprite(0, 0, this.TRbg, getVertexBufferObjectManager());
		aScene.attachChild(bg);
		Sprite hud = new Sprite(20, 30, this.TRhud, getVertexBufferObjectManager());
		hud.setZIndex(10);
		aScene.attachChild(hud);
		TXTlives = new Text(160, 35, litho, "99", getVertexBufferObjectManager());
		TXTlives.setZIndex(10);
		TXTlives.setText(String.valueOf(lives));
		aScene.attachChild(TXTlives);
		TXTscore = new Text(740, 35, litho, "99", getVertexBufferObjectManager());
		TXTscore.setZIndex(10);
		TXTscore.setText(String.valueOf(score));
		aScene.attachChild(TXTscore);
		ship = new Ship(400, 240, getVertexBufferObjectManager());
		aScene.attachChild(ship);
		right = new Sprite(732, 360, this.TRright, getVertexBufferObjectManager()){
			@Override
			public boolean onAreaTouched(TouchEvent pSceneTouchEvent, float pTouchAreaLocalX, float pTouchAreaLocalY) {
				if (pSceneTouchEvent.getAction() == TouchEvent.ACTION_DOWN) {
		        	ship.set_angle_vel(2.5f);
		        }if (pSceneTouchEvent.getAction() == TouchEvent.ACTION_UP) {
		        	ship.set_angle_vel(0f);
		        }
		        return true;
			    }};
	    right.setAlpha(0.8f);
	    right.setZIndex(20);
		aScene.attachChild(right);
		left = new Sprite(550, 360, this.TRleft, getVertexBufferObjectManager()){
			@Override
			public boolean onAreaTouched(TouchEvent pSceneTouchEvent, float pTouchAreaLocalX, float pTouchAreaLocalY) {
		        if (pSceneTouchEvent.getAction() == TouchEvent.ACTION_DOWN) {
		        	ship.set_angle_vel(-2.5f);
		        }if (pSceneTouchEvent.getAction() == TouchEvent.ACTION_UP) {
		        	ship.set_angle_vel(0);
		        }
		        return true;
			    }};
	    left.setAlpha(0.8f);
	    left.setZIndex(20);
	    aScene.attachChild(left);
	    forward = new Sprite(622, 342, this.TRforward, getVertexBufferObjectManager()){
			@Override
			public boolean onAreaTouched(TouchEvent pSceneTouchEvent, float pTouchAreaLocalX, float pTouchAreaLocalY) {
				if (pSceneTouchEvent.getAction() == TouchEvent.ACTION_DOWN) {
		        	ship.set_thrust(true);
		        }if (pSceneTouchEvent.getAction() == TouchEvent.ACTION_UP) {
		        	ship.set_thrust(false);
		        }
		        return true;
			    }};
		forward.setAlpha(0.8f);
		forward.setZIndex(20);
	    aScene.attachChild(forward);
	    fire = new Sprite(56, 354, this.TRfire, getVertexBufferObjectManager()){
			@Override
			public boolean onAreaTouched(TouchEvent pSceneTouchEvent, float pTouchAreaLocalX, float pTouchAreaLocalY) {
				if (pSceneTouchEvent.getAction() == TouchEvent.ACTION_DOWN) {
					if (allMissiles.size()<5){
					float[] forward = angle_to_vector(ship.getRotation()-90);
					Laser aLaser = new Laser(ship.getX() + ship.getWidth()/2 +(forward[0]*50), ship.getY() + ship.getHeight()/2 + (forward[1]*50),
			        		ship.vel_x + (forward[0]*8), ship.vel_y + (forward[1]*8), ship.getRotation(), 40, getVertexBufferObjectManager());
					aScene.attachChild(aLaser);
					allMissiles.add(aLaser);
					}
				}
		        return true;
			    }};
		fire.setAlpha(0.8f);
		fire.setZIndex(20);
	    aScene.attachChild(fire);
	    splash = new Sprite(130, 52, TRsplash, getVertexBufferObjectManager());
		aScene.attachChild(splash);
		splash.setZIndex(20);
		New = new Sprite(205, 250, TRnew, getVertexBufferObjectManager()){
		@Override
		public boolean onAreaTouched(TouchEvent pSceneTouchEvent, float pTouchAreaLocalX, float pTouchAreaLocalY) {
			if (pSceneTouchEvent.getAction() == TouchEvent.ACTION_DOWN) {
	        	new_game();
	        }
	        return true;
		    }
		};
		aScene.attachChild(New);
		New.setZIndex(20);
		back = new Sprite(470, 250, TRback, getVertexBufferObjectManager()){
			@Override
			public boolean onAreaTouched(TouchEvent pSceneTouchEvent, float pTouchAreaLocalX, float pTouchAreaLocalY) {
				if (pSceneTouchEvent.getAction() == TouchEvent.ACTION_DOWN) {
		        	Ricerocks.this.finish();
		        }
		        return true;
			    }
		};
		aScene.attachChild(back);
		back.setZIndex(20);
		help = new Sprite(340, 250, TRhelp, getVertexBufferObjectManager()){
			@Override
			public boolean onAreaTouched(TouchEvent pSceneTouchEvent, float pTouchAreaLocalX, float pTouchAreaLocalY) {
				if (pSceneTouchEvent.getAction() == TouchEvent.ACTION_DOWN) {
		        	Ricerocks.this.finish();
		        }
		        return true;
			    }
		};
		aScene.attachChild(help);
		help.setZIndex(20);
	    aScene.registerTouchArea(New);
	    aScene.registerTouchArea(help);
	    aScene.registerTouchArea(back);
	    aScene.registerTouchArea(right);
	    aScene.registerTouchArea(left);
	    aScene.registerTouchArea(forward);
	    aScene.registerTouchArea(fire);
		aScene.setTouchAreaBindingOnActionDownEnabled(true);
		end_game();
		new Thread(new Runnable() {
			@Override
			public void run() {
				while (true) {
					if (allAsteroids.size()<10 && started){
						try{
							Asteroid ast = new Asteroid(getVertexBufferObjectManager());
							aScene.attachChild(ast);
							allAsteroids.add(ast);
							aScene.sortChildren();
						}catch(Exception e){}
					}
					SystemClock.sleep(2500);
				}
			}
		}).start();
		return aScene;
	}
	private void new_game(){
		if (!started){
			lives = 3;
			score = 0;
			TXTlives.setText(String.valueOf(lives));
			TXTscore.setText(String.valueOf(score));
			started = true;
			fire.setVisible(true);
			left.setVisible(true);
			right.setVisible(true);
			forward.setVisible(true);
			splash.setVisible(false);
			New.setVisible(false);
			back.setVisible(false);
			help.setVisible(false);
			aScene.unregisterTouchArea(New);
		    aScene.unregisterTouchArea(back);
		    aScene.unregisterTouchArea(help);
		    aScene.registerTouchArea(right);
		    aScene.registerTouchArea(left);
		    aScene.registerTouchArea(forward);
		    aScene.registerTouchArea(fire);
		}
	}private void end_game(){
		started = false;
		fire.setVisible(false);
		left.setVisible(false);
		right.setVisible(false);
		forward.setVisible(false);
		splash.setVisible(true);
		New.setVisible(true);
		help.setVisible(true);
		back.setVisible(true);
		aScene.registerTouchArea(New);
		aScene.registerTouchArea(help);
	    aScene.registerTouchArea(back);
	    aScene.unregisterTouchArea(right);
	    aScene.unregisterTouchArea(left);
	    aScene.unregisterTouchArea(forward);
	    aScene.unregisterTouchArea(fire);
		Object[] temp = allAsteroids.toArray();
		for (int i = 0; i < temp.length; i++) {
			aScene.detachChild((Asteroid)temp[i]);
			allAsteroids.remove((Asteroid)temp[i]);
		}
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
	class Ship extends Sprite{
		public float vel_x = 0, vel_y = 0;
		private float ang_vel=0;
		private Sprite thrust;
		public Ship(float pX, float pY, VertexBufferObjectManager VertexBufferObject) {
			super(pX, pY, TRidle, VertexBufferObject);
			thrust = new Sprite(pX, pY, TRthrust, VertexBufferObject);
			thrust.setVisible(false);
	        this.setRotationCenter(this.getWidth()/2, this.getHeight()/2);
	        thrust.setRotationCenter(this.getWidth()/2, this.getHeight()/2);
		}
		@Override
		public void onAttached() {
			this.getParent().attachChild(thrust);
			super.onAttached();
		}
		@Override
		protected void onManagedUpdate(float pSecondsElapsed) {
			if (this.thrust.isVisible()){
					vel_x += (angle_to_vector(this.getRotation()-90)[0]/4.0);
					vel_y += (angle_to_vector(this.getRotation()-90)[1]/4.0);
			}
			//limiting the velocity
			vel_x *= 0.98;
	        vel_y *= 0.98;
			//# update angle
	        this.setRotation(this.getRotation() + this.ang_vel);
	        this.thrust.setRotation(this.thrust.getRotation()+this.ang_vel);
	        //# update position
	        this.setX((this.getX() + this.vel_x + CAMERA_WIDTH) % CAMERA_WIDTH);
	        this.setY((this.getY() + this.vel_y + CAMERA_HEIGHT) % CAMERA_HEIGHT);
	        this.thrust.setX((this.thrust.getX() + this.vel_x + CAMERA_WIDTH) % CAMERA_WIDTH);
	        this.thrust.setY((this.thrust.getY() + this.vel_y + CAMERA_HEIGHT) % CAMERA_HEIGHT);
	        final Object[] temp = allAsteroids.toArray();
	        for (int i = 0; i < temp.length; i++) {
	        	final int j = i;
	        	if (this.collidesWith((Asteroid) temp[i])){
	        		int x = (int) (((this.getX()+this.getWidth()/2) + (((Asteroid)temp[i]).getX()+((Asteroid)temp[i]).getWidth()/2))/2);
	        		int y = (int) (((this.getY()+this.getHeight()/2) + (((Asteroid)temp[i]).getY()+((Asteroid)temp[i]).getHeight()/2))/2);
	        		Explosion exp = new Explosion(x, y, getVertexBufferObjectManager()); 
					aScene.attachChild(exp);
	        		runOnUpdateThread(new Runnable() {
						public void run() {
							((Asteroid) temp[j]).detachSelf();
							allAsteroids.remove((Asteroid) temp[j]);
							lives -= 1;
							TXTlives.setText(String.valueOf(lives));
							if (lives==0){
								end_game();
							}
						}
					});
		        }				
			}
			super.onManagedUpdate(pSecondsElapsed);
		}
		public void set_angle_vel(float vel){
	        ang_vel = vel;
		}
		public void set_thrust(boolean value){
			thrust.setVisible(value);
		}
	}
	class Asteroid extends Sprite{
		private float vel_x = 0, vel_y = 0, ang_vel;
		ArrayList<Integer> dir = new ArrayList<Integer>();
		public Asteroid(VertexBufferObjectManager pSpriteVertexBufferObject) {
			super((float) (0 + (Math.random() * ((CAMERA_WIDTH - 0) + 1))), (float) (0 + (Math.random() * ((CAMERA_HEIGHT - 0) + 1))), TRasteroids, pSpriteVertexBufferObject);
			dir.add(-1);
			dir.add(1);
			setAlpha(0.95f);
			setZIndex(5);
	 	    //random position, velocity, angle, directions etc
	        //gradually increasing velocity of rocks by score
	        vel_x = (float) (0.7 + (Math.random() * ((1.2 - 0.7) + 1)));
	        vel_y = (float) (0.7 + (Math.random() * ((1.2 - 0.7) + 1)));
	        setRotationCenter(this.getWidth()/2, this.getHeight()/2);
	        Collections.shuffle(dir); vel_x *= dir.get(0);
	        Collections.shuffle(dir); vel_y *= dir.get(0);
	        this.mRotation = (float) (0 + (Math.random() * ((360 - 0) + 1)));
	        this.ang_vel = (float) (0.01 + (Math.random() * ((0.5 - 0.08) + 1)));
	        Collections.shuffle(dir); ang_vel *= dir.get(0);
//	        #if ship is not near spawning point only than spawn
	        if (getX() > ship.getX()-100 || getX() < ship.getX()+100){
	        	setX(getX()+150);
	        }
	        if (getY() < ship.getY()-100 || getY() > ship.getY()+100){
	        	setY(getY()+150);
	        }
		}
		@Override
		protected void onManagedUpdate(float pSecondsElapsed) {
			//# update angle
	        this.mRotation += this.ang_vel;
	        //# update position
	        this.setX((this.getX() + this.vel_x + CAMERA_WIDTH) % CAMERA_WIDTH);
	        this.setY((this.getY() + this.vel_y + CAMERA_HEIGHT) % CAMERA_HEIGHT);
			super.onManagedUpdate(pSecondsElapsed);
		}
	}class Laser extends Sprite{
		private float vel_x = 0, vel_y = 0;
		private int life = 0, age= 0;
		ArrayList<Integer> dir = new ArrayList<Integer>();
		public Laser(float px, float py, float vx, float vy, float ang, int life, VertexBufferObjectManager pSpriteVertexBufferObject) {
			super(px, py, TRlaser, pSpriteVertexBufferObject);
			vel_x = vx;
			vel_y = vy;
			setX(getX()-getWidth()/2);
			setY(getY()-getHeight()/2);
			setRotationCenter(this.getWidth()/2, this.getHeight()/2);
			this.life = life;
	        this.mRotation = ang;
		}
		@Override
		protected void onManagedUpdate(float pSecondsElapsed) {
	        age += 1;
			//# update position
	        this.setX((this.getX() + this.vel_x + CAMERA_WIDTH) % CAMERA_WIDTH);
	        this.setY((this.getY() + this.vel_y + CAMERA_HEIGHT) % CAMERA_HEIGHT);
	        if (!isDead()){
	        	final Object[] temp;
	        	try{
	        		temp = allAsteroids.toArray();
		        	for (int i = 0; i < temp.length; i++) {
		        		final int j = i;
		        		if(this.collidesWith((Asteroid) temp[i])){
		        			Explosion exp = new Explosion(((Asteroid) temp[j]).getX(), ((Asteroid) temp[j]).getY(), getVertexBufferObjectManager()); 
							aScene.attachChild(exp);
		        			runOnUpdateThread(new Runnable() {
		        		        @Override
		        		        public void run() {
		        						aScene.detachChild(Laser.this);
		        			        	allMissiles.remove(Laser.this);
		        			        	((Asteroid) temp[j]).detachSelf();
		    							allAsteroids.remove((Asteroid) temp[j]);
		    							score += 1;
		    							TXTscore.setText(String.valueOf(score)); 
		        		        }
		        		    });
		        		}
					}
	        	}catch(Exception e){
	        	}
	        }else{
	        runOnUpdateThread(new Runnable() {
		        @Override
		        public void run() {
						aScene.detachChild(Laser.this);
			        	allMissiles.remove(Laser.this);
		        }
		    });
	        }
			super.onManagedUpdate(pSecondsElapsed);
	        }
		public boolean isDead() {
			return (age>=life && this.hasParent());

		}
	}class Explosion extends Sprite{
		private int age= 0, index=0;
		private ArrayList<Sprite> frames; 
		ArrayList<Integer> dir = new ArrayList<Integer>();
		public Explosion(float px, float py, VertexBufferObjectManager pSpriteVertexBufferObject) {
			super(px, py, TRexplo.get(0), pSpriteVertexBufferObject);
			setZIndex(6);
			setVisible(false);
			this.frames = new ArrayList<Sprite>();
			for (int i = 0; i < TRexplo.size(); i++) {
				frames.add(new Sprite(px, py, TRexplo.get(i), pSpriteVertexBufferObject));
				frames.get(i).setVisible(false);
			}
			frames.get(index).setVisible(true);
		}
		@Override
		public void onAttached() {
			for (int i = frames.size()-1;i>=0;i--) {
				aScene.attachChild(frames.get(i));
			}
			super.onAttached();
		}
		@Override
		public void onDetached() {
			for (int i = 0; i < frames.size(); i++) {
				aScene.detachChild(frames.get(i));
			}
			super.onDetached();
		}
		@Override
		protected void onManagedUpdate(float pSecondsElapsed) {
	        age += 1;
	        if ((int)(age % 4) == 0){
        		frames.get(index).setVisible(false);
        		if(index+1<frames.size()){
        		frames.get(index+1).setVisible(true);
	        		}
        		index += 1;
	        }
	        if (index==frames.size()){
	        	runOnUpdateThread(new Runnable() {
			        @Override
			        public void run() {
							detachSelf();
			        }
			    });
	        }
			super.onManagedUpdate(pSecondsElapsed);
	        }
	}
	private float[] angle_to_vector(float ang) {
		float arr[] = {(float) Math.cos(Math.toRadians(ang)), (float) Math.sin(Math.toRadians(ang))};
		return arr;
	}
}
