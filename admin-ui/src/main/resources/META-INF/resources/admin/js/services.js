'use strict';

var module = angular.module('keycloak.services', [ 'ngResource' ]);

module.service('Dialog', function($dialog) {
	var dialog = {};

	var escapeHtml = function(str) {
		var div = document.createElement('div');
		div.appendChild(document.createTextNode(str));
		return div.innerHTML;
	};

	dialog.confirmDelete = function(name, type, success) {
		var title = 'Delete ' + escapeHtml(type.charAt(0).toUpperCase() + type.slice(1));
		var msg = '<span class="primary">Are you sure you want to permanently delete the ' + escapeHtml(type) + ' "' + escapeHtml(name) + '"?</span>' +
            '<span>This action can\'t be undone.</span>';
		var btns = [ {
			result : 'cancel',
			label : 'Cancel',
            cssClass : 'btn btn-default'
		}, {
			result : 'ok',
			label : 'Delete',
			cssClass : 'btn btn-danger'
		} ];

		$dialog.messageBox(title, msg, btns).open().then(function(result) {
			if (result == "ok") {
				success();
			}
		});
	}

    dialog.confirmGenerateKeys = function(name, type, success) {
        var title = 'Generate new keys for realm';
        var msg = '<span class="primary">Are you sure you want to permanently generate new keys for ' + name + '"?</span>' +
            '<span>This action can\'t be undone.</span>';
        var btns = [ {
            result : 'cancel',
            label : 'Cancel',
            cssClass : 'btn btn-default'
        }, {
            result : 'ok',
            label : 'Generate new keys',
            cssClass : 'btn btn-danger'
        } ];

        $dialog.messageBox(title, msg, btns).open().then(function(result) {
            if (result == "ok") {
                success();
            }
        });
    }

    dialog.confirm = function(title, message, success, cancel) {
        var title = title;
        var msg = '<span class="primary">' + message + '"</span>' +
            '<span>This action can\'t be undone.</span>';
        var btns = [ {
            result : 'cancel',
            label : 'Cancel',
            cssClass : 'btn btn-default'
        }, {
            result : 'ok',
            label : title,
            cssClass : 'btn btn-danger'
        } ];

        $dialog.messageBox(title, msg, btns).open().then(function(result) {
            if (result == "ok") {
                success();
            } else {
                cancel && cancel();
            }
        });
    }

	return dialog
});

module.factory('Notifications', function($rootScope, $timeout) {
	// time (in ms) the notifications are shown
	var delay = 5000;

	var notifications = {};

	var scheduled = null;
	var schedulePop = function() {
		if (scheduled) {
			$timeout.cancel(scheduled);
		}

		scheduled = $timeout(function() {
			$rootScope.notification = null;
			scheduled = null;
		}, delay);
	};

	if (!$rootScope.notifications) {
		$rootScope.notifications = [];
	}

	notifications.message = function(type, header, message) {
		$rootScope.notification = {
			type : type,
			header: header,
			message : message
		};

		schedulePop();
	}

	notifications.info = function(message) {
		notifications.message("info", "Info!", message);
	};

	notifications.success = function(message) {
		notifications.message("success", "Success!", message);
	};

	notifications.error = function(message) {
		notifications.message("danger", "Error!", message);
	};

	notifications.warn = function(message) {
		notifications.message("warning", "Warning!", message);
	};

	return notifications;
});

module.factory('Realm', function($resource) {
	return $resource('/auth/rest/admin/realms/:id', {
		id : '@realm'
	}, {
		update : {
			method : 'PUT'
		},
        create : {
            method : 'POST',
            params : { id : ''}
        }

    });
});

module.factory('ServerInfo', function($resource) {
    return $resource('/auth/rest/admin/serverinfo');
});

module.factory('User', function($resource) {
	return $resource('/auth/rest/admin/realms/:realm/users/:userId', {
		realm : '@realm',
		userId : '@userId'
	}, {
        update : {
            method : 'PUT'
        }
	});
});

