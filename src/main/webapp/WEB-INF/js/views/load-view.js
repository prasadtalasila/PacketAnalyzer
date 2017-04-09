window.LoadView =  BaseView.extend({
    el : $('body'),

    events: {
      'click #help' :'userHelpPage',
      'click #logout' : 'userLogout',
      'click #analyzeBtn' : 'analysis',
      'click #validateBtn' : 'graphValidation',
      'click #expList tbody tr': 'rowClick',
      'slidechange #slider': 'setPrefetchValue'
    },
    initialize: function () {
      this.delegateEvents();
      this.userParseGraph = null;
      this._layers = [];
      this.fillTable();
    },  

    fillTable : function(event) {
      $.ajax({
      url:'http://localhost:9200/_stats/index',
      type:'GET',
      contentType: 'application/json; charset=utf-8',
      success:function (data) {
          var indices = Object.keys(JSON.parse(JSON.stringify(data)).indices);
          indices = $.grep(indices, function(item, index) {
            return item.includes('protocol_session');
          });
          for(var id=0; id<indices.length;id+=1){    //index of array starts from 0
          var td, tr;
          var tdata = $("#expList tbody")
          tr = $("<tr>");
          //packetList
          td = $("<td>").text(indices[id]);
          tr.append(td);
          tdata.append(tr);
      }
        },
        error:function(){
          alert("Error running experiment. Please try again later.");
        }
      });
    },
    analysis : function(session){
    sessionStorage.setItem('sessionName',session);
    app.navigate("#/analysis",{trigger: true});
  },

    rowClick: function(event) {
    var session = event.currentTarget.children[0].innerHTML;
    this.analysis(session);
    },

    render: function () {
        $(this.el).html(this.template());
    //slider initialization  
    $(function() {
      $("#slider").slider({
        range: "max",
        min: 20,
        max: 1000,
        step:10,
        value: sessionStorage.getItem('sliderValue'),
        slide: function( event, ui ) {
          $("#prefetch-amount").val(ui.value);
        }
      });
      $("#prefetch-amount").val($("#slider").slider("value"));
    });
    $(document).ready(this.disableAnalyzeButton);
        return this;
    }
});
