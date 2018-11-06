package cc.cnplay.store.domain;

import java.util.Date;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Transient;

import cc.cnplay.core.annotation.Memo;
import cc.cnplay.core.domain.SuperCheckEntity;

/**
 * 盘点记录
 * 
 * @author Administrator
 *
 */
@Entity
@Table(name = "store_check")
public class StoreCheck extends SuperCheckEntity {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Memo("盘点时间")
	@Column(name = "check_date")
	private Date checkDate = new Date();

	@Memo("经办人")
	@Column(name = "operator")
	private String operator;

	@Memo("所在网点")
	@Column(name = "org_id")
	private String orgId;

	@Memo("网点名称")
	@Column(name = "org_name")
	private String orgName;

	@Memo("报警信息")
	@Column(name = "warn_message", length = 1000)
	private String warnMessage;

	@Memo("库存总数")
	@Column(name = "count_store")
	private int countStore;

	@Memo("盘点总数")
	@Column(name = "count_check")
	private int countCheck;

	@Memo("盘点明细")
	@Transient
	private List<StoreCheckItem> itemList;

	public Date getCheckDate() {
		return checkDate;
	}

	public void setCheckDate(Date checkDate) {
		this.checkDate = checkDate;
	}

	public String getOperator() {
		return operator;
	}

	public void setOperator(String operator) {
		this.operator = operator;
	}

	public String getOrgId() {
		return orgId;
	}

	public void setOrgId(String orgId) {
		this.orgId = orgId;
	}

	public String getOrgName() {
		return orgName;
	}

	public void setOrgName(String orgName) {
		this.orgName = orgName;
	}

	public String getWarnMessage() {
		return warnMessage;
	}

	public void setWarnMessage(String warnMessage) {
		this.warnMessage = warnMessage;
	}

	public int getCountStore() {
		return countStore;
	}

	public void setCountStore(int countStore) {
		this.countStore = countStore;
	}

	public int getCountCheck() {
		return countCheck;
	}

	public void setCountCheck(int countCheck) {
		this.countCheck = countCheck;
	}

	public List<StoreCheckItem> getItemList() {
		return itemList;
	}

	public void setItemList(List<StoreCheckItem> itemList) {
		this.itemList = itemList;
	}

}