module.factory('UserCredentials', function($resource) {
    var credentials = {};

    credentials.resetPassword = $resource('/auth/rest/admin/realms/:realm/users/:userId/reset-password', {
        realm : '@realm',
        userId : '@userId'
    }, {
        update : {
            method : 'PUT'
        }
    }).update;

    credentials.removeTotp = $resource('/auth/rest/admin/realms/:realm/users/:userId/remove-totp', {
        realm : '@realm',
        userId : '@userId'
    }, {
        update : {
            method : 'PUT'
        }
    }).update;

    credentials.resetPasswordEmail = $resource('/auth/rest/admin/realms/:realm/users/:userId/reset-password-email', {
        realm : '@realm',
        userId : '@userId'
    }, {
        update : {
            method : 'PUT'
        }
    }).update;

    return credentials;
});

module.factory('RealmRoleMapping', function($resource) {
    return $resource('/auth/rest/admin/realms/:realm/users/:userId/role-mappings/realm', {
        realm : '@realm',
        userId : '@userId'
    });
});

module.factory('ApplicationRoleMapping', function($resource) {
    return $resource('/auth/rest/admin/realms/:realm/users/:userId/role-mappings/applications/:application', {
        realm : '@realm',
        userId : '@userId',
        application : "@application"
    });
});

module.factory('ApplicationRealmScopeMapping', function($resource) {
    return $resource('/auth/rest/admin/realms/:realm/applications/:application/scope-mappings/realm', {
        realm : '@realm',
        application : '@application'
    });
});

module.factory('ApplicationApplicationScopeMapping', function($resource) {
    return $resource('/auth/rest/admin/realms/:realm/applications/:application/scope-mappings/applications/:targetApp', {
        realm : '@realm',
        application : '@application',
        targetApp : '@targetApp'
    });
});



module.factory('RealmRoles', function($resource) {
    return $resource('/auth/rest/admin/realms/:realm/roles', {
        realm : '@realm'
    });
});

module.factory('RoleRealmComposites', function($resource) {
    return $resource('/auth/rest/admin/realms/:realm/roles-by-id/:role/composites/realm', {
        realm : '@realm',
        role : '@role'
    });
});

module.factory('RoleApplicationComposites', function($resource) {
    return $resource('/auth/rest/admin/realms/:realm/roles-by-id/:role/composites/applications/:application', {
        realm : '@realm',
        role : '@role',
        application : "@application"
    });
});


