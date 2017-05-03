window.LoadView =  BaseView.extend({
    el : $('body'),

    events: {
      'click #help' :'userHelpPage',
      'click #logout' : 'userLogout',
      'click #loadExperiment' : 'load',
      'click #validateBtn' : 'graphValidation',
      'click #expListMy tbody tr': 'rowClick',
      'click #expListShared tbody tr': 'rowClick',
      'slidechange #slider': 'setPrefetchValue',
      'change #share' : 'share',
      'click #shareExperiment' : 'shareExperiment',
      'click #navclick': 'loadDash'
    },
    initialize: function () {
      this.fillShare();
      LoadView.globalMy = [];
      LoadView.globalShared = [];
      this.fillTable();
    },  

    fillTable : function(event) {
      $.ajax({
      url:'http://localhost:9200/protocol/info/_search',
      type:'POST',
      data : '{"from":0,"size":200,"query":{"multi_match":{"query":"'+Cookies.get('userName')+'","fields":["experimenter","collaborators"]}}}',
      contentType: 'application/json; charset=utf-8',
      success:function (data) {
          var jsonData = JSON.parse(JSON.stringify(data)).hits.hits;
          for(var id=1; id<=jsonData.length;id+=1){
            if (jsonData[id-1]._source.experimenter==Cookies.get('userName')) {
              LoadView.globalMy.push(jsonData[id-1]);
            }
            if (jsonData[id-1]._source.collaborators.indexOf(Cookies.get('userName')) !== -1) {
              LoadView.globalShared.push(jsonData[id-1]);
            }
          }
          for(var id=1; id<=LoadView.globalMy.length;id+=1){    //index of array starts from 0
          var td, tr;
          var tdata = $("#expListMy tbody")
          tr = $("<tr>");
          td = $("<td>").text(id);
          tr.append(td);
          //packetList
          td = $("<td>").text(LoadView.globalMy[id-1]._source.experimentName);
          tr.append(td);
          td = $("<td style='display:none'>").text("Owned");
          tr.append(td);
          tdata.append(tr);
      }
      for(var id=1; id<=LoadView.globalShared.length;id+=1){    //index of array starts from 0
          var td, tr;
          var tdata = $("#expListShared tbody")
          tr = $("<tr>");
          td = $("<td>").text(id);
          tr.append(td);
          //packetList
          td = $("<td>").text(LoadView.globalShared[id-1]._source.experimentName);
          tr.append(td);
          td = $("<td style='display:none'>").text("Shared");
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
            if (options[id-1]._source.email == Cookies.get('userName')) {
              continue;
            }
            var opt = document.createElement("option");
            opt.value= options[id-1]._source.email;
            opt.innerHTML = options[id-1]._source.email;
            select.appendChild(opt);
          }
          $('select').material_select();
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
          sessionStorage.setItem('sliderValue',10000);
          app.navigate("#/analysis",{trigger: true});
        },
        error:function(){
          alert("Error running experiment. Please try again later.");
        }
      });
    },

    rowClick: function(event) {
    $("#shareWrap *").removeAttr("disabled");
    $("#noneop").attr("disabled", "disabled");
    document.getElementById('loadExperiment').disabled = false;
    document.getElementById('shareExperiment').disabled = false;
    var id = Number(event.currentTarget.children[0].innerHTML) - 1;
    var share = event.currentTarget.children[2].innerHTML;
    $('#sidePanel').html('');
    var sidePanel = document.getElementById('sidePanel');
    var newDiv = document.createElement('section');
    sidePanel.appendChild(newDiv);
    var data = [];
    if (share == "Owned") {
      data = LoadView.globalMy;
      var col = data[id]._source.collaborators.split(/\s*,\s*/);
      var selectEl = $('select');
      selectEl.material_select();
      selectEl.val(col);
      selectEl.material_select('refresh');
    } else {
      data = LoadView.globalShared;
      // document.getElementById("shareWrap").disabled = true;
      $("#shareWrap *").attr("disabled", "disabled");
      document.getElementById('shareExperiment').disabled = true;
    }
    newDiv.outerHTML = '<p>'+
          'Experiment Name : ' + data[id]._source.experimentName + '<br/>' +
          'Description : '+ data[id]._source.description +'<br/>' +
          'PCAP Path : '+ data[id]._source.pcapPath + '<br/>' +
          'Session: '+ data[id]._source.id +
        '</p>';
    document.getElementById('loadExperiment').setAttribute('session',data[id]._source.id);
    document.getElementById('shareExperiment').setAttribute('expId',data[id]._id);
    },

    share: function(event) {
      
    },

    shareExperiment: function(event) {
      var share = $('select').val();
      var experimentId = document.getElementById('shareExperiment').getAttribute('expId')
      $.ajax({
            url:'http://localhost:9200/protocol/info/' + experimentId + '/_update',
            type:'POST',
            contentType: 'application/json; charset=utf-8',
            dataType:'text',
            data: '{"doc":{"collaborators":"' + share + '"}}',
            success:function (data) {
              alert("Successfully shared with " + share);
              app.navigate("#/load",{trigger: true});
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
      document.getElementById("username").innerHTML = Cookies.get('userName');
    });
        return this;
    }
});
