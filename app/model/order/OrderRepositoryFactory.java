package model.order;

public class OrderRepositoryFactory {
	public static OrderRepository get() {
		return new OrderRepositoryImpl();
	}
}
