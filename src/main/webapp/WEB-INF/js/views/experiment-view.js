window.ExperimentView = BaseView.extend({
		el : $('body'),

		events: {
			 'click #newExperiment' : 'newExperiment',
			 'click #loadExperiment' : 'loadExperiment',
             'click #attachBtn' : 'readSingleFile',
             'click #help' :'userHelpPage',
             'click #logout' : 'userLogout',
             'slidechange #slider': 'setPrefetchValue'
		},

		newExperiment : function(event) {
            event.preventDefault();
        	var formValues = {
        		experimentName : $('#experimentName').val(),
        		description : $('#description').val(),
        		experimenter : Cookies.get('userName'),
        		pcapPath : $('#pcapPath').val()
        	};
            //for passing to backend
            sessionStorage.setItem('pcapPath', $('#pcapPath').val());
            //set dafult slider value
            sessionStorage.setItem('sliderValue',50);
            $.ajax({
            url:'/protocolanalyzer/sessioninfo',
             type:'POST',
             contentType: 'application/json; charset=utf-8',
             dataType:'text',
             data: JSON.stringify(formValues),
             success:function (data) {
                 var jsonData = JSON.parse(data);
                 var status = jsonData.status;
                 if(status === "success") {
                    $.ajax({
                        url:'http://localhost:9200/protocol/_search',
                        type:'POST',
                        contentType: 'application/json; charset=utf-8',
                        dataType:'text',
                        data: '{"query":{"match":{"experimentName":"' + $('#experimentName').val() + '"}}}',
                        success:function (data) {
                            var experimentId = JSON.parse(data).hits.hits[0]._id;
                            sessionStorage.setItem('experimentId', experimentId);
                            $.ajax({
                                url:'http://localhost:9200/protocol/delegate/' + experimentId,
                                type:'POST',
                                contentType: 'application/json; charset=utf-8',
                                dataType:'text',
                                data: '{"experimenter" : "' + Cookies.get('userName') + '","collaborators" : ""}',
                                success:function (data) {
                                    app.navigate("#/config");
                                }
                            });
                        }
                    });
                 }
                 else if(status ==="failure"){
                    alert(jsonData.remark);
                 }
             },
             error:function(){
                console.log("Error connecting to the server!");
             }
             });

    	},
        setPrefetchValue : function(){
            sessionStorage.setItem('sliderValue',$('#slider').slider("option", "value"));  
        },
    	loadExperiment : function() {
            app.navigate("#/load");
    	},
		initialize: function () {
			this.delegateEvents();
		},
        readSingleFile : function(evt) {
            var f = document.getElementById("fileInput").files[0]; 
            if (f) {
                var r = new FileReader();
                r.onload = function(e) { 
                var userParseGraph = e.target.result;
                var temp = userParseGraph;
            }
            r.readAsText(f);
            } else { 
                alert("Failed to load file");
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
                    value: sessionStorage.getItem('sliderValue'),
                    slide: function( event, ui ) {
                        $("#prefetch-amount").val(ui.value);
                    }
                });
                $("#prefetch-amount").val($("#slider").slider("value"));
            });
            return this;
		}
	});
