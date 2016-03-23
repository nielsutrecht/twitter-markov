var appModule = angular.module('myApp');

appModule.directive('status', ['mainService', function(mainService) {
    return {
         restrict: 'E',
         templateUrl: 'directives/status.html'
    };
}]);

appModule.directive('tweet', ['mainService', function(mainService) {
    return {
         restrict: 'E',
         templateUrl: 'directives/tweet.html',

         scope: {
            tweet:"="
         },
         controller: function($scope) {
         }
    };
}]);