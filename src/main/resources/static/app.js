var appModule = angular.module('myApp', []);

appModule.controller('MainCtrl', ['mainService','$scope', '$interval', function(mainService, $scope, $interval) {
    $scope.greeting = 'Welcome to the Twitter Markov chain example!';
    $scope.randomTweets = [];

    $scope.update = function() {
        mainService.applicationStatus().then(function(applicationStatus) {
            $scope.applicationStatus = applicationStatus;
        });
        mainService.markovStatus().then(function(markovStatus) {
            $scope.markovStatus = markovStatus;
        });
        mainService.latestTweet().then(function(latestTweet) {
            $scope.latestTweet = latestTweet;
        });
        $scope.generate();
    }

    $scope.generate = function() {
        mainService.random().then(function(randomTweet) {
            $scope.randomTweets.push(randomTweet);
        });
    }

    $scope.stop = $interval(function() {
        $scope.update();
    }, 1000);
}]);

appModule.service('mainService', function($http) {
    return {
        applicationStatus : function(username) {
            return $http.get('/applicationStatus').then(function(response) {
                return response.data;
            });
        },

        markovStatus : function(username) {
            return $http.get('/markovStatus').then(function(response) {
                return response.data;
            });
        },

        latestTweet : function(role) {
            return $http.get('/latestTweet').then(function(response){
                return response.data;
            });
        },

        random : function(role) {
            return $http.get('/random').then(function(response){
                return response.data;
            });
        }
    };
});