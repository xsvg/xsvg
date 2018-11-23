package cc.cnplay.uhf.fragment;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONObject;

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
import android.widget.LinearLayout;
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
	private int inventoryFlag = 2;
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
				new String[] { "tagUii", "tagLen", "tagCount", "tagRssi" },
				new int[] { R.id.TvTagUii, R.id.TvTagLen, R.id.TvTagCount,
						R.id.TvTagRssi });

		BtClear.setOnClickListener(new BtClearClickListener());

		BtInventory.setOnClickListener(new BtInventoryClickListener());

		LvTags.setAdapter(adapter);

		handler = new Handler() {

			@Override
			public void handleMessage(Message msg) {

				// Bundle bundle = msg.getData();
				// String tagEPC = bundle.getString("tagEPC");

				String result = msg.obj + "";
				String[] strs = result.split("@");
				addEPCToList(strs[0], strs[1]);
				mContext.playSound(1);
			}
		};

		inventoryFlag = 2;
	}

	@Override
	public void onPause() {
		super.onPause();

		// 停止识别
		stopInventory();

	}

	/**
	 * 添加EPC到列表中
	 * 
	 * @param epc
	 */
	private void addEPCToList(String epc, String rssi) {
		if (!TextUtils.isEmpty(epc)) {
			int index = checkIsExist(epc);

			map = new HashMap<String, String>();

			map.put("tagUii", epc);
			map.put("tagCount", String.valueOf(1));
			map.put("tagRssi", rssi);

			// mContext.getAppContext().uhfQueue.offer(epc + "\t 1");

			if (index == -1) {
				tagList.add(map);
				LvTags.setAdapter(adapter);
				tv_count.setText("" + adapter.getCount());
			} else {
				int tagcount = Integer.parseInt(
						tagList.get(index).get("tagCount"), 10) + 1;

				map.put("tagCount", String.valueOf(tagcount));

				tagList.set(index, map);

			}
			adapter.notifyDataSetChanged();
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
					String message = msg.getData().getString("msg");
					String data = msg.getData().getString("data");
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

			// 开启同时寻EPC和TID

			if (!mContext.mReader.setEPCTIDMode(true)) {
				UIHelper.ToastMessage(mContext,
						"open inventory EPC and TID mode failure");
			}

			switch (inventoryFlag) {
			case 0:// 单步
			{

				String strUII = mContext.mReader.inventorySingleTag();
				if (!TextUtils.isEmpty(strUII)) {
					String strEPC = mContext.mReader.convertUiiToEPC(strUII);
					addEPCToList(strEPC, "N/A");

					tv_count.setText("" + adapter.getCount());
				} else {
					UIHelper.ToastMessage(mContext,
							R.string.uhf_msg_inventory_fail);

				}

			}
				break;
			case 1:// 标签循环 此功能已注销
			{

				if (mContext.mReader.startInventory_BankPtrCnt((byte) 0,
						(byte) 0, (byte) 3, (byte) 0, (byte) 4) == 0) {
					// if (mContext.mReader.startInventoryTag((byte) 0, (byte)
					// 0)) {
					BtInventory.setText(mContext
							.getString(R.string.title_stop_Inventory));
					loopFlag = true;

					new TagThread(10).start();
				} else {
					mContext.mReader.stopInventory();
					UIHelper.ToastMessage(mContext,
							R.string.uhf_msg_inventory_open_fail);
				}
			}

				break;
			case 2:// 防碰撞
			{
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
			}
				break;

			default:
				break;
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

				// strUII = mContext.mReader.readUidFormBuffer();
				//
				// Log.i("UHFReadTagFragment", "TagThread uii=" + strUII);
				//
				// if (StringUtils.isNotEmpty(strUII)) {
				// strEPC = mContext.mReader.convertUiiToEPC(strUII);
				//
				// Message msg = handler.obtainMessage();
				// // Bundle bundle = new Bundle();
				// // bundle.putString("tagEPC", strEPC);
				//
				// // msg.setData(bundle);
				//
				// msg.obj = strEPC;
				// handler.sendMessage(msg);
				//
				// }

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
					msg.obj = strResult + "EPC:"
							+ mContext.mReader.convertUiiToEPC(res[1]) + "@"
							+ res[2];
					Log.i("msg",
							strResult + "EPC:"
									+ mContext.mReader.convertUiiToEPC(res[1])
									+ "@" + res[2]);
					handler.sendMessage(msg);
				}
				try {
					sleep(mBetween);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
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
