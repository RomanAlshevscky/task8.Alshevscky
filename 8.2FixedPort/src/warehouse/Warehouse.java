package warehouse;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class Warehouse {
	private List<Container> containerList;
	private int size;
	private Lock lock;

	public Warehouse(int size) {
		containerList = new ArrayList<Container>(size);
		lock = new ReentrantLock();
		this.size = size;
	}

	public boolean addContainer(Container container) throws InterruptedException {
		boolean isLocked = false;
		try {
			if (lock.tryLock(30, TimeUnit.SECONDS)) {
				isLocked = true;
				return containerList.add(container);
			}
		} finally {
			if(isLocked){
				lock.unlock();
			}
		}
		return false;
	}

	public boolean addContainer(List<Container> containers) throws InterruptedException {
		boolean result = false;
		boolean isLocked = false;
		try {
			if (lock.tryLock(30, TimeUnit.SECONDS)) {
				isLocked = true;
				if (containerList.size() + containers.size() <= size) {
					result = containerList.addAll(containers);
				}
			}
		} finally {
			if(isLocked){
				lock.unlock();
			}
		}
		return result;
	}

	public Container getContainer() throws InterruptedException {
		boolean isLocked = false;
		try {
			if (lock.tryLock(30, TimeUnit.SECONDS)) {
				isLocked = true;
				if (containerList.size() > 0) {
					return containerList.remove(0);
				}
			}
		} finally {
			if(isLocked){
				lock.unlock();
			}
		}
		return null;
	}

	public List<Container> getContainer(int amount) throws InterruptedException {
		boolean isLocked = false;
		try {
			if (lock.tryLock(30, TimeUnit.SECONDS)) {
				isLocked = true;
				if (containerList.size() >= amount) {
					List<Container> cargo = new ArrayList<Container>(containerList.subList(0, amount));
					containerList.removeAll(cargo);
					return cargo;
				}
			}
		} finally {
			if(isLocked){
				lock.unlock();
			}
		}
		return null;
	}
	
	public int getSize(){
		return size;
	}
	
	public int getRealSize(){
		return containerList.size();
	}
	
	public int getFreeSize(){
		return size - containerList.size();
	}

}
