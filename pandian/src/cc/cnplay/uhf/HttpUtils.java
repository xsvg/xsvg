package cc.cnplay.uhf;

import java.util.Map;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

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
				Message msg = handler.obtainMessage();
				try {
					msg.obj = HttpUtils.postJSON(url, json, header);
				} catch (Exception e) {
					msg.obj = null;
				}
				handler.handleMessage(msg);
			}
		}).start();
	}
}
