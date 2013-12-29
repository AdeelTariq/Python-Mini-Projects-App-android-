package com.adeel.pythonmini;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

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
import org.andengine.util.HorizontalAlign;
import org.andengine.util.adt.io.in.IInputStreamOpener;
import org.andengine.util.debug.Debug;

import android.content.Intent;
import android.os.SystemClock;
import android.util.Log;

public class Black extends SimpleBaseGameActivity{
	
	private static int CAMERA_WIDTH = 480;
	private static int CAMERA_HEIGHT = 800;
	private int CARD_WIDTH = 71, CARD_HEIGHT = 96;
	private TextureRegion TRtitle, TRback, TRlabel, TRstand, TRhit, TRdeal, TRhelp, TRcover;
	private ITexture Texturedeck;
	private boolean in_play = false;
	Deck deck;
	private Hand player, dealer;
	private IFont litho;
	private Text prompt;
	private Sprite cover, stand, hit, deal;
	private Scene aScene;
	private String SUITS[] = {"C", "S", "H", "D"};
	private String RANKS[] = {"A", "2", "3", "4", "5", "6", "7", "8", "9", "T", "J", "Q", "K"};
	private Map<String, Integer> VALUES = new HashMap<String, Integer>();
	
	@Override
	public EngineOptions onCreateEngineOptions() {
		 VALUES.put("A", 1); VALUES.put("2", 2);	VALUES.put("3", 3); VALUES.put("4", 4);
		 VALUES.put("5", 5);	VALUES.put("6", 6); VALUES.put("7", 7); VALUES.put("8", 8);
		 VALUES.put("9", 9); VALUES.put("T", 10); VALUES.put("J", 11); VALUES.put("Q", 12); VALUES.put("K", 13);
		 final Camera camera = new Camera(0, 0, CAMERA_WIDTH, CAMERA_HEIGHT);
		 return new EngineOptions(true, ScreenOrientation.PORTRAIT_FIXED, 
		    new RatioResolutionPolicy(CAMERA_WIDTH, CAMERA_HEIGHT), camera);
	}
	@Override
	protected void onCreateResources() {
		    this.TRtitle = TextureRegionFactory.extractFromTexture(loadSprite("black/btitle.png"));
		    this.TRback = TextureRegionFactory.extractFromTexture(loadSprite("black/bback.png"));
		    this.TRhelp = TextureRegionFactory.extractFromTexture(loadSprite("black/bhelp.png"));
		    this.TRlabel = TextureRegionFactory.extractFromTexture(loadSprite("black/label.png"));
		    this.TRstand = TextureRegionFactory.extractFromTexture(loadSprite("black/stand.png"));
		    this.TRhit = TextureRegionFactory.extractFromTexture(loadSprite("black/hit.png"));
		    this.TRdeal = TextureRegionFactory.extractFromTexture(loadSprite("black/deal.png"));
		    this.TRcover = TextureRegionFactory.extractFromTexture(loadSprite("black/cover.png"));
		    this.Texturedeck = loadSprite("black/cards1.png");
		    litho = FontFactory.createFromAsset(this.getFontManager(), this.getTextureManager(), 256, 256, this.getAssets(),
		    	    "fonts/lithospro.otf", 36, true, android.graphics.Color.parseColor("#c7ff51"));
		    litho.load();
			player = new Hand(2);
			dealer = new Hand(2);
	}
	
