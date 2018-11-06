package cc.cnplay.platform.service;

import java.io.File;

import jcifs.smb.SmbFile;

import org.springframework.web.multipart.MultipartFile;

import cc.cnplay.core.service.GenericService;
import cc.cnplay.platform.domain.Attachment;
import cc.cnplay.platform.domain.User;

public interface AttachmentService extends GenericService<Attachment, String>
{

	public Attachment getAttachment(String attaId);

	Attachment saveAttachment(File path, SmbFile smbFile, MultipartFile fileItem, User user);

}
