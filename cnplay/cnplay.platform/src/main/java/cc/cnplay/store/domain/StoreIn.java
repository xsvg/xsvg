package cc.cnplay.store.domain;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import cc.cnplay.core.annotation.Memo;
import cc.cnplay.core.domain.SuperCheckEntity;

@Entity
@Table(name = "store_in")
public class StoreIn extends SuperCheckEntity {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Memo("资料信息")
	@Column(name = "item_id")
	private String itemId;

	@Memo("经办人")
	@Column(name = "operator")
	private String operator;

	@Memo("审批时间")
	@Column(name = "audit_date")
	private Date autidDate = new Date();

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
