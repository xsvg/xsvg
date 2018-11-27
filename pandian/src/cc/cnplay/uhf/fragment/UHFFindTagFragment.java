package cc.cnplay.uhf.fragment;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONObject;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
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

import com.google.gson.Gson;

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
				new String[] { "tagUii", "tagLen", "tagStatus" }, new int[] {
						R.id.TvTagUii, R.id.TvTagLen, R.id.TvTagCount });

		BtClear.setOnClickListener(new BtClearClickListener());
		BtInventory.setOnClickListener(new BtInventoryClickListener());
		LvTags.setAdapter(adapter);
		handler = new Handler() {

			@Override
			public void handleMessage(Message msg) {
				StoreItem item = (StoreItem) msg.obj;
				addEPCToList(item);
				mContext.playSound(1);
			}
		};
	}

	@Override
	public void onPause() {
		super.onPause();
		stopInventory();

	}

	public class BtClearClickListener implements OnClickListener {

		@Override
		public void onClick(View v) {

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

	class TagThread extends Thread {

		private int mBetween = 80;
		HashMap<String, String> map;

		public TagThread(int iBetween) {
			mBetween = iBetween;
		}

		public void run() {
			Map<String, StoreItem> itemMap = new HashMap<String, StoreItem>();
			try {
				tagList.clear();
				JSONObject json = new JSONObject();
				json.put("dywOwner", dywOwner.getText().toString());
				json.put("dywOwnerId", dywOwnerId.getText().toString());
				Map<String, String> header = new HashMap<String, String>();
				header.put("token", App.login.getAcctoken());
				String url = App.url("/home/store/find");
				String data = HttpUtils.postJSON(url, json.toString(), header);
				if (StringUtils.isNotEmpty(data)) {
					Gson gson = new Gson();
					StoreItem[] items = gson.fromJson(data, StoreItem[].class);
					for (int i = 0; i < items.length; i++) {
						StoreItem item = items[i];
						itemMap.put(item.getRfid(), item);
						Message msg = handler.obtainMessage();
						msg.obj = item;
						handler.sendMessage(msg);
					}
				}
				if (itemMap.size() == 0) {
					Message msg = handler.obtainMessage();
					StoreItem item = new StoreItem();
					item.setRfid("找不到抵押物");
					item.setStatus(0);
					itemMap.put(item.getRfid(), item);
					msg.obj = item;
					handler.sendMessage(msg);
				}
			} catch (Throwable ex) {
				Message msg = handler.obtainMessage();
				StoreItem item = new StoreItem();
				item.setRfid("读取失败");
				item.setStatus(0);
				if (itemMap.containsKey(item.getRfid())) {
					item = itemMap.get(item.getRfid());
					item.setStatus(0);
				} else {
					itemMap.put(item.getRfid(), item);
				}
				msg.obj = item;
				handler.sendMessage(msg);
			}

			String epc;
			String[] res = null;
			while (loopFlag) {
				res = mContext.mReader.readTagFromBuffer();
				if (res != null) {
					epc = mContext.mReader.convertUiiToEPC(res[1]);
					if (itemMap.containsKey(epc)) {
						Message msg = handler.obtainMessage();
						StoreItem item = itemMap.get(epc);
						item.setStatus(1);
						msg.obj = item;
						handler.sendMessage(msg);
					}
				}
				try {
					sleep(mBetween);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}

			}
		}
	}

	/**
	 * 添加EPC到列表中
	 * 
	 * @param epc
	 */
	private void addEPCToList(StoreItem item) {
		try {
			map = new HashMap<String, String>();
			map.put("tagUii", item.getRfid());
			map.put("tagStatus", "未找到");
			for (int i = 0; i < tagList.size(); i++) {
				Map<String, String> tmpMap = tagList.get(i);
				if (tmpMap.get("tagUii").equals(item.getRfid())) {
					tmpMap.put("tagStatus", "已找到");
					adapter.notifyDataSetChanged();
					return;
				}
			}
			tagList.add(map);
			LvTags.setAdapter(adapter);
			tv_count.setText(tagList.size());
			adapter.notifyDataSetChanged();
		} catch (Throwable e) {
		}
	}

	@Override
	public void myOnKeyDwon() {
		readTag();
	}

}
