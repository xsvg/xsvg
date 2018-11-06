package cc.cnplay.store.domain;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import cc.cnplay.core.annotation.Memo;
import cc.cnplay.core.domain.Identity;

/**
 * 
 * 标签号
 * 
 * @author peixere@qq.com
 * 
 * @version 2013-08-13
 * 
 */
@Entity
@Table(name = "store_sn")
public class StoreSn extends Identity implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Memo("序号生成时间")
	@Column(name = "sn_time")
	private String snTime;

	@Memo("抵押物编号")
	@Column(name = "rfid", unique = true)
	private String rfid;

	@Memo("序号")
	@Column(name = "store_sn", unique = true)
	private int storeSn;

	public String getSnTime() {
		return snTime;
	}

	public void setSnTime(String snTime) {
		this.snTime = snTime;
	}

	public String getRfid() {
		return rfid;
	}

	public void setRfid(String rfid) {
		this.rfid = rfid;
	}

	public int getStoreSn() {
		return storeSn;
	}

	public void setStoreSn(int storeSn) {
		this.storeSn = storeSn;
	}

}
