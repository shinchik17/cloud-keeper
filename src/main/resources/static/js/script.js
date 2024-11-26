Dropzone.autoDiscover = false;
const baseUrl = "http://" + window.location.host + window.location.pathname
let renameModal = document.getElementById("rename-modal")
let fileInput = document.getElementById("file-upload");
let uploadBtn = document.getElementById("upload-btn");
let successModalElement = document.getElementById('success-modal')
successModalElement.addEventListener("hide.bs.modal", reloadPage)
successModalElement.querySelector("#success-modal .modal-footer button").addEventListener("click", reloadPage)
let errorModalElement = document.getElementById('error-modal')
let successModal = new bootstrap.Modal(successModalElement)
let errorModal = new bootstrap.Modal(errorModalElement)


renameModal.addEventListener("show.bs.modal", function (event) {
    let button = event.relatedTarget
    let objName = button.getAttribute("data-obj-name")

    let modalInput = document.getElementById("new-obj-name")
    modalInput.value = removeExtension(objName)

    let okBtn = renameModal.querySelector(".modal-footer .btn-primary")
    okBtn.setAttribute("data-obj-name", objName)
})
uploadBtn.addEventListener("click", () => fileInput.click())
fileInput.addEventListener("change", uploadObj)

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
    dictFileTooBig: "One or more files size exceeds limit ({{maxFilesize}} MB).",
    dictMaxFilesExceeded: "You have reached the maximum uploading files limit ({{maxFiles}}). Not all files would be uploaded.",

    // Note: using "function()" here to bind `this` to
    // the Dropzone instance.
    init: function () {
        // this.on("addedfile", file => {
        //     console.log("A file has been added");
        // });

        this.on("success", function (file, response) {
            if (file.previewElement) {
                file.previewElement.classList.add("dz-success");
            }
        });

        this.on("successmultiple", function (file, message, xhr) {
            setSuccessMessage("Files uploaded successfully");
            successModal.show();
            this.removeAllFiles();
        });

        this.on("error", function (file, message, xhr) {
            setErrorMessage(xhr && xhr.response ? JSON.parse(xhr.response).message : message);
            errorModal.show()
            this.removeAllFiles(true);
        });

    }
};

Dropzone.discover()


function mkDir(mkBtn) {
    let url = baseUrl + mkBtn.getAttribute("data-req-path");
    let dirname = document.getElementById("mk-dir-name").value
    let formData = new FormData();
    formData.append("path", getCurPath());
    formData.append("objName", dirname);
    formData.append("_csrf", getCsrfToken())

    fetch(url, {
        method: 'POST',
        headers: {
            'ContentType': 'application/x-www-form-urlencoded;utf-8',
        },
        body: formData
    })
        .then(response => {
            if (response.ok) {
                console.info(`Folder ${dirname} has been created successfully`)

                // setSuccessMessage(`Folder ${dirname} has been created successfully`)
                // let href = getCurPath() === "" ? baseUrl : baseUrl + `?path=${getCurPath()}`
                location.reload()
            } else {
                // TODO: exception handling
                setErrorMessage("Failed to create folder")
                errorModal.show()
            }
        })

        .catch(error => {
            console.error(`While creating new folder following error occurred: ${error}`)
            // setErrorMessage("Failed to create folder")
            // errorModal.show()
        })

}

function renameObj(renameBtn) {

    let newNameInput = document.getElementById("new-obj-name")
    if (!newNameInput.checkValidity()){
        document.getElementById("rename-feedback").textContent = ""
        document.getElementById("rename-feedback").style.display = "block"
    }

    let url = baseUrl + renameBtn.getAttribute("data-req-path");
    let newObjName = document.getElementById("new-obj-name").value;
    let objName = renameBtn.getAttribute("data-obj-name")
    let formData = new FormData();
    formData.append("path", getCurPath());
    formData.append("objName", objName);
    formData.append("newObjName", String(newObjName));
    formData.append("_csrf", getCsrfToken())
    formData.append("_method", "PATCH")

    fetch(url, {
        method: 'POST',
        headers: {
            'ContentType': 'application/x-www-form-urlencoded;utf-8',
        },
        body: formData
    })
        .then(response => {
            if (response.ok) {
                console.info(`Renamed "${objName}" to "${newObjName}" successfully`)
                // setSuccessMessage(`Renamed "${objName}" to "${newObjName}" successfully`)
                // let href = getCurPath() === "" ? baseUrl : baseUrl + `?path=${getCurPath()}`
                location.reload()
            } else {
                // TODO: exception handling
                setErrorMessage("Failed to rename")
                errorModal.show()
            }
        })

        .catch(error => {
            console.error(`While renaming the error occurred: ${error}`)
            // setErrorMessage("Failed to rename")
            // errorModal.show()
        })

}

function uploadObj() {
    let url = baseUrl + uploadBtn.getAttribute("data-req-path");
    let formData = new FormData();
    formData.append("path", getCurPath());
    formData.append("_csrf", getCsrfToken())

    let files = fileInput.files;
    for (let i = 0; i < files.length; i++) {
        formData.append(`documents[${i}]`, files[i], files[i].name);
    }

    fetch(url, {
        method: 'POST',
        headers: {
            'ContentType': 'multipart/form-data;utf-8',
        },
        body: formData
    })
        .then(response => {
            if (response.ok) {
                // let href = getCurPath() === "" ? baseUrl : baseUrl + `?path=${getCurPath()}`
                setSuccessMessage(`Files have been uploaded successfully`)
                successModal.show()
            } else {
                // TODO: exception handling
                setErrorMessage("Failed to upload")
                errorModal.show()
            }
        })

        .catch(error => {
            console.error(`While uploading the error occurred: ${error}`)
            // setErrorMessage("Failed to rename")
            // errorModal.show()
        })
}

function clearDirName(){
    document.getElementById("mk-dir-name").value = ""
}

// TODO: check if it would be more convenient to call "show" inside these functions
function setSuccessMessage(message) {
    successModalElement.querySelector(".alert-text").textContent = message;
}

function setErrorMessage(message) {
    errorModalElement.querySelector(".alert-text").textContent = message;
}

function reloadPage() {
    location.reload();
}

function removeExtension(filename) {
    let lastDotIndex = filename.lastIndexOf(".");
    if (lastDotIndex !== -1) {
        return filename.substring(0, lastDotIndex);
    }
    return filename
}

function getCurPath() {
    let path = document.getElementById("my-dropzone").querySelector("input[name='path']").value
    // return typeof (path) === "string" ? encodeURIComponent(path) : "";
    return typeof (path) === "string" ? path : "";
}

function getCsrfToken() {
    let csrfToken = document.querySelector("input[name='_csrf']").value;

    if (typeof csrfToken !== "string") {
        throw new Error("CSRF token not found")
    }

    return csrfToken;
}

// function validateObjName(name){
//     if (typeof name !== "string"){
//         throw new Error("Name is undefined");
//     }
//
//     //should be in the word\word\word format
//     // [a-zA-Z0-9@_.]{4,50}
//     let pattern=/[a-zA-Z0-9@_.]{1,20}/;
//
//     //If the inputString is NOT a match
//     if (!pattern.test(inputString)) {
//         alert("not a match");
//     }
//     else
//     {
//         alert("match");
//     }
//
//     "Allowed latin letters, numbers and symbols @_.!#$%^&*. 20 characters maximum."
// }

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
