package dev.jam.bunnyblocks;

import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import androidx.appcompat.app.AppCompatActivity;
import dev.jam.bunnyblocks.databinding.ActivityStartBinding;

public class StartActivity extends AppCompatActivity {

    private MediaPlayer mp;
    private MediaPlayer bgsound;
    private int playmusic = 0;
    private int playsound = 0;
    private ImageView music_off;
    private ImageView music_on;
    private boolean firstRun = false;
    private static final String PREFS_NAME = "FirstRun";
    private SharedPreferences pref;

    private ImageView settingsButton;
    private ActivityStartBinding binding;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityStartBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());


        settingsButton = binding.settings;

        bgsound = MediaPlayer.create(this, R.raw.casbg);
        bgsound.setLooping(true);

        pref = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        firstRun = pref.getBoolean("firstRun", true);

        if (firstRun) {
            playmusic = 1;
            pref.edit().putBoolean("firstRun", false).apply();
        } else {
            playmusic = pref.getInt("music", 1);
            checkmusic();
        }

        Log.d("MUSIC", String.valueOf(playmusic));

        int initialVolume = pref.getInt("volume", 100);
        setVolume(initialVolume);

        binding.btnStart.setOnClickListener(view -> startActivity(new Intent(StartActivity.this, MainActivity.class)));

        binding.btnIns.setOnClickListener(view -> startActivity(new Intent(StartActivity.this, Instructions.class)));

        binding.btnPol.setOnClickListener(view -> startActivity(new Intent(StartActivity.this, Policy.class)));


        settingsButton.setOnClickListener(view -> {
            if (playsound == 1) {
                mp.start();
            }
            showSettingsDialog();
        });
    }

    private void showSettingsDialog() {
        Dialog dialog = new Dialog(this, R.style.WinDialog);
        dialog.getWindow().setContentView(R.layout.settings);
        dialog.getWindow().setGravity(Gravity.CENTER_HORIZONTAL);
        dialog.getWindow().setBackgroundDrawableResource(R.color.transparent);

        ImageView tempMusicOn = dialog.findViewById(R.id.music_on);
        tempMusicOn.setOnClickListener(view -> {
            playmusic = 0;
            checkmusic();
            tempMusicOn.setVisibility(View.INVISIBLE);
            music_off.setVisibility(View.VISIBLE);
            pref.edit().putInt("music", playmusic).apply();
        });
        music_on = tempMusicOn;

        ImageView tempMusicOff = dialog.findViewById(R.id.music_off);
        tempMusicOff.setOnClickListener(view -> {
            playmusic = 1;
            bgsound.start();
            recreate();
            dialog.show();
            music_on.setVisibility(View.VISIBLE);
            tempMusicOff.setVisibility(View.INVISIBLE);
            pref.edit().putInt("music", playmusic).apply();
        });
        music_off = tempMusicOff;

        ImageView exitButton = dialog.findViewById(R.id.exit);
        exitButton.setOnClickListener(view -> finishAffinity());

        ImageView closeButton = dialog.findViewById(R.id.close);
        closeButton.setOnClickListener(view -> dialog.dismiss());

        checkmusicdraw();
        dialog.show();
    }

    @Override
    protected void onPause() {
        super.onPause();
        bgsound.pause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        checkmusic();
    }

    private void checkmusic() {
        if (playmusic == 1) {
            bgsound.start();
        } else {
            bgsound.pause();
        }
    }

    private void checkmusicdraw() {
        if (playmusic == 1) {
            music_on.setVisibility(View.VISIBLE);
            music_off.setVisibility(View.INVISIBLE);
        } else {
            music_on.setVisibility(View.INVISIBLE);
            music_off.setVisibility(View.VISIBLE);
        }
    }

    private void setVolume(int volume) {
        float volumeLevel = volume / 100.0f;
        bgsound.setVolume(volumeLevel, volumeLevel);
    }
}