package cc.cnplay.uhf.fragment;

import java.math.BigDecimal;

/**
 * 
 * @author Administrator
 * 
 */
public class StoreItem {


	private String id;

	private String areaId;

	private String areaName;

	private String orgId;

	private String rfid;

	private String sn;

	private String name;

	private String storeman;

	private String dywOwner;

	private String dywOwnerId;

	private String dywId;

	private String registerDate;

	private String htId;

	private String htStartDate;

	private String htEndDate;

	private String jkrxm;

	private String jkrsfz;

	private BigDecimal jkje = BigDecimal.ZERO;

	private BigDecimal pgje = BigDecimal.ZERO;

	private int status = 0;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
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

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
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

	public String getSn() {
		return sn;
	}

	public void setSn(String sn) {
		this.sn = sn;
	}

}
