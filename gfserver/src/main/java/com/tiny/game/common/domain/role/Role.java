package com.tiny.game.common.domain.role;

import java.util.Collection;
import java.util.Date;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.google.protobuf.InvalidProtocolBufferException;
import com.tiny.game.common.domain.item.Item;
import com.tiny.game.common.domain.item.ItemId;
import com.tiny.game.common.exception.InternalBugException;
import com.tiny.game.common.server.main.bizlogic.role.RoleService;
import com.tiny.game.common.util.NetMessageUtil;

import game.protocol.protobuf.GameProtocol.S_RoleData;

/**
 * gm:
 * add itemkey value
 * set itemkey value
 * del itemkey value
 * 
 * user:
 * use itemkey value (del)
 * get itemkey value (add)
 * setting: set itemkey value (set)
 */
public class Role {

	private String roleId;
//	private String userId;

//	private String headIcon;
//	private String name;
//
//	private int level;
//	private int levelExp;
//	
//	private int systemDonategolds; 
//	private int golds;
//
//	private int systemDonateDiamonds;
//	private int diamonds;
//	
//	private int point;  // 积分
//
//	private RoleBattleInfoBean battleInfo;

//	private RoleSettingBean setting = new RoleSettingBean();
	
	private Map<String, OwnItem> items = new ConcurrentHashMap<String, OwnItem>();
	
	private Date lastUpdateTime = null;
	
	public byte[] toBinData(){
		return NetMessageUtil.convertRole(this).toByteArray();
	}
	
	public static Role toRole(byte[] bin){
		try {
			S_RoleData data = S_RoleData.PARSER.parseFrom(bin, 0, bin.length);
			return NetMessageUtil.convertS_RoleData(data);
		} catch (InvalidProtocolBufferException e) {
			throw new InternalBugException("Failed to parse role bin data: " + e.getMessage(), e);
		}
	}
	
	public String toString(){
		StringBuffer sb = new StringBuffer();
		sb.append("Role: " + roleId);
		for(OwnItem item : items.values()){
			sb.append(","+item.toString());
		}
		return sb.toString();
	}
	
	public boolean equals(Object o) {
		if(o==null || !(o instanceof Role)) {
			return false;
		}
		
		return roleId.equals(((Role) o).roleId);
	}
	
	public Collection<OwnItem> getOwnItems(){
		return items.values();
	}
	
	public OwnItem getRoleOwnItem(String ownKey) {
		return items.get(ownKey);
	}
	
	public int getSystemDonategolds() {
		return getRoleOwnItemValue(ItemId.systemDonateGold);
	}

	public int getBuyGolds() {
		return getRoleOwnItemValue(ItemId.buyGold);
	}
	
	public int getSystemDonateGems() {
		return getRoleOwnItemValue(ItemId.systemDonateGem);
	}

	public int getBuyGems() {
		return getRoleOwnItemValue(ItemId.buyGem);
	}
	
	public int getAllGolds() {
		return getSystemDonategolds()+getBuyGolds();
	}
	
	public int getAllGems() {
		return getSystemDonateGems()+getBuyGems();
	}
	
	public int getLevel(){
		return getRoleOwnItemValue(ItemId.roleLevel);
	}
	
	public int getRoleOwnItemValue(ItemId itemId){
		return getRoleOwnItem(Item.getKey(itemId)).getValue();
	}
	
	public void addRoleOwnItem(OwnItem item) {
		OwnItem oldItem = items.get(item.getKey());
		if(oldItem==null) {
			oldItem = item;
			items.put(item.getKey(), oldItem);
		} else {
			boolean isAccumulative = oldItem.getItem().isAccumulative();
			if(isAccumulative){
				int newCount = oldItem.getValue() + item.getValue();
				oldItem.setValue(newCount);
				// TODO: check count < 0?
				// TODO: how about attr?
				
				// handle some special logic like exp
				if(item.getItem().getItemId() == ItemId.roleExp){
					RoleService.fixupRoleExpChange(this);
				} 
			} else {
				oldItem.setValue(item.getValue());
			}
		}
	}
	
	public void setRoleOwnItem(OwnItem item) {
		OwnItem oldItem = items.get(item.getKey());
		if(oldItem==null) {
			oldItem = item;
			items.put(item.getKey(), oldItem);
		} else {
			boolean isAccumulative = oldItem.getItem().isAccumulative();
			if(isAccumulative){
				throw new InternalBugException("Invalid method invoked, change setRoleOwnItem to addRoleOwnItem to item: " + item.getKey());
			}
			oldItem.setValue(item.getValue());
		}
	}
	
	public void deleteRoleOwnItem(OwnItem item) {
		OwnItem oldItem = items.get(item.getKey());
		if(oldItem!=null) {
			boolean isAccumulative = oldItem.getItem().isAccumulative();
			if(!isAccumulative){
				throw new InternalBugException("Invalid method invoked, item is not accumulative: " + item.getKey());
			} 
			int newCount = oldItem.getValue() - item.getValue();
			oldItem.setValue(newCount);
		}
	}
	
	public String getRoleId() {
		return roleId;
	}

	public void setRoleId(String roleId) {
		this.roleId = roleId;
	}

	public Date getLastUpdateTime() {
		return lastUpdateTime;
	}

	public void setLastUpdateTime(Date lastUpdateTime) {
		this.lastUpdateTime = lastUpdateTime;
	}

//	public int getPoint() {
//		return point;
//	}
//
//	public void setPoint(int point) {
//		this.point = point;
//	}

//	public RoleBattleInfoBean getBattleInfo() {
//		return battleInfo;
//	}
//
//	public void setBattleInfo(RoleBattleInfoBean battleInfo) {
//		this.battleInfo = battleInfo;
//	}

//	public RoleSettingBean getSetting() {
//		return setting;
//	}
//
//	public void setSetting(RoleSettingBean setting) {
//		this.setting = setting;
//	}
	
	
}
