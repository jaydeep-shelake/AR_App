package com.mrspd.myfirstarapp;

import android.media.MediaPlayer;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.google.ar.sceneform.AnchorNode;
import com.google.ar.sceneform.math.Vector3;
import com.google.ar.sceneform.rendering.Color;
import com.google.ar.sceneform.rendering.ExternalTexture;
import com.google.ar.sceneform.rendering.ModelRenderable;
import com.google.ar.sceneform.ux.ArFragment;

public class MainActivity extends AppCompatActivity {
    private ModelRenderable videoRendrable;
    private float HEIGHT = 0.95f;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ExternalTexture texture = new ExternalTexture();
        MediaPlayer mediaPlayer = MediaPlayer.create(this, R.raw.video);
        mediaPlayer.setSurface(texture.getSurface());
        mediaPlayer.setLooping(true);


        ModelRenderable.builder()
                .setSource(this, R.raw.video_screen)
                .build()
                .thenAccept(modelRenderable -> {
                    videoRendrable = modelRenderable;
                    videoRendrable.getMaterial().setExternalTexture("videoTexture", texture);
                    videoRendrable.getMaterial().setFloat4("keyColor", new Color(0.01843f, 1.0f, 0.098f));


                });
        ArFragment arFragment = (ArFragment) getSupportFragmentManager().findFragmentById(R.id.fragment);
        arFragment.setOnTapArPlaneListener(((hitResult, plane, motionEvent) -> {
            AnchorNode anchorNode = new AnchorNode(hitResult.createAnchor());
            if (!mediaPlayer.isPlaying()) {
                mediaPlayer.start();
                texture.getSurfaceTexture().setOnFrameAvailableListener(surfaceTexture -> {
                    anchorNode.setRenderable(videoRendrable);
                    texture.getSurfaceTexture().setOnFrameAvailableListener(null);
                });
            } else {
                anchorNode.setRenderable(videoRendrable);
            }

            float width = mediaPlayer.getVideoWidth();
            float height = mediaPlayer.getVideoHeight();

            anchorNode.setLocalScale(new Vector3(HEIGHT *(height/width),HEIGHT,0.95f));
            arFragment.getArSceneView().getScene().addChild(anchorNode);

        }));
    }
}
