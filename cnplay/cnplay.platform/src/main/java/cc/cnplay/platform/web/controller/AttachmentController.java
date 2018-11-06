package cc.cnplay.platform.web.controller;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.util.Iterator;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import jcifs.UniAddress;
import jcifs.smb.NtlmPasswordAuthentication;
import jcifs.smb.SmbFile;
import jcifs.smb.SmbFileInputStream;
import jcifs.smb.SmbSession;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.multipart.commons.CommonsMultipartResolver;

import cc.cnplay.core.vo.Json;
import cc.cnplay.platform.Constants;
import cc.cnplay.platform.domain.Attachment;
import cc.cnplay.platform.service.AttachmentService;
import cc.cnplay.platform.service.SystemConfigService;

import com.fasterxml.jackson.databind.ObjectMapper;

@Controller
@RequestMapping("/home")
public class AttachmentController extends AbsController
{
	protected final static Logger logger = Logger.getLogger(AttachmentController.class);

	@Autowired
	private AttachmentService attachmentService;

	@Autowired
	private SystemConfigService configService;

	public static String uploadPathType;
	public static String path;
	public static String smbUser;
	public static String smbPwd;
	public static File serverPath;
	public static String domainIp;
	public static NtlmPasswordAuthentication authentication;
	public static SmbFile smbFile;
	public static boolean configCorrect = false;

	private void init()
	{
		try
		{
			if (configCorrect)
			{
				return;
			}
			uploadPathType = configService.getByName("upload.path.type");
			path = configService.getByName("upload.path");
			domainIp = configService.getByName("upload.smb.domain.ip");
			smbUser = configService.getByName("upload.smb.user");
			smbPwd = configService.getByName("upload.smb.pwd");
			if (StringUtils.equals(uploadPathType, "0"))
			{
				if (!StringUtils.isEmpty(path))
				{
					serverPath = new File(path);
					if (!serverPath.exists())
					{
						serverPath.mkdirs();
					}
				}
			}
			else
			{

				InetAddress address = InetAddress.getByName(domainIp);
				UniAddress uniAddress = new UniAddress(address);
				authentication = new NtlmPasswordAuthentication(domainIp, smbUser, smbPwd);
				SmbSession.logon(uniAddress, authentication);
				smbFile = new SmbFile(path, authentication);
				if (!smbFile.exists())
				{
					logger.error("配置上传文件保存共享路径错误:{path=" + path + ",user=" + smbUser + "+password=" + smbPwd + ",domain=" + domainIp + "}");
				}
			}
			configCorrect = true;
		}
		catch (Exception e)
		{
			logger.error("配置上传文件保存共享路径错误:{path=" + path + ",user=" + smbUser + "+password=" + smbPwd + ",domain=" + domainIp + "}");
		}
		if (!configCorrect)
		{
			uploadPathType = "0";
			path = Constants.webappAbsolutePath + File.separator + "upload";
			if (!StringUtils.isEmpty(path))
			{
				serverPath = new File(path);
				if (!serverPath.exists())
				{
					serverPath.mkdirs();
				}
			}
			configCorrect = true;
		}
		logger.error("上传文件保存路径：" + path);
	}

	@RequestMapping(value = "/upload")
	@ResponseBody
	public String upload(HttpServletRequest request, HttpServletResponse response) throws Throwable
	{
		init();
		Json<Attachment> json = new Json<Attachment>();
		try
		{
			CommonsMultipartResolver multipartResolver = new CommonsMultipartResolver(request.getSession().getServletContext());
			if (multipartResolver.isMultipart(request))
			{

				MultipartHttpServletRequest multiRequest = (MultipartHttpServletRequest) request;
				Iterator<MultipartFile> iterator = multiRequest.getFileMap().values().iterator();
				while (iterator.hasNext())
				{
					MultipartFile item = iterator.next();
					if (!item.isEmpty())
					{
						Attachment atta = null;
						if (StringUtils.equals(uploadPathType, "0"))
						{
							atta = attachmentService.saveAttachment(serverPath, null, item, getSessionUser());
						}
						else
						{
							atta = attachmentService.saveAttachment(null, smbFile, item, getSessionUser());
						}
						json.OK(atta, "");
						break;
					}
				}
			}
		}
		catch (Exception e)
		{
			json.setSuccess(false);
			json.setMsg("文件上传失败");
			logger.error("文件上传失败", e);
		}
		response.setCharacterEncoding("utf-8");
		response.setContentType("text/html; charset=utf-8");
		ObjectMapper om = new ObjectMapper();
		om.writeValue(response.getOutputStream(), json);
		// return json;
		return null;
	}

	@RequestMapping(value = "/download")
	@ResponseBody
	public String dowload(String id, boolean alt)
	{
		init();
		InputStream in = null;
		OutputStream out = null;
		try
		{
			HttpServletResponse response = getResponse();
			response.setCharacterEncoding("utf-8");
			response.setContentType("text/html; charset=utf-8");
			Attachment atta = attachmentService.getAttachment(id);
			if (atta != null)
			{
				response.setContentType(atta.getContentType());
				String fileName = atta.getName();
				if (alt)
				{
					response.setHeader("Content-Disposition", "attachment;fileName=" + new String(fileName.getBytes("gbk"), "iso-8859-1"));
				}
				if (StringUtils.equals(uploadPathType, "0"))
				{
					in = new FileInputStream(new File(serverPath, atta.getId() + "." + atta.getSuffix()));
				}
				else
				{
					SmbFile saveFile = new SmbFile(smbFile, atta.getId() + "." + atta.getSuffix());
					in = new SmbFileInputStream(saveFile);
				}
				out = response.getOutputStream();
				byte[] buffer = new byte[1024];
				int i = -1;
				while ((i = in.read(buffer)) != -1)
				{
					out.write(buffer, 0, i);
				}
				out.flush();
			}

		}
		catch (Exception e)
		{
			logger.error("文件下载失败" + e.getMessage());
		}
		finally
		{
			IOUtils.closeQuietly(in);
			IOUtils.closeQuietly(out);
		}
		return null;
	}

}
