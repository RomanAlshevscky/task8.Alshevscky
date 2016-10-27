package port;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

import org.apache.log4j.Logger;

import ship.Ship;
import warehouse.Container;
import warehouse.Warehouse;

public class Port {
	private final static Logger logger = Logger.getRootLogger();
	
	private BlockingQueue<Berth> berthList;
	private Warehouse portWarehouse;
	
	private Map<Ship, Berth> usedBerths;

	public Port(int berthSize, int warehouseSize) {
		portWarehouse = new Warehouse(warehouseSize);
		berthList = new ArrayBlockingQueue<Berth>(berthSize);
		for (int i = 0; i < berthSize; i++) {
			berthList.add(new Berth(i, portWarehouse));
		}
		usedBerths = new HashMap<Ship, Berth>();
		logger.debug("Порт создан.");
	}
	
	public void setContainersToWarehouse(List<Container> containerList){
		portWarehouse.addContainer(containerList);
	}

	public boolean lockBerth(Ship ship) {
		Berth berth;
		try {
			berth = berthList.take();
			usedBerths.put(ship, berth); //Не потокобезопасное добавление в map
		} catch (InterruptedException e) {
			logger.debug("Кораблю " + ship.getName() + " отказано в швартовке.");
			return false;
		}		
		return true;
	}
	
	
	public boolean unlockBerth(Ship ship) {
		Berth berth = usedBerths.get(ship);
		
		try {
			berthList.put(berth);
			usedBerths.remove(ship);//Не потокобезопасное извлечение из map
		} catch (InterruptedException e) {
			logger.debug("Корабль " + ship.getName() + " не смог отшвартоваться.");
			return false;
		}		
		return true;
	}
	
	public Berth getBerth(Ship ship) throws PortException {
		
		Berth berth = usedBerths.get(ship);
		if (berth == null){
			throw new PortException("Try to use Berth without blocking.");
		}
		return berth;		
	}
}
