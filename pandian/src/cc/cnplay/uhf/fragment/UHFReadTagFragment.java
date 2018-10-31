

package cc.cnplay.uhf.fragment;


import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.SimpleAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import cc.cnplay.uhf.R;
import cc.cnplay.uhf.StringUtils;
import cc.cnplay.uhf.UHFMainActivity;
import cc.cnplay.uhf.UIHelper;

import java.util.ArrayList;
import java.util.HashMap;


import com.rscja.deviceapi.RFIDWithUHF;

public class UHFReadTagFragment extends KeyDwonFragment {

	private boolean loopFlag = false;
	private int inventoryFlag = 2;
	private LinearLayout llQValue;
	Handler handler;
	private ArrayList<HashMap<String, String>> tagList;
	SimpleAdapter adapter;

	Button BtClear;
	TextView tv_count;
	RadioGroup RgInventory;
	RadioButton RbInventorySingle;
	//RadioButton RbInventoryLoop;
	RadioButton RbInventoryAnti;
	Spinner SpinnerQ;
	Button BtInventory;
	ListView LvTags;
	Button setFilter;
	byte initQ;
	PopupWindow popFilter;
	private EditText et_between;
	private LinearLayout llContinuous;

	private UHFMainActivity mContext;

	private HashMap<String, String> map;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		Log.i("MY", "UHFReadTagFragment.onCreateView");

		return inflater
				.inflate(R.layout.uhf_readtag_fragment, container, false);
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		Log.i("MY", "UHFReadTagFragment.onActivityCreated");
		super.onActivityCreated(savedInstanceState);

		mContext = (UHFMainActivity) getActivity();

		tagList = new ArrayList<HashMap<String, String>>();
setFilter=(Button)mContext.findViewById(R.id.btnSetFilter);
		BtClear = (Button) mContext.findViewById(R.id.BtClear);
		tv_count = (TextView) mContext.findViewById(R.id.tv_count);
		RgInventory = (RadioGroup) mContext.findViewById(R.id.RgInventory);
		RbInventorySingle = (RadioButton) mContext
				.findViewById(R.id.RbInventorySingle);
		//RbInventoryLoop = (RadioButton) mContext.findViewById(R.id.RbInventoryLoop);
		RbInventoryAnti = (RadioButton) mContext
				.findViewById(R.id.RbInventoryAnti);
		SpinnerQ = (Spinner) mContext.findViewById(R.id.SpinnerQ);
		BtInventory = (Button) mContext.findViewById(R.id.BtInventory);
		LvTags = (ListView) mContext.findViewById(R.id.LvTags);

		et_between = (EditText) mContext.findViewById(R.id.et_between);

		llContinuous = (LinearLayout) mContext.findViewById(R.id.llContinuous);

		adapter = new SimpleAdapter(mContext, tagList, R.layout.listtag_items,
				new String[] { "tagUii", "tagLen", "tagCount", "tagRssi" },
				new int[] { R.id.TvTagUii, R.id.TvTagLen, R.id.TvTagCount,
						R.id.TvTagRssi });

		setFilter.setOnClickListener(new btnfilterClicklistener());
		BtClear.setOnClickListener(new BtClearClickListener());
		RgInventory
				.setOnCheckedChangeListener(new RgInventoryCheckedListener());
	
		BtInventory.setOnClickListener(new BtInventoryClickListener());
		SpinnerQ.setEnabled(false);
		SpinnerQ.setOnItemSelectedListener(new QItemSelectedListener());

		llQValue = (LinearLayout) mContext.findViewById(R.id.llQValue);

		LvTags.setAdapter(adapter);
		clearData();

		Log.i("MY", "UHFReadTagFragment.EtCountOfTags=" + tv_count.getText());

		handler = new Handler() {

			@Override
			public void handleMessage(Message msg) {

				// Bundle bundle = msg.getData();
				// String tagEPC = bundle.getString("tagEPC");

				String result = msg.obj + "";
				String[] strs = result.split("@");
				addEPCToList(strs[0],strs[1]);
				mContext.playSound(1);
			}
		};

