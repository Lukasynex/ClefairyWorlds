package matim.development;

import java.util.Random;

import org.andengine.engine.camera.Camera;
import org.andengine.engine.handler.IUpdateHandler;
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
import org.andengine.extension.physics.box2d.PhysicsConnector;
import org.andengine.extension.physics.box2d.PhysicsFactory;
import org.andengine.extension.physics.box2d.PhysicsWorld;
import org.andengine.extension.physics.box2d.util.Vector2Pool;
import org.andengine.input.touch.TouchEvent;
import org.andengine.opengl.font.Font;
import org.andengine.opengl.font.FontFactory;
import org.andengine.opengl.texture.TextureOptions;
import org.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlas;
import org.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlasTextureRegionFactory;
import org.andengine.opengl.texture.region.ITextureRegion;
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

public class GameActivity extends BaseGameActivity implements
		IOnSceneTouchListener {
	Scene scene;
	Camera mCamera;
	BitmapTextureAtlas playerTexture;
	BitmapTextureAtlas oponentTexture;

	ITextureRegion playerTextureRegion;
	TiledTextureRegion TiledplayerTextureRegion;
	ITextureRegion oponentTextureRegion;

	PhysicsWorld physicsWorld;
	SceneManager sceneManager;
	Body body;
	// Sprite sPlayer;
	AnimatedSprite sPlayer;

	Random generator = new Random();
	private boolean passed=true;

	private Font mFont;
	private Entity Coins = new Entity();
	private Text leftText;
	private BitmapTextureAtlas StaticTextures;
//	private Sprite SquirtleMoveable;

	private TextureRegion meadowTextureRegion;
	private TextureRegion pavementTextureRegion;
	private TextureRegion dojoTextureRegion;
	private TextureRegion grassTextureRegion;
	private boolean moveLeft;
	private int indexForPlatforms = 0;
	private TextureRegion coinTextureRegion;
	private int ALL_COINS_NUMBER=0;
	protected boolean rotateEnabled=false;
	protected boolean isOnGround = true;
	protected Entity GROUNDS = new Entity();
	
	protected boolean BulbyLeft;
	
	
	public AnimatedSprite SquirtleMoveable;
	
	public TiledTextureRegion TiledSquirtleRegion;
	public BitmapTextureAtlas squirtleTexture;
	public TiledTextureRegion TiledHiddenSquirtleRegion;
	
	
	
	
	
	
	
	
	//public VertexBufferObjectManager vbo = getVertexBufferObjectManager();
	
	
	protected float SpeedForSquirtle=1f;
	protected int touches_from_top=0;
	private BitmapTextureAtlas pokeAtlas;
	private TextureRegion pokecenter;
	private TextureRegion pokeshop;
	private TextureRegion poketree;
	private Opponents blendzior = null;
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
	private void LoadNewOpponent(){
		blendzior = new Opponents(this, 600, 900);
		blendzior.onLoad();
	}
	private void onLoadClefairy() {
		BitmapTextureAtlasTextureRegionFactory.setAssetBasePath("gfx/");
		// width and height power of 2^x
		playerTexture = new BitmapTextureAtlas(getTextureManager(), 512, 512,
				TextureOptions.BILINEAR);

		this.TiledplayerTextureRegion = BitmapTextureAtlasTextureRegionFactory
				.createTiledFromAsset(this.playerTexture, this,
						"clefairy_fullwalk.png", 0, 0, 7, 1); // 385x55
		playerTexture.load();
	}
	private void onLoadSquirtle() {
		BitmapTextureAtlasTextureRegionFactory.setAssetBasePath("gfx/");
		// width and height power of 2^x
		squirtleTexture = new BitmapTextureAtlas(getTextureManager(), 256, 256,
				TextureOptions.BILINEAR);

		this.TiledSquirtleRegion = BitmapTextureAtlasTextureRegionFactory
				.createTiledFromAsset(this.squirtleTexture, this,
						"squirtle_walk.png", 0, 0, 6, 1); // 385x55
		this.TiledHiddenSquirtleRegion = BitmapTextureAtlasTextureRegionFactory
				.createTiledFromAsset(this.squirtleTexture, this,
						"squirtle_hidden.png", 0, 256-30, 8, 1); // 385x55
		squirtleTexture.load();
	}
	
	private void onCreateFonts() {
		this.mFont = FontFactory.create(this.getFontManager(),
				this.getTextureManager(), 256, 256,
				Typeface.create(Typeface.DEFAULT, Typeface.BOLD), 32);
		this.mFont.load();
	}

	private void onCreateSceneFonts() {
		final VertexBufferObjectManager vertexBufferObjectManager = this
				.getVertexBufferObjectManager();
		leftText = new Text(100, 170, this.mFont, "nic", new TextOptions(
				HorizontalAlign.LEFT), vertexBufferObjectManager);
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
		LoadNewOpponent();

		//		onLoadSquirtle();
		onCreateFonts();
		pOnCreateResourcesCallback.onCreateResourcesFinished();
	}

	private void loadStaticGfx() {
		// TODO Auto-generated method stub
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

//		oponentTexture = new BitmapTextureAtlas(getTextureManager(), 64, 64);
//		oponentTextureRegion = BitmapTextureAtlasTextureRegionFactory
//				.createFromAsset(oponentTexture, this, "bulby.png", 0, 0);
//		oponentTexture.load();
		
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
		// TODO Auto-generated method stub
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

	private void onCreateSquirtle() {
//		int i = -1;
//		SquirtleMoveable = new Sprite(-64f + i * 64f, CAMERA_HEIGHT - 72f
//				- 16f * i, oponentTextureRegion,
//				this.mEngine.getVertexBufferObjectManager());
//		scene.attachChild(SquirtleMoveable);
		SquirtleMoveable = new AnimatedSprite(-128, CAMERA_HEIGHT -56f,
				TiledSquirtleRegion,
				this.mEngine.getVertexBufferObjectManager());
		SquirtleMoveable.animate(240);
		this.scene.attachChild(SquirtleMoveable);
		
	}
	//TODO:onCreateBulbasaur
	//refactor SquirtleMoveable to bulby
	//add animation to bulby, and idle state when dead
	//add moveable shell (like turtles in mario)
	private void onCreateItems() {
		Sprite[] trees = new Sprite[10];
		Sprite[] pokeshops = new Sprite[10];
		Sprite[] pokecenters = new Sprite[10];
		final VertexBufferObjectManager vbo = 
				this.mEngine.getVertexBufferObjectManager();
		final float dY = CAMERA_HEIGHT -56f+37;
		
		for(int i=0;i<10;i++){
			trees[i] = new Sprite(100+200*i, dY-53f, poketree, vbo);
			scene.attachChild(trees[i]);
			pokeshops[i] = new Sprite(50+300*i, dY-62, pokeshop, vbo);
			scene.attachChild(pokeshops[i]);
			pokecenters[i] = new Sprite(400*i, dY-71, pokecenter, vbo);
			scene.attachChild(pokecenters[i]);
			
		}
		
	}
	private void onCreateClefairy() {
		sPlayer = new AnimatedSprite(CAMERA_WIDTH / 3, CAMERA_HEIGHT / 2,
				TiledplayerTextureRegion,
				this.mEngine.getVertexBufferObjectManager());
		sPlayer.animate(180);
		sPlayer.setRotation(-3.0f);
		final FixtureDef PLAYER_FIX = PhysicsFactory.createFixtureDef(10.0f,
				0.5f, 190.0f);
//		body = PhysicsFactory.createBoxBody(physicsWorld, sPlayer, BodyType.DynamicBody, PLAYER_FIX);
		body = PhysicsFactory.createCircleBody(physicsWorld, sPlayer,
				BodyType.DynamicBody, PLAYER_FIX);
		this.scene.attachChild(sPlayer);
		physicsWorld.registerPhysicsConnector(new PhysicsConnector(sPlayer,
				body, true, false));
		mCamera.setChaseEntity(sPlayer);
	}
	

	@Override
	public void onPopulateScene(Scene pScene,
			OnPopulateSceneCallback pOnPopulateSceneCallback) throws Exception {
		
		onCreateItems();
		onCreateClefairy();
		blendzior.onCreate();
		
		//onCreateSquirtle();
		createWalls();
		onCreateSceneFonts();
		scene.attachChild(Coins);
		scene.attachChild(GROUNDS);
		// mEngine.registerUpdateHandler(new TimerHandler(3f, new
		// ITimerCallback() {
		// @Override
		// public void onTimePassed(TimerHandler pTimerHandler) {
		// // TODO Auto-generated method stub
		// mEngine.unregisterUpdateHandler(pTimerHandler);
		// sceneManager.loadMenuResources();
		// sceneManager.createMenuScene();
		// sceneManager.setCurrentScene(AllScenes.MENU);
		// }
		// }));
		mEngine.registerUpdateHandler(new IUpdateHandler() {

			@Override
			public void reset() {
			}

			@Override
			public void onUpdate(float pSecondsElapsed) {
				if(blendzior!=null){
					blendzior.LoopForOponents();
					if(SquirtleMoveable.collidesWith(sPlayer))
						blendzior.CollisionDetector(sPlayer);
				}
//				if(SquirtleMoveable.getX()==600 || SquirtleMoveable.getX()==601 || SquirtleMoveable.getX()==602)
//					BulbyLeft=false;
//				if(SquirtleMoveable.getX()==400 || SquirtleMoveable.getX()==401 || SquirtleMoveable.getX()==402)
//					BulbyLeft=!false;
//				
//				if(!BulbyLeft){
//					SquirtleMoveable.setX(SquirtleMoveable.getX()+1*SpeedForSquirtle);
//					SquirtleMoveable.setFlippedHorizontal(true);
//				}
//				else {
//					SquirtleMoveable.setX(SquirtleMoveable.getX()-1*SpeedForSquirtle);
//					SquirtleMoveable.setFlippedHorizontal(false);
//					if(SquirtleMoveable.getX()-1*SpeedForSquirtle == 200 || SquirtleMoveable.getX()-1*SpeedForSquirtle == 201 || SquirtleMoveable.getX()-1*SpeedForSquirtle == 202)
//						BulbyLeft = false;
//				}
//				
				final VertexBufferObjectManager vertexBufferObjectManager = getVertexBufferObjectManager();
//				if (sPlayer.getX() > CAMERA_WIDTH - 15) {
//					
//					if(passed){
//					scene.detachChild(leftText);
//					leftText = new Text(100, 170, mFont, "WYGRAŁEŚ WOLNOŚĆ!",
//							new TextOptions(HorizontalAlign.LEFT),
//							vertexBufferObjectManager);
//					scene.attachChild(leftText);
//					}passed = false;
//				}
//				if (sPlayer.collidesWith(SquirtleMoveable)) {
//					//if(PlayerHasDroppedOn(sPlayer, SquirtleMoveable)){
//					if((float)(sPlayer.getY()+sPlayer.getHeight()-SquirtleMoveable.getHeight()/4) <=
//							SquirtleMoveable.getY()){
//						
//						if(SpeedForSquirtle==1){
//						final float dx=SquirtleMoveable.getX();
//						final float dy=SquirtleMoveable.getY()+15f;
//						
//					//	SquirtleMoveable.setRotation(SquirtleMoveable.getRotation()-90);
//						scene.detachChild(SquirtleMoveable);
//						SquirtleMoveable = new AnimatedSprite(dx,dy,
//								TiledHiddenSquirtleRegion,
//								mEngine.getVertexBufferObjectManager());
//						SquirtleMoveable.animate(240);
//						scene.attachChild(SquirtleMoveable);
//						SpeedForSquirtle*=3;
//						}
//						++touches_from_top;
//					}
//					else
//						scene.setBackground(new Background(Color.PINK));
//					if(touches_from_top>=2){
//						
//						SpeedForSquirtle=6;
//						final float dx=SquirtleMoveable.getX();
//						final float dy=SquirtleMoveable.getY();
//						
//						
//						if(dy<0 || dx<-100 || dx>1000)
//						scene.detachChild(SquirtleMoveable);
//						
//						}
//					if (moveLeft)
//						body.setLinearVelocity(-5f, -5f);
//					else
//						body.setLinearVelocity(5f, -5f);
//					scene.setBackground(new Background(Color.YELLOW));
//					String alert = "ZOSTAŁEŚ ZABITY\n"
//							+ " PRZEZ GROŹNEGO DRAPIEŻCĘ\nBulbasaura";
//
//					scene.detachChild(leftText);
//					leftText = new Text(100, 170, mFont, alert,
//							new TextOptions(HorizontalAlign.LEFT),
//							vertexBufferObjectManager);
//					scene.attachChild(leftText);
					
					
//				} else {
//					scene.setBackground(new Background(Color.RED));
//
//				}
				
				for(int i = 1; i < Coins.getChildCount(); i++)
					if(sPlayer.collidesWith((Sprite)Coins.getChildByIndex(i))){
						Coins.detachChild(Coins.getChildByIndex(i));
						scene.detachChild(leftText);
						leftText = new Text(sPlayer.getX(), sPlayer.getY()-50, mFont, "masz "
						+(ALL_COINS_NUMBER-Coins.getChildCount())+" $",
								new TextOptions(HorizontalAlign.LEFT),
								vertexBufferObjectManager);
						scene.attachChild(leftText);
						break;
				}

				if(Coins.getChildCount()==1){
					scene.detachChild(leftText);
					leftText = new Text(sPlayer.getX()+50, sPlayer.getY()-80, mFont, "Hajs sie zgadza",
							new TextOptions(HorizontalAlign.LEFT),
							vertexBufferObjectManager);
					scene.attachChild(leftText);
				}
//				if(rotateEnabled)
//					sPlayer.setRotation(sPlayer.getRotation()-3f);
//				float ax = body.getLinearVelocity().x;
//				float ay = body.getLinearVelocity().y;
//				if(isOnGround )
//				if(euclides(ax,ay) < 1f)
//					body.setLinearVelocity(ax/2, ay/2);
//				//flaga na bycie w powietrzu lub nie
//				for(int i =0; i < GROUNDS.getChildCount();i++){
//				if(!sPlayer.collidesWith((Rectangle)GROUNDS.getChildByIndex(i) ))
//					isOnGround=false;
				//}
				
				
			}

			
		});
		pOnPopulateSceneCallback.onPopulateSceneFinished();
	}
	
	protected boolean PlayerHasDroppedOn(AnimatedSprite sPlayer2,
			AnimatedSprite SquirtleMoveable2) {
		final float dY = sPlayer2.getY()+sPlayer.getHeight()/2 - SquirtleMoveable2.getY()+SquirtleMoveable2.getY()/2;
		final float dX = sPlayer2.getX()+sPlayer.getWidth()/2 - SquirtleMoveable2.getX()+SquirtleMoveable2.getWidth()/2;
		final float Norm =euclides(dY,dX); 
		//float nX = dX/Norm;
		if(dY<0)
			return true;
		else
			return false;
	}

	private float euclides(float ax, float ay) {
		return (float)Math.sqrt(ax*ax+ay*ay);
	}
	private void addCoin(float dx, float dy) {
		Sprite geld = new Sprite(dx, dy, coinTextureRegion,
				getVertexBufferObjectManager());
		Coins.attachChild(geld);
		

		Coins.setChildrenVisible(true);
	}

	private void createBlock(float left, float top, float width, float height,
			FixtureDef fix) {
		Rectangle ground = new Rectangle(left, top, width, height,
				this.mEngine.getVertexBufferObjectManager());
		ground.setColor(new Color(15, 51, 5));
		PhysicsFactory.createBoxBody(physicsWorld, ground, BodyType.StaticBody,
				fix);
		scene.attachChild(ground);
		//GROUNDS.attachChild(ground);
		int j = 0;
		// for(int j=0;j<height/16;j++)
		for (int i = 0; i < width / 16; i++) {
			Sprite s = new Sprite(left + i * 16, top + j * 16,
					pavementTextureRegion,
					this.mEngine.getVertexBufferObjectManager());
			scene.attachChild(s);
		}

		// scene.attachChild(floorScene);

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

//		for (int i = 0; i < 6 * CAMERA_WIDTH / 64; i++){
// 	createBlock(-64f + i * 64f, CAMERA_HEIGHT - 32f - 16f * i, 64f,
//					16f, WALL_FIX);
//			if(i%3==0)
//			addCoin(-64f+32 + i * 64f, CAMERA_HEIGHT - 32 - 32f - 16f * i);		
//		}
		for(int i = 0; i <  6 * CAMERA_WIDTH / 64; i++)
		addCoin(-64f+32 + i * 64f, CAMERA_HEIGHT - 32 - 32f);
		
		ALL_COINS_NUMBER = Coins.getChildCount();
		//scene.attachChild(GROUNDS);
	}
	
	private void createWallsDeprecated() {
		FixtureDef WALL_FIX = PhysicsFactory.createFixtureDef(0.0f, 0.0f, 0.0f);

		// fixture definition for each of the wall:
		// ground Rectangle
		createBlock(0f, CAMERA_HEIGHT - 15, CAMERA_WIDTH, 15, WALL_FIX);
		createBlock(0f, 0f, 15, CAMERA_HEIGHT, WALL_FIX);
		createBlock(CAMERA_WIDTH - 15, 0f, 15, CAMERA_HEIGHT / 3, WALL_FIX);
		createBlock(CAMERA_WIDTH - 15, 2 * CAMERA_HEIGHT / 3, 15,
				CAMERA_HEIGHT / 3, WALL_FIX);

		createBlock(0f, 0f, CAMERA_WIDTH, 15, WALL_FIX);
		createBlock(CAMERA_WIDTH / 8, CAMERA_HEIGHT - 60, CAMERA_WIDTH / 8, 15,
				WALL_FIX);
	}

	private void createPlatform() {
		FixtureDef FLOOR_FIX = PhysicsFactory.createFixtureDef(
				generator.nextFloat(), generator.nextFloat(),
				generator.nextFloat());
		float dx = 64 + 10 + 64 * indexForPlatforms;
		float dy = 30 - CAMERA_HEIGHT;
		createBlock(dx, dy, 64, 16, FLOOR_FIX);
		TextureRegion region = (generator.nextBoolean()) ? grassTextureRegion
				: dojoTextureRegion;
		for (int i = 0; i < 4; i++) {
			Sprite s = new Sprite(dx + i * 16, dy, region,
					this.mEngine.getVertexBufferObjectManager());
			scene.attachChild(s);
		}
		++indexForPlatforms;

	}

	private void createRandomPlatform() {
		FixtureDef FLOOR_FIX = PhysicsFactory.createFixtureDef(
				generator.nextFloat(), generator.nextFloat(),
				generator.nextFloat());

		int dx = generator.nextInt(CAMERA_WIDTH);
		int dy = generator.nextInt(CAMERA_HEIGHT);
		if (dy + 30 > CAMERA_HEIGHT)
			dy -= 30;
		if (dx + CAMERA_WIDTH / 8 > CAMERA_WIDTH)
			dx -= CAMERA_WIDTH / 8;
		createBlock(dx, dy, 64, 16, FLOOR_FIX);
		TextureRegion region = (generator.nextBoolean()) ? grassTextureRegion
				: dojoTextureRegion;
		for (int i = 0; i < 4; i++) {
			Sprite s = new Sprite(dx + i * 16, dy, region,
					this.mEngine.getVertexBufferObjectManager());
			scene.attachChild(s);
		}

	}

	@Override
	public boolean onSceneTouchEvent(Scene pScene, TouchEvent pSceneTouchEvent) {
		// TODO Auto-generated method stub
		if (pSceneTouchEvent.isActionDown()) {
			float dy = pSceneTouchEvent.getY() - sPlayer.getY();
			float dx = pSceneTouchEvent.getX() - sPlayer.getX();
			
			if(isOnGround){
				Vector2 velocity;
				float euclides = (float) Math.sqrt(dy * dy + dx * dx);
				float Len = 5.0f;
				if (dy != 0)
					velocity = Vector2Pool.obtain(Len * dx / euclides, Len * dy
							/ euclides);
				else
					velocity = Vector2Pool.obtain(Len * dx / euclides, 0f);
				// velocity = Vector2Pool.obtain(0f,-5f);
				// body.setAngularVelocity(dx);
				body.setLinearVelocity(velocity);
				Vector2Pool.recycle(velocity);
			}
			if (dx > 0) {
				sPlayer.setFlippedHorizontal(true);
				moveLeft = true;
			} else {
				sPlayer.setFlippedHorizontal(false);
				moveLeft = false;
			}
			return true;
		}
		return false;
	}
	public VertexBufferObjectManager vbo() {
		// TODO Auto-generated method stub
		return this.getVertexBufferObjectManager();
	}

}