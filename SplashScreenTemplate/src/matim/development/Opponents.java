package matim.development;

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
	public static final int MAX_NUMBER_OF_OPPONENTS = 5;
	public int getIndex() { return index;}
	
	private GameActivity activity;
	
	private boolean SquirtleLeft=false;
	private int left,right;
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
		activity.SquirtleMoveable = new AnimatedSprite(left, GameActivity.CAMERA_HEIGHT -56f,
				activity.TiledSquirtleRegion,
				activity.vbo());
		activity.SquirtleMoveable.animate(240);
		activity.scene.attachChild(activity.SquirtleMoveable);
	}
	
	public void LoopForOponents() {
		if(activity.SquirtleMoveable.getX()==right || activity.SquirtleMoveable.getX()==right+1
				|| activity.SquirtleMoveable.getX()==right+2)
			SquirtleLeft=false;
		if(activity.SquirtleMoveable.getX()==left || activity.SquirtleMoveable.getX()==left+1 
				|| activity.SquirtleMoveable.getX()==left+2)
			SquirtleLeft=!false;
		
		if(!SquirtleLeft){
			activity.SquirtleMoveable.setX(activity.SquirtleMoveable.getX()+1*SpeedForSquirtle);
			activity.SquirtleMoveable.setFlippedHorizontal(true);
		}
		else {
			activity.SquirtleMoveable.setX(activity.SquirtleMoveable.getX()-1*SpeedForSquirtle);
			activity.SquirtleMoveable.setFlippedHorizontal(false);
			if(activity.SquirtleMoveable.getX()-1*SpeedForSquirtle == right-left
					|| activity.SquirtleMoveable.getX()-1*SpeedForSquirtle == right-left+1 
					|| activity.SquirtleMoveable.getX()-1*SpeedForSquirtle == right-left+2)
				SquirtleLeft = false;
		}
	}
	public void CollisionDetector(Sprite sPlayer){}
//TODO: zrobić detekcje, odpalić dla klasy opponents, ma działać!!!
	public void CollisionDetector(AnimatedSprite sPlayer){
		if((float)(sPlayer.getY()+sPlayer.getHeight()-activity.SquirtleMoveable.getHeight()/4) <=
				activity.SquirtleMoveable.getY()){
			
			if(SpeedForSquirtle==1){
			final float dx=activity.SquirtleMoveable.getX();
			final float dy=activity.SquirtleMoveable.getY()+15f;
			
		//	activity.SquirtleMoveable.setRotation(activity.SquirtleMoveable.getRotation()-90);
			activity.scene.detachChild(activity.SquirtleMoveable);
			activity.SquirtleMoveable = new AnimatedSprite(dx,dy,
					activity.TiledHiddenSquirtleRegion,activity.vbo());
			activity.SquirtleMoveable.animate(240);
			activity.scene.attachChild(activity.SquirtleMoveable);
			SpeedForSquirtle*=3;
			}
			++touches_from_top;
		}
		else
			activity.scene.setBackground(new Background(Color.PINK));
		if(touches_from_top>=2){
			
			SpeedForSquirtle=6;
			final float dx=activity.SquirtleMoveable.getX();
			final float dy=activity.SquirtleMoveable.getY();
			
			
			if(dy<0 || dx<-1000 || dx>1000)
				activity.scene.detachChild(activity.SquirtleMoveable);
			
			}
		//if (moveLeft)
			//TODO:zrobić klasę Character, aby miałą pola publiczne body, AnimatedSprite, prsoty dostep itd...
			//body.setLinearVelocity(-5f, -5f);
	//	else
		//	body.setLinearVelocity(5f, -5f);
		activity.scene.setBackground(new Background(Color.BLUE));
	} 
	
	
}
