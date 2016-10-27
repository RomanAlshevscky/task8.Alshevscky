package port;

import warehouse.Container;
import warehouse.Warehouse;

import java.util.List;

public class Berth {

	private int id;
	private Warehouse portWarehouse;

	public Berth(int id, Warehouse warehouse) {
		this.id = id;
		portWarehouse = warehouse;
	}

	public int getId() {
		return id;
	}

	public boolean add(Warehouse shipWarehouse, int numberOfConteiners) throws InterruptedException {
		boolean result = false;
		int newConteinerCount = portWarehouse.getRealSize() + numberOfConteiners;
		if (newConteinerCount <= portWarehouse.getFreeSize()) {
			result = doMoveFromShip(shipWarehouse, numberOfConteiners);
		}

		return result;
	}
	
	private boolean doMoveFromShip(Warehouse shipWarehouse, int numberOfConteiners) throws InterruptedException {
		if (shipWarehouse.getRealSize() >= numberOfConteiners) {
			List<Container> containers = shipWarehouse.getContainer(numberOfConteiners);
			portWarehouse.addContainer(containers);
			return true;
		}

		return false;
	}

	public boolean get(Warehouse shipWarehouse, int numberOfConteiners) throws InterruptedException {
		boolean result = false;
		if (numberOfConteiners <= portWarehouse.getRealSize()) {
			result = doMoveFromPort(shipWarehouse, numberOfConteiners);
		}
		return result;
	}
	
	private boolean doMoveFromPort(Warehouse shipWarehouse, int numberOfConteiners) throws InterruptedException {
		int newConteinerCount = shipWarehouse.getRealSize() + numberOfConteiners;
		if (newConteinerCount <= shipWarehouse.getFreeSize()) {
			List<Container> containers = portWarehouse.getContainer(numberOfConteiners);
			shipWarehouse.addContainer(containers);
			return true;
		}

		return false;
	}
}
