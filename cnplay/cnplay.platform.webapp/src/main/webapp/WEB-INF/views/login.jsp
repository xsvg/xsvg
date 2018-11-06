<!DOCTYPE html>
<%@ page pageEncoding="UTF-8" contentType="text/html;charset=utf-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<c:set var="ctxp" value="${pageContext.request.contextPath}" />
<html>
<head>
<title>用户登录</title>
<meta name="viewport" content="width=device-width" />
<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
<meta http-equiv="X-UA-Compatible" content="IE=10" />
<link href="${ctxp}/favicon.ico" type="image/x-icon" rel="icon" />
<link href="${ctxp}/favicon.ico" type="image/x-icon" rel="Shortcut Icon" />
<link href="${ctxp}/static/css/login.css" rel="stylesheet" type="text/css" />
<script src="${ctxp}/static/jquery/jquery-1.10.1.min.js" type="text/javascript"></script>

<script src="${ctxp}/static/shared/ocxComm.js" type="text/javascript"></script>
<!-- 
<script src="${ctxp}/static/shared/${finger}" type="text/javascript"></script>
<script src="${ctxp}/static/shared/CreateControl.js" type="text/javascript"></script>
<script src="${ctxp}/static/js/base64.js" type="text/javascript"></script>
 -->
</head>
<body style='background-color: #EAEDF1;'>
	<div class="login_top">
		<div id='logo' style="float: left; width: 262px; height: 53px; margin-top: 0px; margin-left: 10px;">
			<div class="version" id="version">
				<label id="versionNO">版本号：V1.0.0</label>
			</div>
		</div>
	</div>
	<div class="login_center">
		<div id="login" class="login"></div>
		<div id="loginup" class="loginup">
			<div class="LoginWay">
				<label id="loginMsg" for="loginTypePassword">${message}</label>
			</div>
			<input id="userAccount" name="userAccount" type="text" value="" /> <input id="password" name="password" type="password" value="" /> <a class="btn_login" id="btnSubmit"></a> <a class="btn_cancel" id="btnCanel"></a>
		</div>
		<div id="loadimageid" style="display:;position: absolute; top: 50%; left: 50%; margin-left: -63px; margin-top: -99px; border: 0px;z-index: 11;">
			<img alt="加载中..." src="<c:url value='/static/icons/load.gif'/>" />
		</div>
	</div>
	<div class="login_bottom"></div>
	<script type="text/javascript">
		var fingerData = '-1';
		var deviceInfo = 1;
		var verifyFinger = false;
		var verifyPwd = true;
		function loginConf()
		{
			document.getElementById('loadimageid').style.display="block";			
			$('#password').removeAttr("disabled");
			$.post("${ctxp}/loginConf", {
				username : $("#userAccount").val()
			}, function(result)
			{
				var row = result.rows;
				deviceInfo = row.fingerDev;
				verifyFinger = row.verifyFinger;
				verifyPwd = row.verifyPwd;
				$("#loginMsg").text(row.msg);				
				if(!verifyPwd)
				{
					$('#password').attr("disabled","disabled")
					$("#btnSubmit").click();
				}else{
					document.getElementById('loadimageid').style.display="none";
					$('#password').focus();
				}	
			});			
		}
		$(document).ready(function()
		{
			$("#userAccount").focus();
			$('#userAccount').change(function(event)
			{
				loginConf();
			});
			$('#userAccount').on('keyup', function(event)
			{
				if (event.keyCode == 13)
				{
					loginConf();
				}
			});
			$('#password').on('keyup', function(event)
			{
				if (event.keyCode == 13)
				{
					if ($('#password').val() == "")
					{
						$("#loginMsg").text("请输入密码！");
						return;
					}else{
						$("#btnSubmit").click();					
					}					
				}
			});
			$('#logo').dblclick(function()
			{
				document.getElementById('loadimageid').style.display="block";
				window.location.href = '${ctxp}/logon.html';
			});
			
			$('#btnCanel').click(function()
			{
				$("#userAccount").val("");
				$("#password").val("");
				$("#userAccount").focus();
			});

			$("#btnSubmit").click(function()
			{
				document.getElementById('loadimageid').style.display="block";
				if ($("#userAccount").val() == "")
				{
					$("#loginMsg").text("用户名不能为空！");
					$('#userAccount').focus();
					return;
				}
				if (verifyFinger)
				{
					$("#loginMsg").text("请录取指纹！");
					fingerData = finger.getFingerFeature(deviceInfo);
					if (fingerData == '-1')
					{
						document.getElementById('loadimageid').style.display="none";
						$("#loginMsg").text("指纹获取失败！");
						$('#userAccount').focus();
						return;
					}
				}
				//else
				{
					var password = $("#password").val();
					var pwdEn = pwdEncoding(password);
					var passwordEncoder = (password != pwdEn);
					if(passwordEncoder === false)
					{
						//pwdEn = window.BASE64.encoder(password);
					}
					$.post("${ctxp}/loginSubmit", {
						username : $("#userAccount").val(),
						password : pwdEn,
						passwordEncoder : passwordEncoder,
						fingerData : fingerData,
						fingerDev : deviceInfo
					}, function(result)
					{
						fingerData = '';
						if (!result.success)
						{
							document.getElementById('loadimageid').style.display="none";
							$("#loginMsg").text(result.msg);
							$('#userAccount').focus();
						}
						else
						{
							document.getElementById('loadimageid').style.display="block";
							window.location.href = "${ctxp}/index.html?" + Math.random();
						}
					});
				}
			});

		});
		$(document).ready(function()
		{
			document.getElementById('loginup').style.display="";
			document.getElementById('login').style.display="";
			document.getElementById('loadimageid').style.display="none";			
			//禁止退格键 作用于Firefox、Opera   
			document.onkeypress = banBackSpace;
			//禁止退格键 作用于IE、Chrome  
			document.onkeydown = banBackSpace;
		});
		function banBackSpace(e)
		{
			//alert(event.keyCode)  
			var ev = e || window.event;//获取event对象     
			var obj = ev.target || ev.srcElement;//获取事件源       
			var t = obj.type || obj.getAttribute('type');//获取事件源类型       
			//获取作为判断条件的事件类型   
			var vReadOnly = obj.readOnly;
			var vDisabled = obj.disabled;
			//处理undefined值情况   
			vReadOnly = (vReadOnly == undefined) ? false : vReadOnly;
			vDisabled = (vDisabled == undefined) ? true : vDisabled;
			//当敲Backspace键时，事件源类型为密码或单行、多行文本的，    
			//并且readOnly属性为true或disabled属性为true的，则退格键失效    
			var flag1 = ev.keyCode == 8 && (t == "password" || t == "text" || t == "textarea") && (vReadOnly == true || vDisabled == true);
			//当敲Backspace键时，事件源类型非密码或单行、多行文本的，则退格键失效      
			var flag2 = ev.keyCode == 8 && t != "password" && t != "text" && t != "textarea";
			//判断      
			if (flag2 || flag1)
				event.returnValue = false;//这里如果写 return false 无法实现效果   
		}
	</script>
</body>
</html>