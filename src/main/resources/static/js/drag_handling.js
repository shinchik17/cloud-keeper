// function handleDragEnter( e ) {
//     // make sure we're dragging a file
//     var dt = ( e && e.dataTransfer );
//     var isFile = ( dt && dt.types && dt.types.length == 1 && dt.types[0] == "Files" );
//     if ( isFile ) {
//         // and, if so, show the overlay
//         showOverlay();
//     }
// }
//
// function handleDragLeave( e ) {
//     // was our dragleave off the page?
//     if ( e && e.pageX == 0 && e.pageY == 0 ) {
//         // then hide the overlay
//         hideOverlay();
//     }
// }
//
// function handleDragOver(e) {
//     // look for any dropzones being hovered
//     var isHovering = document.getElementsByClassName( "dz-drag-hover" ).length > 0;
//     if ( isHovering ) {
//         // found some? then we're over a dropzone and want to allow dropping
//         e.dataTransfer.dropEffect = 'copy';
//     } else {
//         // we're just on the overlay. don't allow dropping.
//         e.dataTransfer.dropEffect = 'none';
//         e.preventDefault();
//     }
// }
//
// function showOverlay() {
//     // only show the overlay if it's not already shown (can prevent flickering)
//     if (getComputedStyle(overlay, null).display == "none" )
//         overlay.style.display = "block";
// }
//
// function hideOverlay() {
//     overlay.style.display = "none";
// }
//
// // listen to dragenter on the window for obvious reasons
// window.addEventListener("dragenter", handleDragEnter);
// // our fullscreen overlay will cover up the window, so we need to listen to it for dragleave events
// overlay.addEventListener("dragleave", handleDragLeave);
// // same thing for dragover
// overlay.addEventListener("dragover", handleDragOver);