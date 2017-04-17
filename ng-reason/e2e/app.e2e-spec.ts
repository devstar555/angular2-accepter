import { NgReasonPage } from './app.po';

describe('ng-reason App', function() {
  let page: NgReasonPage;

  beforeEach(() => {
    page = new NgReasonPage();
  });

  it('should display message saying app works', () => {
    page.navigateTo();
    expect(page.getParagraphText()).toEqual('app works!');
  });
});
