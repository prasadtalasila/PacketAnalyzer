window.LoadView =  BaseView.extend({
    el : $('body'),

    events: {
      'click #help' :'userHelpPage',
      'click #logout' : 'userLogout',
      'click #loadExperiment' : 'load',
      'click #validateBtn' : 'graphValidation',
      'click #expList tbody tr': 'rowClick',
      'slidechange #slider': 'setPrefetchValue'
    },
    initialize: function () {
      LoadView.globalData = [];
      this.fillTable();
    },  

    fillTable : function(event) {
      $.ajax({
      url:'http://localhost:9200/protocol/info/_search',
      type:'GET',
      contentType: 'application/json; charset=utf-8',
      success:function (data) {
          LoadView.globalData = JSON.parse(JSON.stringify(data)).hits.hits;
          for(var id=1; id<=LoadView.globalData.length;id+=1){    //index of array starts from 0
          var td, tr;
          var tdata = $("#expList tbody")
          tr = $("<tr>");
          td = $("<td>").text(id);
          tr.append(td);
          //packetList
          td = $("<td>").text(LoadView.globalData[id-1]._source.experimentName);
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
    load : function(event){
    var session = document.getElementById('loadExperiment').getAttribute('session');
    $.ajax({
      url:'http://localhost:9200/protocol_' + session + '/_search',
      type:'POST',
      contentType: 'application/json; charset=utf-8',
      dataType:'text',
      data: '',
      success:function (data) {
          var packetCount = JSON.parse(data).hits.total;
          sessionStorage.setItem('sessionName',session.replace('protocol_',''));
          sessionStorage.setItem('packetCount', packetCount);
          sessionStorage.setItem('layers','start,ethernet,ipv4,tcp,end');
          sessionStorage.setItem('sliderValue','50');
          app.navigate("#/analysis",{trigger: true});
        },
        error:function(){
          alert("Error running experiment. Please try again later.");
        }
      });
    },

    rowClick: function(event) {
    var id = Number(event.currentTarget.children[0].innerHTML) - 1;
    $('#sidePanel').html('');
    var sidePanel = document.getElementById('sidePanel');
    var newDiv = document.createElement('section');
    sidePanel.appendChild(newDiv);
    newDiv.outerHTML = '<p>'+
          'Experiment Name : ' + LoadView.globalData[id]._source.experimentName + '<br/>' +
          'Experimenter : '+ LoadView.globalData[id]._source.experimenter + '<br/>' +
          'Description : '+ LoadView.globalData[id]._source.description +'<br/>' +
          'PCAP Path : '+ LoadView.globalData[id]._source.pcapPath + '<br/>' +
          'Session: '+ LoadView.globalData[id]._source.id +
        '</p>';
    document.getElementById('loadExperiment').setAttribute('session',LoadView.globalData[id]._source.id);
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
