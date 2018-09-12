(function($) {
	"use strict"

	///////////////////////////
	// Preloader
	$(window).on('load', function() {
		$("#preloader").delay(600).fadeOut();
	});

	///////////////////////////
	// Scrollspy
	$('body').scrollspy({
		target: '#nav',
		offset: $(window).height() / 2
	});

	///////////////////////////
	// Smooth scroll
	$("#nav .main-nav a[href^='#']").on('click', function(e) {
		e.preventDefault();
		var hash = this.hash;
		$('html, body').animate({
			scrollTop: $(this.hash).offset().top
		}, 600);
	});

	$('#back-to-top').on('click', function(){
		$('body,html').animate({
			scrollTop: 0
		}, 600);
	});

	///////////////////////////
	// Btn nav collapse
	$('#nav .nav-collapse').on('click', function() {
		$('#nav').toggleClass('open');
	});

	///////////////////////////
	// Mobile dropdown
	$('.has-dropdown a').on('click', function() {
		$(this).parent().toggleClass('open-drop');
	});

	///////////////////////////
	// On Scroll
	$(window).on('scroll', function() {
		var wScroll = $(this).scrollTop();

		// Fixed nav
		wScroll > 1 ? $('#nav').addClass('fixed-nav') : $('#nav').removeClass('fixed-nav');

		// Back To Top Appear
		wScroll > 700 ? $('#back-to-top').fadeIn() : $('#back-to-top').fadeOut();
	});

	///////////////////////////
	// magnificPopup
	$('.work').magnificPopup({
		delegate: '.lightbox',
		type: 'image'
	});

	///////////////////////////
	// Owl Carousel
	$('#about-slider').owlCarousel({
		items:1,
		loop:true,
		margin:15,
		nav: true,
		navText : ['<i class="fa fa-angle-left"></i>','<i class="fa fa-angle-right"></i>'],
		dots : true,
		autoplay : true,
		animateOut: 'fadeOut'
	});

	$('#testimonial-slider').owlCarousel({
		loop:true,
		margin:15,
		dots : true,
		nav: false,
		autoplay : true,
		responsive:{
			0: {
				items:1
			},
			992:{
				items:2
			}
		}
	});
	
	///////////////////////////
	// Upload image
	$('button[type=submit]').click(function(e) {
	    e.preventDefault();
	    //Disable submit button
	    $(this).prop('disabled',true);
	    
	    var form = document.forms[0];
	    var formData = new FormData(form);
	    	
	    // Ajax call for file uploaling
	    var ajaxReq = $.ajax({
	      url : 'channel/fileUpload',
	      type : 'POST',
	      data : formData,
	      cache : false,
	      contentType : false,
	      processData : false,
	      xhr: function(){
	        //Get XmlHttpRequest object
	         var xhr = $.ajaxSettings.xhr() ;
	        
	        //Set onprogress event handler 
	         xhr.upload.onprogress = function(event){
	          	var perc = Math.round((event.loaded / event.total) * 100);
	          	$('#progressBar').text(perc + '%');
	          	$('#progressBar').css('width',perc + '%');
	         };
	         return xhr ;
	    	},
	    	beforeSend: function( xhr ) {
	    		//Reset alert message and progress bar
	    		$('#alertMsg').text('');
	    		$('#progressBar').text('');
	    		$('#progressBar').css('width','0%');
	              }
	    });
	  
	    // Called on success of file upload
	    ajaxReq.done(function(msg) {
	      $('#alertMsg').text(msg);
	      $('input[type=file]').val('');
	      $('button[type=submit]').prop('disabled',false);
	    });
	    
	    // Called on failure of file upload
	    ajaxReq.fail(function(jqXHR) {  
	    	if(jqXHR.status == 400){
	  	      $('#alertMsg').text(jqXHR.responseText+'('+jqXHR.status+
	  	      		' - '+jqXHR.statusText+')');
	  	      $('button[type=submit]').prop('disabled',false);	    		
	    	} else{
	    	  $('#alertMsg').text("Some error occurred. Please try again."+'('+jqXHR.status+
	 	  	      		' - '+jqXHR.statusText+')');
	 	  	  $('button[type=submit]').prop('disabled',false);	  
	    	}
	    });
	  });
	
})(jQuery);

// Save JSON
function onclickFunction(id){
	$.ajax({
		  type: 'GET',
		  url: "/Tracker/channel/"+id,
		  cache: false,
		  success: function(data){
		    //console.log(data);		  
		    var json = JSON.stringify(data);
		    var blob = new Blob([json], {type: "application/json"});
		    var url  = URL.createObjectURL(blob);
		    console.log(data);
		    console.log("aaaaaa");
		    
		    var a = document.getElementById("anchorDownload"+id);
		    a.href        = url;
		    a.click();
		    console.log(a);
		  }
		});	
}
