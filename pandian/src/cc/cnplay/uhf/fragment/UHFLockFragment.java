package cc.cnplay.uhf.fragment;

import cc.cnplay.uhf.R;
import cc.cnplay.uhf.UHFMainActivity;
import cc.cnplay.uhf.UIHelper;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RadioButton;



public class UHFLockFragment extends KeyDwonFragment {

    private static final String TAG = "UHFLockFragment";

    private UHFMainActivity mContext;

    EditText EtTagUii_Write;
    EditText EtAccessPwd_Write;
    Button btnReadEpc;
    Button btnLock;
    EditText etLockCode;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.uhf_lock_fragment, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        mContext = (UHFMainActivity) getActivity();


        EtTagUii_Write = (EditText) mContext.findViewById(R.id.EtTagUii_Write);
        etLockCode = (EditText) mContext.findViewById(R.id.etLockCode);
        EtAccessPwd_Write = (EditText) mContext
                .findViewById(R.id.EtAccessPwd_Write);
        btnReadEpc = (Button) mContext.findViewById(R.id.btnReadEpc);
        btnLock = (Button) mContext.findViewById(R.id.btnLock);

        etLockCode.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {

                AlertDialog.Builder builder = new AlertDialog.Builder(mContext);

                builder.setTitle(R.string.tvLockCode);
                final View vv = LayoutInflater.from(mContext).inflate(R.layout.uhf_dialog_lock_code, null);
                builder.setView(vv);

                builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                });


                builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        RadioButton rbTemp = (RadioButton) vv.findViewById(R.id.rbTemp);
                        CheckBox cbKillRead = (CheckBox) vv.findViewById(R.id.cbKillRead);
                        CheckBox cbKillWrite = (CheckBox) vv.findViewById(R.id.cbKillWrite);
                        CheckBox cbAccessRead = (CheckBox) vv.findViewById(R.id.cbAccessRead);
                        CheckBox cbAccessWrite = (CheckBox) vv.findViewById(R.id.cbAccessWrite);
                        CheckBox cbUIIRead = (CheckBox) vv.findViewById(R.id.cbUIIRead);
                        CheckBox cbUIIWrite = (CheckBox) vv.findViewById(R.id.cbUIIWrite);
                        CheckBox cbTidRead = (CheckBox) vv.findViewById(R.id.cbTidRead);
                        CheckBox cbTidWrite = (CheckBox) vv.findViewById(R.id.cbTidWrite);
                        CheckBox cbUserRead = (CheckBox) vv.findViewById(R.id.cbUserRead);
                        CheckBox cbUserWrite = (CheckBox) vv.findViewById(R.id.cbUserWrite);

                        String mask = "";
                        String value = "";

                        //临时掩码
                        if (rbTemp.isChecked()) {
                            mask += cbKillRead.isChecked() ? "10" : "00";
                            mask += cbAccessRead.isChecked() ? "10" : "00";
                            mask += cbUIIRead.isChecked() ? "10" : "00";
                            mask += cbTidRead.isChecked() ? "10" : "00";
                            mask += cbUserRead.isChecked() ? "10" : "00";

                            value += cbKillWrite.isChecked() ? "10" : "00";
                            value += cbAccessWrite.isChecked() ? "10" : "00";
                            value += cbUIIWrite.isChecked() ? "10" : "00";
                            value += cbTidWrite.isChecked() ? "10" : "00";
                            value += cbUserWrite.isChecked() ? "10" : "00";

                        } else {
                            mask += cbKillRead.isChecked() ? "01" : "00";
                            mask += cbAccessRead.isChecked() ? "01" : "00";
                            mask += cbUIIRead.isChecked() ? "01" : "00";
                            mask += cbTidRead.isChecked() ? "01" : "00";
                            mask += cbUserRead.isChecked() ? "01" : "00";

                            value += cbKillWrite.isChecked() ? "01" : "00";
                            value += cbAccessWrite.isChecked() ? "01" : "00";
                            value += cbUIIWrite.isChecked() ? "01" : "00";
                            value += cbTidWrite.isChecked() ? "01" : "00";
                            value += cbUserWrite.isChecked() ? "01" : "00";


                        }

                        Log.i(TAG, "mask + value=" + mask + value);

                        String code = "0" + String.format("%5s", Integer.toHexString(Integer.valueOf(mask + value, 2)));

                        Log.i(TAG, "  code=" + code);

                        etLockCode.setText(code.replace(" ", "0") + "");


                    }
                });


                builder.create().show();
            }
        });


//        etLockCode.setOnClickListener(new OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                AlertDialog.Builder builder= new AlertDialog.Builder(mContext);
//
//                builder.setTitle(R.string.tvLockCode);
//                builder.create().show();
//            }
//        });

        btnReadEpc.setOnClickListener(new BtUii_WriteClickListener());

        btnLock.setOnClickListener(new btnLockOnClickListener());

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

    public class btnLockOnClickListener implements OnClickListener {

        @Override
        public void onClick(View v) {

            String strPWD = EtAccessPwd_Write.getText().toString().trim();// 访问密码

            String strLockCode = etLockCode.getText().toString().trim();


            String strUII = EtTagUii_Write.getText().toString().trim();
            if (TextUtils.isEmpty(strUII)) {
                UIHelper.ToastMessage(mContext,
                        R.string.uhf_msg_tag_must_not_null);
                return;
            }


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

            if (!TextUtils.isEmpty(strLockCode)) {
                UIHelper.ToastMessage(mContext, R.string.rfid_mgs_error_nolockcode);

                return;
            }


            if (mContext.mReader.lockMem(strPWD, strLockCode, strUII)) {
                UIHelper.ToastMessage(mContext, R.string.rfid_mgs_lock_succ);

            } else {
                UIHelper.ToastMessage(mContext, R.string.rfid_mgs_lock_fail);

            }

        }
    }


}
