var appModule = angular.module('myApp', []);

appModule.controller('MainCtrl', ['mainService', 'searchService','$scope', '$interval', function(mainService, searchService, $scope, $interval) {
    $scope.user = {name:null, tweets:[], history:['barackobama', 'venkat_s', 'nielsutrecht']};
    $scope.randomTweets = [];
    $scope.forUser = false;
    $scope.timelineInterval = null;

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

    $scope.toggle = function() {
        $scope.forUser = !$scope.forUser;
    }

    $scope.timelineGo = function(name) {
        $scope.user.tweets.length = 0;
        if($scope.timelineInterval) {
            $interval.cancel($scope.timelineInterval);
        }
        if(name) {
            $scope.user.name = name;
        }
        if($scope.user.name) {
            $scope.addtoHistory($scope.user.name);
            $scope.timelineInterval = $interval(function() {
                $scope.updateTimeline();
            }, 1000);
        }
    }

    $scope.addtoHistory = function(user) {
        var index = $scope.user.history.indexOf(user);
        if(index >= 0) {
            $scope.user.history.splice(index, 1);
        }
        $scope.user.history.push(user);
    }

    $scope.updateTimeline = function() {
        searchService.timeline($scope.user.name).then(function(tweet) {
            $scope.user.tweets.push(tweet);
        });
    }

    $scope.stop = $interval(function() {
        $scope.update();
    }, 1000);
}]);

appModule.service('mainService', function($http) {
    return {
        applicationStatus : function() {
            return $http.get('/applicationStatus').then(function(response) {
                return response.data;
            });
        },

        markovStatus : function() {
            return $http.get('/markovStatus').then(function(response) {
                return response.data;
            });
        },

        latestTweet : function() {
            return $http.get('/latestTweet').then(function(response){
                return response.data;
            });
        },

        random : function() {
            return $http.get('/random').then(function(response){
                return response.data;
            });
        }
    };
});

appModule.service('searchService', function($http) {
    return {
        query : function(query) {
            return $http.get('/search/query/' + query).then(function(response) {
                return response.data;
            });
        },

        timeline : function(user) {
            return $http.get('/search/timeline/' + user).then(function(response) {
                return response.data;
            });
        }
    };
});