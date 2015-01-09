package com.luk.game;

import java.util.Random;

import org.andengine.engine.camera.Camera;
import org.andengine.engine.handler.IUpdateHandler;
import org.andengine.engine.handler.timer.ITimerCallback;
import org.andengine.engine.handler.timer.TimerHandler;
import org.andengine.engine.options.EngineOptions;
import org.andengine.engine.options.ScreenOrientation;
import org.andengine.engine.options.resolutionpolicy.RatioResolutionPolicy;
import org.andengine.entity.Entity;
import org.andengine.entity.primitive.Rectangle;
import org.andengine.entity.scene.IOnSceneTouchListener;
import org.andengine.entity.scene.Scene;
import org.andengine.entity.scene.background.Background;
import org.andengine.entity.sprite.AnimatedSprite;
import org.andengine.entity.sprite.Sprite;
import org.andengine.entity.text.Text;
import org.andengine.entity.text.TextOptions;
import org.andengine.entity.util.FPSLogger;
import org.andengine.extension.physics.box2d.PhysicsFactory;
import org.andengine.extension.physics.box2d.PhysicsWorld;
import org.andengine.extension.physics.box2d.util.Vector2Pool;
import org.andengine.input.touch.TouchEvent;
import org.andengine.opengl.font.Font;
import org.andengine.opengl.font.FontFactory;
import org.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlas;
import org.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlasTextureRegionFactory;
import org.andengine.opengl.texture.region.TextureRegion;
import org.andengine.opengl.texture.region.TiledTextureRegion;
import org.andengine.opengl.vbo.VertexBufferObjectManager;
import org.andengine.ui.activity.BaseGameActivity;
import org.andengine.util.HorizontalAlign;
import org.andengine.util.color.Color;

import android.graphics.Typeface;
import android.hardware.SensorManager;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.FixtureDef;
/**
 //STARTED SINCE
 //TODO: stan 7 I 15
  * [done]naprawić ustawianie animacji po skończonej akcji
  * -> zrobić platformy i goombasy (te niebieskie luje, ew bulbasaury)
 * @author lukasz
 *
 */
