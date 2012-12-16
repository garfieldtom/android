package com.xxh.callmonitor;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class AboutActivity extends Activity {
	private Button btnClose=null;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);
        
        btnClose=(Button)findViewById(R.id.btnAboutClose);
        btnClose.setOnClickListener(new OnClickListener(){
        	//@Override
        	public void onClick(View v) {
        		finish();
    		}
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_about, menu);
        return true;
    }
}