	@Override
	protected Scene onCreateScene() {
		aScene = new Scene(){
			@Override
			public void onManagedUpdate(float pSeconds){
				if (in_play){
					cover.setVisible(true);
				}else{
					cover.setVisible(false);
				}
		}};
		Rectangle backgroundSprite = makeColoredRectangle(0, 0, CAMERA_WIDTH, CAMERA_HEIGHT, 0.278f, 0.278f, 0.278f);
		aScene.attachChild(backgroundSprite);
		Sprite title = new Sprite(122, 30, this.TRtitle, getVertexBufferObjectManager());
		Sprite label = new Sprite(30, 200, this.TRlabel, getVertexBufferObjectManager());
	    Sprite back = new Sprite(388, 710, this.TRback, getVertexBufferObjectManager()){
			@Override
			public boolean onAreaTouched(TouchEvent pSceneTouchEvent, float pTouchAreaLocalX, float pTouchAreaLocalY) {
		        if (pSceneTouchEvent.getAction() == TouchEvent.ACTION_UP) {
		        	Black.this.finish();
		        }
		        return true;
			    }};
	    Sprite help = new Sprite(0, 710, this.TRhelp, getVertexBufferObjectManager()){
			@Override
			public boolean onAreaTouched(TouchEvent pSceneTouchEvent, float pTouchAreaLocalX, float pTouchAreaLocalY) {
		        if (pSceneTouchEvent.getAction() == TouchEvent.ACTION_UP) {
		        	Intent i = new Intent(Black.this,About.class);
		        	i.putExtra("help", "black");
		        	startActivity(i);
		        }
		        return true;
			    }};
	    hit = new Sprite(0, 595, this.TRhit, getVertexBufferObjectManager()){
			@Override
			public boolean onAreaTouched(TouchEvent pSceneTouchEvent, float pTouchAreaLocalX, float pTouchAreaLocalY) {
		        if (pSceneTouchEvent.getAction() == TouchEvent.ACTION_UP) {
		        	if (in_play){
		                if (player.get_value()<=21){
		                    player.add_card(deck.deal_card());
		                }
		                if (player.get_value()>21){
		                    prompt.setText("Dealer won!");
		                    in_play = false;
		                }
		        	}
		        }
		        return true;
			}};
		deal = new Sprite(0, 110, this.TRdeal, getVertexBufferObjectManager()){
			@Override
			public boolean onAreaTouched(TouchEvent pSceneTouchEvent, float pTouchAreaLocalX, float pTouchAreaLocalY) {
		        if (pSceneTouchEvent.getAction() == TouchEvent.ACTION_UP) {
		        	new_deal();
		        }
		        return true;
			}};
		stand = new Sprite(275, 595, this.TRstand, getVertexBufferObjectManager()){
			@Override
			public boolean onAreaTouched(TouchEvent pSceneTouchEvent, float pTouchAreaLocalX, float pTouchAreaLocalY) {
		        if (pSceneTouchEvent.getAction() == TouchEvent.ACTION_UP) {
		        	if (in_play){
		                while (dealer.get_value()<17){	// if hand is in play, repeatedly hit dealer until his hand has value 17 or more
		                    dealer.add_card(deck.deal_card());
		                }
		                if (dealer.get_value()>21){
		                    prompt.setText("Player Won!");
		                    in_play = false;
		                }else{
		                    if (dealer.get_value()>=player.get_value()){
		                    	prompt.setText("Dealer won!");
		                        in_play = false;
		                    }else if (dealer.get_value()<player.get_value()){
		                        prompt.setText("Player won!");
		                        in_play = false;
		                    }
		                }
	                }else{
	                	prompt.setText("You are busted!");
	                }
		        }
		        return true;
			}};
	    prompt = new Text(40f, 525f, litho, "UUUUUUUUUUUUUUUUUUUUUUUUUUUU", getVertexBufferObjectManager());
	    prompt.setText("Hit or Stand ?");
	    prompt.setHorizontalAlign(HorizontalAlign.CENTER);
	    cover = new Sprite(60, 240, this.TRcover, getVertexBufferObjectManager());
	    cover.setZIndex(2);
	    aScene.attachChild(cover);
	    aScene.attachChild(deal);
	    aScene.attachChild(stand);
	    aScene.attachChild(title);
	    aScene.attachChild(back);
	    aScene.attachChild(help);
	    aScene.attachChild(hit);
	    aScene.attachChild(label);
	    aScene.attachChild(prompt);
	    aScene.registerTouchArea(back);
	    aScene.registerTouchArea(hit);
	    aScene.registerTouchArea(help);
	    aScene.registerTouchArea(stand);
	    aScene.registerTouchArea(deal);
		aScene.setTouchAreaBindingOnActionDownEnabled(true);
		new_deal();
		Thread timer = new Thread(new Runnable() {
			@Override
			public void run() {
				while(true){
					if (in_play){
						if (stand.getAlpha() < 1){
							aScene.registerTouchArea(stand);
							stand.setAlpha((float) (stand.getAlpha()+0.1));
						}if (hit.getAlpha() < 1){
							aScene.registerTouchArea(hit);
							hit.setAlpha((float) (hit.getAlpha()+0.1));
						}if (deal.getAlpha() > 0){
							aScene.unregisterTouchArea(deal);
							deal.setAlpha((float) (deal.getAlpha()-0.1));
							}
					}else{
						if (stand.getAlpha() > 0){
							aScene.unregisterTouchArea(stand);
							stand.setAlpha((float) (stand.getAlpha()-0.1));
						}if (hit.getAlpha() > 0){
							aScene.unregisterTouchArea(hit);
							hit.setAlpha((float) (hit.getAlpha()-0.1));
						}if (deal.getAlpha() < 1){
							aScene.registerTouchArea(deal);
							deal.setAlpha((float) (deal.getAlpha()+0.1));
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
	private void new_deal(){
		if (in_play){
            prompt.setText("Player lose!");
            in_play = false;
    	}else{
            deck = new Deck();
            player.unattach_all();
            dealer.unattach_all();
            player = new Hand(402);
            dealer = new Hand(240);
            player.add_card(deck.deal_card());
            player.add_card(deck.deal_card());
            dealer.add_card(deck.deal_card());
            dealer.add_card(deck.deal_card());
            prompt.setText("Hit or Stand?");
            in_play = true;
    	}
	}
	
// define card class
class Card extends TextureRegion{
	private String suit = "", rank = "";
	public Card(float x, float y, ITexture texture, String suit, String rank){
		super(texture, x, y, CARD_WIDTH, CARD_HEIGHT);
		if ((Arrays.asList(SUITS).contains(suit)) && (Arrays.asList(RANKS).contains(rank))){
        	this.suit = suit;
            this.rank = rank;
        }else{
        	Log.e("error", "Invalid card");
        }
	}
    public String get_suit(){
        return this.suit;
    }

    public String get_rank(){
        return this.rank;
    }
}

class Hand{
	private ArrayList<Card> cards = new ArrayList<Card>();
	private int y;
	private ArrayList<Sprite> all = new ArrayList<Sprite>();
	Hand(int y){
			this.y = y;
		}
    public void add_card(Card card){
        this.cards.add(card); // add a card object to a hand
        all.add(new Sprite(60 + (83 * (this.cards.size()-1)), this.y, cards.get(cards.size()-1), getVertexBufferObjectManager()));
        all.get(all.size()-1).setZIndex(1);
        aScene.attachChild(all.get(all.size()-1));
        aScene.sortChildren(false);
        }
    public void unattach_all(){
    	for (int j=0; j < all.size();j++){
            aScene.detachChild(all.get(j));
        }
    }
    public int get_value(){
        // count aces as 1, if the hand has an ace, then add 10 to hand value if it doesn't bust
        int value = 0;
        for (Iterator<Card> i = this.cards.iterator(); i.hasNext();){
            value += VALUES.get(i.next().rank);
        }
        for (Iterator<Card> j = this.cards.iterator(); j.hasNext();){
            if (j.next().rank=="A"){
                if (!(value+10>21)){
                    value += 10;
                }
            }
        }
		return value;
    }
}
class Deck{

	private ArrayList<Card> cards;
	public Deck(){
		cards = new ArrayList<Card>();
		for (int i=0; i < SUITS.length;i++){
            for (int j=0; j < RANKS.length;j++){
                cards.add(new Card(0 + (CARD_WIDTH*i), 0 + (CARD_HEIGHT*j), Texturedeck, SUITS[i], RANKS[j]));
            }
        }
        Collections.shuffle(cards);
	}
    public Card deal_card(){
        return this.cards.remove(this.cards.size()-1);
    }   
}
private Rectangle makeColoredRectangle(final float pX, final float pY, final float pW, final float pH, final float pRed, final float pGreen, final float pBlue) {
    final Rectangle coloredRect = new Rectangle(pX, pY, pW, pH, this.getVertexBufferObjectManager());
    coloredRect.setColor(pRed, pGreen, pBlue);
    return coloredRect;
}}
