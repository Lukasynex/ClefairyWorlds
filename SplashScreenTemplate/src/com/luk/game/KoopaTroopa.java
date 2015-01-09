package com.luk.game;

import org.andengine.engine.handler.timer.ITimerCallback;
import org.andengine.engine.handler.timer.TimerHandler;
import org.andengine.entity.scene.background.Background;
import org.andengine.entity.sprite.AnimatedSprite;
import org.andengine.entity.sprite.TiledSprite;
import org.andengine.opengl.texture.TextureOptions;
import org.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlas;
import org.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlasTextureRegionFactory;
import org.andengine.opengl.texture.region.ITiledTextureRegion;
import org.andengine.opengl.texture.region.TextureRegion;
import org.andengine.opengl.texture.region.TiledTextureRegion;
import org.andengine.util.color.Color;
import org.andengine.util.debug.Debug;

import android.util.Log;


public class KoopaTroopa {
	public static int INDEX_FOR_OPPONENT=0;
	private final int index;
	public static final int MAX_NUMBER_OF_OPPONENTS = 15;
	public int getIndex() { return index;}
	public boolean Dead = false;
	private GameActivity activity;
	public TimerHandler spriteTimerHandler=null;
	private boolean SquirtleLeft=false;
	private final int left;
	private final int right;
	private int SpeedForSquirtle = 1;
	public int number_of_collisions;
	public int getCollisionsNumber(){
		return number_of_collisions;
	}
	
	public KoopaTroopa(GameActivity activity, int left_pos, int right_pos){
	index = INDEX_FOR_OPPONENT;
	this.activity = activity;
	left = left_pos;
	right = right_pos;
	number_of_collisions=0;
	++INDEX_FOR_OPPONENT;
	}
	public void onLoad(){
			BitmapTextureAtlasTextureRegionFactory.setAssetBasePath("gfx/");
			// width and height power of 2^x
			activity.squirtleTexture = new BitmapTextureAtlas(activity.getTextureManager(), 256, 256,
					TextureOptions.BILINEAR);

			activity.TiledSquirtleRegion = BitmapTextureAtlasTextureRegionFactory
					.createTiledFromAsset(activity.squirtleTexture, activity,
							"squirtle_walk.png", 0, 0, 6, 1); // 230x37
			activity.TiledBitingSquirtleRegion = BitmapTextureAtlasTextureRegionFactory
					.createTiledFromAsset(activity.squirtleTexture, activity,
							"squirtle_walk_bite.png", 0, 37, 6, 1); // 385x55
			activity.TiledHiddenSquirtleRegion = BitmapTextureAtlasTextureRegionFactory
					.createTiledFromAsset(activity.squirtleTexture, activity,
							"squirtle_hidden.png", 0, 2*37, 8, 1); //230x37
			activity.deadWalkingSquirtleFall = BitmapTextureAtlasTextureRegionFactory
					.createTiledFromAsset(activity.squirtleTexture, activity,
							"squirtle_death.png", 0, 3*37,2,1); //230x37
			
			activity.deadSquirtleShellCrash = BitmapTextureAtlasTextureRegionFactory
					.createTiledFromAsset(activity.squirtleTexture, activity,
							"squirtle_death_shell.png", 0, 4*37,2,1); //230x37
			
			
			
			activity.squirtleTexture.load();
	}	
	public void onCreate(){
		activity.SquirtleMoveable[index] = new AnimatedSprite((left+right)/2, GameActivity.CAMERA_HEIGHT -56f,
				activity.TiledSquirtleRegion,
				activity.vbo());
		//activity.SquirtleMoveable[index].setFlippedHorizontal(false);
		((AnimatedSprite) activity.SquirtleMoveable[index]).animate(240);
		activity.scene.attachChild(activity.SquirtleMoveable[index]);
	}
	
