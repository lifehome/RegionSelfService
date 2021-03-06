package com.mtihc.regionselfservice.v2.plots;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.util.BlockVector;

import com.mtihc.regionselfservice.v2.plots.signs.ForRentSignData;
import com.mtihc.regionselfservice.v2.plots.signs.PlotSignType;


public class PlotData implements ConfigurationSerializable {
    
    protected final String regionId;
    private double sellCost = 0;
    private double rentCost = 0;
    private long rentTime = 0;
    protected final Map<BlockVector, IPlotSignData> signs = new HashMap<BlockVector, IPlotSignData>();
    
    public PlotData(String regionId, double sellCost, double rentCost, long rentTime) {
	this(regionId, sellCost, rentCost, rentTime, null);
    }
    
    public PlotData(String regionId, double sellCost, double rentCost, long rentTime, Collection<IPlotSignData> signs) {
	this.regionId = regionId;
	this.sellCost = sellCost;
	this.rentCost = rentCost;
	this.rentTime = rentTime;
	
	if (signs != null) {
	    for (IPlotSignData data : signs) {
		setSign(data);
	    }
	}
    }
    
    public PlotData(Map<String, Object> values) {
	this.regionId = (String) values.get("region-id");
	this.sellCost = (Double) values.get("sell-cost");
	this.rentCost = (Double) values.get("rent-cost");
	this.rentTime = (Integer) values.get("rent-time");
	
	Map<?, ?> signsSection = (Map<?, ?>) values.get("signs");
	if (signsSection != null) {
	    Collection<?> signsValues = signsSection.values();
	    for (Object object : signsValues) {
		if (!(object instanceof IPlotSignData)) {
		    continue;
		}
		IPlotSignData data = (IPlotSignData) object;
		setSign(data);
	    }
	}
	
    }
    
    @Override
    public Map<String, Object> serialize() {
	Map<String, Object> values = new LinkedHashMap<String, Object>();
	
	values.put("region-id", this.regionId);
	values.put("sell-cost", this.sellCost);
	values.put("rent-cost", this.rentCost);
	values.put("rent-time", this.rentTime);
	
	Map<String, Object> signsSection = new LinkedHashMap<String, Object>();
	Collection<IPlotSignData> signs = getSigns();
	int index = 0;
	for (IPlotSignData data : signs) {
	    signsSection.put("sign" + index, data);
	    index++;
	}
	values.put("signs", signsSection);
	
	return values;
    }
    
    public boolean isForSale() {
	return hasSign(PlotSignType.FOR_SALE);
    }
    
    public double getSellCost() {
	return this.sellCost;
    }
    
    public void setSellCost(double cost) {
	this.sellCost = cost;
    }
    
    public boolean isForRent() {
	return hasSign(PlotSignType.FOR_RENT);
    }
    
    public boolean hasRenters() {
	Collection<IPlotSignData> signs = getSigns(PlotSignType.FOR_RENT);
	for (IPlotSignData sign : signs) {
	    ForRentSignData rentSign = (ForRentSignData) sign;
	    if (rentSign.isRentedOut()) {
		return true;
	    }
	}
	return false;
    }
    
    public double getRentCost() {
	return this.rentCost;
    }
    
    public long getRentTime() {
	return this.rentTime;
    }
    
    public void setRentCost(double cost, long millisec) {
	this.rentCost = cost;
	this.rentTime = millisec;
    }
    
    public String getRegionId() {
	return this.regionId;
    }
    
    public IPlotSignData getSign(BlockVector coords) {
	return this.signs.get(coords);
    }
    
    public void setSign(IPlotSignData data) {
	this.signs.put(data.getBlockVector(), data);
	
    }
    
    public boolean hasSign(BlockVector coords) {
	return this.signs.containsKey(coords);
    }
    
    public boolean hasSign(PlotSignType type) {
	Collection<IPlotSignData> values = this.signs.values();
	for (IPlotSignData value : values) {
	    if (value == null) {
		continue;
	    }
	    if (value.getType() == type) {
		return true;
	    }
	}
	return false;
    }
    
    public IPlotSignData removeSign(BlockVector coords) {
	return this.signs.remove(coords);
    }
    
    public Collection<IPlotSignData> getSigns() {
	return getSigns(null);
    }
    
    public Collection<IPlotSignData> getSigns(PlotSignType type) {
	Collection<IPlotSignData> values = this.signs.values();
	ArrayList<IPlotSignData> result = new ArrayList<IPlotSignData>();
	for (IPlotSignData value : values) {
	    if (value == null) {
		continue;
	    }
	    if (type == null || value.getType() == type) {
		result.add(value);
	    }
	}
	return result;
    }
}
