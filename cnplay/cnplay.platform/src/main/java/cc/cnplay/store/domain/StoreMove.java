package cc.cnplay.store.domain;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Transient;

import cc.cnplay.core.annotation.Memo;
import cc.cnplay.core.domain.SuperCheckEntity;

@Entity
@Table(name = "store_move")
public class StoreMove extends SuperCheckEntity {

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

	@Memo("接收人")
	@Column(name = "moveto")
	private String moveto;

	@Memo("转移时间")
	@Column(name = "move_date")
	private Date moveDate = new Date();

	@Transient
	private transient String moveDates;

	@Transient
	private transient String[] itemIds;

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

	public String getMoveto() {
		return moveto;
	}

	public void setMoveto(String moveto) {
		this.moveto = moveto;
	}

	public Date getMoveDate() {
		return moveDate;
	}

	public void setMoveDate(Date moveDate) {
		this.moveDate = moveDate;
	}

	public String[] getItemIds() {
		return itemIds;
	}

	public void setItemIds(String[] itemIds) {
		this.itemIds = itemIds;
	}

	public String getMoveDates() {
		return moveDates;
	}

	public void setMoveDates(String moveDates) {
		this.moveDates = moveDates;
	}

}
