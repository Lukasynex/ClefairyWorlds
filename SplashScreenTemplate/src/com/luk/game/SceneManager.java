package com.luk.game;

import org.andengine.engine.Engine;
import org.andengine.engine.camera.Camera;
import org.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlas;
import org.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlasTextureRegionFactory;
import org.andengine.opengl.texture.region.ITextureRegion;
import org.andengine.ui.activity.BaseGameActivity;
import org.andengine.entity.scene.Scene;
import org.andengine.entity.scene.background.Background;
import org.andengine.entity.sprite.Sprite;

import org.andengine.engine.handler.timer.ITimerCallback;
import org.andengine.engine.handler.timer.TimerHandler;
import org.andengine.engine.options.EngineOptions;
import org.andengine.engine.options.ScreenOrientation;
import org.andengine.engine.options.resolutionpolicy.RatioResolutionPolicy;
import org.andengine.entity.primitive.Rectangle;
import org.andengine.extension.physics.box2d.PhysicsConnector;
import org.andengine.extension.physics.box2d.PhysicsFactory;
import org.andengine.extension.physics.box2d.PhysicsWorld;

import org.andengine.util.color.Color;

import android.hardware.SensorManager;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.FixtureDef;


public class SceneManager {
	public SceneManager(BaseGameActivity activity, Engine engine, Camera camera) {
		super();
		this.activity = activity;
		this.engine = engine;
		this.camera = camera;
	}

	private AllScenes currentScene;
	BaseGameActivity activity;
	Engine engine;
	Camera camera;
	BitmapTextureAtlas splashTA;
	ITextureRegion splashTR;
	Scene splashScene, gameScene,
	 menuScene;
	enum AllScenes{
		SPLASH,
		MENU,
		GAME
	}
	public AllScenes getCurrentScene() {
		return currentScene;
	}

	public void loadSplashResources(){
		BitmapTextureAtlasTextureRegionFactory.setAssetBasePath("gfx/");
		splashTA = new BitmapTextureAtlas(this.activity.getTextureManager(),
				256, 256);
		splashTR = BitmapTextureAtlasTextureRegionFactory.createFromAsset(splashTA, this.activity,
				"splash.png", 0,0);
		splashTA.load();
		
	}
	
	public void loadGameResources(){
		//GameActivity.loadGameGfx();
	}
	
	public Scene createSplashScene(){
		splashScene = new Scene();
		splashScene.setBackground(new Background(1,1,1));
		
		Sprite icon = new Sprite(0, 0, splashTR, engine.getVertexBufferObjectManager());
		icon.setPosition((camera.getWidth()-icon.getWidth())/2, (camera.getHeight()-icon.getHeight())/2);
		splashScene.attachChild(icon);
		return splashScene;
	}
	public Scene createMenuScene(){
		menuScene = new Scene();
		menuScene.setBackground(new Background(0,0,0));
		
		Sprite icon = new Sprite(0, 0, splashTR, engine.getVertexBufferObjectManager());
		icon.setPosition((camera.getWidth()-icon.getWidth())/2, (camera.getHeight()-icon.getHeight())/2);
		menuScene.attachChild(icon);
		return menuScene;
	}
	
	public Scene createGameScene(){
//		GameActivity.scene = new Scene();
//		GameActivity.scene.setBackground(new Background(0,125,58));
//		GameActivity.physicsWorld = new PhysicsWorld(new Vector2(0,SensorManager.GRAVITY_EARTH),false);
//		GameActivity.scene.registerUpdateHandler(GameActivity.physicsWorld);
	
		return null;
	}
	public void setCurrentScene(AllScenes currentScene) {
		this.currentScene = currentScene;
		switch (currentScene) {
		case SPLASH:
			break;
		case MENU:
			engine.setScene(menuScene);
			break;
		case GAME:
			engine.setScene(gameScene);
			break;

		default:
			break;
		}
	}

	public void loadMenuResources() {
		// TODO Auto-generated method stub
		
	}

	

}
