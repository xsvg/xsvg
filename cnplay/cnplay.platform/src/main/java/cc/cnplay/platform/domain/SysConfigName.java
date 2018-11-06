package cc.cnplay.platform.domain;

import cc.cnplay.core.annotation.Memo;

public abstract class SysConfigName
{

	public static final String module_runtime = "运行参数";

	@Memo("未受控资源")
	public static final String authorization_none = "authorization_none";
	@Memo("登录未受控资源")
	public static final String authorization_login = "authorization_login";
	public static final String authorization_login_not_tip = "authorization_login_not_tip";
	@Memo("登录受控资源")
	public static final String authorization_check = "authorization_check";
	public static final String authorization_check_not_tip = "authorization_check_not_tip";
	public static final String home_logo_img = "home_logo_img";
	public static final String home_logobg_img = "home_logobg_img";
	public static final String home_font_style = "home_font_style";
	public static final String home_logout_text = "home_logout_text";
	public static final String home_desktop_panel = "home_desktop_panel";
	public static final String system_name = "system_name";
	public static final String system_title = "system_title";
	public static final String system_installed = "system_installed";
	public static final String system_debug = "system_debug";

	public static final String Login_Try_No = "login_try_no";
	public static final String admin_login_type = "admin_login_type";
	public static final String UpdatePwdNeed = "updatePwdNeed";
	@Memo("复核配置：0.根据功能配置；1.全部需要复核；2.全部不需要复核")
	public static final String AllNeedCheck = "AllNeedCheck";

	// 运行参数KEY
	public static final String userCodeMinLength = "userCodeMinLength";
	public static final String userCodeMaxLength = "userCodeMaxLength";
	public static final String smsDevice = "sms.device";
	public static final String smsDriver = "sms.driver";
	public static final String fingerDriver = "finger.driver";

}
