package cc.cnplay.store.domain;

import java.math.BigDecimal;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Transient;

import cc.cnplay.core.annotation.Memo;
import cc.cnplay.core.domain.SuperEntity;

/**
 * 
 * @author Administrator
 *
 */
@Entity
@Table(name = "store_check_item")
public class StoreCheckItem extends SuperEntity {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Memo("盘点记录ID")
	@Column(name = "check_id")
	private String checkId;

	@Memo("存放区")
	@Column(name = "area_id")
	private String areaId;

	@Memo("存放区")
	@Transient
	private String areaName;

	@Memo("所在网点")
	@Column(name = "org_id")
	private String orgId;

	@Memo("抵押物编号")
	@Column(name = "rfid")
	private String rfid;

	@Memo("抵押物名称")
	@Column(name = "name")
	private String name;

	@Memo("保管员")
	@Column(name = "storeman")
	private String storeman;

	@Memo("抵押物所有人姓名")
	@Column(name = "dyw_owner")
	private String dywOwner;

	@Memo("抵押物所有人身份证号")
	@Column(name = "dyw_owner_id")
	private String dywOwnerId;

	@Memo("抵押物证号码")
	@Column(name = "dyw_id")
	private String dywId;

	@Memo("登记日期")
	@Column(name = "register_date")
	private String registerDate;

	@Memo("贷款合同号")
	@Column(name = "ht_id")
	private String htId;

	@Memo("合同起始日期")
	@Column(name = "ht_start_date")
	private String htStartDate;

	@Memo("合同到期日期")
	@Column(name = "ht_end_date")
	private String htEndDate;

	@Memo("借款人姓名")
	@Column(name = "jkrxm")
	private String jkrxm;

	@Memo("借款人身份证号")
	@Column(name = "jkrsfz")
	private String jkrsfz;

	@Memo("借款金额")
	@Column(name = "jkje")
	private BigDecimal jkje = BigDecimal.ZERO;

	@Memo("评估金额")
	@Column(name = "pgje")
	private BigDecimal pgje = BigDecimal.ZERO;

	@Memo("盘点内容：1.盘点到的数据，0.盘不到的数据")
	@Column(name = "check_flag")
	private Integer checkFlag = 1;
	
	public final static int CHECKFLAG_OK = 1;
	public final static int CHECKFLAG_NOT = 0;

	public String getCheckId() {
		return checkId;
	}

	public void setCheckId(String checkId) {
		this.checkId = checkId;
	}

	public String getAreaId() {
		return areaId;
	}

	public void setAreaId(String areaId) {
		this.areaId = areaId;
	}

	public String getOrgId() {
		return orgId;
	}

	public void setOrgId(String orgId) {
		this.orgId = orgId;
	}

	public String getStoreman() {
		return storeman;
	}

	public void setStoreman(String storeman) {
		this.storeman = storeman;
	}

	public String getDywOwner() {
		return dywOwner;
	}

	public void setDywOwner(String dywOwner) {
		this.dywOwner = dywOwner;
	}

	public String getDywOwnerId() {
		return dywOwnerId;
	}

	public void setDywOwnerId(String dywOwnerId) {
		this.dywOwnerId = dywOwnerId;
	}

	public String getDywId() {
		return dywId;
	}

	public void setDywId(String dywId) {
		this.dywId = dywId;
	}

	public String getRegisterDate() {
		return registerDate;
	}

	public void setRegisterDate(String registerDate) {
		this.registerDate = registerDate;
	}

	public String getHtId() {
		return htId;
	}

	public void setHtId(String htId) {
		this.htId = htId;
	}

	public String getHtStartDate() {
		return htStartDate;
	}

	public void setHtStartDate(String htStartDate) {
		this.htStartDate = htStartDate;
	}

	public String getHtEndDate() {
		return htEndDate;
	}

	public void setHtEndDate(String htEndDate) {
		this.htEndDate = htEndDate;
	}

	public String getJkrxm() {
		return jkrxm;
	}

	public void setJkrxm(String jkrxm) {
		this.jkrxm = jkrxm;
	}

	public String getJkrsfz() {
		return jkrsfz;
	}

	public void setJkrsfz(String jkrsfz) {
		this.jkrsfz = jkrsfz;
	}

	public BigDecimal getJkje() {
		return jkje;
	}

	public void setJkje(BigDecimal jkje) {
		this.jkje = jkje;
	}

	public BigDecimal getPgje() {
		return pgje;
	}

	public void setPgje(BigDecimal pgje) {
		this.pgje = pgje;
	}

	public String getAreaName() {
		return areaName;
	}

	public void setAreaName(String areaName) {
		this.areaName = areaName;
	}

	public String getRfid() {
		return rfid;
	}

	public void setRfid(String rfid) {
		this.rfid = rfid;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Integer getCheckFlag() {
		return checkFlag;
	}

	public void setCheckFlag(Integer checkFlag) {
		this.checkFlag = checkFlag;
	}

}
