package com.example.diegocuervo.sinradio;

import android.app.Activity;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

/**
 * Created by Diego Cuervo on 09/08/2016.
 */
public class TTS extends AppCompatActivity {
    private Button speakNowButton;
    private EditText editText;
    TTSManager ttsManager = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tts);

        ttsManager = new TTSManager();
        ttsManager.init(this);

        editText = (EditText) findViewById(R.id.input_text);
        speakNowButton = (Button) findViewById(R.id.speak_now);
        speakNowButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                String text = editText.getText().toString();
                ttsManager.initQueue(text);
            }
        });
    }

    /**
     * Releases the resources used by the TextToSpeech engine.
     */
    @Override
    public void onDestroy() {
        super.onDestroy();
        ttsManager.shutDown();
    }
}