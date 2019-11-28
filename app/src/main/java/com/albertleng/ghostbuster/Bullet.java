package com.albertleng.ghostbuster;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Rect;

import static com.albertleng.ghostbuster.GameView.screenRatioX;
import static com.albertleng.ghostbuster.GameView.screenRatioY;

public class Bullet {

    int x, y, width, height;
    Bitmap bullet;

    Bullet(Resources res) {
        bullet = BitmapFactory.decodeResource(res, R.drawable.bullet);

        width = bullet.getWidth();
        height = bullet.getHeight();

        width /= 8;
        height /= 8;

        width *= (int) screenRatioX;
        height *= (int) screenRatioY;

        bullet = Bitmap.createScaledBitmap(bullet, width, height, false);

    }

    Rect getCollisionShape() {
        return new Rect(x, y, x + width, y + height);
    }


}
