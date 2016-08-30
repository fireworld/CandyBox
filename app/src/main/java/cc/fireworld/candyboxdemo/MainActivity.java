package cc.fireworld.candyboxdemo;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import cc.fireworld.candybox.CandyBox;
import cc.fireworld.candybox.Pack;


public class MainActivity extends Activity implements View.OnClickListener, CandyBox.Eater {
    private TextView text;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        text = (TextView) findViewById(R.id.text);
        findViewById(R.id.button).setOnClickListener(this);
        findViewById(R.id.load).setOnClickListener(this);
        findViewById(R.id.jump).setOnClickListener(this);

        CandyBox.register(this);
    }

    @Override
    protected void onDestroy() {
        CandyBox.unregister(this);
        super.onDestroy();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.button:
                CandyBox.put(Pack.pack("MainActivity", "Say hi from MainActivity!"));
                break;
            case R.id.load:
                loadAsync("http://www.baidu.com");
                break;
            case R.id.jump:
                startActivity(new Intent(this, AActivity.class));
                break;
            default:
                break;
        }
    }

    @Override
    public void onEat(@NonNull Pack p) {
        switch (p.getType()) {
            case "AActivity":
                text.setText(String.valueOf(p.getCandy()));
                break;
            case "NET":
                text.setText(String.valueOf(p.getCandy()));
                break;
        }
    }

    private static void loadAsync(final String url) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                String r = load(url);
                if (r != null) {
                    Log.e("MainActivity", r);
                    CandyBox.put(Pack.pack("NET", r));
                }
            }
        }).start();
    }

    private static String load(String url) {
        try {
            URL l = new URL(url);
            HttpURLConnection conn = (HttpURLConnection) l.openConnection();
            BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line);
            }
            conn.disconnect();
            reader.close();
            return sb.toString();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
