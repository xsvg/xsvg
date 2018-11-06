/**
 * 指纹接口
 * 
 * @author peishaoguo@sgsg.cc
 * @version 2015-08-23
 */
function Finger()
{
	this.id = 'OcxFinger';

	// 登录时用，只取录一次指纹
	this.getFingerFeature = function(dev)
	{
		try
		{
			var OcxFinger = document.getElementById(this.id);
			OcxFinger.nSucStrFmtTyp = 1;
			OcxFinger.nUsbProtocol = 0;
			OcxFinger.nNotSpeedUp = 4;	
			var nRet = OcxFinger.TcWhereAreu(0, 16, 3, 0 , "");
			if (nRet>=0) {
				nRet = OcxFinger.FPIGetFeature(nRet, 10000);
				if (!nRet) {
					return OcxFinger.FPIGetFingerInfo();
				}
			} else {
				alert("没有发现设备");
			}
			return "-1";
		}
		catch (ex)
		{
			var msg = "指纹控件调用异常：" + ex.message;
			var userAgent = window.navigator.userAgent;
			alert(msg);
			if (userAgent.indexOf("MSIE") >= 0)
			{
				// alert(msg);
			}
			else
			{
				console.log(msg);
			}
			return "-1";
		}
	};
}
var finger = new Finger();
(function()
{
	var pathName = document.location.pathname;
	var index = pathName.substr(1).indexOf("/");
	var ctxp = pathName.substr(0, index + 1);
	document.write('<object classid="CLSID:94793CDE-C768-449B-BE87-40147B56032D" codebase="' + ctxp + '/static/control/libFPDev_TESO.ocx" id="' + finger.id + '" height="0" width="0"></object>');
})();