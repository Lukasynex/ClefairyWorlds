package com.luk.game;

import org.andengine.entity.scene.background.Background;
import org.andengine.entity.sprite.AnimatedSprite;
import org.andengine.entity.sprite.Sprite;

import org.andengine.opengl.texture.TextureOptions;
import org.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlas;
import org.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlasTextureRegionFactory;
import org.andengine.ui.activity.BaseGameActivity;
import org.andengine.util.color.Color;

import android.content.Context;

public class Opponents {
	public static int INDEX_FOR_OPPONENT=0;
	private final int index;
	public static final int MAX_NUMBER_OF_OPPONENTS = 15;
	public int getIndex() { return index;}
	
	private GameActivity activity;
	
	private boolean SquirtleLeft=false;
	private final int left;
	private final int right;
	private int SpeedForSquirtle = 1;
	private int touches_from_top=0;
	
	public Opponents(GameActivity activity, int left_pos, int right_pos){
	index = INDEX_FOR_OPPONENT;
	this.activity = activity;
	left = left_pos;
	right = right_pos;
	++INDEX_FOR_OPPONENT;
	}
	public void onLoad(){
			BitmapTextureAtlasTextureRegionFactory.setAssetBasePath("gfx/");
			// width and height power of 2^x
			activity.squirtleTexture = new BitmapTextureAtlas(activity.getTextureManager(), 256, 256,
					TextureOptions.BILINEAR);

			activity.TiledSquirtleRegion = BitmapTextureAtlasTextureRegionFactory
					.createTiledFromAsset(activity.squirtleTexture, activity,
							"squirtle_walk.png", 0, 0, 6, 1); // 385x55
			activity.TiledHiddenSquirtleRegion = BitmapTextureAtlasTextureRegionFactory
					.createTiledFromAsset(activity.squirtleTexture, activity,
							"squirtle_hidden.png", 0, 256-30, 8, 1); // 385x55
			activity.squirtleTexture.load();
	}	
	public void onCreate(){
		activity.SquirtleMoveable[index] = new AnimatedSprite((left+right)/2, GameActivity.CAMERA_HEIGHT -56f,
				activity.TiledSquirtleRegion,
				activity.vbo());
		//activity.SquirtleMoveable[index].setFlippedHorizontal(false);
		activity.SquirtleMoveable[index].animate(240);
		activity.scene.attachChild(activity.SquirtleMoveable[index]);
	}
	
	public void LoopForOponents() {
		//booleans checking if Squirtle not crosses area
		if(activity.SquirtleMoveable[index].getX()==right || activity.SquirtleMoveable[index].getX()==right+1
				|| activity.SquirtleMoveable[index].getX()==right+2)
			SquirtleLeft=true;
		if(activity.SquirtleMoveable[index].getX()==left || activity.SquirtleMoveable[index].getX()==left+1 
				|| activity.SquirtleMoveable[index].getX()==left+2)
			SquirtleLeft=false;
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
	//put after collides with:
	public void CollisionWalkDetected(){
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
//TODO: zrobić detekcje, odpalić dla klasy opponents, ma działać!!!
	public void CollisionDetector(AnimatedSprite sPlayer){
		if((float)(sPlayer.getY()+sPlayer.getHeight()-activity.SquirtleMoveable[index].getHeight()/4) <=
				activity.SquirtleMoveable[index].getY()){
			
			if(SpeedForSquirtle==1){
			final float dx=activity.SquirtleMoveable[index].getX();
			final float dy=activity.SquirtleMoveable[index].getY()+15f;
			
			activity.scene.detachChild(activity.SquirtleMoveable[index]);
			activity.SquirtleMoveable[index] = new AnimatedSprite(dx,dy,
					activity.TiledHiddenSquirtleRegion,activity.vbo());
			activity.SquirtleMoveable[index].animate(240);
			activity.scene.attachChild(activity.SquirtleMoveable[index]);
			SpeedForSquirtle*=3;
			}
			++touches_from_top;
		}
		else
			activity.scene.setBackground(new Background(Color.PINK));
		if(touches_from_top==2){
			SpeedForSquirtle=6;
		}
		if(touches_from_top>2)
			activity.scene.detachChild(activity.SquirtleMoveable[index]);
		
		//if (moveLeft)
			//TODO:zrobić klasę Character, aby miałą pola publiczne body, AnimatedSprite, prsoty dostep itd...
			//body.setLinearVelocity(-5f, -5f);
	//	else
		//	body.setLinearVelocity(5f, -5f);
		activity.scene.setBackground(new Background(Color.BLUE));
	} 
	
	
}
