package androidbee.nodejs.client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.TextView;

public class MainActivity extends Activity {

	private static final String HOST_IP = "http://192.168.0.14:12345";

	HttpClient client;

	TextView txtOut;
	EditText editId;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		client = new DefaultHttpClient();

		txtOut = (TextView) findViewById(R.id.txt_out);

		Button btn_go = (Button) findViewById(R.id.btn_go);
		btn_go.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				String result = null;
				EditText editId = (EditText) findViewById(R.id.editText1);
				EditText editName = (EditText) findViewById(R.id.editText2);
				RadioGroup rg = (RadioGroup) findViewById(R.id.radioGroup1);
				if (rg.getCheckedRadioButtonId() == R.id.radio0)
					; // Node.js

				User user = new User(editId.getText().toString(), editName
						.getText().toString());
				try {
					result = new HttpPostTask().execute(user).get();
				} catch (InterruptedException e) {
					e.printStackTrace();
				} catch (ExecutionException e) {
					e.printStackTrace();
				}

				// } else {
				// try {
				// result = new HttpDataTask().execute(HOST_IP + "/data").get();
				// } catch (InterruptedException e) {
				// e.printStackTrace();
				// } catch (ExecutionException e) {
				// e.printStackTrace();
				// }
				// }

				txtOut.setText(result);
			}
		});

	}

	private String getHttpResponse(HttpResponse resp) {
		StringBuffer sb = new StringBuffer();
		BufferedReader br = null;

		try {
			String str = null;
			br = new BufferedReader(new InputStreamReader(resp.getEntity()
					.getContent()));
			while ((str = br.readLine()) != null) {
				sb.append(str);
			}
			br.close();

		} catch (IllegalStateException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return sb.toString();
	}

	class HttpPostTask extends AsyncTask<User, Void, String> {

		@Override
		protected String doInBackground(User... param) {

			HttpResponse resp = null;
			UrlEncodedFormEntity entity = null;

			List<NameValuePair> params = new ArrayList<NameValuePair>();
			params.add(new BasicNameValuePair("user_id", param[0].id));
			params.add(new BasicNameValuePair("user_name", param[0].name));

			HttpPost post = new HttpPost();
			try {
				entity = new UrlEncodedFormEntity(params);
				post.setEntity(entity);
				post.setURI(new URI(HOST_IP));
				resp = client.execute(post);
			} catch (URISyntaxException e1) {
				e1.printStackTrace();
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			} catch (ClientProtocolException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
			String result = getHttpResponse(resp);

			return result;
		}
	}

	class HttpDataTask extends AsyncTask<String, Void, String> {

		private HttpResponse response;

		@Override
		protected String doInBackground(String... params) {
			String uri = params[0];
			HttpGet get = new HttpGet();
			try {
				get.setURI(new URI(uri));
				response = client.execute(get);
			} catch (URISyntaxException e) {
				e.printStackTrace();
			} catch (ClientProtocolException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
			String result = getHttpResponse(response);

			return result;
		}

	}

	class User {
		public String id;
		public String name;

		public User(String i, String n) {
			this.id = i;
			this.name = n;
		}
	}

}
