package cc.cnplay.store.domain;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import cc.cnplay.core.annotation.Memo;
import cc.cnplay.core.domain.SuperCheckEntity;

@Entity
@Table(name = "store_out")
public class StoreOut extends SuperCheckEntity {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Memo("身份证原件确认")
	@Column(name = "confirm_id")
	private Boolean confirmId = false;

	@Memo("抵押凭证客户联是否收回")
	@Column(name = "take_Back_Certificate")
	private Boolean takeBackCertificate = false;

	@Memo("贷款是否还清")
	@Column(name = "paid_off")
	private Boolean paidOff = false;

	@Memo("是否本人签名")
	@Column(name = "sign_off")
	private Boolean selfSign = false;

	@Memo("资料信息")
	@Column(name = "item_id")
	private String itemId;

	@Memo("经办人")
	@Column(name = "operator")
	private String operator;

	@Memo("审批时间")
	@Column(name = "audit_date")
	private Date autidDate = new Date();;

	public Boolean getConfirmId() {
		return confirmId;
	}

	public void setConfirmId(Boolean confirmId) {
		this.confirmId = confirmId;
	}

	public Boolean getTakeBackCertificate() {
		return takeBackCertificate;
	}

	public void setTakeBackCertificate(Boolean takeBackCertificate) {
		this.takeBackCertificate = takeBackCertificate;
	}

	public Boolean getPaidOff() {
		return paidOff;
	}

	public void setPaidOff(Boolean paidOff) {
		this.paidOff = paidOff;
	}

	public Boolean getSelfSign() {
		return selfSign;
	}

	public void setSelfSign(Boolean selfSign) {
		this.selfSign = selfSign;
	}

	public String getItemId() {
		return itemId;
	}

	public void setItemId(String itemId) {
		this.itemId = itemId;
	}

	public String getOperator() {
		return operator;
	}

	public void setOperator(String operator) {
		this.operator = operator;
	}

	public Date getAutidDate() {
		return autidDate;
	}

	public void setAutidDate(Date autidDate) {
		this.autidDate = autidDate;
	}

}
