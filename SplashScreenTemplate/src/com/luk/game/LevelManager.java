package com.luk.game;

import org.andengine.entity.primitive.Rectangle;
import org.andengine.entity.sprite.Sprite;
import org.andengine.extension.physics.box2d.PhysicsFactory;
import org.andengine.util.color.Color;

import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;

public class LevelManager {
	private int current_level;
	private final GameActivity activity;
	private static enum ITEM_TYPE {
		BLANK, STONE, GRASS, TREE, PCENTER, PSHOP, CHECKPOINT, SPRING, PLATFORM
	};
	//level schemas
	private static final int MAX_WIDTH = 6 * GameActivity.CAMERA_WIDTH;
	private static final int MAX_HEIGHT = GameActivity.CAMERA_HEIGHT;
	private static final int TILE_SIZE = 50;
	private final ITEM_TYPE level0[][] = new ITEM_TYPE[MAX_WIDTH/TILE_SIZE][MAX_HEIGHT/TILE_SIZE];
	//TODO:wrapper leveli jako tablice enumów na tworzenie blocków
	private void populateLevel0(){
		for(int i = 0; i < MAX_WIDTH/TILE_SIZE; i++){
			for(int j = 0; j < MAX_HEIGHT/TILE_SIZE; j++){
				level0[i][j] = ITEM_TYPE.BLANK;
				if(j==0 || i==0 || i==MAX_HEIGHT/TILE_SIZE - 1 || i==MAX_HEIGHT/TILE_SIZE - 1){
					level0[i][j] = ITEM_TYPE.STONE;
				}
			}
			
		}
		
	}
	
	public LevelManager(GameActivity gm, int level){
		current_level = level;
		activity = gm;
	}
	public void setLevel(int id){ current_level = id; }
	public void LoadLevel(){
		LoadLevel(current_level);
	}
	private void LoadLevel(int c) {
		switch(c){
		case 1:
			createLevel1();
			break;
		case 2:
			createLevel2();
			break;
		default:
			createLevel0();
			break;
		}
	}
	private void createBlock(float left, float top, float width, float height,
			FixtureDef fix) {
		Rectangle ground = new Rectangle(left, top, width, height,
				activity.vbo());
		ground.setColor(new Color(15, 51, 5));
		PhysicsFactory.createBoxBody(activity.physicsWorld, ground, BodyType.StaticBody,
				fix);
		activity.scene.attachChild(ground);
		//GROUNDS.attachChild(ground);
		int j = 0;
		// for(int j=0;j<height/16;j++)
		for (int i = 0; i < width / 16; i++) {
			Sprite s = new Sprite(left + i * 16, top + j * 16,
					activity.pavementTextureRegion,
					activity.vbo());
			activity.scene.attachChild(s);
		}
	}
	private void addCoin(float dx, float dy) {
		Sprite geld = new Sprite(dx, dy, activity.coinTextureRegion,
				activity.vbo());
		activity.Coins.attachChild(geld);
		activity.Coins.setChildrenVisible(true);
	}
	private void createLevel0() {
		FixtureDef WALL_FIX = PhysicsFactory.createFixtureDef(0.0f, 0.0f, 9000.0f);
		// floor
		createBlock(-16.0f, GameActivity.CAMERA_HEIGHT - 16, 6 * GameActivity.CAMERA_WIDTH, 16, WALL_FIX);
		// left wall
		createBlock(-16.0f, -16f, 16, GameActivity.CAMERA_HEIGHT + 16, WALL_FIX);
		// right wall
		createBlock(6 *GameActivity.CAMERA_WIDTH - 16.0f, -16f, 16, GameActivity.CAMERA_HEIGHT + 16,
				WALL_FIX);

		for(int i = 0; i <  6 * GameActivity.CAMERA_WIDTH / 64; i++)
			addCoin(-64f+32 + i * 64f, GameActivity.CAMERA_HEIGHT - 32 - 32f);
		
		activity.ALL_COINS_NUMBER = activity.Coins.getChildCount();
		
		//populating blendziors
		
		//creating final goal (e.g. flag) to complete level
		

	}
	private void createLevel1() {
		//creating static platforms
		
		//creating opponents
		
		//creating final goal (e.g. flag)
		
	}
	private void createLevel2() {

	}
}
