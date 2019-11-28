package com.albertleng.ghostbuster;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Rect;
import android.util.Log;

import java.util.Random;

import static com.albertleng.ghostbuster.GameView.screenRatioX;
import static com.albertleng.ghostbuster.GameView.screenRatioY;

//TODO: https://icons8.com/icons/set/ghost#
//TODO: https://www.fesliyanstudios.com/royalty-free-music/downloads-c/scary-horror-music/8 is it coming?

public class Bird {
    public int speed = 5;//20;
    public boolean wasShot = true;
    int x, y, width, height, birdCounter = 1;
    Bitmap bird1, bird2, bird3, bird4;

    int defHeight, defWidth;

    Bird (Resources res) {
        bird1 = BitmapFactory.decodeResource(res, R.drawable.bird1);
        bird2 = BitmapFactory.decodeResource(res, R.drawable.bird2);
        bird3 = BitmapFactory.decodeResource(res, R.drawable.bird3);
        bird4 = BitmapFactory.decodeResource(res, R.drawable.bird4);

        width = bird1.getWidth();
        height = bird1.getHeight();

        defHeight = height;
        defWidth = width;

//        width /= 6;
//        height /= 6;

        width *= (int) screenRatioX;
        height *= (int) screenRatioY;

        bird1 = Bitmap.createScaledBitmap(bird1, width, height, false);
        bird2 = Bitmap.createScaledBitmap(bird2, width, height, false);
        bird3 = Bitmap.createScaledBitmap(bird3, width, height, false);
        bird4 = Bitmap.createScaledBitmap(bird4, width, height, false);

        y = -height;
    }

    Bitmap getBird() {

        if (birdCounter == 1) {
            birdCounter++;
            return bird1;
        }

        if (birdCounter == 2) {
            birdCounter++;
            return bird2;
        }

        if (birdCounter == 3) {
            birdCounter++;
            return bird3;
        }

        birdCounter = 1;
        return bird4;

    }

    Bitmap returnRandomSizeBird(Bitmap bird) {


        Random random = new Random();
        int sizeFactor = random.nextInt(12);
        if (sizeFactor < 6)
            sizeFactor = 6;

        Log.d("returnRandomSize", "sizeFactor:" + sizeFactor);

        int localWidth, localHeight;

        localWidth = defWidth/sizeFactor;
        localHeight = defHeight/sizeFactor;

        localWidth *= (int) screenRatioX;
        localHeight *= (int) screenRatioY;

        Log.d("returnRandomSize", "width:" + localWidth);
        Log.d("returnRandomSize", "height:" + localHeight);


        Bitmap localBird = Bitmap.createScaledBitmap(bird, localWidth, localHeight, false);

        return localBird;
    }

    Rect getCollisionShape() {
        return new Rect(x, y, x + width, y + height);
    }
}
