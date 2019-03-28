using System;
using System.Collections.Generic;
using System.ComponentModel;
using System.Management;
using System.Runtime.InteropServices;
using System.Text;
using System.Timers;
using System.Windows.Forms;

namespace UHFActiveX
{
    public delegate void RecvMsgHandler(string msg);
    public delegate void MessageHandler();

    [ComVisible(true)]
    [GuidAttribute("1A585C4D-3371-48dc-AF8A-AFFECC1B0967")]
    [InterfaceTypeAttribute(ComInterfaceType.InterfaceIsIDispatch)]
    public interface ControlEvents
    {

        [DispIdAttribute(0x000)]
        void OnMessage();
        [DispIdAttribute(0x001)]
        void OnRecvMsg(string msg);
    }

    [ComVisible(true)]
    //将事件接收接口连接到托管类
    [ComSourceInterfacesAttribute(typeof(ControlEvents))]
    [Guid("1B5537B1-C9BF-48A3-82AD-A8425F298888")]
    public class UHFActiveX : ActiveXControl
    {
        public event RecvMsgHandler OnRecvMsg;
        public event MessageHandler OnMessage;

        private System.Timers.Timer timer = new System.Timers.Timer();
        private int port = 0;
        private int frmcomportindex = 0;
        private byte fComAdr = 0xff;

        public UHFActiveX()
        {
            timer.Enabled = false;
            timer.Interval = 200;
            timer.AutoReset = true;
            timer.Elapsed += new System.Timers.ElapsedEventHandler(TimerElapsed);
        }

        public string RFID { set; get; }

        public string GetRFID()
        {
            return RFID;
        }

        public void OnEvent(string value)
        {
            try
            {
                if (this.InvokeRequired)
                {
                    RecvMsgHandler d = new RecvMsgHandler(OnEvent);
                    this.Invoke(d, new object[] { value });
                }
                else
                {
                    if (OnMessage != null)
                    {
                        OnMessage.Invoke();
                    }
                    if (OnRecvMsg != null)
                    {
                        OnRecvMsg.Invoke(value);
                    }
                }
            }
            catch
            {
                
            }
        }

        private void TimerElapsed(object sender, System.Timers.ElapsedEventArgs e)
        {
            
            try
            {
                string rfid = Inventory();
                if (rfid != null && rfid.Length == 26)
                {
                    RFID = rfid.Substring(2, 24);
                    OnEvent(RFID);
                }
            }
            catch (Exception ex)
            {
                //RFID = ex.Message;
                StopRead();
            }

        }

        public string GetMacAddress()
        {
            var mc = new ManagementClass("Win32_NetworkAdapterConfiguration");
            var mos = mc.GetInstances();
            var sb = new StringBuilder();

            foreach (ManagementObject mo in mos)
            {
                var macAddress = mo["MacAddress"];

                if (macAddress != null)
                    sb.AppendLine("M;" + (macAddress.ToString()));
            }

            return sb.ToString();
        }

        public string StartRead()
        {
            try
            {
                RFID = "";
                byte fBaud = 5;
                StopRead();
                int openresult = ReaderB.StaticClassReaderB.AutoOpenComPort(ref port, ref fComAdr, fBaud, ref frmcomportindex);
                timer.Enabled = true;
                timer.Start();
                return "" + openresult;
            }
            catch (Exception ex)
            {
                return ex.Message;
            }

        }

        public string StopRead()
        {
            try
            {
                timer.Enabled = false;
                timer.Stop();
                int fCmdRet = ReaderB.StaticClassReaderB.CloseSpecComPort(port);
                return "" + fCmdRet;
            }
            catch(Exception ex)
            {
                return ex.Message;
            }
        }
       
        public string Inventory()
        {
            string rfid = "";
            try
            {
                int CardNum = 0;
                int Totallen = 0;
                byte[] EPC = new byte[5000];
                string temps;
                byte AdrTID = 0;
                byte LenTID = 0;
                byte TIDFlag = 0;
                int fCmdRet = ReaderB.StaticClassReaderB.Inventory_G2(ref fComAdr, AdrTID, LenTID, TIDFlag, EPC, ref Totallen, ref CardNum, frmcomportindex);
                if ((fCmdRet == 1) | (fCmdRet == 2) | (fCmdRet == 3) | (fCmdRet == 4) | (fCmdRet == 0xFB))//代表已查找结束，
                {
                    byte[] daw = new byte[Totallen];
                    Array.Copy(EPC, daw, Totallen);
                    temps = ByteArrayToHexString(daw);
                    if (temps.Trim().Length > 0)
                    {
                        rfid = temps;
                    }
                }
                if (rfid == null)
                {
                    rfid = "";
                }
            }
            catch (Exception ex)
            {
                rfid = ex.ToString();
            }
            return rfid.Trim();
        }

        private string ByteArrayToHexString(byte[] data)
        {
            StringBuilder sb = new StringBuilder(data.Length * 3);
            foreach (byte b in data)
                sb.Append(Convert.ToString(b, 16).PadLeft(2, '0'));
            return sb.ToString().ToUpper();

        }

    }
}