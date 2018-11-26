package cc.cnplay.uhf.fragment;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import cc.cnplay.uhf.App;
import cc.cnplay.uhf.HttpUtils;
import cc.cnplay.uhf.R;
import cc.cnplay.uhf.StringUtils;
import cc.cnplay.uhf.UHFMainActivity;
import cc.cnplay.uhf.UIHelper;

public class UHFFindTagFragment extends KeyDwonFragment {

	private boolean loopFlag = false;
	Handler handler;
	private ArrayList<HashMap<String, String>> tagList;
	SimpleAdapter adapter;

	Button BtClear;
	TextView tv_count;
	Button BtInventory;
	ListView LvTags;
	PopupWindow popFilter;
	private EditText dywOwner;
	private EditText dywOwnerId;

	private UHFMainActivity mContext;

	private HashMap<String, String> map;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		Log.i("MY", "UHFReadTagFragment.onCreateView");

		return inflater
				.inflate(R.layout.uhf_findtag_fragment, container, false);
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		Log.i("MY", "UHFReadTagFragment.onActivityCreated");
		super.onActivityCreated(savedInstanceState);
		mContext = (UHFMainActivity) getActivity();
		tagList = new ArrayList<HashMap<String, String>>();
		BtClear = (Button) mContext.findViewById(R.id.BtClear);
		tv_count = (TextView) mContext.findViewById(R.id.tv_count);
		BtInventory = (Button) mContext.findViewById(R.id.BtInventory);
		LvTags = (ListView) mContext.findViewById(R.id.LvTags);
		dywOwner = (EditText) mContext.findViewById(R.id.et_dywOwner);
		dywOwnerId = (EditText) mContext.findViewById(R.id.et_dywOwnerId);
		adapter = new SimpleAdapter(mContext, tagList, R.layout.listtag_items,
				new String[] { "tagUii", "tagLen", "tagCount" }, new int[] {
						R.id.TvTagUii, R.id.TvTagLen, R.id.TvTagCount });

		BtClear.setOnClickListener(new BtClearClickListener());
		BtInventory.setOnClickListener(new BtInventoryClickListener());
		LvTags.setAdapter(adapter);
		handler = new Handler() {

			@Override
			public void handleMessage(Message msg) {
				String result = msg.obj + "";
				addEPCToList(result, "", true);
				mContext.playSound(1);
			}
		};
	}

	@Override
	public void onPause() {
		super.onPause();
		stopInventory();

	}

	/**
	 * 添加EPC到列表中
	 * 
	 * @param epc
	 */
	private void addEPCToList(String epc, String rssi, boolean isFind) {
		mContext.playSound(1);
		try {
			if (!TextUtils.isEmpty(epc)) {
				int index = checkIsExist(epc);
				if (isFind && index == -1) {
					return;
				}
				map = new HashMap<String, String>();
				map.put("tagUii", epc);
				map.put("tagCount", "未找到");
				if (!TextUtils.isEmpty(rssi)) {
					map.put("tagRssi", rssi);
				}
				if (index == -1) {
					tagList.add(map);
					LvTags.setAdapter(adapter);
					tv_count.setText("未找到");
				} else {
					map.put("tagCount", "已找到");
					tagList.set(index, map);
				}
				adapter.notifyDataSetChanged();
			}
		} catch (Throwable e) {
			mContext.playSound(2);
		}
	}

	public class BtClearClickListener implements OnClickListener {

		@Override
		public void onClick(View v) {
			find(dywOwner.getText().toString().trim(), dywOwnerId.getText()
					.toString().trim());
		}
	}

	private void find(String dywOwner, String dywOwnerId) {
		try {
			UIHelper.ToastMessage(mContext, "正在查询", 0);
			JSONObject json = new JSONObject();
			json.put("dywOwner", dywOwner);
			json.put("dywOwnerId", dywOwnerId);
			Map<String, String> header = new HashMap<String, String>();
			header.put("token", App.login.getAcctoken());
			String url = App.url("/home/store/find");
			Handler handler = new Handler() {
				public void handleMessage(Message msg) {
					try {
						String message = msg.getData().getString("msg");
						String data = msg.getData().getString("data");
						if (StringUtils.isNotEmpty(data)) {
							Gson gson = new Gson();
							StoreItem[] items = gson.fromJson(data,
									StoreItem[].class);
							mContext.playSound(1);
							for (int i = 0; i < items.length; i++) {
								StoreItem item = items[i];
								addEPCToList(item.getRfid(),item.getSn()+"/"+item.getAreaName(), false);
							}
						}
					} catch (Throwable e) {
						mContext.playSound(2);
					}
				}
			};
			HttpUtils.postJSON(url, json.toString(), header, handler);
		} catch (Throwable ex) {
			UIHelper.ToastMessage(mContext, "程序异常：" + ex.toString(), 100000);
		}
	}

	public class BtInventoryClickListener implements OnClickListener {

		@Override
		public void onClick(View v) {
			readTag();
		}
	}

	private void readTag() {
		if (BtInventory.getText().equals(
				mContext.getString(R.string.btInventory)))// 识别标签
		{
			if (!mContext.mReader.setEPCTIDMode(true)) {
				UIHelper.ToastMessage(mContext,
						"open inventory EPC and TID mode failure");
			}
			if (mContext.mReader.startInventoryTag((byte) 0, (byte) 0)) {
				BtInventory.setText(mContext
						.getString(R.string.title_stop_Inventory));
				loopFlag = true;
				new TagThread(10).start();
			} else {
				mContext.mReader.stopInventory();
				UIHelper.ToastMessage(mContext,
						R.string.uhf_msg_inventory_open_fail);
			}
		} else {// 停止识别
			stopInventory();
		}
	}

	/**
	 * 停止识别
	 */
	private void stopInventory() {

		if (loopFlag) {
			loopFlag = false;
			if (mContext.mReader.stopInventory()) {
				BtInventory.setText(mContext.getString(R.string.btInventory));
			} else {
				UIHelper.ToastMessage(mContext,
						R.string.uhf_msg_inventory_stop_fail);
			}

		}
	}

	/**
	 * 判断EPC是否在列表中
	 * 
	 * @param strEPC
	 *            索引
	 * @return
	 */
	public int checkIsExist(String strEPC) {
		int existFlag = -1;
		if (StringUtils.isEmpty(strEPC)) {
			return existFlag;
		}

		String tempStr = "";
		for (int i = 0; i < tagList.size(); i++) {
			HashMap<String, String> temp = new HashMap<String, String>();
			temp = tagList.get(i);
			tempStr = temp.get("tagUii");
			if (strEPC.equals(tempStr)) {
				existFlag = i;
				break;
			}
		}

		return existFlag;
	}

	class TagThread extends Thread {

		private int mBetween = 80;
		HashMap<String, String> map;

		public TagThread(int iBetween) {
			mBetween = iBetween;
		}

		public void run() {
			String strTid;
			String strResult;
			String[] res = null;
			while (loopFlag) {
				res = mContext.mReader.readTagFromBuffer();// .readTagFormBuffer();
				if (res != null) {
					strTid = res[0];
					if (!strTid.equals("0000000000000000")
							&& !strTid.equals("000000000000000000000000")) {
						strResult = "TID:" + strTid + "\n";
					} else {
						strResult = "";
					}
					Message msg = handler.obtainMessage();
					msg.obj = strTid;
					handler.sendMessage(msg);
				}
				try {
					sleep(mBetween);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}

			}
		}
	}

	@Override
	public void myOnKeyDwon() {
		readTag();
	}

}
