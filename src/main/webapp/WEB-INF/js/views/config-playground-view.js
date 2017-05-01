window.ConfigPlaygroundView =  BaseView.extend({
	el : $('body'),

	events: {
		'click #help' :'userHelpPage',
		'click #logout' : 'userLogout',
    'click #analyzeBtn' : 'analysis',
    'click #validateBtn' : 'graphValidation',
    'slidechange #slider': 'setPrefetchValue'
	},
	initialize: function () {
		this.delegateEvents();
    this.userParseGraph = null;
    this._layers = [];
	},	
  setPrefetchValue : function(){
    sessionStorage.setItem('sliderValue',$('#slider').slider("option", "value"));  
  },
	analysis : function(event){
		event.preventDefault();
    _this = this;
    var pcapPath = sessionStorage.getItem('pcapPath');
    $.ajax({
      url:'/protocolanalyzer/session/analysis',
      type:'GET',
      contentType: 'application/json; charset=utf-8',
      dataType:'text',
      data: {pcapPath : pcapPath,graph : _this.userParseGraph},
      success:function (data) {
          var jsonData = JSON.parse(data);
          var sessionName = jsonData.sessionName;
          var packetCount = jsonData.packetCount;
          sessionStorage.setItem('sessionName',sessionName);
          sessionStorage.setItem('packetCount', packetCount);
          sessionStorage.setItem('layers',_this._layers);
          var experimentId = sessionStorage.getItem('experimentId');
          $.ajax({
            url:'http://localhost:9200/protocol/info/' + experimentId + '/_update',
            type:'POST',
            contentType: 'application/json; charset=utf-8',
            dataType:'text',
            data: '{"doc":{"id":"' + sessionName + '"}}',
            success:function (data) {
                app.navigate("#/analysis",{trigger: true});
            }
          });
        },
        error:function(){
          alert("Error running experiment. Please try again later.");
        }
      });
	},

  graphValidation : function(){
    var f = document.getElementById("fileInput").files[0];
    var editor = ace.edit("editor");
    var pre = editor.getSession().getValue();
    _this = this;
    if (f || pre) {
      function validator(code, type) { 
          var flag =0;
          //test graph is the main graph, user graph checked against this
          //ECMA 6 : replace with backticks , is cleaner
          var testGraph = 'graph start {'+
            'ethernet;'+
          '}'+
          'graph ethernet {'+
            'switch(ethertype) {'+
              'case 0800: ipv4;'+
            '}'+
          '}'+
          'graph ipv4 {'+
            'switch(protocol) {'+
              'case 06:  tcp;'+
            '}'+
          '}'+
          'graph tcp {'+
          '}'+
          'graph end {'+
          '}';
          var flag = 0;
          var testParsing = testGraph.split(/[\{\}]/);
          for (var i = 0; i < testParsing.length; i++) {
            testParsing[i] = testParsing[i].trim();
          }
          //user provided p4 graph
          var graphIndices =[]; //for keeping index of the graph elements
          var graphValues= []; //for keeping value of graph elements
          var indexOfGraphElements=0;
          var mainCounter =0; //for seeing if graph has appropriate number of layer matches

          if (type=='editor')
            _this.userParseGraph = code;
          else if (type=='file')
            _this.userParseGraph = code.target.result;

          var userParsing = _this.userParseGraph.split(/[\{\};]/);
          for (var i = 0; i < userParsing.length; i++) {
            userParsing[i] = userParsing[i].trim();
            if(userParsing[i].search('graph')===0){
              graphIndices[indexOfGraphElements] = i;
              graphValues[indexOfGraphElements] = userParsing[i];
              indexOfGraphElements++;
            }  
            if(userParsing[i].split(' ')[0].trim().search('case')===0){    
              mainCounter++;
            }  
          }
          for (var k =0;k<indexOfGraphElements;k++){
            _this._layers[k] =  graphValues[k].split(' ')[1];
            if(k===0 && _this._layers[k] !=='start') {
              //start condition check
              flag++;
            }
            if(k===(indexOfGraphElements-1) && _this._layers[k]!=='end' ) { 
              //end condition check
              flag++; 
            }
          }  
          //trim results
          for (var i = 0; i < _this._layers.length; i++) {
            _this._layers[i] = _this._layers[i].trim();
          }
          //start node check
          if(userParsing[1] !== _this._layers[1]){
            flag++;
          }
          //create map of next _this._layers : key is current layer and values are all the possibilities for next layer
          var nextLayers={};
          var countCases =0;
          var countCases2=0;
          for(var i =0;i< userParsing.length;i++){
            if(userParsing[i].search('graph')===0){
              nextLayers[_this._layers[countCases-1]] = nextLayerList;
              var nextLayerList=[];
              countCases++;
              countCases2=0;
            }
            if(userParsing[i].search('case')===0){ 
              var temp = userParsing[i].split(':')[1].trim();
              nextLayerList.push(temp);
              countCases2++;
            }
          }
          // _this._layers contains the list of _this._layers possible for this experiment.
          //nextLayers is a 2D array containing the next possible _this._layers for each layer.
          //checking valid next _this._layers
          var matchOneLayerToNext =0;
          //length-1 because the last layer will not have cases within it, even if it does, those are ignored
            for(key in nextLayers){
              if(nextLayers.hasOwnProperty(key)){
                var values = nextLayers[key];
                if(values!== undefined){
                  for(var prop=0;prop<values.length;prop++){
                    for(var i=0;i < _this._layers.length-1;i++){   
                      if(_this._layers[i]===values[prop]){
                        matchOneLayerToNext++;
                      }
                    }
                  }
                }
              }
            }
          if(matchOneLayerToNext!==mainCounter){
            flag++;
          }
          if(flag===0){
            alert("P4 Graph is valid");
            document.getElementById('analyzeBtn').disabled = false; 
          }
          else{
            alert("P4 Graph is not valid, please enter a valid configuration");
          }
      }
      var r = new FileReader();
      r.onload = function(e) { 
          validator(e, 'file');
      }
      if (f)
        r.readAsText(f);
      else
        validator(pre, 'editor');


    } else { 
      alert("Failed to load file");
    }
  },
  isInArray: function(array, search)
  {
    return array.indexOf(search) >= 0;
  },
  indices : function(source,find){
    var result = [];
    for (i = 0; i < source.length; ++i) {
      if (source.substring(i, i + find.length) == find) {
        result.push(i);
      }
    }
    return result;
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
      document.getElementById('analyzeBtn').disabled = true;
      document.getElementById("username").innerHTML = Cookies.get('userName');
    });
		return this;
	}
});
