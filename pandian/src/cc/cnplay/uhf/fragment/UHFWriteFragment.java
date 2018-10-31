package cc.cnplay.uhf.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;

import cc.cnplay.uhf.R;
import cc.cnplay.uhf.StringUtils;
import cc.cnplay.uhf.UHFMainActivity;
import cc.cnplay.uhf.UIHelper;

import com.rscja.deviceapi.RFIDWithUHF.BankEnum;

import com.rscja.utility.StringUtility;

public class UHFWriteFragment extends KeyDwonFragment {

    private static final String TAG = "UHFWriteFragment";

    private UHFMainActivity mContext;

    CheckBox CkWithUii_Write;
    EditText EtTagUii_Write;
    Spinner SpinnerBank_Write;
    EditText EtPtr_Write;
    EditText EtLen_Write;
    EditText EtData_Write;
    EditText EtAccessPwd_Write;
    Button BtUii_Write;
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

        CkWithUii_Write = (CheckBox) mContext
                .findViewById(R.id.CkWithUii_Write);
        EtTagUii_Write = (EditText) mContext.findViewById(R.id.EtTagUii_Write);
        SpinnerBank_Write = (Spinner) mContext
                .findViewById(R.id.SpinnerBank_Write);
        EtPtr_Write = (EditText) mContext.findViewById(R.id.EtPtr_Write);
        EtLen_Write = (EditText) mContext.findViewById(R.id.EtLen_Write);
        EtData_Write = (EditText) mContext.findViewById(R.id.EtData_Write);
        EtAccessPwd_Write = (EditText) mContext
                .findViewById(R.id.EtAccessPwd_Write);
        BtUii_Write = (Button) mContext.findViewById(R.id.BtUii_Write);
        BtWrite = (Button) mContext.findViewById(R.id.BtWrite);
        
        BtErase = (Button) mContext.findViewById(R.id.BtErase);

        BtUii_Write.setEnabled(false);
        EtTagUii_Write.setKeyListener(null);

        CkWithUii_Write.setOnClickListener(new CkWithUii_WriteClickListener());
        BtUii_Write.setOnClickListener(new BtUii_WriteClickListener());

        BtWrite.setOnClickListener(new BtWriteOnClickListener());
        BtErase.setOnClickListener(new BtEraseOnClickListener());

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
    
    public class BtEraseOnClickListener implements OnClickListener {

        @Override
        public void onClick(View v) {
        	
        	String strPtr = EtPtr_Write.getText().toString().trim();

            if (StringUtils.isEmpty(strPtr)) {
                UIHelper.ToastMessage(mContext, R.string.uhf_msg_addr_not_null);

                return;
            } else if (!StringUtility.isDecimal(strPtr)) {
                UIHelper.ToastMessage(mContext,
                        R.string.uhf_msg_addr_must_decimal);
                return;
            }

            String strPWD = EtAccessPwd_Write.getText().toString().trim();// 访问密码

            if (StringUtils.isNotEmpty(strPWD)) {
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
                strPWD = "00000000";
            }
            
            
            String cntStr = EtLen_Write.getText().toString().trim();
            if (StringUtils.isEmpty(cntStr)) {
                UIHelper.ToastMessage(mContext, R.string.uhf_msg_len_not_null);

                return;
            } else if (!StringUtility.isDecimal(cntStr)) {
                UIHelper.ToastMessage(mContext,
                        R.string.uhf_msg_len_must_decimal);

                return;
            }

           
            
            
            
            if (CkWithUii_Write.isChecked())// 指定标签
            {

                String strUII = EtTagUii_Write.getText().toString().trim();
                if (StringUtils.isEmpty(strUII)) {
                    UIHelper.ToastMessage(mContext,
                            R.string.uhf_msg_tag_must_not_null);
                    return;
                }
                
                if (mContext.mReader.eraseData(strPWD,
                        BankEnum.valueOf(SpinnerBank_Write.getSelectedItem()
                                .toString()), Integer.parseInt(strPtr), Integer
                                .valueOf(cntStr), strUII)) {
                    UIHelper.ToastMessage(mContext, "erase succ");
                } else {
                	UIHelper.ToastMessage(mContext, "erase fail");

                }

            } else {

                String strReUII = mContext.mReader.eraseData(strPWD,
                        BankEnum.valueOf(SpinnerBank_Write.getSelectedItem()
                                .toString()), Integer.parseInt(strPtr), Integer
                                .valueOf(cntStr));// 返回的UII
                if (StringUtils.isNotEmpty(strReUII)) {

                    UIHelper.ToastMessage(mContext,
                    		"erase succ" + "\nUII: "
                                    + strReUII);
                } else {
                    UIHelper.ToastMessage(mContext, "erase fail");
                }
            }
            
            
        }
        
        }
    
    

    public class BtWriteOnClickListener implements OnClickListener {

        @Override
        public void onClick(View v) {

            String strPtr = EtPtr_Write.getText().toString().trim();

            if (StringUtils.isEmpty(strPtr)) {
                UIHelper.ToastMessage(mContext, R.string.uhf_msg_addr_not_null);

                return;
            } else if (!StringUtility.isDecimal(strPtr)) {
                UIHelper.ToastMessage(mContext,
                        R.string.uhf_msg_addr_must_decimal);
                return;
            }

            String strPWD = EtAccessPwd_Write.getText().toString().trim();// 访问密码

            if (StringUtils.isNotEmpty(strPWD)) {
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
                strPWD = "00000000";
            }

            String strData = EtData_Write.getText().toString().trim();// 要写入的内容

            if (StringUtils.isEmpty(strData)) {
                UIHelper.ToastMessage(mContext,
                        R.string.uhf_msg_write_must_not_null);

                return;
            } else if (!mContext.vailHexInput(strData)) {

                UIHelper.ToastMessage(mContext, R.string.rfid_mgs_error_nohex);
                return;
            }

            // 多字单次

            String cntStr = EtLen_Write.getText().toString().trim();
            if (StringUtils.isEmpty(cntStr)) {
                UIHelper.ToastMessage(mContext, R.string.uhf_msg_len_not_null);

                return;
            } else if (!StringUtility.isDecimal(cntStr)) {
                UIHelper.ToastMessage(mContext,
                        R.string.uhf_msg_len_must_decimal);

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

            if (CkWithUii_Write.isChecked())// 指定标签
            {

                String strUII = EtTagUii_Write.getText().toString().trim();
                if (StringUtils.isEmpty(strUII)) {
                    UIHelper.ToastMessage(mContext,
                            R.string.uhf_msg_tag_must_not_null);
                    return;
                }

                if (mContext.mReader.writeData(strPWD,
                        BankEnum.valueOf(SpinnerBank_Write.getSelectedItem()
                                .toString()), Integer.parseInt(strPtr), Integer
                                .valueOf(cntStr), strData, strUII)) {
                    UIHelper.ToastMessage(mContext, R.string.uhf_msg_write_succ);
                } else {
                    UIHelper.ToastMessage(mContext, R.string.uhf_msg_write_fail);

                }

            } else {

                String strReUII = mContext.mReader.writeData(strPWD,
                        BankEnum.valueOf(SpinnerBank_Write.getSelectedItem()
                                .toString()), Integer.parseInt(strPtr), Integer
                                .valueOf(cntStr), strData);// 返回的UII
                if (StringUtils.isNotEmpty(strReUII)) {

                    UIHelper.ToastMessage(mContext,
                            getString(R.string.uhf_msg_write_succ) + "\nUII: "
                                    + strReUII);
                } else {
                    UIHelper.ToastMessage(mContext, R.string.uhf_msg_write_fail);
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
