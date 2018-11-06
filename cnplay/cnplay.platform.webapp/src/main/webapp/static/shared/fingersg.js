/**
 * 指纹接口
 * 
 * @author peishaoguo@sgsg.cc
 * @version 2015-08-23
 */
function Finger()
{
	this.id = 'OcxFinger';

	this.getFingerData = function(dev)
	{
		try
		{
			var OcxFinger = document.getElementById(this.id);
			var fingerData = OcxFinger.GetFpTemplate(dev, 0, 18000);
			if (fingerData.length <= 3)
			{
				fingerData = '-1';
			}
			return fingerData;
		}
		catch (ex)
		{
			var msg = "指纹控件调用异常：" + ex.message;
			alert(msg);
			var userAgent = window.navigator.userAgent;
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

	// 登录时用，只取录一次指纹
	this.getFingerFeature = function(dev)
	{
		try
		{
			var OcxFinger = document.getElementById(this.id);
			var fingerData = '-1';
			var fingerData = OcxFinger.GetFingerFeature(dev, 0, 18000);
			if (fingerData.length <= 3)
			{
				fingerData = '-1';
			}
			return fingerData;
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
	document.write('<object classid="CLSID:03BA901A-B3A7-4CC9-816F-910A43B0FCBD" codebase="' + ctxp + '/static/control/SGFingerCollect.cab#version=1,0,2,0" id="' + finger.id + '" height="0" width="0"></object>');
})();