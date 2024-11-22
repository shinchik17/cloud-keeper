Dropzone.autoDiscover = false;

// Note that the name "myDropzone" is the camelized
// id of the form.

const baseUrl = "http://" + window.location.host + window.location.pathname

Dropzone.options.myDropzone = {
    url: baseUrl + "files",
    renameFile: function (file) {
        return file.fullPath;
    },
    paramName: "documents",
    uploadMultiple: true,
    autoProcessQueue: true,
    maxFilesize: 100,
    maxFiles: 100,
    parallelUploads: 100,
    dictFileTooBig: "Размер файла больше {{maxFilesize}} мб!",

    // Note: using "function()" here to bind `this` to
    // the Dropzone instance.
    init: function () {
        this.on("addedfile", file => {
            console.log("A file has been added");
        });

        this.on("success", function (file, response) {
            this.removeAllFiles();
        });

        this.on("error", function (file, message, xhr) {

            // let errorSpan = file.previewElement.querySelector('.dz-error-message');
            // let errorMessage = xhr
            // if (xhr.status === 404){
            //     a = file.previewElement
            // }
            file.previewElement.querySelector('.dz-error-message').innerText = xhr && xhr.response
                ? JSON.parse(xhr.response).message
                : message;
        });


        // this.on("sendingmultiple", function (files, xhr, formData) {
        //     formData.append('data', JSON.stringify(files));
        //     let currentPath = document.querySelector('input[name="currentPath"]').value;
        //     formData.append('path', currentPath);
        // });
    }
};
    // TODO: include into index regular upload form and check its fields
Dropzone.discover()


function mkDir(mkBtn){
    let url = baseUrl + mkBtn.getAttribute("data-mk-path");
    let path = document.getElementById("my-dropzone").querySelector("input[name='path']").value
    path = typeof (path) === "string" ? path : "";
    let dirname = document.getElementById("mk-dir-name").value
    let csrfToken = document.querySelector("input[name='_csrf']").value;

    let formData = new FormData();
    formData.append("path", path);
    formData.append("objName", String(dirname));
    formData.append("_csrf", csrfToken)

    fetch(url, {
        method: 'POST',
        headers: {
            'ContentType': 'application/x-www-form-urlencoded;utf-8',
        },
        body: formData
    })
        .then(response => {
            if (response.ok) {
                console.log(`Folder added successfully`)
                let href = path === "" ? baseUrl : baseUrl + `?path=${path}`
                location.replace(href)
            }
        })

        .catch(error => {
            console.error('Error: ', error)
        })

}


//
// myDropzone.on("sendingmultiple", function (files, xhr, formData) {
//     formData.append('data', JSON.stringify(files));
//     let currentPath = document.querySelector('input[name="currentPath"]').value;
//     formData.append('path', currentPath);
// });
//
// myDropzone.on("success", function (file, response) {
//     var data = document.getElementById("data");
//     data.style.display = "block";
//     data.innerHTML = response;
//     this.removeFile(file);
// });
//
// myDropzone.on("error", function (file, message, xhr) {
//     file.previewElement.querySelector('.dz-error-message').innerText = xhr && xhr.response
//         ? JSON.parse(xhr.response).message
//         : message;
// });
//
// myDropzone.on("uploadprogress", function (file, progress, bytesSent) {
//     file.previewElement.querySelector('.dz-progress .dz-upload').style.width = progress + "%";
// });
//
// myDropzone.on("addedfile", function () {
//     document.getElementById("startUpload").style.display = "block";
//     document.getElementById("clearQueue").style.display = "block";
// });
//
// myDropzone.on("removedfile", function () {
//     if (myDropzone.getQueuedFiles().length === 0) {
//         document.getElementById("startUpload").style.display = "none";
//         document.getElementById("clearQueue").style.display = "none";
//     }
// });
//
// document.getElementById('startUpload').addEventListener('click', function () {
//     myDropzone.processQueue();
// });
//
// document.getElementById('clearQueue').addEventListener('click', function () {
//     myDropzone.removeAllFiles();
// })
// TODO: handle functions such as remove after fail an so on
