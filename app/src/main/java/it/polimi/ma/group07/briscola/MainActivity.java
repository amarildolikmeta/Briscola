package it.polimi.ma.group07.briscola;

import android.content.Intent;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;

public class MainActivity extends FragmentActivity {
    private Button testButton;
    private Button singlePlayerButton;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        testButton=(Button) findViewById(R.id.testButton);
        singlePlayerButton=(Button) findViewById(R.id.singlePlayerButton);

        testButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent  intent=new Intent(MainActivity.this,GameActivity.class);
                intent.putExtra("singlePlayer",false);
                startActivity(intent);
            }
        });
        singlePlayerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent  intent=new Intent(MainActivity.this,GameActivity.class);
                intent.putExtra("singlePlayer",true);
                startActivity(intent);
            }
        });
    }
}
