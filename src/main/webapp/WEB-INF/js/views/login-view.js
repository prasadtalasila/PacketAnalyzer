window.LoginView = Backbone.View.extend({
        el : $('body'),
        initialize: function () {
        },
        events: {
            'click #signinButton' : 'login',
            'click #signupRedirectButton' : 'signupRedirect',
            'click #signinRedirectButton' : 'signinRedirect',
            'click #signupButton' : 'signup'
        },
        initialize : function() {
        	this.delegateEvents();
        },
        login : function(event) {
            event.preventDefault();
            var formValues = {
                email: $('#inputEmail').val(),
                password: $('#inputPassword').val()
            };
             $.ajax({
            url:'/protocolanalyzer/signin',
             type:'POST',
             contentType: 'application/json; charset=utf-8',
             dataType:'text',
             data: JSON.stringify(formValues),
             success:function (data) {
                 var jsonData = JSON.parse(data);
                 var status = jsonData.status;
                 var loginHash = jsonData.loginHash;
                 if(status === "success") {
                    Cookies.set('userName', $('#inputEmail').val(),{ expires: 1 });
                    Cookies.set('userAuth', loginHash,{ expires: 1 });
                    app.navigate("#/home");
                 }
                 else if(status ==="failure"){
                    alert("Error logging in, please check your details");
                 }
             },
             error:function(){
                console.log("Error logging in");
             }
             });            
        },
        signupRedirect : function(){
            $("#signinform").css("display", "none");
            $("#signupform").css("display", "block");
        },
        signinRedirect : function(){
            $("#signinform").css("display", "block");
            $("#signupform").css("display", "none");
        },
        signup : function(){
            var registerValues = {
              email:$('#registerEmail').val(),
              password: $('#registerPassword').val()
          };
        $.ajax({
            url:'/protocolanalyzer/signup',
            type:'POST',
            contentType: 'application/json; charset=utf-8',
            dataType:'text',
            data: JSON.stringify(registerValues),
              success:function (data) {
                  if(data === "success") {
                    alert("User successfully registered.");
                    signinRedirect();
                    // app.navigate("#");
                  }
                  else if(data === "failure"){ 
                    alert("An account with this email ID already exists");
                  }
              },
              error:function(){
                alert("There was an issue connecting at this time, please try again later");
              }
           });
        },
        render: function () {
            $(this.el).html(this.template());
            return this;
        }
    });

