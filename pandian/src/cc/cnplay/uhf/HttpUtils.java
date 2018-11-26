package cc.cnplay.uhf;

import java.util.Map;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

public class HttpUtils {

	public static String postJSON(final String url, final String json,
			final Map<String, String> header) throws Exception {
		HttpPost httpPost = new HttpPost(url);
		HttpClient client = new DefaultHttpClient();
		String respContent = null;
		StringEntity entity = new StringEntity(json, "utf-8");
		entity.setContentEncoding("UTF-8");
		entity.setContentType("application/json");
		httpPost.setEntity(entity);
		if (header != null && header.size() > 0) {
			for (String key : header.keySet()) {
				httpPost.setHeader(key, header.get(key));
			}
		}
		HttpResponse resp = client.execute(httpPost);
		if (resp.getStatusLine().getStatusCode() == 200) {
			HttpEntity he = resp.getEntity();
			respContent = EntityUtils.toString(he, "UTF-8");
		}
		return respContent;
	}

	public static void postJSON(final String url, final String json,
			final Map<String, String> header, final Handler handler)
			throws Exception {
		new Thread(new Runnable() {
			@Override
			public void run() {
				Message msg = new Message();
				Bundle data = new Bundle();
				try {
					String content = "";
					data.putString("data", content);
					content = HttpUtils.postJSON(url, json, header);
					data.putString("data", content);
					msg.setData(data);
					data.putString("msg", "success");
				} catch (Exception e) {
					data.putString("msg", "ERROR: " + e.getMessage());
				}
				handler.handleMessage(msg);
			}
		}).start();
	}
}
