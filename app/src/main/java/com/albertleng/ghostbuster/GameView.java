package com.albertleng.ghostbuster;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.SoundPool;
import android.os.Build;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceView;
import android.view.WindowManager;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class GameView extends SurfaceView implements Runnable {

    private Thread thread;
    private boolean isPlaying, isGameOver = false, isStarted = false;
    private int screenX, screenY, score = 0;
    public static float screenRatioX, screenRatioY;
    private Paint paint, paintForTimer, paintForHighScore;
    private List<Bullet> bullets;
    private Player player;
    private Ghost[] ghosts;
    private SharedPreferences prefs;
    private Random random;
    private GameActivity activity;
    private MediaPlayer backgroundMusic;
    private Background background1, background2;
    private SoundPool soundPool;
    private int sound;
    private int timeRemaining = 0;
    private int counter = 0;
    private int currentHighScore = 0;
    private long totalPlayTimeMs = 60000;
    private int numOfGhosts = 4;
    private int randomGhostIdx;

    public GameView(final GameActivity activity, int screenX, int screenY) {
        super(activity);

        this.activity = activity;

        prefs = activity.getSharedPreferences("game", Context.MODE_PRIVATE);
        //Turn on/off audio based on saved Preference
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            AudioAttributes audioAttributes = new AudioAttributes.Builder()
                    .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                    .setUsage(AudioAttributes.USAGE_GAME)
                    .build();

            soundPool = new SoundPool.Builder()
                    .setAudioAttributes(audioAttributes)
                    .build();
        } else {
            soundPool = new SoundPool(1, AudioManager.STREAM_MUSIC, 0);
        }

        currentHighScore = prefs.getInt("highscore", 0);
        sound = soundPool.load(activity, R.raw.shoot, 1);

        this.screenX = screenX;
        this.screenY = screenY;
        Log.d("GameView", "screenX: " + screenX + ", screenY: " + screenY);

        WindowManager wm = (WindowManager) activity.getSystemService(Context.WINDOW_SERVICE);
        Point size = new Point();
        wm.getDefaultDisplay().getRealSize(size);
        String resolution = size.x + "x" + size.y;

        Log.d("GameView", resolution);

        //TODO: Get the phone's resolution
        screenRatioX = size.x / screenX;
        screenRatioY = size.y / screenY;
        Log.d("GameView", "screenRatioX: " + screenRatioX + ", screenRatioY: " + screenRatioY);


        background1 = new Background(screenX, screenY, getResources());
        background2 = new Background(screenX, screenY, getResources());

        player = new Player(this, screenY, getResources());

        bullets = new ArrayList<>();

        background2.x = screenX;
        paint = new Paint();
        paint.setTextSize(128);
        paint.setColor(Color.WHITE);

        paintForTimer = new Paint();
        paintForTimer.setTextSize(64);
        paintForTimer.setColor((Color.WHITE));

        paintForHighScore = new Paint();
        paintForHighScore.setTextSize(50);
        paintForHighScore.setColor((Color.WHITE));

        ghosts = new Ghost[numOfGhosts];
        for (int i = 0; i < numOfGhosts; i++) {
            Ghost ghost = new Ghost(getResources());
            ghosts[i] = ghost;
        }

        random = new Random();

        //Default time to play
        new CountDownTimer(totalPlayTimeMs, 1000) {

            public void onTick(long millisUntilFinished) {
                timeRemaining = (int) (millisUntilFinished / 1000);


                if (!prefs.getBoolean("isMute", false)) {
                    if (!isStarted) {
                        backgroundMusic = MediaPlayer.create(activity, R.raw.background_music);
                        backgroundMusic.setLooping(true);
                        backgroundMusic.start();
                        isStarted = true;
                    }
                }

            }

            public void onFinish() {
                timeRemaining = 0;
                isGameOver = true;

                if (backgroundMusic != null) {
                    backgroundMusic.stop();
                    backgroundMusic.release();
                    backgroundMusic = null;
                }

            }
        }.start();
    }

    @Override
    public void run() {
        while (isPlaying) {

            update();
            draw();
            sleep();
        }
    }

    //Update data to be used be draw()
    private void update() {

        background1.x -= 10 * screenRatioX;
        background2.x -= 10 * screenRatioX;

        if (background1.x + background1.background.getWidth() < 0) {
            background1.x = screenX;
        }

        if (background2.x + background2.background.getWidth() < 0) {
            background2.x = screenX;
        }

        saveIfHighScore();
        currentHighScore = prefs.getInt("highscore", 0);

        if (player.isGoingUp) {
            player.y -= 30 * screenRatioY;
        } else if (player.isGoingDown) {
            player.y += 30 * screenRatioY;
        }

        if (player.y < 0) {
            player.y = 0;
        }

        if (player.y > screenY - player.height)
            player.y = screenY - player.height;

        List<Bullet> trash = new ArrayList<>();

        for (Bullet bullet : bullets) {

            if (bullet.x > screenX)
                trash.add(bullet);

            bullet.x += 50 * screenRatioX;

            for (Ghost ghost : ghosts) {
                if (Rect.intersects(ghost.getCollisionShape(), bullet.getCollisionShape())) {
                    score++;
                    ghost.x = -500;
                    bullet.x = screenX + 500;
                    ghost.wasShot = true;
                }
            }
        }

        for (Bullet bullet : trash) {
            bullets.remove(bullet);
        }

        for (Ghost ghost : ghosts) {
            ghost.x -= ghost.speed;

            if (ghost.x + ghost.width < 0) {

                int bound = (int) (10 * screenRatioX);
                ghost.speed = random.nextInt(bound);

                if (ghost.speed < 10 * screenRatioX)
                    ghost.speed = (int) (10 * screenRatioX);

                ghost.x = screenX;
                ghost.y = random.nextInt(screenY - ghost.height);

                ghost.wasShot = false;
            }

            if (Rect.intersects(ghost.getCollisionShape(), player.getCollisionShape())) {
                isGameOver = true;
                return;
            }
        }
    }

    private void draw() {

        if (getHolder().getSurface().isValid()) {
            Canvas canvas = getHolder().lockCanvas();

            canvas.drawBitmap(background1.background, background1.x, background1.y, paint);
            canvas.drawBitmap(background2.background, background2.x, background2.y, paint);

            canvas.drawText("HighScore:" + currentHighScore + "", screenX - 350, 50, paintForHighScore);

            //Display ghosts randomly
            if (counter % 10 == 0) {
                randomGhostIdx = random.nextInt(numOfGhosts);
            }
            if (counter >= 10) {
                counter = 0;
            }
            counter++;
            for (Ghost ghost : ghosts) {
                Log.d("GameView-draw-drawBitmap", "ghost.x: " + ghost.x + ", ghost.y: " + ghost.y);
                canvas.drawBitmap(ghost.getGhostIndex(randomGhostIdx), ghost.x, ghost.y, paint);
            }

            if (timeRemaining == 0) {
                canvas.drawText("Time's Up!", 10, 64, paintForTimer);
            } else {
                int minuteRem = timeRemaining / 60;
                int secondRem = timeRemaining % 60;

                canvas.drawText(String.format("%02d:%02d", minuteRem, secondRem), 10, 64, paintForTimer);
            }


            canvas.drawText(score + "", screenX / 2f, 164, paint);

            if (isGameOver) {
                isPlaying = false;
                canvas.drawBitmap(player.getDead(), player.x, player.y, paint);
                getHolder().unlockCanvasAndPost(canvas);
                if (backgroundMusic != null) {
                    backgroundMusic.stop();
                    backgroundMusic.release();
                    backgroundMusic = null;
                }
                saveIfHighScore();
                waitBeforeExiting();
                return;
            }

            canvas.drawBitmap(player.getPlayer(), player.x, player.y, paint);

            for (Bullet bullet : bullets)
                canvas.drawBitmap(bullet.bullet, bullet.x, bullet.y, paint);

            getHolder().unlockCanvasAndPost(canvas);

        }
    }

    private void waitBeforeExiting() {
        try {
            Thread.sleep(3000);

            activity.startActivity(new Intent(activity, MainActivity.class));
            activity.finish();

        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void saveIfHighScore() {

        if (prefs.getInt("highscore", 0) < score) {
            SharedPreferences.Editor editor = prefs.edit();
            editor.putInt("highscore", score);
            editor.apply();
        }
    }

    private void sleep() {
        try {
            Thread.sleep(17);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void resume() {
        isPlaying = true;
        thread = new Thread(this);
        thread.start();

    }

    public void pause() {
        try {
            isPlaying = false;
            thread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                if (event.getX() < player.x + player.width) {
                    if (event.getY() < player.y) {
                        player.isGoingUp = true;
                    } else if (event.getY() > player.y + player.height) {
                        player.isGoingDown = true;
                    }
                }

                break;
            case MotionEvent.ACTION_UP:
                player.isGoingUp = false;
                player.isGoingDown = false;

                if (event.getX() > player.x + player.width) {
                    player.toShoot++;
                }
                break;
        }
        return true;
    }

    public void newBullet() {

        if (!prefs.getBoolean("isMute", false))
            soundPool.play(sound, 0.5f, 0.5f, 0, 0, 1);

        Bullet bullet = new Bullet(getResources());
        bullet.x = player.x + player.width;
        bullet.y = player.y - (player.height / 4);

        bullets.add(bullet);
    }
}
