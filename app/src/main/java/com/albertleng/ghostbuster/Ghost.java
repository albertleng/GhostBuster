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

public class Ghost {
    public int speed = 20;
    public boolean wasShot = true;
    int x = 0, y, width, height, ghostCounter = 1;
    Bitmap ghost1, ghost2, ghost3, ghost4;

    int defHeight, defWidth;

    Ghost(Resources res) {
        ghost1 = BitmapFactory.decodeResource(res, R.drawable.ghost1);
        ghost2 = BitmapFactory.decodeResource(res, R.drawable.ghost2);
        ghost3 = BitmapFactory.decodeResource(res, R.drawable.ghost3);
        ghost4 = BitmapFactory.decodeResource(res, R.drawable.ghost4);

        width = ghost1.getWidth();
        height = ghost1.getHeight();

        defHeight = height;
        defWidth = width;

        width *= (int) screenRatioX;
        height *= (int) screenRatioY;

        ghost1 = Bitmap.createScaledBitmap(ghost1, width, height, false);
        ghost2 = Bitmap.createScaledBitmap(ghost2, width, height, false);
        ghost3 = Bitmap.createScaledBitmap(ghost3, width, height, false);
        ghost4 = Bitmap.createScaledBitmap(ghost4, width, height, false);

        y = -height;
    }

    Bitmap getGhostIndex(int idx) {
        switch (idx) {
            case 0:
                return ghost1;
            case 1:
                return ghost2;
            case 2:
                return ghost3;
            case 3:
                return ghost4;
            default:
                return ghost1;
        }
    }

    Bitmap getGhost() {

        Random random = new Random();
        int ghostCounter = random.nextInt(4);

        switch (ghostCounter) {
            case 0:
                return ghost1;
            case 1:
                return ghost2;
            case 2:
                return ghost3;
            case 3:
                return ghost4;
        }


        if (ghostCounter == 1) {
            ghostCounter++;
            Log.d("Ghost-getGhost", "return ghost1");

            return ghost1;
        }

        if (ghostCounter == 2) {
            ghostCounter++;
            Log.d("Ghost-getGhost", "return ghost2");

            return ghost2;
        }

        if (ghostCounter == 3) {
            ghostCounter++;
            Log.d("Ghost-getGhost", "return ghost3");

            return ghost3;
        }

        ghostCounter = 1;
        Log.d("Ghost-getGhost", "return ghost4");

        return ghost4;

    }

    Rect getCollisionShape() {
        return new Rect(x, y, x + width, y + height);
    }
}