function roleControl($scope, realm, role, roles, applications,
                     ApplicationRole, RoleById, RoleRealmComposites, RoleApplicationComposites,
                     $http, $location, Notifications, Dialog) {

    $scope.$watch(function () {
        return $location.path();
    }, function () {
        $scope.path = $location.path().substring(1).split("/");
    });

    $scope.$watch('role', function () {
        if (!angular.equals($scope.role, role)) {
            $scope.changed = true;
        }
    }, true);

    $scope.update = function () {
        RoleById.update({
            realm: realm.realm,
            role: role.id
        }, $scope.role, function () {
            $scope.changed = false;
            role = angular.copy($scope.role);
            Notifications.success("Your changes have been saved to the role.");
        });
    };

    $scope.reset = function () {
        $scope.role = angular.copy(role);
        $scope.changed = false;
    };

    if (!role.id) return;

    $scope.compositeSwitch = role.composite;
    $scope.compositeSwitchDisabled = role.composite;
    $scope.realmRoles = angular.copy(roles);
    $scope.selectedRealmRoles = [];
    $scope.selectedRealmMappings = [];
    $scope.realmMappings = [];
    $scope.applications = applications;
    $scope.applicationRoles = [];
    $scope.selectedApplicationRoles = [];
    $scope.selectedApplicationMappings = [];
    $scope.applicationMappings = [];

    console.log('remove self');
    for (var j = 0; j < $scope.realmRoles.length; j++) {
        if ($scope.realmRoles[j].id == role.id) {
            var realmRole = $scope.realmRoles[j];
            var idx = $scope.realmRoles.indexOf(realmRole);
            $scope.realmRoles.splice(idx, 1);
            break;
        }
    }


    $scope.realmMappings = RoleRealmComposites.query({realm : realm.realm, role : role.id}, function(){
        for (var i = 0; i < $scope.realmMappings.length; i++) {
            var role = $scope.realmMappings[i];
            for (var j = 0; j < $scope.realmRoles.length; j++) {
                var realmRole = $scope.realmRoles[j];
                if (realmRole.id == role.id) {
                    var idx = $scope.realmRoles.indexOf(realmRole);
                    if (idx != -1) {
                        $scope.realmRoles.splice(idx, 1);
                        break;
                    }
                }
            }
        }
    });

    $scope.addRealmRole = function() {
        $scope.compositeSwitchDisabled=true;
        $http.post('/auth/rest/admin/realms/' + realm.realm + '/roles-by-id/' + role.id + '/composites',
                $scope.selectedRealmRoles).success(function() {
                for (var i = 0; i < $scope.selectedRealmRoles.length; i++) {
                    var role = $scope.selectedRealmRoles[i];
                    var idx = $scope.realmRoles.indexOf($scope.selectedRealmRoles[i]);
                    if (idx != -1) {
                        $scope.realmRoles.splice(idx, 1);
                        $scope.realmMappings.push(role);
                    }
                }
                $scope.selectRealmRoles = [];
            });
    };

    $scope.deleteRealmRole = function() {
        $scope.compositeSwitchDisabled=true;
        $http.delete('/auth/rest/admin/realms/' + realm.realm + '/roles-by-id/' + role.id + '/composites',
            {data : $scope.selectedRealmMappings, headers : {"content-type" : "application/json"}}).success(function() {
                for (var i = 0; i < $scope.selectedRealmMappings.length; i++) {
                    var role = $scope.selectedRealmMappings[i];
                    var idx = $scope.realmMappings.indexOf($scope.selectedRealmMappings[i]);
                    if (idx != -1) {
                        $scope.realmMappings.splice(idx, 1);
                        $scope.realmRoles.push(role);
                    }
                }
                $scope.selectedRealmMappings = [];
            });
    };

    $scope.addApplicationRole = function() {
        $scope.compositeSwitchDisabled=true;
        $http.post('/auth/rest/admin/realms/' + realm.realm + '/roles-by-id/' + role.id + '/composites',
                $scope.selectedApplicationRoles).success(function() {
                for (var i = 0; i < $scope.selectedApplicationRoles.length; i++) {
                    var role = $scope.selectedApplicationRoles[i];
                    var idx = $scope.applicationRoles.indexOf($scope.selectedApplicationRoles[i]);
                    if (idx != -1) {
                        $scope.applicationRoles.splice(idx, 1);
                        $scope.applicationMappings.push(role);
                    }
                }
                $scope.selectedApplicationRoles = [];
            });
    };

    $scope.deleteApplicationRole = function() {
        $scope.compositeSwitchDisabled=true;
        $http.delete('/auth/rest/admin/realms/' + realm.realm + '/roles-by-id/' + role.id + '/composites',
            {data : $scope.selectedApplicationMappings, headers : {"content-type" : "application/json"}}).success(function() {
                for (var i = 0; i < $scope.selectedApplicationMappings.length; i++) {
                    var role = $scope.selectedApplicationMappings[i];
                    var idx = $scope.applicationMappings.indexOf($scope.selectedApplicationMappings[i]);
                    if (idx != -1) {
                        $scope.applicationMappings.splice(idx, 1);
                        $scope.applicationRoles.push(role);
                    }
                }
                $scope.selectedApplicationMappings = [];
            });
    };


    $scope.changeApplication = function() {
        $scope.applicationRoles = ApplicationRole.query({realm : realm.realm, application : $scope.compositeApp.name}, function() {
                $scope.applicationMappings = RoleApplicationComposites.query({realm : realm.realm, role : role.id, application : $scope.compositeApp.name}, function(){
                    for (var i = 0; i < $scope.applicationMappings.length; i++) {
                        var role = $scope.applicationMappings[i];
                        for (var j = 0; j < $scope.applicationRoles.length; j++) {
                            var realmRole = $scope.applicationRoles[j];
                            if (realmRole.id == role.id) {
                                var idx = $scope.applicationRoles.indexOf(realmRole);
                                if (idx != -1) {
                                    $scope.applicationRoles.splice(idx, 1);
                                    break;
                                }
                            }
                        }
                    }
                });
                for (var j = 0; j < $scope.applicationRoles.length; j++) {
                    if ($scope.applicationRoles[j] == role.id) {
                        var appRole = $scope.applicationRoles[j];
                        var idx = $scope.applicationRoles.indexof(appRole);
                        $scope.applicationRoles.splice(idx, 1);
                        break;
                    }
                }
            }
        );
    };




}