		SpinnerQ.setSelection(3);
	}

	@Override
	public void onPause() {
		Log.i("MY", "UHFReadTagFragment.onPause");
		super.onPause();

		// 停止识别
		stopInventory();

	}

	/**
	 * 添加EPC到列表中
	 * 
	 * @param epc
	 */
	private void addEPCToList(String epc,String rssi) {
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

			clearData();

		}
	}

	private void clearData() {
		tv_count.setText("0");

		tagList.clear();

		Log.i("MY", "tagList.size " + tagList.size());

		adapter.notifyDataSetChanged();
	}

	public class RgInventoryCheckedListener implements OnCheckedChangeListener {

		@Override
		public void onCheckedChanged(RadioGroup group, int checkedId) {

		llQValue.setVisibility(View.GONE);
		llContinuous.setVisibility(View.GONE);

			if (checkedId == RbInventorySingle.getId()) {
				// 单步识别
				inventoryFlag = 0;
				SpinnerQ.setEnabled(false);
			} 

			 else {
				// 防碰撞识别
				inventoryFlag = 2;
				SpinnerQ.setEnabled(true);
				llContinuous.setVisibility(View.VISIBLE);
				llQValue.setVisibility(View.VISIBLE);
			}
		}
	}
	
	public class QItemSelectedListener implements OnItemSelectedListener {

		@Override
		public void onItemSelected(AdapterView<?> parent, View view,
				int position, long id) {

			initQ = Byte.valueOf((String) SpinnerQ.getSelectedItem(), 10);
		}

		@Override
		public void onNothingSelected(AdapterView<?> parent) {

		}
	}

	public class btnfilterClicklistener implements OnClickListener {

		@Override
		public void onClick(View v) {

			if (popFilter == null) {
				View viewPop = LayoutInflater.from(mContext).inflate(R.layout.popwindow_filter, null);

				popFilter = new PopupWindow(viewPop, WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT, true);

				popFilter.setTouchable(true);
				popFilter.setOutsideTouchable(true);
				popFilter.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
				popFilter.setBackgroundDrawable(new BitmapDrawable());

				final EditText etLen = (EditText) viewPop.findViewById(R.id.etLen);
				final EditText etPtr = (EditText) viewPop.findViewById(R.id.etPtr);
				final EditText etData = (EditText) viewPop.findViewById(R.id.etData);
				final RadioButton rbEPC = (RadioButton) viewPop.findViewById(R.id.rbEPC);
				final RadioButton rbTID = (RadioButton) viewPop.findViewById(R.id.rbTID);
				final RadioButton rbUser = (RadioButton) viewPop.findViewById(R.id.rbUser);
				final  Button btSet=  (Button) viewPop.findViewById(R.id.btSet);

				btSet.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View view) {
						String 	filterBank="UII";
						if (rbEPC.isChecked()) {
							filterBank="UII";
						} else if (rbTID.isChecked()) {
							filterBank="TID";
						}else if (rbUser.isChecked()) {
							filterBank="USER";
						}
						if(etLen.getText().toString()==null || etLen.getText().toString().isEmpty())
						{
							UIHelper.ToastMessage(mContext,"数据长度不能为空");
							return;
						}
						if(etPtr.getText().toString()==null || etPtr.getText().toString().isEmpty())
						{
							UIHelper.ToastMessage(mContext,"起始地址不能为空");
							return;
						}
						int ptr = StringUtils.toInt(etPtr.getText().toString(), 0);
						int len =StringUtils.toInt(etLen.getText().toString(), 0);
						String data = etData.getText().toString().trim();
						if(len>0) {
							String rex = "[\\da-fA-F]*"; //匹配正则表达式，数据为十六进制格式
							if (data == null || data.isEmpty() || !data.matches(rex)) {
								UIHelper.ToastMessage(mContext, "过滤的数据必须是十六进制数据");
//								mContext.playSound(2);
								return;
							}

							if (mContext.mReader.setFilter( RFIDWithUHF.BankEnum.valueOf(filterBank), ptr,len ,data,false)) {
								UIHelper.ToastMessage(mContext, R.string.uhf_msg_set_filter_succ);
							} else {
								UIHelper.ToastMessage(mContext, R.string.uhf_msg_set_filter_fail);
//								mContext.playSound(2);
							}
						}else{
							//禁用过滤
							String dataStr = "";
							if(mContext.mReader.setFilter(RFIDWithUHF.BankEnum.valueOf("UII"), 0,0, dataStr,false)
									&& mContext.mReader.setFilter(RFIDWithUHF.BankEnum.valueOf("TID"), 0,0, dataStr,false)
									&& mContext.mReader.setFilter(RFIDWithUHF.BankEnum.valueOf("USER"), 0,0, dataStr,false)) {
								UIHelper.ToastMessage(mContext, R.string.msg_disable_succ);
							} else {
								UIHelper.ToastMessage(mContext, R.string.msg_disable_fail);
							}
						}



					}
				});
				CheckBox cb_filter = (CheckBox) viewPop.findViewById(R.id.cb_filter);
				rbEPC.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View view) {
						if (rbEPC.isChecked()) {
							etPtr.setText("32");
						}
					}
			     });
				rbTID.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View view) {
						if (rbTID.isChecked()) {
							etPtr.setText("0");
						}
					}
				});
				rbUser.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View view) {
						if (rbUser.isChecked()) {
							etPtr.setText("0");
						}
					}
				});

				cb_filter.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
					
					@Override
					public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
						// TODO Auto-generated method stub
						if(isChecked) { //启用过滤

						} else { //禁用过滤

						}
						popFilter.dismiss();
					}
				});
				
			}
			if (popFilter.isShowing()) {
				popFilter.dismiss();
				popFilter = null;
			} else {
				popFilter.showAsDropDown(v);
			}
			
			
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
			
			
			//开启同时寻EPC和TID
			
			if(!mContext.mReader.setEPCTIDMode(true))
			{
				UIHelper.ToastMessage(mContext,
						"open inventory EPC and TID mode failure");
			}
			
			switch (inventoryFlag) {
			case 0:// 单步
			{

				String strUII = mContext.mReader.inventorySingleTag();
				if (!TextUtils.isEmpty(strUII)) {
					String strEPC = mContext.mReader.convertUiiToEPC(strUII);
					addEPCToList(strEPC,"N/A");

					tv_count.setText("" + adapter.getCount());
				} else {
					UIHelper.ToastMessage(mContext,
							R.string.uhf_msg_inventory_fail);

				}

			}
				break;
			case 1:// 标签循环 此功能已注销
			{
				
if(mContext.mReader.startInventory_BankPtrCnt((byte) 0, (byte) 0,(byte)3, (byte)0, (byte)4)==0){
				//if (mContext.mReader.startInventoryTag((byte) 0, (byte) 0)) {
					BtInventory.setText(mContext
							.getString(R.string.title_stop_Inventory));
					loopFlag = true;
					setViewEnabled(false);

					new TagThread(StringUtils.toInt(et_between.getText()
							.toString().trim(), 0)).start();
				} else {
					mContext.mReader.stopInventory();
					UIHelper.ToastMessage(mContext,
							R.string.uhf_msg_inventory_open_fail);
				}
			}

				break;
			case 2:// 防碰撞
			{
				if (mContext.mReader.startInventoryTag((byte)0, (byte)0)) {
					BtInventory.setText(mContext
							.getString(R.string.title_stop_Inventory));
					loopFlag = true;
					setViewEnabled(false);
					new TagThread(StringUtils.toInt(et_between.getText()
							.toString().trim(), 0)).start();
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

	private void setViewEnabled(boolean enabled) {
		RbInventorySingle.setEnabled(enabled);
		
		RbInventoryAnti.setEnabled(enabled);
		et_between.setEnabled(enabled);
		SpinnerQ.setEnabled(enabled);

	}

	/**
	 * 停止识别
	 */
	private void stopInventory() {

		if (loopFlag) {

			loopFlag = false;

			setViewEnabled(true);

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

				res = mContext.mReader.readTagFromBuffer();//.readTagFormBuffer();

				if (res != null) {

					strTid = res[0];
					if (!strTid.equals("0000000000000000")&&!strTid.equals("000000000000000000000000")) {
						strResult = "TID:" + strTid + "\n";
					} else {
						strResult = "";
					}
					Message msg = handler.obtainMessage();
					msg.obj = strResult + "EPC:"
							+ mContext.mReader.convertUiiToEPC(res[1]) + "@"
							+ res[2];
					Log.i("msg", strResult + "EPC:"
							+ mContext.mReader.convertUiiToEPC(res[1]) + "@"
							+ res[2]);
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
