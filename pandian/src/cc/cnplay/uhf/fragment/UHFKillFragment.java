package cc.cnplay.uhf.fragment;

import cc.cnplay.uhf.R;
import cc.cnplay.uhf.UHFMainActivity;
import cc.cnplay.uhf.UIHelper;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;



public class UHFKillFragment extends KeyDwonFragment {

    private static final String TAG = "UHFKillFragment";

    private UHFMainActivity mContext;

    CheckBox CkWithUii_Write;
    EditText EtTagUii_Write;
    EditText EtAccessPwd_Write;
    Button BtUii_Write;
    Button btnKill;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.uhf_kill_fragment, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        mContext = (UHFMainActivity) getActivity();

        CkWithUii_Write = (CheckBox) mContext
                .findViewById(R.id.CkWithUii_Write);
        EtTagUii_Write = (EditText) mContext.findViewById(R.id.EtTagUii_Write);

        EtAccessPwd_Write = (EditText) mContext
                .findViewById(R.id.EtAccessPwd_Write);
        BtUii_Write = (Button) mContext.findViewById(R.id.BtUii_Write);
        btnKill = (Button) mContext.findViewById(R.id.btnKill);

        BtUii_Write.setEnabled(false);
        EtTagUii_Write.setKeyListener(null);

        CkWithUii_Write.setOnClickListener(new CkWithUii_WriteClickListener());
        BtUii_Write.setOnClickListener(new BtUii_WriteClickListener());

        btnKill.setOnClickListener(new btnKillOnClickListener());

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

    public class btnKillOnClickListener implements OnClickListener {

        @Override
        public void onClick(View v) {

            String strPWD = EtAccessPwd_Write.getText().toString().trim();// 访问密码

            if (!TextUtils.isEmpty(strPWD)) {
                if (strPWD.length() != 8) {
                    UIHelper.ToastMessage(mContext,
                            R.string.uhf_msg_addr_must_len8);
                    return;
                } else if (!mContext.vailHexInput(strPWD)) {
                    UIHelper.ToastMessage(mContext,
                            R.string.rfid_mgs_error_nohex);

                    return;
                }
            } else {
                UIHelper.ToastMessage(mContext, R.string.rfid_mgs_error_nopwd);

                return;
            }

            if (CkWithUii_Write.isChecked())// 指定标签
            {

                String strUII = EtTagUii_Write.getText().toString().trim();
                if (TextUtils.isEmpty(strUII)) {
                    UIHelper.ToastMessage(mContext,
                            R.string.uhf_msg_tag_must_not_null);
                    return;
                }

                if (mContext.mReader.killTag(strPWD, strUII)) {
                    UIHelper.ToastMessage(mContext, R.string.rfid_mgs_kill_succ);

                } else {
                    UIHelper.ToastMessage(mContext, R.string.rfid_mgs_kill_fail);
                }

            } else {
                String strKillUII = mContext.mReader.killTag(strPWD);
                if (!TextUtils.isEmpty(strKillUII)) {
                    UIHelper.ToastMessage(mContext, strKillUII + " "
                            + getString(R.string.rfid_mgs_kill_succ));
                } else {
                    UIHelper.ToastMessage(mContext, R.string.rfid_mgs_kill_fail);
                }
            }
        }
    }

    public class CkWithUii_WriteClickListener implements OnClickListener {

        @Override
        public void onClick(View v) {

            EtTagUii_Write.setText("");

            if (CkWithUii_Write.isChecked()) {
                BtUii_Write.setEnabled(true);
                BtUii_Write.setVisibility(View.VISIBLE);
            } else {
                BtUii_Write.setEnabled(false);
                BtUii_Write.setVisibility(View.GONE);
            }
        }
    }

}