module.factory('Role', function($resource) {
    return $resource('/auth/rest/admin/realms/:realm/roles/:role', {
        realm : '@realm',
        role : '@role'
    },  {
        update : {
            method : 'PUT'
        }
    });
});

module.factory('RoleById', function($resource) {
    return $resource('/auth/rest/admin/realms/:realm/roles-by-id/:role', {
        realm : '@realm',
        role : '@role'
    },  {
        update : {
            method : 'PUT'
        }
    });
});

module.factory('ApplicationRole', function($resource) {
    return $resource('/auth/rest/admin/realms/:realm/applications/:application/roles/:role', {
        realm : '@realm',
        application : "@application",
        role : '@role'
    },  {
        update : {
            method : 'PUT'
        }
    });
});


module.factory('Application', function($resource) {
    return $resource('/auth/rest/admin/realms/:realm/applications/:application', {
        realm : '@realm',
        application : '@name'
    },  {
        update : {
            method : 'PUT'
        }
    });
});

module.factory('ApplicationInstallation', function($resource) {
    var url = '/auth/rest/admin/realms/:realm/applications/:application/installation';
    var resource = $resource('/auth/rest/admin/realms/:realm/applications/:application/installation', {
        realm : '@realm',
        application : '@application'
    },  {
        update : {
            method : 'PUT'
        }
    });
    resource.url = function(parameters) {
        return url.replace(':realm', parameters.realm).replace(':application', parameters.application);
    }
    return resource;
});

module.factory('ApplicationCredentials', function($resource) {
    return $resource('/auth/rest/admin/realms/:realm/applications/:application/credentials', {
        realm : '@realm',
        application : '@application'
    },  {
        update : {
            method : 'PUT',
            isArray : true
        }
    });
});

module.factory('ApplicationOrigins', function($resource) {
    return $resource('/auth/rest/admin/realms/:realm/applications/:application/allowed-origins', {
        realm : '@realm',
        application : '@application'
    },  {
        update : {
            method : 'PUT',
            isArray : true
        }
    });
});

module.factory('OAuthClient', function($resource) {
    return $resource('/auth/rest/admin/realms/:realm/oauth-clients/:id', {
        realm : '@realm',
        id : '@id'
    },  {
        update : {
            method : 'PUT'
        }
    });
});

module.factory('OAuthClientCredentials', function($resource) {
    return $resource('/auth/rest/admin/realms/:realm/oauth-clients/:oauth/credentials', {
        realm : '@realm',
        oauth : '@oauth'
    },  {
        update : {
            method : 'PUT',
            isArray : true
        }
    });
});

module.factory('OAuthClientRealmScopeMapping', function($resource) {
    return $resource('/auth/rest/admin/realms/:realm/oauth-clients/:oauth/scope-mappings/realm', {
        realm : '@realm',
        oauth : '@oauth'
    });
});

module.factory('OAuthClientApplicationScopeMapping', function($resource) {
    return $resource('/auth/rest/admin/realms/:realm/oauth-clients/:oauth/scope-mappings/applications/:targetApp', {
        realm : '@realm',
        oauth : '@oauth',
        targetApp : '@targetApp'
    });
});

