window.BaseView = Backbone.View.extend({
	el : $('body'),

	events: {
	},
	userHelpPage : function(){
	  window.open("https://github.com/prasadtalasila/packetanalyzer",'_blank');
	},
	userLogout  : function(){
    sessionStorage.clear();
		Cookies.remove('userName');
		Cookies.remove('userAuth');		
    
		app.navigate("/",{trigger: true});
		alert("You have been logged out. Please login to continue");
        return false;
	},
	loadDash : function(){
		event.preventDefault();
		app.navigate("#/dashboard");
	}
});