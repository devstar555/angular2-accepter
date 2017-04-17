package model.order;

public class OrderRepositoryImplTest extends OrderRepositoryTest {
	@Override
	protected OrderRepository newRepository() {
		return new OrderRepositoryImpl();
	}
}
