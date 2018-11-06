/**
 * 兼容渤海银行和天成农行指纹接口
 * 
 * @author peishaoguo@sgsg.cc
 * @version 2015-08-23
 */
function Finger()
{
	this.id = 'OcxFinger';

	this.getFingerData = function()
	{
		try
		{
			var bohaiFinger = document.getElementById('bohaiFinger');
			var nRet = bohaiFinger.TcWhereAreu(0, 16, 3, 0, "");
			if (nRet >= 0)
			{
				// logger.debug("发现渤海银行的指纹仪");
				bohaiFinger.nSucStrFmtTyp = 1; // 0x30 拆分格式
				bohaiFinger.nComProtocol = 2; // 商行协议
				bohaiFinger.nUsbProtocol = 0; // USB协议
				bohaiFinger.nRegChkToLv = 1; // 是否检测抬起
				bohaiFinger.nRegShowDlg = 1; // 是否显示 注册指纹模板时的对话框
				bohaiFinger.nVerShowDlg = 1; // 是否显示 验证指纹特征时的对话框
				bohaiFinger.nNotSpeedUp = 4; // 是否禁用自动提速 0-启用自动提速，
				// 4=固定9600波特率
				bohaiFinger.nComShwOnOcx = 10;
				nRet = bohaiFinger.FPIGetTemplate(0, 18000);
				if (nRet)
				{
					return '-1';
					// alert("获取指纹模版失败");
				}
				else
				{
					return bohaiFinger.FPIGetFingerInfo();
					// alert("获取指纹模版成功！");
				}
			}
			else
			{
				// logger.debug("发现USB设备的指纹仪");
				var OcxFinger = document.getElementById(this.id);
				var fingerData = OcxFinger.Func_GetFingerTemplate();
				if (fingerData == "-1")
				{
					// alert("操作提示","获取指纹模版失败！");
				}
				return fingerData;
			}
		}
		catch (ex)
		{
			var msg = "指纹控件调用异常：" + ex.message;
			var userAgent = window.navigator.userAgent;
			if (userAgent.indexOf("MSIE") >= 0)
			{
				alert(msg);
			}
			else
			{
				console.log(msg);
			}
			return "-1";
		}
	};

	//登录时用，只取录一次指纹
	this.getFingerFeature = function()
	{
		try
		{
			var bohaiFinger = document.getElementById('bohaiFinger');
			var nRet = bohaiFinger.TcWhereAreu(0, 16, 3, 0, "");
			if (nRet >= 0)
			{
				// logger.debug("发现渤海银行的指纹仪");
				bohaiFinger.nSucStrFmtTyp = 1; // 0x30 拆分格式
				bohaiFinger.nComProtocol = 2; // 商行协议
				bohaiFinger.nUsbProtocol = 0; // USB协议
				bohaiFinger.nRegChkToLv = 1; // 是否检测抬起
				bohaiFinger.nRegShowDlg = 1; // 是否显示 注册指纹模板时的对话框
				bohaiFinger.nVerShowDlg = 1; // 是否显示 验证指纹特征时的对话框
				bohaiFinger.nNotSpeedUp = 4; // 是否禁用自动提速 0-启用自动提速，
				// 4=固定9600波特率
				bohaiFinger.nComShwOnOcx = 10;
				nRet = bohaiFinger.FPIGetFeature(0, 18000);
				if (nRet)
				{
					return '-1';
					// alert("获取指纹模版失败");
				}
				else
				{
					return bohaiFinger.FPIGetFingerInfo();
					// alert("获取指纹模版成功！");
				}
			}
			else
			{
				// logger.debug("发现USB设备的指纹仪");
				var OcxFinger = document.getElementById(this.id);
				var fingerData = OcxFinger.Func_GetFingerFeature();
				if (fingerData == "-1")
				{
					// alert("操作提示","获取指纹模版失败！");
				}
				return fingerData;
			}
		}
		catch (ex)
		{
			var msg = "指纹控件调用异常：" + ex.message;
			var userAgent = window.navigator.userAgent;
			if (userAgent.indexOf("MSIE") >= 0)
			{
				alert(msg);
			}
			else
			{
				console.log(msg);
			}
			return "-1";
		}
	};
	
	this.getFingerDev = function()
	{
		var bohaiFinger = document.getElementById('bohaiFinger');
		var nRet = bohaiFinger.TcWhereAreu(0, 16, 3, 0, "");
		if (nRet >= 0)
		{
			// logger.debug("发现渤海银行的指纹仪");
			return '1';
		}
		else
		{
			var OcxFinger = document.getElementById(this.id);
			return OcxFinger.Func_GetProductInfo();
		}
	}
}
var finger = new Finger();
(function()
{
	var pathName = document.location.pathname;
	var index = pathName.substr(1).indexOf("/");
	var ctxp = pathName.substr(0, index + 1);
	document.write('<object classid="CLSID:1C46791B-9C87-4497-8662-EC3C6AE931B9" codebase="' + ctxp + '/static/control/Finger.cab#version=1,0,0,1" id="' + finger.id + '" height="0" width="0"></object>');
	document.write('<object classid="CLSID:94793CDE-C768-449B-BE87-40147B56032D" codebase="' + ctxp + '/static/control/fingerbohai.cab#version=8,0,0,7" id="bohaiFinger" width="0" height="0"></object>');
})();