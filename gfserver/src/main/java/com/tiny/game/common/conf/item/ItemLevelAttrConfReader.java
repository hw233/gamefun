package com.tiny.game.common.conf.item;


import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.tiny.game.common.conf.ConfAnnotation;
import com.tiny.game.common.conf.ConfReader;
import com.tiny.game.common.domain.item.Item;
import com.tiny.game.common.domain.item.ItemId;
import com.tiny.game.common.domain.item.LevelItem;

@ConfAnnotation(confClass = ItemLevelAttrConfReader.class, path = "resources/config/item_attr.csv")
public class ItemLevelAttrConfReader extends ConfReader<LevelItem> {
	
	private static Map<String, Integer> itemMaxLevelMap = new ConcurrentHashMap<String, Integer>();
	
	private static void adjustItemMaxLevel(LevelItem bean) {
		Integer maxValue = itemMaxLevelMap.get(Item.getKey(bean.getItemId()));
		if(maxValue == null || bean.getLevel() > maxValue) {
			itemMaxLevelMap.put(Item.getKey(bean.getItemId()), bean.getLevel());
		} 
	}
	
	public static int getMaxLevel(ItemId itemId) {
		Integer maxValue = itemMaxLevelMap.get(Item.getKey(itemId));
		return maxValue==null ? 1 : maxValue;
	}
	
	@Override
	protected void parseCsv(String[] csv) {
		LevelItem bean = new LevelItem();
		bean.setItemId(ItemId.valueOf(Integer.parseInt(getSafeValue(csv, "id"))));
		bean.setName(getSafeValue(csv, "name"));
		bean.setAvatarId(getSafeValue(csv, "avatarId"));
		bean.setLevel(Integer.parseInt(getSafeValue(csv, "level")));
		
		int propStartIndex = getIndex("avatarId") + 1;
		for(int i=propStartIndex; i<csv.length; i++){
			String attrKey = getColumnKey(i);
			String value = csv[i];
			if(value!=null && value.trim().length() > 0){
				bean.addAttr(attrKey, value);
			}
		}
		
//		System.out.println(bean.toString());
		addConfBean(bean.getKey(), bean);
		adjustItemMaxLevel(bean);
	}
	
}
