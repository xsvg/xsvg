var ctxp = '';
(function()
{
	function getQueryParam(name)
	{
		var regex = RegExp('[?&]' + name + '=([^&]*)');

		var match = regex.exec(location.search) || regex.exec(path);
		return match && decodeURIComponent(match[1]);
	}

	function hasOption(opt, queryString)
	{
		var s = queryString || location.search;
		var re = new RegExp('(?:^|[&?])' + opt + '(?:[=]([^&]*))?(?:$|[&])', 'i');
		var m = re.exec(s);

		return m ? (m[1] === undefined || m[1] === '' ? true : m[1]) : false;
	}

	function getCookieValue(name)
	{
		var cookies = document.cookie.split('; '), i = cookies.length, cookie, value = '';

		while (i--)
		{
			cookie = cookies[i].split('=');
			if (cookie[0] === name)
			{
				value = cookie[1];
			}
		}

		return value;
	}

	function getContextPath()
	{
		var pathName = document.location.pathname;
		var index = pathName.substr(1).indexOf("/");
		var result = pathName.substr(0, index + 1);
		return result;
	}

	var scriptEls = document.getElementsByTagName('script');
	var path = scriptEls[scriptEls.length - 1].src, rtl = getQueryParam('rtl'), theme = getQueryParam('theme') || 'classic', includeCSS = !hasOption('nocss', path), neptune = (theme === 'neptune'), repoDevMode = getCookieValue('ExtRepoDevMode'), suffix = [], i = 3, neptunePath;

	rtl = rtl && rtl.toString() === 'true';
	while (i--)
	{
		path = path.substring(0, path.lastIndexOf('/'));
	}
	var domain = '';
	if (path.indexOf('//') > -1)
	{
		var tmp = path.substring(path.indexOf('//') + 2, path.length);
		if (tmp.indexOf('/') >= 0)
		{
			domain = tmp.substring(0, tmp.indexOf('/'));
		}
		else
		{
			domain = path;
		}
	}
	else
	{
		try
		{
			var str = window.location.toString();
			str = str.substring(str.indexOf('//') + 2, str.length);
			domain = str.substring(0, str.indexOf('/'));
		}
		catch (error)
		{
			alert(error);
		}
	}
	if (theme && theme !== 'classic')
	{
		suffix.push(theme);
	}
	if (rtl)
	{
		suffix.push('rtl');
	}
	suffix = (suffix.length) ? ('-' + suffix.join('-')) : '';
	// var prefix = 'http://192.168.1.170:8081/ext4.2.1';
	ctxp = getContextPath();
	var prefix = ctxp + '/static/ext4.2.1';
	if (includeCSS)
	{
		document.write('<link rel="stylesheet" type="text/css" href="' + prefix + '/resources/css/ext-all' + suffix + '.css"/>');
		document.write('<link rel="stylesheet" type="text/css" href="' + ctxp + '/static/ext-patch.css"/>');
	}
	document.write('<script type="text/javascript" src="' + prefix + '/ext-all' + (rtl ? '-rtl' : '') + '.js"></script>');
	document.write('<script type="text/javascript" src="' + prefix + '/locale/ext-lang-zh_CN.js"></script>');
	document.write('<script type="text/javascript" src="' + ctxp + '/static/shared/Common.js?_dc=' + Math.random() + '"></script>');
	// document.write('<script type="text/javascript" src="' + ctxp
	// +'/ext4/shared/options-toolbar.js"></script>');
})();