module.factory('OAuthClientInstallation', function($resource) {
    var url = '/auth/rest/admin/realms/:realm/oauth-clients/:oauth/installation';
    var resource = $resource('/auth/rest/admin/realms/:realm/oauth-clients/:oauth/installation', {
        realm : '@realm',
        oauth : '@oauth'
    },  {
        update : {
            method : 'PUT'
        }
    });
    resource.url = function(parameters) {
        return url.replace(':realm', parameters.realm).replace(':oauth', parameters.oauth);
    }
    return resource;
});


module.factory('Current', function(Realm, $route) {
    var current = {};

    current.realms = {};
    current.realm = null;
    current.applications = {};
    current.application = null;

    current.refresh = function() {
        current.realm = null;
        current.realms = Realm.query(null, function(realms) {
            if ($route.current.params.realm) {
                for (var i = 0; i < realms.length; i++) {
                    if (realms[i].realm == $route.current.params.realm) {
                        current.realm =  realms[i];
                    }
                }
            }
        });
    }

    current.refresh();

    return current;
});

module.factory('TimeUnit', function() {
    var t = {};

    t.autoUnit = function(time) {
        var unit = 'Seconds';
        if (time % 60 == 0) {
            unit = 'Minutes';
            time  = time / 60;
        }
        if (time % 60 == 0) {
            unit = 'Hours';
            time = time / 60;
        }
        if (time % 24 == 0) {
            unit = 'Days'
            time = time / 24;
        }
        return unit;
    }

    t.toSeconds = function(time, unit) {
        switch (unit) {
            case 'Seconds': return time;
            case 'Minutes': return time * 60;
            case 'Hours': return time * 360;
            case 'Days': return time * 86400;
            default: throw 'invalid unit ' + unit;
        }
    }

    t.toUnit = function(time, unit) {
        switch (unit) {
            case 'Seconds': return time;
            case 'Minutes': return Math.ceil(time / 60);
            case 'Hours': return Math.ceil(time / 360);
            case 'Days': return Math.ceil(time / 86400);
            default: throw 'invalid unit ' + unit;
        }
    }

    t.convert = function(time, from, to) {
        var seconds = t.toSeconds(time, from);
        return t.toUnit(seconds, to);
    }

    return t;
});


module.factory('PasswordPolicy', function() {
    var p = {};

    p.policyMessages = {
        length:         "Minimal password length (integer type). Default value is 8.",
        digits:         "Minimal number (integer type) of digits in password. Default value is 1.",
        lowerCase:      "Minimal number (integer type) of lowercase characters in password. Default value is 1.",
        upperCase:      "Minimal number (integer type) of uppercase characters in password. Default value is 1.",
        specialChars:   "Minimal number (integer type) of special characters in password. Default value is 1."
    }

    p.allPolicies = [
        { name: 'length', value: 8 },
        { name: 'digits', value: 1 },
        { name: 'lowerCase', value: 1 },
        { name: 'upperCase', value: 1 },
        { name: 'specialChars', value: 1 }
    ];

    p.parse = function(policyString) {
        var policies = [];

        if (!policyString || policyString.length == 0){
            return policies;
        }

        var policyArray = policyString.split(" and ");

        for (var i = 0; i < policyArray.length; i ++){
            var policyToken = policyArray[i];
            var re = /(\w+)\(*(\d*)\)*/;

            var policyEntry = re.exec(policyToken);

            policies.push({ name: policyEntry[1], value: parseInt(policyEntry[2]) });

        }

        return policies;
    };

    p.toString = function(policies) {
        if (!policies || policies.length == 0) {
            return null;
        }

        var policyString = "";

        for (var i in policies){
            policyString += policies[i].name;
            if ( policies[i].value ){
                policyString += '(' + policies[i].value + ')';
            }
            policyString += " and ";
        }

        policyString = policyString.substring(0, policyString.length - 5);

        return policyString;
    };

    return p;
});