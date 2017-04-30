window.DashboardView = BaseView.extend({
  el : $('body'),

  events: {
     'click #help' :'userHelpPage',
     'click #logout' : 'userLogout',
     'click #newExperiment' : 'newExperiment',
     'click #loadExperiment' : 'loadExperiment',
     'click #navclick': 'loadDash'
  },

  newExperiment : function(event) {
    event.preventDefault();
    app.navigate("#/home");
  },
  loadExperiment : function() {
    event.preventDefault();
    app.navigate("#/load");
  },
  initialize: function () {
    this.delegateEvents();
  },
  render: function () { 
    $(this.el).html(this.template());
        $(document).ready(function() {
          document.getElementById("username").innerHTML = Cookies.get('userName');
        });
        return this;
  }
});
