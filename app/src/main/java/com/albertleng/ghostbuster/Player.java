package com.albertleng.ghostbuster;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Rect;

import static com.albertleng.ghostbuster.GameView.screenRatioX;
import static com.albertleng.ghostbuster.GameView.screenRatioY;

public class Player {
    public boolean isGoingUp = false;
    public int toShoot = 0;
    public boolean isGoingDown = false;
    int x, y, width, height, wingCounter = 0, shootCounter = 1;
    Bitmap player, shoot, dead;
    private GameView gameView;

    Player (GameView gameView, int screenY, Resources res ) {
        this.gameView = gameView;

        player   = BitmapFactory.decodeResource(res, R.drawable.human_walk);

        width = player.getWidth();
        height = player.getHeight();


        width *= (int) screenRatioX;
        height *= (int) screenRatioY;

        player = Bitmap.createScaledBitmap(player, (int)(width*0.9), (int)(height*0.9), false);

        //To be replaced  with better picture
        shoot = BitmapFactory.decodeResource(res, R.drawable.human_shoot);
        shoot = Bitmap.createScaledBitmap(shoot, width, height, false);

        dead = BitmapFactory.decodeResource(res, R.drawable.human_drop);
        dead = Bitmap.createScaledBitmap(dead, (int)(width*0.9), (int)(height*0.9), false);

        y = screenY / 2;
        x = (int)(64 * screenRatioX);
    }

    // Return player based on player's action
    Bitmap getPlayer() {

        if (toShoot != 0) {
            gameView.newBullet();
            toShoot--;
            return shoot;

        }

        return player;
    }

    Rect getCollisionShape() {
        return new Rect(x, y, x + width, y + height);
    }

    Bitmap getDead() {
        return dead;
    }
}
