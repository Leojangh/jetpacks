function getBoundingOnDeviceRect(e) {
    let rect = e.getBoundingClientRect()
    let ratio = window.devicePixelRatio
    //the value the rect contains is the CSS pixel.
    return {
        "left": rect.left * ratio,
        "top": rect.top * ratio,
        "right": rect.right * ratio,
        "bottom": rect.bottom * ratio
    }
}