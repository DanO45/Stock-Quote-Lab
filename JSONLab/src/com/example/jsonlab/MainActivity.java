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

	String URL;
	JSONObject JSONObj;
	TextView stock;
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
				
			} catch (JSONException e) {
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
		button.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {

				EditText et = (EditText) findViewById(R.id.stockSymbol);
				String stockSymbol = et.getText().toString();
				URL = "http://finance.yahoo.com/webservice/v1/symbols/" + stockSymbol + "/quote?format=json";
				
				Thread t = new Thread(){
					@Override
					public void run (){

						URL url = null;
						String pageContent = "";
						
						try 
						{
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
						}
						
						catch (Exception e)
						{
							e.printStackTrace();
						}
					}
				};
				
				t.start();
			}
		});
	}
}
