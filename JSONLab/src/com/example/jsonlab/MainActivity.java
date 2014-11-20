package com.example.jsonlab;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class MainActivity extends Activity {

	JSONObject JSONObj;
	TextView stock;
	Thread t;

	Handler JSONHandler = new Handler(new Handler.Callback() {

		@Override
		public boolean handleMessage(Message msg) {

			String json = msg.obj.toString();

			try {
				JSONObj = new JSONObject(json);

				JSONObject list = JSONObj.getJSONObject("list");
				JSONArray resources = list.getJSONArray("resources");
				JSONObject resource = resources.getJSONObject(0).getJSONObject("resource");
				JSONObject fields = resource.getJSONObject("fields");
				//System.out.println(resources.name());
				String companyName = fields.getString("name");
				String stockPrice = fields.getString("price");
				String quote = "Company Name: " + companyName + "\n" + "Current Stock Price: " + stockPrice;

				stock = (TextView) findViewById(R.id.stockQuote);
				stock.setText(String.valueOf(quote));

			} 

			catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return false;
		}
	});

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		Button button = (Button) findViewById(R.id.btnSearch);
		
		t = new Thread(new Runnable(){
			public void run (){

				EditText et = (EditText) findViewById(R.id.stockSymbol);
				

				Thread.currentThread();
				while(!Thread.interrupted())
				{
					try 
					{
						String stockSymbol = et.getText().toString();
						URL url;
						String pageContent = "";
						String URL = "http://finance.yahoo.com/webservice/v1/symbols/" + stockSymbol + "/quote?format=json";
						url = new URL(URL);

						BufferedReader br = new BufferedReader(new InputStreamReader(url.openStream()));

						String tmpString = br.readLine();

						while(tmpString != null)
						{
							pageContent += tmpString;
							tmpString = br.readLine();
						}

						//System.out.println("This is the message:" + pageContent);
						Message msg = JSONHandler.obtainMessage();
						msg.obj = pageContent;
						JSONHandler.sendMessage(msg);
						//msg.setTarget(webPageHandler);
						//msg.sendToTarget();

						Thread.sleep(2000);
					}//end of try block

					catch (Exception e)
					{
						e.printStackTrace();
					}
					
				}//end of while loop
				
			}//end of run method

		});//end of thread

		View.OnClickListener listener = new View.OnClickListener() {

			@Override
			public void onClick(View v) {

				if(!t.isAlive())
				{
					t.start();
				}
				
			}//end of on click 
			
		};//end of click listener
		
		button.setOnClickListener(listener);
		
	}//end of onCreate
}
