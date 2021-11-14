document.addEventListener("click", function(event){
    let rect = getBoundingOnDeviceRect(event.target)
    
    Android.transit(rect.left, rect.top, rect.right, rect.bottom)
});