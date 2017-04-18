window.LoadView =  BaseView.extend({
    el : $('body'),

    events: {
      'click #help' :'userHelpPage',
      'click #logout' : 'userLogout',
      'click #loadExperiment' : 'load',
      'click #validateBtn' : 'graphValidation',
      'click #expList tbody tr': 'rowClick',
      'slidechange #slider': 'setPrefetchValue',
      'change #share' : 'share',
      'click #shareExperiment' : 'shareExperiment'
    },
    initialize: function () {
      this.fillShare();
      LoadView.globalData = [];
      LoadView.globalDelegate = [];
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

    fillShare : function(event) {
      $.ajax({
      url:'http://localhost:9200/protocol/credentials/_search',
      type:'GET',
      contentType: 'application/json; charset=utf-8',
      success:function (data) {
          var options = JSON.parse(JSON.stringify(data)).hits.hits;
          var select = document.getElementById("share");
          for(var id=1; id<=options.length;id+=1) {
            var opt = document.createElement("option");
            opt.value= id;
            opt.innerHTML = options[id-1]._source.email;
            select.appendChild(opt);
          }
          $('select').material_select();
          $.ajax({
            url:'http://localhost:9200/protocol/delegate/_search',
            type:'GET',
            contentType: 'application/json; charset=utf-8',
            success:function (data) {
                LoadView.globalDelegate = JSON.parse(JSON.stringify(data)).hits.hits;
              }
            });
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
    document.getElementById('loadExperiment').disabled = false;
    document.getElementById('shareExperiment').disabled = false;
    var id = Number(event.currentTarget.children[0].innerHTML) - 1;
    $('#sidePanel').html('');
    var sidePanel = document.getElementById('sidePanel');
    var newDiv = document.createElement('section');
    sidePanel.appendChild(newDiv);
    newDiv.outerHTML = '<p>'+
          'Experiment Name : ' + LoadView.globalData[id]._source.experimentName + '<br/>' +
          // 'Experimenter : '+ LoadView.globalData[id]._source.experimenter + '<br/>' +
          'Description : '+ LoadView.globalData[id]._source.description +'<br/>' +
          'PCAP Path : '+ LoadView.globalData[id]._source.pcapPath + '<br/>' +
          'Session: '+ LoadView.globalData[id]._source.id +
        '</p>';
    document.getElementById('loadExperiment').setAttribute('session',LoadView.globalData[id]._source.id);
    document.getElementById('shareExperiment').setAttribute('expId',LoadView.globalData[id]._id);
    var col = LoadView.globalDelegate[id]._source.collaborators.split(/\s*,\s*/)
    for (var i = 0; i < col.length; i++) {
      // var opt = document.querySelector('select#share option[value=' + col[i] + ']')
      // // document.getElementById('share').value = col[i];
      // alert(opt);
    }
    },

    share: function(event) {
      
    },

    shareExperiment: function(event) {
        var share = [];
      $('#share :selected').each(function(i, selected){ 
        share[i] = $(selected).text(); 
      });
      // var index = share.indexOf(Cookies.get('userName'));
      // if (index) {
      //   share.splice(index, 1);
      // }
      share.shift();
      var experimentId = document.getElementById('shareExperiment').getAttribute('expId')
      $.ajax({
            url:'http://localhost:9200/protocol/delegate/' + experimentId + '/_update',
            type:'POST',
            contentType: 'application/json; charset=utf-8',
            dataType:'text',
            data: '{"doc":{"collaborators":"' + share + '"}}',
            success:function (data) {
                alert("Successfully shared with " + share);
            }
          });
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
    $(document).ready(function() {
      document.getElementById('shareExperiment').disabled = true;
      document.getElementById('loadExperiment').disabled = true;
    });
        return this;
    }
});
