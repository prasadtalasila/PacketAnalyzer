window.AnalysisView = BaseView.extend({
		el : $('body'),

		events: {
			 'click #help' :'userHelpPage',
			 'click #logout': 'userLogout',
			 'click #populateTable': 'footerDisplay',
       'click #packetInfo tbody tr': 'rowClick',
       'slidechange #slider': 'setPrefetchValue'
		},
		initialize: function () {
			this.delegateEvents();
      this.globalData = [];
		},
    setPrefetchValue : function(){
      sessionStorage.setItem('sliderValue',$('#slider').slider("option", "value"));  
    },
    rowClick : function(event){
      //get index from table
      var currentRow = Number(event.currentTarget.children[0].innerHTML);
      var tablePacketInfo = document.getElementById('packetInfo');
      var lastRow = tablePacketInfo.rows.length;
      //count number of layers, subtract 2 to discount start and endof p4 graph
      var layersCount = sessionStorage.getItem('layers').split(',').length -2;
      var countForContainer= 1;
      //loop through layers and write to footer
      for(var countForLayers=((currentRow-1)*(layersCount));countForLayers< (((currentRow-1)*(layersCount))+layersCount);countForLayers++){
        botInfo = this.globalData[countForLayers]["_source"]; //array index starts from 0
        var packetDetails="";
        for(var key in botInfo){
          packetDetails += '<span class ="footerPacketDetails">\t'+key + " : " + JSON.stringify(botInfo[key]).replace(/\"/g, "") +' </span>';
        }
        document.getElementById("dataContainer"+countForContainer).innerHTML = packetDetails;
        countForContainer++;
      }

      //multi get data based on which row user is on
      var rowDiff = lastRow - currentRow;
      var sliderValue = sessionStorage.getItem('sliderValue'); 
      if(rowDiff< sliderValue){
        this.multiGet(lastRow+1,lastRow +(sliderValue - rowDiff),layersCount);
      }
    },
		footerDisplay :function(){
      $("#packetInfo tr").remove(); 
      _this = this;
      $('#lowerHalfcol').html('');
      $('#lowerHalfTitle').click(function() {
          $('#lowerHalf').toggle();
      });
      var layerCount =0; //for knowing number of layers
      var layers = sessionStorage.getItem('layers').split(',');
      var lowerHalfAppend = document.getElementById('lowerHalfcol');
      for(var i =1;i<layers.length -1;i++){
        layerCount++;
        var newDivId = 'dataContainer'+i;
        var newDiv = document.createElement('section');
        newDiv.setAttribute('id',newDivId);
        lowerHalfAppend.appendChild(newDiv);
        newDiv.outerHTML = '<li>'+
              '<div class="collapsible-header blue-grey darken-3"><i class="material-icons">filter_drama</i>'+
              layers[i].charAt(0).toUpperCase() + layers[i].slice(1)+'</div>'+
                '<div class="collapsible-body"><span id = dataContainer'+ i+'></span></div>'+
            '</li>';
      }

      //function creates multi-get request and receiving and displaying data initially
      this.multiGet(1,sessionStorage.getItem('sliderValue'),layerCount);
		},
    multiGet : function(startId,endId,layerCount){
      startId = Number(startId);
      endId = Number(endId);
      layerCount = Number(layerCount);
      if(endId > sessionStorage.getItem('packetCount')){
        endId = sessionStorage.getItem('packetCount');
      }
      if(startId<=endId){
        var sessionName = sessionStorage.getItem('sessionName');
        var layers = sessionStorage.getItem('layers').split(',');
        //creating the multi get request from ids and layers
        var multiGetRequest ='{ "docs" : [' ;
        for(var id = startId;id<=endId;id++){
          for(var countForLayers = 1;countForLayers<=layerCount;countForLayers++){
            multiGetRequest = multiGetRequest.concat('{ "_type" : "'+layers[countForLayers]+'", "_id" : "'+id+'" },');
          }
        }
        multiGetRequest = multiGetRequest.substring(0,multiGetRequest.length-1);
        multiGetRequest = multiGetRequest.concat('] }');
        //performance analysis
        var preCallTime = new Date();
          //AJAX call for getting data
          $.ajax({
            url : 'http://localhost:9200/protocol_'+sessionName+'/_mget',
            type : 'POST',
            beforeSend: function() {
              //document.getElementById('packetInfo').innerHTML = '<div class="loader"><div class="line"></div><div class="line"></div><div class="line"></div><div class="line"></div></div>';   
            },
            data : multiGetRequest,
            contentType : 'application/json; charset=utf-8',
            success : function(data) {
              //document.getElementById('packetInfo').innerHTML = ' <thead><tr><th>Packet ID</th><th>Source MAC</th><th>Destination MAC</th></tr></thead><tbody></tbody>';
            }
          }).done(function(data) { 
                var postCallTime = new Date();
                console.log('Operation took ' + (postCallTime.getTime() - preCallTime.getTime()) + ' msec');
                var tablePacketInfo = document.getElementById('packetInfo');
                var lastRow = tablePacketInfo.rows.length;
                var layersCount = sessionStorage.getItem('layers').split(',').length -2;
                for(var dataSetter =0; dataSetter < data['docs'].length; dataSetter++){
                  _this.globalData[dataSetter+(lastRow*layersCount)] = data['docs'][dataSetter];
                }
                _this.populateTable(startId,endId);
              })    
            .fail(function() { alert("Error connecting to elasticsearch!!"); });
        }
    },
    populateTable:function(startId,endId){
      var tablePacketInfo = document.getElementById('packetInfo');
      var lastRow = tablePacketInfo.rows.length;
      var layersCount = sessionStorage.getItem('layers').split(',').length -2;
      for(var id=(startId-1)*layersCount; id<endId*layersCount;id+=layersCount){    //index of array starts from 0
          var row = this.globalData[id];
          var td, tr;
          var tdata = $("#packetInfo tbody")
          var rowSource = row["_source"];
          tr = $("<tr>");
          //packetId
          td = $("<td>").text(rowSource["packetId"]);
          tr.append(td);
          //Source MAC
          td = $("<td>").text(rowSource["src_addr"]);
          tr.append(td);
          //Dest MAC
          td = $("<td>").text(rowSource["dst_addr"]);
          tr.append(td);
          tdata.append(tr);
      }
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
              value:sessionStorage.getItem('sliderValue'),
              slide: function( event, ui ) {
                $("#prefetch-amount").val(ui.value);
              }
            });
            $("#prefetch-amount").val($("#slider").slider("value"));
          });
          $(document).ready(function() {
            document.getElementById("username").innerHTML = Cookies.get('userName');
          });

          //collapsible sidebar intialization
          (function() {
            $(function() {
              var collapseMyMenu, expandMyMenu, hideMenuTexts, showMenuTexts,expandMainMenu,collapseMainMenu;
              expandMyMenu = function() {
                return $("nav.sidebar").removeClass("sidebar-menu-collapsed").addClass("sidebar-menu-expanded");
              };
              expandMainMenu = function() {
                return $(".main").removeClass("normal-width").addClass("max-width");
              };
              collapseMyMenu = function() {
                return $("nav.sidebar").removeClass("sidebar-menu-expanded").addClass("sidebar-menu-collapsed");
              };
              collapseMainMenu = function() {
                return $(".main").removeClass("max-width").addClass("normal-width");
              };
              showMenuTexts = function() {
                return $("nav.sidebar ul a span.expanded-element").show();
              };
              hideMenuTexts = function() {
                return $("nav.sidebar ul a span.expanded-element").hide();
              };
              return $("#justify-icon").click(function(e) {
              if ($(this).parent("nav.sidebar").hasClass("sidebar-menu-collapsed")) {
                expandMyMenu();
                showMenuTexts();
                collapseMainMenu();
                $(this).css({
                color: "#00ACE1"
              });
              } else if ($(this).parent("nav.sidebar").hasClass("sidebar-menu-expanded")) {
                collapseMyMenu();
                expandMainMenu();
                hideMenuTexts();
                $(this).css({
                color: "#FFF"
              });
            }
            return false;
          });
        });
      }).call(this);
    	return this;
		}
});
