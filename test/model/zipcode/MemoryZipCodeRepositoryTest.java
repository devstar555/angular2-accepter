package model.zipcode;

public class MemoryZipCodeRepositoryTest extends ZipCodeRepositoryTest {

	@Override
	protected ZipCodeRepository newRepository() {
		return new MemoryZipCodeRepository();
	}

}
