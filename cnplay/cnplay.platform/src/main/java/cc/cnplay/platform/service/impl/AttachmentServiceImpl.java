package cc.cnplay.platform.service.impl;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

import javax.transaction.Transactional;

import jcifs.smb.SmbFile;
import jcifs.smb.SmbFileOutputStream;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import cc.cnplay.core.spring.dao.UniversalDao;
import cc.cnplay.core.spring.service.AbsGenericService;
import cc.cnplay.platform.domain.Attachment;
import cc.cnplay.platform.domain.User;
import cc.cnplay.platform.service.AttachmentService;

@Service
@Transactional
public class AttachmentServiceImpl extends AbsGenericService<Attachment, String> implements AttachmentService
{

	@Autowired
	private UniversalDao dao;

	public Attachment getAttachment(String attaId)
	{
		return dao.getById(Attachment.class, attaId);
	}

	@Override
	public Attachment saveAttachment(File path, SmbFile smbFile, MultipartFile fileItem, User user)
	{
		InputStream in = null;
		OutputStream ou = null;
		try
		{
			in = fileItem.getInputStream();
			String fileName = fileItem.getOriginalFilename();
			String suffix = FilenameUtils.getExtension(fileName);
			//fileName = FilenameUtils.getBaseName(fileName) + "." + suffix;
			long fileSize = fileItem.getSize();
			Attachment attachment = new Attachment();
			attachment.setName(fileName);
			attachment.setSize(fileSize);
			attachment.setSuffix(suffix);
			attachment.setContentType(fileItem.getContentType());
			attachment.setUser(user);			
			if (path != null)
			{
				File saveFile = new File(path, attachment.getId() + "." + attachment.getSuffix());
				ou = new BufferedOutputStream(new FileOutputStream(saveFile));
			}
			else
			{
				SmbFile saveFile = new SmbFile(smbFile, attachment.getId() + "." + attachment.getSuffix());
				ou = new BufferedOutputStream(new SmbFileOutputStream(saveFile));
			}
			byte[] buffer = new byte[1024];
			while (in.read(buffer) != -1)
			{
				ou.write(buffer);
				buffer = new byte[1024];
			}
			dao.save(attachment);
			return attachment;
		}
		catch (Exception e)
		{

			return null;
		}
		finally
		{
			IOUtils.closeQuietly(in);
			IOUtils.closeQuietly(ou);
		}

	}
}
