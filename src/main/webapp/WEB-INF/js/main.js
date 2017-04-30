 
 // Tell jQuery to watch for any 401 or 403 errors and handle them appropriately
$.ajaxSetup({
    statusCode: {
        401: function(){
            // Redirec the to the login page.
            window.location.replace('#login');
         
        },
        403: function() {
            // 403 -- Access denied
            window.location.replace('#login');
        }
    }
});

function loginCheck(callback) {
     $.ajax({
            url:'/protocolanalyzer/auth',
             type:'GET',
             contentType: 'application/json; charset=utf-8',
             dataType:'text',
             data: {loginHash : Cookies.get('userAuth'), user : Cookies.get('userName')},
             success:function (data) {
                 if(data==="success"){
                    callback(true);
                 }
                 else{
                    Cookies.remove('userName');
                    Cookies.remove('userAuth');
                    app.navigate("/",{trigger:true});
                    alert("You have been logged out. Please login to continue");
                    callback(false);
                 }
             },
             error:function(){
                Cookies.remove('userName');
                Cookies.remove('userAuth');
                app.navigate("#",{trigger:true});
                alert("You have been logged out. Please login to continue");
                callback(false);
             }
             });
}

window.Router = Backbone.Router.extend({
    routes: {
        '':'loginViewDisplay',
        'home': 'experimentViewDisplay',
        'config': 'configPlaygroundViewDisplay',
        'analysis': 'analysisViewDisplay',
        'load': 'loadViewDisplay',
        'dashboard': 'dashboardViewDisplay'
    },

    initialize: function () {
    },
    loginViewDisplay: function () {
        _this = this;
        view = new LoginView();
        _this.render(view);
    },
    experimentViewDisplay: function () {    
        _this = this;
        loginCheck(function(result) {
            if (result) {
             view = new ExperimentView();
            }
            _this.render(view);
        });
    },
    configPlaygroundViewDisplay: function () {
        _this = this;
        loginCheck(function(result) {
            if (result) {
             view = new ConfigPlaygroundView();
            }
            _this.render(view);
        });
    },
    analysisViewDisplay: function(){
        _this = this;
        loginCheck(function(result) {
            if (result) {
             view = new AnalysisView();
            }
            _this.render(view);
        });    
    },
    loadViewDisplay: function(){
        _this = this;
        loginCheck(function(result) {
            if (result) {
             view = new LoadView();
            }
            _this.render(view);
        });    
    },
    dashboardViewDisplay: function(){
        _this = this;
        loginCheck(function(result) {
            if (result) {
             view = new DashboardView();
            }
            _this.render(view);
        });    
    },
    render : function(view){
        //Close the current view
        if (this.currentView) {
            this.currentView.undelegateEvents();
        }
        //Set the current view
        this.currentView = view;
        //render the new view
        this.currentView.render();
        return this;
    }
});

templateLoader.load(["DashboardView","LoadView","LoginView","ExperimentView","ConfigPlaygroundView","AnalysisView"],
    function () {
        app = new Router();
        Backbone.history.start();
    });