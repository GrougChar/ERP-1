package com.zh.web.util;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import com.zh.core.exception.ProjectException;
import com.zh.web.model.bean.Stock;
import com.zh.web.model.bean.StorageDetail;
import com.zh.web.service.StockService;
import com.zh.web.service.StorageDetailService;

/**
 * 
 * @Title: StockUtil.java
 * @Package com.zh.web.util
 * @Description: 入库相关操作
 * @date 2015年4月14日 上午10:53:14
 * @author taozhaoping 26078
 * @author mail taozhaoping@gmail.com
 * @version V1.0
 */
public class StockUtil implements ApplicationContextAware {

	private static ApplicationContext ctx;

	private static StockUtil stockUtil;

	private static StorageDetailService storageDetailService;

	private static StockService stockService;

	/**
	 * 实现ApplicationContextAware接口的回调方法，设置上下文环境
	 * 
	 * @param applicationContext
	 * @throws BeansException
	 */
	public void setApplicationContext(ApplicationContext applicationContext)
			throws BeansException {
		ctx = applicationContext;
	}


	/**
	 * 
	 * @Title: getInstance
	 * @Description: 获取实例
	 * @param @param stx
	 * @param @return 参数
	 * @return StockUtil 返回类型
	 * @throws
	 * @author taozhaoping 26078
	 * @author mail taozhaoping@gmail.com
	 */
	public synchronized static StockUtil getInstance() {
		if (stockUtil == null) {
				storageDetailService = (StorageDetailService) ctx
						.getBean("storageDetailService");
				stockService = (StockService) ctx.getBean("stockService");
				stockUtil  = (StockUtil) ctx.getBean("stockUtil");
		}

		return stockUtil;
	}
	
	/**
	 * 验证
	 */
	private static void verification(Integer productsID,Integer warehouseID,Integer StorageNumber)
	{
		
		if (productsID == null || "".equals(productsID.toString())) {
			throw new ProjectException("产品编号不允许为null");
		}
		
		if (warehouseID == null || "".equals(warehouseID.toString())) {
			throw new ProjectException("仓库ID不允许为null");
		}
		
		if (StorageNumber == null || "".equals(StorageNumber.toString())) {
			throw new ProjectException("数量不允许为null");
		}
	}

	/**
	 * 
	 * @Title: increaseStock
	 * @Description: 增加库存
	 * @param @param storageDetail 参数
	 * @return void 返回类型
	 * @throws
	 * @author taozhaoping 26078
	 * @author mail taozhaoping@gmail.com
	 */
	public synchronized void increaseStock(StorageDetail storageDetail) {
		Integer productsID = storageDetail.getProductsID();
		Integer warehouseID = storageDetail.getWarehouseID();
		Integer StorageNumber = storageDetail.getStorageNumber();
		verification(productsID, warehouseID, StorageNumber);
		Stock stock = new Stock();
		stock.setProductsID(productsID);
		stock.setWarehouseID(warehouseID);
		Stock reult = stockService.query(stock);
		if (reult == null)
		{
			throw new ProjectException("数据库不存在该产品编号");
		}
		Float stockNumber = reult.getStockNumber();
		Float currentNumber = stockNumber 
				+ StorageNumber;
		stock = new Stock();
		stock.setId(reult.getId());
		stock.setStockNumber(currentNumber);
		stockService.update(stock);
	}

	/**
	 * @Title: reduceStock
	 * @Description: 减少库存
	 * @param @param storageDetail 参数
	 * @return void 返回类型
	 * @throws
	 * @author taozhaoping 26078
	 * @author mail taozhaoping@gmail.com
	 */
	public synchronized void reduceStock(StorageDetail storageDetail) {
		Integer id = storageDetail.getId();
		if (id == null || "".equals(id.toString())) {
			throw new ProjectException("入库明细主建不允许为空!");
		}
		StorageDetail storageDetailReult = storageDetailService.query(storageDetail);
		Stock stock = new Stock();
		stock.setProductsID(storageDetailReult.getProductsID());
		stock.setWarehouseID(storageDetailReult.getWarehouseID());
		Stock reult = stockService.query(stock);
		if (reult == null)
		{
			throw new ProjectException("数据库不存在该产品编号");
		}
		Float stockNumber = reult.getStockNumber();
		Float currentNumber = stockNumber 
				- storageDetailReult.getStorageNumber();
		stock = new Stock();
		stock.setId(reult.getId());
		stock.setStockNumber(currentNumber);
		stockService.update(stock);
	}
	
}