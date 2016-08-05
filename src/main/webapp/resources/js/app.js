(function(angular, $, _, TL) {
  'use strict';
  var deps = _.union(window.dependencies || [], [
    'ngAria',
    'ngAnimate',
    "angular-notification-icons",
    'ui.bootstrap',
    'irida.session',
    'irida.notifications',
    'irida.cart'
  ]);

  angular.module('irida', deps)
    .config(function($httpProvider) {
      $httpProvider.defaults.headers.post['Content-Type'] = 'application/x-www-form-urlencoded';

      // Make sure that all ajax form data is sent in the correct format.
      $httpProvider.defaults.transformRequest = function(data) {
        if (data === undefined) {
          return data;
        }
        return $.param(data);
      };
    })
    .run(function(uibPaginationConfig) {
      uibPaginationConfig.firstText = TL.lang.page.first;
      uibPaginationConfig.previousText = TL.lang.page.prev;
      uibPaginationConfig.nextText = TL.lang.page.next;
      uibPaginationConfig.lastText = TL.lang.page.last;
      uibPaginationConfig.boundaryLinks = true;
      uibPaginationConfig.directionLinks = true;
      uibPaginationConfig.maxSize = 8;
      uibPaginationConfig.rotate = false;
    });
})(window.angular, window.$, window._, window.TL);