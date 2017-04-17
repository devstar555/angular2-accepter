/* tslint:disable:no-unused-variable */

import { TestBed, async, inject } from '@angular/core/testing';
import { TestDataSetService } from './test-data-set.service';

describe('TestDataSetService', () => {
  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [TestDataSetService]
    });
  });

  it('should ...', inject([TestDataSetService], (service: TestDataSetService) => {
    expect(service).toBeTruthy();
  }));
});
