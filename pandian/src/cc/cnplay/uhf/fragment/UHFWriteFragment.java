package cc.cnplay.uhf.fragment;

import java.util.HashMap;
import java.util.Map;

import org.json.JSONObject;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import cc.cnplay.uhf.App;
import cc.cnplay.uhf.HttpUtils;
import cc.cnplay.uhf.R;
import cc.cnplay.uhf.UHFMainActivity;
import cc.cnplay.uhf.UIHelper;

public class UHFWriteFragment extends KeyDwonFragment {

	private UHFMainActivity mContext;

	EditText EtTagUii_Write;
	Button BtErase;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		return inflater.inflate(R.layout.uhf_write_fragment, container, false);
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

		mContext = (UHFMainActivity) getActivity();

		EtTagUii_Write = (EditText) mContext.findViewById(R.id.EtTagUii_Write);

		BtErase = (Button) mContext.findViewById(R.id.BtErase);

		EtTagUii_Write.setKeyListener(null);

		BtErase.setOnClickListener(new BtUii_WriteClickListener());

	}

	public class BtUii_WriteClickListener implements OnClickListener {

		@Override
		public void onClick(View v) {
			String uiiStr = mContext.mReader.inventorySingleTag();
			if (uiiStr != null) {
				EtTagUii_Write.setText(uiiStr);
				UIHelper.ToastMessage(mContext, R.string.rfid_msg_read_succ);
				post(uiiStr);
			} else {
				EtTagUii_Write.setText("");
				UIHelper.ToastMessage(mContext, R.string.uhf_msg_read_tag_fail);
			}
		}

		public void post(final String uiiStr) {

			new Thread(new Runnable() {
				@Override
				public void run() {
					try {
						JSONObject json = new JSONObject();
						json.put("rfid", uiiStr);
						Map<String, String> header = new HashMap<String, String>();
						header.put("token", App.login.getAcctoken());
						String url = App.url("/home/store/tag");
						HttpUtils.postJSON(url, json, header);
					} catch (Exception ex) {
						UIHelper.ToastMessage(mContext,
								"程序异常：" + ex.toString(), 100000);
					}
				}
			}).start();

		}
	}

}
