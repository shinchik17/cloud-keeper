// import Dropzone from 'dropzone'
// window.addEventListener("DOMContentLoaded", function () {

    Dropzone.autoDiscover = false;

    // Note that the name "myDropzone" is the camelized
    // id of the form.
    Dropzone.options.myDropzone = {
        url: "/files",
        // renameFile: function (file) {
        //     return file.fullPath;
        // },
        paramName: "documents",
        uploadMultiple: true,
        autoProcessQueue: true,
        maxFilesize: 100,
        maxFiles: 100,
        parallelUploads: 100,
        dictFileTooBig: "Размер файла больше {{maxFilesize}} мб!",
        // Configuration options go here
    };

    Dropzone.discover()

    // TODO: handle functions such as remove after fail an so on

// })