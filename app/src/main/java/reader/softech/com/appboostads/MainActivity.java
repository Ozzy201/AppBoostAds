package reader.softech.com.appboostads;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import reader.softech.com.appboostad.AppBoost;
import reader.softech.com.appboostad.onAdClosedEvent;

public class MainActivity extends AppCompatActivity {

    Button mBtn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        AppBoost.initialize(this);

        AppBoost.setonAdClosedEvent(new onAdClosedEvent() {
            @Override
            public void onAdClosed() {
                Intent intent = new Intent(MainActivity.this,SeconActivity.class);
                startActivity(intent);
                finish();
            }
        });

        mBtn = findViewById(R.id.mbtn);

        mBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                AppBoost.showAd();

            }
        });
    }
}
