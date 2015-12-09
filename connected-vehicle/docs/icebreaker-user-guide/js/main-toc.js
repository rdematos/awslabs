/**
 * interactivity for the left ToC
 */

var AWSDocs = AWSDocs || {};

$(document).ready(function () {
    console.log("Loaded main-toc.js");
    
    $("li.awstoc.closed ul").hide();    
    
    $("li.awstoc").bind("click", function (event) {
        event.stopPropagation();
        
        if (event.target.nodeName == "LI") {
            if ($(event.target).hasClass("closed") || $(event.target).hasClass("opened")) {
                $(event.target).toggleClass("closed opened");
                if ($(event.target).hasClass("closed")) {
                    $(event.target).children("ul").hide();
                }
                if ($(event.target).hasClass("opened")) {
                    $(event.target).children("ul").show();
                }
            }
        }
    });
    
    //if toc scroll cookies set, use that to set current page toc scroll position 
    AWSDocs.setTocScroll();  
    
    $("#search-icon").click(AWSDocs.resizeSearchQueryBox);
    
    var ua = navigator.userAgent.toLowerCase();
    var mobile = (ua.indexOf("mobile") != -1);
    
    if (! mobile) {
        mobile = (ua.indexOf("silk") != -1);
    }
    
    if (! mobile) {
        $("#left-column").resizable({
            handles: "e"
        });     
        
        AWSDocs.resizePanes();
        window.onresize = AWSDocs.resizePanes;
    }
    
    // Need to handle unfixing top nav for silk browsers here for better zooming experience
    if (ua.indexOf("silk") != -1) {
        $("#aws-nav").css("position", "relative");
        $("#content-container").css("margin-top", "0px");
    }
    
});

$(window).unload (function() {
    //set toc cookies before leaving page    
    AWSDocs.setTocCookies();
});

AWSDocs.setTocCookies = function () {
    var currentUrl = window.location.href;
    currentUrl = currentUrl.split('#').pop().split('?').pop();
    var fileName = currentUrl.substring(currentUrl.lastIndexOf('/') + 1);
    var urlPathIndex = currentUrl.indexOf(fileName);
    var urlMatchString = currentUrl.substring(0, urlPathIndex);
    
    //using setCookie from locale-selector.js
    //set toc scroll position cookie for next page load  
    SetCookie ("aws-doc-toc-pos", $("#left-column").scrollTop());
    //set url cookie to determine if toc scroll position will be valid
    SetCookie ("aws-doc-toc-url", urlMatchString);
}

AWSDocs.setTocScroll = function () {
    try
    {
        //using getCookie from locale-selector.js
        var tocCookieName = "aws-doc-toc-pos";
        var tocCookieValue = getCookie(tocCookieName);
        var urlCookieName = "aws-doc-toc-url";
        var urlCookieValue = getCookie(urlCookieName);
        var currentLocation = window.location.href;
        //get filename from url (remove query strings or anchors from url first)
        currentLocation = currentLocation.split('#').pop().split('?').pop();
        var fileName = currentLocation.substring(currentLocation.lastIndexOf('/') + 1);
        var currentUrlPathIndex = currentLocation.indexOf(fileName);
        var currentUrlPath = currentLocation.substring(0, currentUrlPathIndex);
        
        // check if both cookies are set
        if (tocCookieValue.length !== null && urlCookieValue.length !== null)
        {
            //only reset the scroll top value if we're in the same guide 
            //otherwise, toc will be different and scroll value may not be valid
            if (currentUrlPath === urlCookieValue)
            {
            $("#left-column").scrollTop(tocCookieValue);
            }
        }
    }
    catch (error)
    {
       //nothing to do - the toc scroll will be set to position 0
    } 
}

AWSDocs.resizePanes = function () {
    var leftWidth = $("#left-column").width();
    var $mainCol = $("#main-column");
    var $main = $("#main");
    
    if (leftWidth !== null) {
            leftWidthInPx = leftWidth + "px";
            $mainCol.css("margin-left", leftWidthInPx);
        }
       
    $("#search-query").width($("#search-select").width() - 27);
}

AWSDocs.resizeSearchQueryBox = function () {
    $("#finegrainedSearch").toggle();
    $("#search-query").width($("#left-col-top-content").width() - 27);
}