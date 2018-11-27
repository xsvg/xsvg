package cc.cnplay.uhf.fragment;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import cc.cnplay.uhf.R;
import cc.cnplay.uhf.UHFMainActivity;
import cc.cnplay.uhf.UIHelper;

import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;
import com.rscja.utility.StringUtility;

public class UHFSetFragment extends KeyDwonFragment {
	private UHFMainActivity mContext;

	private Button btnSetFre;
	private Button btnGetFre;
	private Spinner spMode;

	@ViewInject(R.id.btnSetPower)
	private Button btnSetPower;
	@ViewInject(R.id.btnGetPower)
	private Button btnGetPower;
	@ViewInject(R.id.spPower)
	private Spinner spPower;
	@ViewInject(R.id.et_worktime)
	private EditText et_worktime;
	@ViewInject(R.id.et_waittime)
	private EditText et_waittime;
	@ViewInject(R.id.btnWorkWait)
	private Button btnWorkWait;
	@ViewInject(R.id.btnExit)
	private Button btnExit;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View root = inflater
				.inflate(R.layout.activity_uhfset, container, false);
		ViewUtils.inject(this, root);

		return root;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

		mContext = (UHFMainActivity) getActivity();

		btnSetFre = (Button) mContext.findViewById(R.id.BtSetFre);
		btnGetFre = (Button) mContext.findViewById(R.id.BtGetFre);

		spMode = (Spinner) mContext.findViewById(R.id.SpinnerMode);

		btnSetFre.setOnClickListener(new SetFreOnclickListener());
		btnGetFre.setOnClickListener(new GetFreOnclickListener());
		btnWorkWait.setOnClickListener(new SetPWMOnclickListener());
		btnExit.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				mContext.finish();
			}
		});

	}

	@Override
	public void onResume() {
		super.onResume();
		getFre();
		getPwm();
		OnClick_GetPower(null);

	}

	public class SetFreOnclickListener implements OnClickListener {

		@Override
		public void onClick(View v) {

			// byte[] bBaseFre = new byte[2];
			//
			// if (mContext.mReader.setFrequency(
			// (byte) spMode.getSelectedItemPosition(), (byte) 0,
			// bBaseFre, (byte) 0, (byte) 0, (byte) 0)) {
			// UIHelper.ToastMessage(mContext,
			// R.string.uhf_msg_set_frequency_succ);
			// } else {
			// UIHelper.ToastMessage(mContext,
			// R.string.uhf_msg_set_frequency_fail);
			// }

			if (mContext.mReader.setFrequencyMode((byte) spMode
					.getSelectedItemPosition())) {
				UIHelper.ToastMessage(mContext,
						R.string.uhf_msg_set_frequency_succ);
			} else {
				UIHelper.ToastMessage(mContext,
						R.string.uhf_msg_set_frequency_fail);
			}

		}
	}

	public void getFre() {
		int idx = mContext.mReader.getFrequencyMode();

		if (idx != -1) {
			int count = spMode.getCount();
			spMode.setSelection(idx > count - 1 ? count - 1 : idx);

			// UIHelper.ToastMessage(mContext,
			// R.string.uhf_msg_read_frequency_succ);
		} else {
			UIHelper.ToastMessage(mContext,
					R.string.uhf_msg_read_frequency_fail);
		}
	}

	public void getPwm() {
		int[] pwm = mContext.mReader.getPwm();

		if (pwm == null || pwm.length < 2) {
			UIHelper.ToastMessage(mContext, R.string.uhf_msg_read_pwm_fail);
			return;
		}

		et_worktime.setText(pwm[0] + "");
		et_waittime.setText(pwm[1] + "");

	}

	public class SetPWMOnclickListener implements OnClickListener {

		@Override
		public void onClick(View v) {
			if (mContext.mReader.setPwm(StringUtility.string2Int(et_worktime
					.getText().toString(), 0), StringUtility.string2Int(
					et_waittime.getText().toString(), 0))) {
				UIHelper.ToastMessage(mContext, R.string.uhf_msg_set_pwm_succ);
			} else {
				UIHelper.ToastMessage(mContext, R.string.uhf_msg_set_pwm_fail);
			}
		}
	}

	public class GetFreOnclickListener implements OnClickListener {

		@Override
		public void onClick(View v) {

			// String strFrequency = mContext.mReader.getFrequency();
			//
			// if (StringUtils.isNotEmpty(strFrequency)) {
			//
			// etFreRange.setText(strFrequency);
			//
			// UIHelper.ToastMessage(mContext,
			// R.string.uhf_msg_read_frequency_succ);
			//
			// } else {
			// UIHelper.ToastMessage(mContext,
			// R.string.uhf_msg_read_frequency_fail);
			// }

			getFre();
		}

	}

	@OnClick(R.id.btnGetPower)
	public void OnClick_GetPower(View view) {
		int iPower = mContext.mReader.getPower();

		Log.i("UHFSetFragment", "OnClick_GetPower() iPower=" + iPower);

		if (iPower > -1) {
			int position = iPower - 5;
			int count = spPower.getCount();
			spPower.setSelection(position > count - 1 ? count - 1 : position);

			// UIHelper.ToastMessage(mContext,
			// R.string.uhf_msg_read_power_succ);

		} else {
			UIHelper.ToastMessage(mContext, R.string.uhf_msg_read_power_fail);
		}

	}

	@OnClick(R.id.btnSetPower)
	public void OnClick_SetPower(View view) {
		int iPower = spPower.getSelectedItemPosition() + 5;

		Log.i("UHFSetFragment", "OnClick_SetPower() iPower=" + iPower);

		if (mContext.mReader.setPower(iPower)) {

			UIHelper.ToastMessage(mContext, R.string.uhf_msg_set_power_succ);
		} else {
			UIHelper.ToastMessage(mContext, R.string.uhf_msg_set_power_fail);
		}

	}

}
