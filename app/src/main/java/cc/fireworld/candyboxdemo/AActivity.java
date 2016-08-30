package cc.fireworld.candyboxdemo;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.TextView;

import cc.fireworld.candybox.CandyBox;
import cc.fireworld.candybox.Pack;


/**
 * Created by cxx on 16-5-31.
 * xx.ch@outlook.com
 */
public class AActivity extends Activity implements CandyBox.Eater {
    private TextView text;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_a);

        text = (TextView) findViewById(R.id.text);
        findViewById(R.id.button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CandyBox.put(Pack.pack("AActivity", "Say hi from AActivity!"));
            }
        });

        CandyBox.register(this);
    }

    @Override
    protected void onDestroy() {
        CandyBox.unregister(this);
        super.onDestroy();
    }

    @Override
    public void onEat(@NonNull Pack p) {
        if ("MainActivity".equals(p.getType())) {
            String txt = String.valueOf(p.getCandy());
            text.setText(txt);
        }
    }
}