	public void LoopForOponents(boolean walking_normally) {
		//booleans checking if Squirtle not crosses area
		if(walking_normally){
		if(activity.SquirtleMoveable[index].getX()==right || activity.SquirtleMoveable[index].getX()==right+1
				|| activity.SquirtleMoveable[index].getX()==right+2)
			SquirtleLeft=true;
		if(activity.SquirtleMoveable[index].getX()==left || activity.SquirtleMoveable[index].getX()==left+1 
				|| activity.SquirtleMoveable[index].getX()==left+2)
			SquirtleLeft=false;
		}
		//left/right move
		if(SquirtleLeft){
			activity.SquirtleMoveable[index].setX(activity.SquirtleMoveable[index].getX()-1*SpeedForSquirtle);
			activity.SquirtleMoveable[index].setFlippedHorizontal(false);
		}
		else {
			activity.SquirtleMoveable[index].setX(activity.SquirtleMoveable[index].getX()+1*SpeedForSquirtle);
			activity.SquirtleMoveable[index].setFlippedHorizontal(true);
		}
	}
	//put after collides with another opponent 
	public void SwitchWalkDirection(){
		
		SquirtleLeft = (SquirtleLeft==true) ? false : true;
		
		if(SquirtleLeft){
			activity.SquirtleMoveable[index].setX(activity.SquirtleMoveable[index].getX()-3*SpeedForSquirtle);
			activity.SquirtleMoveable[index].setFlippedHorizontal(true);
		}
		else{
			activity.SquirtleMoveable[index].setX(activity.SquirtleMoveable[index].getX()+3*SpeedForSquirtle);
			activity.SquirtleMoveable[index].setFlippedHorizontal(false);
		}
			
	}
	/**
	 * śmierć Squirtle'a	
	 */
	public void performDeath() {
		if(number_of_collisions==0){
			setAnimation(activity.deadWalkingSquirtleFall);
		}
		else{
			setAnimation(activity.deadSquirtleShellCrash);
		}
		Dead = true;
		TimerHandler spriteTimerHandler;
        float mEffectSpawnDelay = 1.7f;
        spriteTimerHandler = new TimerHandler(mEffectSpawnDelay,true,new ITimerCallback(){
	        @Override
	        public void onTimePassed(TimerHandler pTimerHandler) {
	        	activity.SquirtleMoveable[index].setX(0); 
	        	activity.SquirtleMoveable[index].setY(-1000);
	        	activity.scene.detachChild(activity.SquirtleMoveable[index]);
	        }
        });
        activity.getEngine().registerUpdateHandler(spriteTimerHandler);
	}
	
	
	public void setSprite(ITiledTextureRegion region){
		final float dx = activity.SquirtleMoveable[index].getX();
		final float dy = activity.SquirtleMoveable[index].getY();

		activity.scene.detachChild(activity.SquirtleMoveable[index]);
		activity.SquirtleMoveable[index] = new TiledSprite(dx,dy,
				region,
				activity.vbo());
		activity.scene.attachChild(activity.SquirtleMoveable[index]);
	}

	public void setAnimation(TiledTextureRegion region, float dx, float dy){
		activity.scene.detachChild(activity.SquirtleMoveable[index]);
		activity.SquirtleMoveable[index] = new AnimatedSprite(dx,dy,
				region,
				activity.vbo());
		((AnimatedSprite) activity.SquirtleMoveable[index]).animate(240);
		activity.scene.attachChild(activity.SquirtleMoveable[index]);
	}
	public void setAnimation(TiledTextureRegion region){
		final float dx = activity.SquirtleMoveable[index].getX();
		final float dy = activity.SquirtleMoveable[index].getY();
		setAnimation(region, dx, dy);
	}

	public void ClefairyCollision() {
		//only from top
		if((float)(activity.ClefairyMoveable.getY()
				+activity.ClefairyMoveable.getHeight()
				-activity.SquirtleMoveable[index].getHeight()/4) <=
				activity.SquirtleMoveable[index].getY()){
//
			++number_of_collisions;	
			if(number_of_collisions==1){
				setAnimation(activity.TiledHiddenSquirtleRegion, 
					activity.SquirtleMoveable[index].getX(),
					activity.SquirtleMoveable[index].getY()+15);
				SpeedForSquirtle*=3;
			}
			else if(number_of_collisions==2){
				SpeedForSquirtle=6;
			}
			
			else if(number_of_collisions>2){
				performDeath();
				activity.scene.setBackground(new Background(Color.BLUE));
			}
		}
	}

	public void performBitingAction() {
		if(!Dead && number_of_collisions==0){
			setAnimation(activity.TiledBitingSquirtleRegion);
			if(spriteTimerHandler!= null)
				activity.getEngine().unregisterUpdateHandler(spriteTimerHandler);
	        float mEffectSpawnDelay = 1.2f;
	        spriteTimerHandler = new TimerHandler(mEffectSpawnDelay,true,new ITimerCallback(){
		        @Override
		        public void onTimePassed(TimerHandler pTimerHandler) {
		        	if(number_of_collisions==0)
		        		setAnimation(activity.TiledSquirtleRegion);
		        	else
		        		setAnimation(activity.TiledHiddenSquirtleRegion);
		        }
	        });
        	activity.getEngine().registerUpdateHandler(spriteTimerHandler);
		}
		
	}
}
