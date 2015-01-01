package matim.development;

import org.andengine.entity.scene.background.Background;
import org.andengine.entity.sprite.AnimatedSprite;
import org.andengine.entity.sprite.Sprite;
import org.andengine.entity.text.Text;
import org.andengine.entity.text.TextOptions;
import org.andengine.extension.physics.box2d.PhysicsConnector;
import org.andengine.extension.physics.box2d.PhysicsFactory;
import org.andengine.opengl.texture.TextureOptions;
import org.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlas;
import org.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlasTextureRegionFactory;
import org.andengine.util.HorizontalAlign;
import org.andengine.util.color.Color;

import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;

public class Clefairy {
	private static GameActivity activity;
	private static Clefairy instance = null;
	private int posX,posY;
	protected Clefairy(GameActivity gm,int dx,int dy){
		activity = gm;
		posX=dx;
		posY=dy;
	}
	public static Clefairy CreateClefairy(GameActivity activity, int dx,int dy) {
		if(instance == null){
			instance = new Clefairy(activity,dx,dy);
		}
		return instance;
	}
	
	public void onLoad() {
		BitmapTextureAtlasTextureRegionFactory.setAssetBasePath("gfx/");
		// width and height power of 2^x
		activity.clefairyTexture = new BitmapTextureAtlas(activity.getTextureManager(), 512, 512,
				TextureOptions.BILINEAR);

		activity.TiledClefairyRegion = BitmapTextureAtlasTextureRegionFactory
				.createTiledFromAsset(activity.clefairyTexture, activity,
						"clefairy_fullwalk.png", 0, 0, 7, 1); // 385x55
		activity.clefairyTexture.load();
	}

	private void onCreateClefairy() {
		activity.ClefairyMoveable = new AnimatedSprite(GameActivity.CAMERA_WIDTH / 3, GameActivity.CAMERA_HEIGHT / 2,
				activity.TiledClefairyRegion,
				activity.vbo());
		activity.ClefairyMoveable.animate(180);
		activity.ClefairyMoveable.setRotation(-3.0f);
		final FixtureDef PLAYER_FIX = PhysicsFactory.createFixtureDef(10.0f,
				0.5f, 190.0f);
//		body = PhysicsFactory.createBoxBody(physicsWorld, sPlayer, BodyType.DynamicBody, PLAYER_FIX);
		activity.ClefairyBody = PhysicsFactory.createCircleBody(activity.physicsWorld, 
				activity.ClefairyMoveable, BodyType.DynamicBody, PLAYER_FIX);
		activity.scene.attachChild(activity.ClefairyMoveable);
		activity.physicsWorld.registerPhysicsConnector(new PhysicsConnector(activity.ClefairyMoveable,
				activity.ClefairyBody, true, false));
		activity.mCamera.setChaseEntity(activity.ClefairyMoveable);
	}

	public void LoopForClefairy(){
		for(int i = 1; i < activity.Coins.getChildCount(); i++)
			if(activity.ClefairyMoveable.collidesWith((Sprite)activity.Coins.getChildByIndex(i))){
				activity.Coins.detachChild(activity.Coins.getChildByIndex(i));
				activity.scene.detachChild(activity.leftText);
				activity.leftText = new Text(activity.ClefairyMoveable.getX(),
						activity.ClefairyMoveable.getY()-50, activity.mFont, "masz "
				+(activity.ALL_COINS_NUMBER-activity.Coins.getChildCount())+" $",
						new TextOptions(HorizontalAlign.LEFT),activity.vbo());
				activity.scene.attachChild(activity.leftText);
				break;
		}

		if(activity.Coins.getChildCount()==1){
			activity.scene.detachChild(activity.leftText);
			activity.leftText = new Text(activity.ClefairyMoveable.getX()+50, 
					activity.ClefairyMoveable.getY()-80, activity.mFont, "Hajs sie zgadza",
					new TextOptions(HorizontalAlign.LEFT),
					activity.vbo());
			activity.scene.attachChild(activity.leftText);
		}
	}

	public void CollisionDetector(Sprite sPlayer) {
	}

	public void CollisionDetector(AnimatedSprite opponent) {
		activity.scene.setBackground(new Background(Color.BLUE));
	}

}