public class GameActivity extends BaseGameActivity implements
		IOnSceneTouchListener {
	Scene scene;
	Camera mCamera;

	PhysicsWorld physicsWorld;
	SceneManager sceneManager;

	Random generator = new Random();

	public Font mFont;
	public Entity Coins = new Entity();
	public Text leftText;
	private BitmapTextureAtlas StaticTextures;
//	private Sprite SquirtleMoveable;

	private TextureRegion meadowTextureRegion;
	public TextureRegion pavementTextureRegion;
	private TextureRegion dojoTextureRegion;
	private TextureRegion grassTextureRegion;
	public TextureRegion coinTextureRegion;
	public  int ALL_COINS_NUMBER=0;
	protected boolean rotateEnabled=false;
	
	protected Entity GROUNDS = new Entity();
	private int PermeableCount =5;
	private Rectangle platforms[] = new Rectangle[PermeableCount];
	private int indexForPermeable = 0;

	
	protected boolean BulbyLeft;
	
	//essential variables for Opponents creation
	public BitmapTextureAtlas squirtleTexture;
	public TiledTextureRegion TiledSquirtleRegion;
	public TiledTextureRegion TiledBitingSquirtleRegion;
	public TiledTextureRegion TiledHiddenSquirtleRegion;
	public TiledTextureRegion deadWalkingSquirtleFall;
	public TiledTextureRegion deadSquirtleShellCrash;
	//public static final double TAN15 = Math.sqrt( (1-Math.sqrt(3)/2) /2) / Math.sqrt( (1+Math.sqrt(3)/2) /2); 
	public int OpponentsCount = 15;
	public Sprite[] SquirtleMoveable = new AnimatedSprite[OpponentsCount];
	private KoopaTroopa[] blendzior = new KoopaTroopa[OpponentsCount];
	
	//essential variables for creation Clefairy-singleton character
	public AnimatedSprite ClefairyMoveable;
	public TiledTextureRegion TiledClefairyRegion;
	public TiledTextureRegion ImmortalTiledClefairyRegion;
	public BitmapTextureAtlas clefairyTexture;
	
	public Body ClefairyBody;
	private Clefairy character;
	protected int isOnGround = 2;
	
	protected float SpeedForSquirtle=1f;
	protected int touches_from_top=0;
	private BitmapTextureAtlas pokeAtlas;
	private TextureRegion pokecenter;
	private TextureRegion pokeshop;
	private TextureRegion poketree;
	protected int currentHunter;
	protected boolean bitten;
	
	
	private Rectangle left_wall_reference;
	private Rectangle right_wall_reference;
	private Rectangle floor_reference;
	
	protected int currentDead2;
	protected int currentDead1;
	private boolean moveLeft;
	protected static final int CAMERA_WIDTH = 800;
	protected static final int CAMERA_HEIGHT = 480;

	@Override
	public EngineOptions onCreateEngineOptions() {
		// TODO Auto-generated method stub
		mCamera = new Camera(0, 0, CAMERA_WIDTH, CAMERA_HEIGHT);
		EngineOptions options = new EngineOptions(true,
				ScreenOrientation.LANDSCAPE_FIXED, new RatioResolutionPolicy(
						CAMERA_WIDTH, CAMERA_HEIGHT), mCamera);
		return options;
	}
	public GameActivity getContext(){
		return this;
	}
	private void LoadNewOpponents(){
		for(int i=0; i <OpponentsCount; i++){
			blendzior[i] = new KoopaTroopa(this, 400 + 200*i, 1000+200*i);
			blendzior[i].onLoad();
		}
	}
	private void onLoadClefairy() {
		character = Clefairy.getClefairy(this, 50, CAMERA_WIDTH/2);
		character.onLoad();
	}
	private void onCreateFonts() {
		this.mFont = FontFactory.create(this.getFontManager(),
				this.getTextureManager(), 256, 256,
				Typeface.create(Typeface.DEFAULT, Typeface.BOLD), 32);
		this.mFont.load();
	}

	private void onCreateSceneFonts() {
		leftText = new Text(100, 170, this.mFont, "nic", new TextOptions(
				HorizontalAlign.LEFT), getVertexBufferObjectManager());
		scene.attachChild(leftText);
	}

	@Override
	public void onCreateResources(
			OnCreateResourcesCallback pOnCreateResourcesCallback)
			throws Exception {
		// TODO Auto-generated method stub
		// sceneManager = new SceneManager(this, mEngine, mCamera);
		// sceneManager.loadSplashResources();
		loadStaticGfx();
		onLoadClefairy();
		LoadNewOpponents();

		onCreateFonts();
		pOnCreateResourcesCallback.onCreateResourcesFinished();
	}

	private void loadStaticGfx() {
		BitmapTextureAtlasTextureRegionFactory.setAssetBasePath("gfx/");
		// width and height power of 2^x
		StaticTextures = new BitmapTextureAtlas(getTextureManager(), 32, 32);
		pavementTextureRegion = BitmapTextureAtlasTextureRegionFactory
				.createFromAsset(StaticTextures, this, "pavement_tile.png", 0,
						0);
		coinTextureRegion = BitmapTextureAtlasTextureRegionFactory
				.createFromAsset(StaticTextures, this, "coin_tile.png", 16, 0);
		grassTextureRegion = BitmapTextureAtlasTextureRegionFactory
				.createFromAsset(StaticTextures, this, "grass_tile.png", 16, 16);
		dojoTextureRegion = BitmapTextureAtlasTextureRegionFactory
				.createFromAsset(StaticTextures, this, "dojo_tile.png", 0, 16);
		StaticTextures.load();

		pokeAtlas = new BitmapTextureAtlas(getTextureManager(),256,256);

		pokecenter = BitmapTextureAtlasTextureRegionFactory
				.createFromAsset(pokeAtlas, this, "poke_center.png", 0,0);

		pokeshop = BitmapTextureAtlasTextureRegionFactory
				.createFromAsset(pokeAtlas, this, "poke_shop.png", 83,0);
		poketree = BitmapTextureAtlasTextureRegionFactory
				.createFromAsset(pokeAtlas, this, "tree.png", 0,71);
		
		pokeAtlas.load();
	}

	@Override
	public void onCreateScene(OnCreateSceneCallback pOnCreateSceneCallback)
			throws Exception {
		this.mEngine.registerUpdateHandler(new FPSLogger());

		this.scene = new Scene();
		this.scene.setOnSceneTouchListener(this);
		this.scene.setBackground(new Background(0, 125, 58));
		physicsWorld = new PhysicsWorld(new Vector2(0,
				SensorManager.GRAVITY_EARTH), false);
		this.scene.registerUpdateHandler(physicsWorld);
		pOnCreateSceneCallback.onCreateSceneFinished(this.scene);
		// pOnCreateSceneCallback.onCreateSceneFinished(sceneManager.createSplashScene());
	}

	private void onCreateItems() {
//		Sprite[] trees = new Sprite[32];
		Sprite[] pokeshops = new Sprite[32];
		Sprite[] pokecenters = new Sprite[32];
		final VertexBufferObjectManager vbo = 
				this.mEngine.getVertexBufferObjectManager();
		final float dY = CAMERA_HEIGHT -56f+37;
		
		for(int i=0;i<31;i++){
//			trees[i] = new Sprite(100+200*i, dY-53f, poketree, vbo);
//			scene.attachChild(trees[i]);
			//TODO:populate trees?
			if(100-65+200*i < 6*CAMERA_WIDTH && 700+200*i < 6*CAMERA_WIDTH){
			pokeshops[i] = new Sprite(200*i, dY-62, pokeshop, vbo);
			scene.attachChild(pokeshops[i]);
			pokecenters[i] = new Sprite(83+200*i, dY-71, pokecenter, vbo);
			scene.attachChild(pokecenters[i]);
			}
		}
		
	}
	private void onCreateClefairy() {
		character.onCreateClefairy();
	}
	@Override
	public void onPopulateScene(Scene pScene,
			OnPopulateSceneCallback pOnPopulateSceneCallback) throws Exception {
		final VertexBufferObjectManager vbom = vbo();
		onCreateItems();
		onCreateClefairy();
		for(int i=0;i<OpponentsCount;i++)
			blendzior[i].onCreate();
		
		createWalls();
		onCreateSceneFonts();
		scene.attachChild(Coins);
		scene.attachChild(GROUNDS);
		// mEngine.registerUpdateHandler(new TimerHandler(3f, new
		// ITimerCallback() {
		// @Override
		// public void onTimePassed(TimerHandler pTimerHandler) {
		// mEngine.unregisterUpdateHandler(pTimerHandler);
		// sceneManager.loadMenuResources();
		// sceneManager.createMenuScene();
		// sceneManager.setCurrentScene(AllScenes.MENU);
		// }
		// }));
		
//		this.scene.registerUpdateHandler(new TimerHandler(0.7f, new ITimerCallback() {
//			@Override
//			public void onTimePassed(TimerHandler pTimerHandler) {		
//			}
//		}));
		mEngine.registerUpdateHandler(new IUpdateHandler() {
			@Override
			public void reset() {
			}
			@Override
			public void onUpdate(float pSecondsElapsed) {
//				if(ALL_COINS_NUMBER-Coins.getChildCount() >5 && ALL_COINS_NUMBER-Coins.getChildCount() < 7)
//					character.setAnimation(ImmortalTiledClefairyRegion);
//				if(ALL_COINS_NUMBER-Coins.getChildCount() == 7 )
//					character.setAnimation(TiledClefairyRegion);
				
				//loop for permeable floors
//				UpdatePermeableBlocks();
				
				//loops for opponents only
				for(int i=0;i<OpponentsCount;i++){
					if(blendzior[i]!=null && !blendzior[i].Dead){
						if(blendzior[i].getCollisionsNumber()==0)
							blendzior[i].LoopForOponents(true);
						else
							blendzior[i].LoopForOponents(false);
						boolean nothing = true;
						for(int j=0; j<OpponentsCount && nothing;j++){
							if(j!=i){
								if(SquirtleMoveable[j].collidesWith( (AnimatedSprite)  SquirtleMoveable[i])){
									if(blendzior[i].getCollisionsNumber()==0){
										blendzior[i].SwitchWalkDirection();
										blendzior[j].SwitchWalkDirection();
									}
									else if(blendzior[i].getCollisionsNumber()<2 
											&& blendzior[j].getCollisionsNumber()==0){
										blendzior[j].performDeath();
									}
									else if(blendzior[i].getCollisionsNumber() >0 
											&& blendzior[j].getCollisionsNumber() >0 &&
											blendzior[i].getCollisionsNumber() <=2 
											&& blendzior[j].getCollisionsNumber() <=2){
										blendzior[i].performDeath();
										blendzior[j].performDeath();
									}
									
									nothing=false;
								}
							}
						}
						
						if(SquirtleMoveable[i].collidesWith(left_wall_reference) ||
							SquirtleMoveable[i].collidesWith(right_wall_reference)){
							blendzior[i].SwitchWalkDirection();
						}
						else if(SquirtleMoveable[i].collidesWith(ClefairyMoveable)){
							//blendzior[i].CollisionWalkDetected();
							if(blendzior[i].getCollisionsNumber()==0){
								blendzior[i].performBitingAction();
							}
							
							float dx = SquirtleMoveable[i].getX() - ClefairyMoveable.getX();
							if(dx<0){
								ClefairyMoveable.setFlippedHorizontal(!true);
								ClefairyBody.setLinearVelocity(3f,-3f);
							}
							else{
								ClefairyMoveable.setFlippedHorizontal(!false);
								ClefairyBody.setLinearVelocity(-3f,-3f);
							}
							blendzior[i].ClefairyCollision();
						}
					}
				}
				//Clefiary on the ground
				if(ClefairyMoveable.collidesWith(floor_reference))
					isOnGround = 2;
				//Clefairy collects coins
				for(int i = 1; i < Coins.getChildCount(); i++)
					if(ClefairyMoveable.collidesWith((Sprite)Coins.getChildByIndex(i))){
						Coins.detachChild(Coins.getChildByIndex(i));
						scene.detachChild(leftText);
						leftText = new Text(ClefairyMoveable.getX(), ClefairyMoveable.getY()-50, mFont, "masz "
						+(ALL_COINS_NUMBER-Coins.getChildCount())+" $",
								new TextOptions(HorizontalAlign.LEFT),
								vbom);
						scene.attachChild(leftText);
						break;
				}

				if(Coins.getChildCount()==1){
					scene.detachChild(leftText);
					leftText = new Text(ClefairyMoveable.getX()+50, ClefairyMoveable.getY()-80, mFont, "Hajs sie zgadza",
							new TextOptions(HorizontalAlign.LEFT),
							vbom);
					scene.attachChild(leftText);
					
				}
			}
		});
		pOnPopulateSceneCallback.onPopulateSceneFinished();
	}

	private void addCoin(float dx, float dy) {
		Sprite geld = new Sprite(dx, dy, coinTextureRegion,
				getVertexBufferObjectManager());
		Coins.attachChild(geld);
		Coins.setChildrenVisible(true);
	}
	
//	private void UpdatePermeableBlocks(){
//		float Yup = ClefairyMoveable.getY();
//		float Ydown = ClefairyMoveable.getY()+ClefairyMoveable.getHeight();
//		for(int i = 0; i < PermeableCount; i++){
//			if(platforms[i].getY() > Yup){
//				if(platforms[i].getY()+platforms[i].getHeight()> Ydown){
//					if(platforms[i].isVisible()==false)
//						scene.attachChild(platforms[i]);
//				}
//				else if (platforms[i].isVisible()){
//					platforms[i].setVisible(false);
//					scene.detachChild(platforms[i]);
//					
//				}
//			}else if (platforms[i].isVisible()){
//				platforms[i].setVisible(false);
//				scene.detachChild(platforms[i]);
//			}
//		}
//	}
	
//	private void createPermeableBlock(float left, float top, float width, float height,
//			FixtureDef fix){
//		platforms[indexForPermeable] = new Rectangle(left, top, width, height,vbo());
//		platforms[indexForPermeable].setVisible(true);
//		
//		
//		PhysicsFactory.createBoxBody(physicsWorld, platforms[indexForPermeable], BodyType.StaticBody,fix);
//		scene.attachChild(platforms[indexForPermeable]);
//
//		++indexForPermeable;
//	}
	
	
	private void createBlock(float left, float top, float width, float height,
			FixtureDef fix) {
		Rectangle ground = new Rectangle(left, top, width, height,
				vbo());
		if(width==16 && height==CAMERA_HEIGHT+16){
			if(left==-16.0f)
				left_wall_reference = ground;
			else if(left==6 * CAMERA_WIDTH - 16.0f)
				right_wall_reference = ground;
		}
		if(left==-16.0f && top == CAMERA_HEIGHT - 16 && width == 6 * CAMERA_WIDTH && height == 16){
			floor_reference = ground;
		}
		ground.setColor(new Color(15, 51, 5));
		PhysicsFactory.createBoxBody(physicsWorld, ground, BodyType.StaticBody,fix);
		scene.attachChild(ground);
		int j = 0;
		for (int i = 0; i < width / 16; i++) {
			Sprite s = new Sprite(left + i * 16, top + j * 16,
					pavementTextureRegion,
					this.mEngine.getVertexBufferObjectManager());
			scene.attachChild(s);
		}
	}

	private void createWalls() {
		FixtureDef WALL_FIX = PhysicsFactory.createFixtureDef(0.0f, 0.0f, 9000.0f);
		// floor
		
		createBlock(-16.0f, CAMERA_HEIGHT - 16, 6 * CAMERA_WIDTH, 16, WALL_FIX);
		// left wall
		createBlock(-16.0f, -16f, 16, CAMERA_HEIGHT + 16, WALL_FIX);
		// right wall
		createBlock(6 * CAMERA_WIDTH - 16.0f, -16f, 16, CAMERA_HEIGHT + 16,
				WALL_FIX);

//		int count=0;
		for(int i = 0; i <  6 * CAMERA_WIDTH / 64; i++){
			addCoin(32 + i * 64f, CAMERA_HEIGHT - 32 - 32f);
//			if(count<3)
//				createPermeableBlock(32 + i * 64f, CAMERA_HEIGHT - 32 - 32f - 10*i,
//						64, 16, WALL_FIX);
//			count++;
		}
		ALL_COINS_NUMBER = Coins.getChildCount();
		
	}

	@Override
	public boolean onSceneTouchEvent(Scene pScene, TouchEvent pSceneTouchEvent) {
		if (pSceneTouchEvent.isActionDown()) {
			float dy = pSceneTouchEvent.getY() - ClefairyMoveable.getY();
			float dx = pSceneTouchEvent.getX() - ClefairyMoveable.getX();
			
			Vector2 velocity;
			float euclides = (float) Math.sqrt(dy * dy + dx * dx);
			float Len = 6.0f;
			if (dy != 0)
				velocity = Vector2Pool.obtain(Len * dx / euclides, Len * dy
						/ euclides);
			else
				velocity = Vector2Pool.obtain(Len * dx / euclides, 0f);
			
			boolean prawieskok = ( Math.abs(Len*dx/euclides) 
					> 3*Math.abs(Len*dy/euclides) ) ? true : false;
			if(isOnGround==2 || isOnGround==1 || prawieskok){
				
				ClefairyBody.setLinearVelocity(velocity);
				Vector2Pool.recycle(velocity);
			if(isOnGround==1 || isOnGround==2)
				--isOnGround;
			}
			if (dx > 0) {
				ClefairyMoveable.setFlippedHorizontal(true);
			} else {
				ClefairyMoveable.setFlippedHorizontal(false);
			}
			return true;
		}
		return false;
	}
	public VertexBufferObjectManager vbo() {
		return this.getVertexBufferObjectManager();
	}

}