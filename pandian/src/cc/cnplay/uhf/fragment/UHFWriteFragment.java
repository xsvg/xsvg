package cc.cnplay.uhf.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import cc.cnplay.uhf.App;
import cc.cnplay.uhf.R;
import cc.cnplay.uhf.StringUtils;
import cc.cnplay.uhf.UHFMainActivity;
import cc.cnplay.uhf.UIHelper;

import com.rscja.deviceapi.RFIDWithUHF.BankEnum;

public class UHFWriteFragment extends KeyDwonFragment {

	private static final String TAG = "UHFWriteFragment";

	private UHFMainActivity mContext;

	EditText EtTagUii_Write;
	EditText EtData_Write;
	Button BtWrite;
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
		EtData_Write = (EditText) mContext.findViewById(R.id.EtData_Write);
		BtWrite = (Button) mContext.findViewById(R.id.BtWrite);

		BtErase = (Button) mContext.findViewById(R.id.BtErase);

		EtTagUii_Write.setKeyListener(null);

		BtWrite.setOnClickListener(new BtWriteOnClickListener());
		BtErase.setOnClickListener(new BtUii_WriteClickListener());

	}

	public class BtUii_WriteClickListener implements OnClickListener {

		@Override
		public void onClick(View v) {

			String uiiStr = mContext.mReader.inventorySingleTag();

			if (uiiStr != null) {
				EtTagUii_Write.setText(uiiStr);
			} else {
				EtTagUii_Write.setText("");

				UIHelper.ToastMessage(mContext, R.string.uhf_msg_read_tag_fail);
			}
		}
	}

	public class BtWriteOnClickListener implements OnClickListener {

		@Override
		public void onClick(View v) {

			String strData = EtData_Write.getText().toString().trim();// 要写入的内容

			if (StringUtils.isEmpty(strData)) {
				UIHelper.ToastMessage(mContext,
						R.string.uhf_msg_write_must_not_null);

				return;
			} else if (!mContext.vailHexInput(strData)) {

				UIHelper.ToastMessage(mContext, R.string.rfid_mgs_error_nohex);
				return;
			}

			if ((strData.length()) % 4 != 0) {
				UIHelper.ToastMessage(mContext,
						R.string.uhf_msg_write_must_len4x);

				return;
			} else if (!mContext.vailHexInput(strData)) {
				UIHelper.ToastMessage(mContext, R.string.rfid_mgs_error_nohex);
				return;
			}

			String strUII = EtTagUii_Write.getText().toString().trim();
			if (StringUtils.isEmpty(strUII)) {
				UIHelper.ToastMessage(mContext,
						R.string.uhf_msg_tag_must_not_null);
				return;
			}

			if (mContext.mReader.writeData(App.PWD, BankEnum.RESERVED,
					App.PORT, App.CNTLEN, strData, strUII)) {
				UIHelper.ToastMessage(mContext, R.string.uhf_msg_write_succ);
			} else {
				UIHelper.ToastMessage(mContext, R.string.uhf_msg_write_fail);

			}
		}
	}

}
