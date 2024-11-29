Dropzone.autoDiscover = false;
const MAX_FILE_SIZE = 100  // MiB
const MAX_FILES = 100
const baseUrl = "http://" + window.location.host + window.location.pathname

const renameModal = document.getElementById("rename-modal")
const mkdirModal = document.getElementById("mkdir-modal")
const fileInput = document.getElementById("file-upload");
const uploadBtn = document.getElementById("upload-btn");
const mkDirBtn = document.getElementById("mkdir-btn");
const rmAllBtn = document.getElementById("rm-all-btn");
const successModalElement = document.getElementById("success-modal")
const errorModalElement = document.getElementById("error-modal")
const successModal = new bootstrap.Modal(successModalElement)
const errorModal = new bootstrap.Modal(errorModalElement)
const uploadDropzoneBtn = document.getElementById("dz-upload-btn")
const clearDropzoneBtn = document.getElementById("dz-clear-btn")
const toolsDropzoneDiv = document.getElementById("dz-tools")

successModalElement.querySelector("#success-modal .modal-footer button").addEventListener("click", reloadPage)
successModalElement.addEventListener("hide.bs.modal", reloadPage)
renameModal.addEventListener("show.bs.modal", function (event) {
    let button = event.relatedTarget
    let objName = button.getAttribute("data-obj-name")

    let modalInput = document.getElementById("new-obj-name")
    modalInput.value = removeExtension(objName)

    let okBtn = renameModal.querySelector(".modal-footer .btn-primary")
    okBtn.setAttribute("data-obj-name", objName)
})
renameModal.addEventListener("hide.bs.modal", clearRenameInput)
mkdirModal.addEventListener("hide.bs.modal", clearMkdirInput)
uploadBtn.addEventListener("click", () => fileInput.click())
fileInput.addEventListener("change", uploadObj)
uploadDropzoneBtn.addEventListener("click", uploadDropzoneFiles)
clearDropzoneBtn.addEventListener("click", clearDropzone)
document.getElementById("rename-form").addEventListener("submit", function (event) {
    event.stopPropagation()
    event.preventDefault()
    renameModal.querySelector(".modal-footer .btn-primary").click()
})
document.getElementById("mkdir-form").addEventListener("submit", function (event) {
    event.preventDefault()
    event.stopPropagation()
    mkdirModal.querySelector(".modal-footer .btn-primary").click()
})
/* Dropzone options, events and additional functions */


Dropzone.options.myDropzone = {
    url: baseUrl + "files",
    renameFile: function (file) {
        return file.fullPath;
    },
    paramName: "documents",
    uploadMultiple: true,
    addRemoveLinks: true,
    autoProcessQueue: false,
    autoQueue: true,
    maxFilesize: MAX_FILE_SIZE,
    maxFiles: MAX_FILES,
    parallelUploads: MAX_FILES,
    dictFileTooBig: `Total size of uploading files exceeds limit (${MAX_FILE_SIZE} MiB).`,
    dictMaxFilesExceeded: `You have reached the maximum uploading files limit (${MAX_FILES}). Not all files would be uploaded.`,

    // Note: using "function()" here to bind `this` to
    // the Dropzone instance.
    init: function () {
        // TODO: implement backend handling filenames violations

        this.on("successmultiple", function () {
            setSuccessMessage("Files uploaded successfully");
            successModal.show();
            this.reset();
            showLeftPaneTools();
        });

        this.on("maxfilesexceeded", function () {
            setErrorMessage(this.options.dictMaxFilesExceeded);
            errorModal.show()

            for (let file of this.files.slice()) {
                if ((file.status !== Dropzone.QUEUED)) {
                    this.removeFile(file);
                }
            }
        });

        this.on("error", function (file, message, xhr) {
            if (message !== this.options.dictMaxFilesExceeded) {
                setErrorMessage(xhr && xhr.response ? JSON.parse(xhr.response).message : message);
                errorModal.show()
                this.removeAllFiles(true);
            }
        });

        this.on("addedfile", function () {
            showDropzoneTools();
        })

        this.on("removedfile", function () {
            if (this.getQueuedFiles().length === 0) {
                showLeftPaneTools()
            }
        })
    }
};

Dropzone.discover()

function uploadDropzoneFiles() {
    let dropzoneElement = document.getElementById("my-dropzone")
    let files = dropzoneElement.dropzone.getQueuedFiles()
    let totalSize = 0;
    for (let i = 0; i < files.length; i++) {
        totalSize += files[i].size

        if (totalSize > MAX_FILE_SIZE * 1024 * 1024) {
            setErrorMessage(Dropzone.options.myDropzone.dictFileTooBig)
            errorModal.show()
            return;
        }
    }

    dropzoneElement.dropzone.processQueue();
}

function clearDropzone() {
    let dropzoneElement = document.getElementById("my-dropzone")
    dropzoneElement.dropzone.removeAllFiles();
    dropzoneElement.dropzone.reset();
    showLeftPaneTools()
}


/* Business logic */
function mkDir(mkBtn) {

    let dirnameInput = document.getElementById("mk-dir-name")
    if (!dirnameInput.checkValidity()) {
        document.getElementById("rename-feedback").style.display = "block"
        return
    } else {
        document.getElementById("rename-feedback").style.display = "none"
    }


    let url = baseUrl + mkBtn.getAttribute("data-req-path");
    let dirname = document.getElementById("mk-dir-name").value
    let formData = new FormData();
    formData.append("path", getCurPath());
    formData.append("objName", dirname);
    formData.append("_csrf", getCsrfToken())

    fetch(url, {
        method: "POST", headers: {
            "ContentType": "application/x-www-form-urlencoded;utf-8",
        }, body: formData
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
    if (!newNameInput.checkValidity()) {
        document.getElementById("rename-feedback").style.display = "block"
        return
    } else {
        document.getElementById("rename-feedback").style.display = "none"
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
        method: "POST", headers: {
            "ContentType": "application/x-www-form-urlencoded;utf-8",
        }, body: formData
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
    if (files.length > MAX_FILES) {
        setErrorMessage(Dropzone.options.myDropzone.dictMaxFilesExceeded)
        errorModal.show()
    }

    let totalSize = 0;
    for (let i = 0; i < Math.min(files.length, MAX_FILES); i++) {
        formData.append(`documents[${i}]`, files[i], files[i].name);
        totalSize += files[i].size

        if (totalSize > MAX_FILE_SIZE * 1024 * 1024) {
            setErrorMessage(Dropzone.options.myDropzone.dictFileTooBig)
            errorModal.show()
            return;
        }

    }

    fetch(url, {
        method: "POST", headers: {
            "ContentType": "multipart/form-data;utf-8",
        }, body: formData
    })
        .then(response => {
            if (response.ok) {
                // let href = getCurPath() === "" ? baseUrl : baseUrl + `?path=${getCurPath()}`
                setSuccessMessage(`Files have been uploaded successfully`);
                successModal.show();
            } else {
                // TODO: exception handling
                setErrorMessage("Failed to upload");
                errorModal.show();
            }
        })

        .catch(error => {
            console.error(`While uploading the error occurred: ${error}`)
            // setErrorMessage("Failed to rename")
            // errorModal.show()
        })
}

function removeObj(rmBtn) {
    let url = baseUrl + rmBtn.getAttribute("data-req-path");
    let path = rmBtn.getAttribute("data-path")
    path = path !== null ? path : ""
    let objName = rmBtn.getAttribute("data-obj-name")
    let formData = new FormData();
    formData.append("_csrf", getCsrfToken())
    formData.append("path", path)
    formData.append("objName", objName)
    formData.append("_method", "DELETE")


    fetch(url, {
        method: "POST", headers: {
            "ContentType": "application/x-www-form-urlencoded;utf-8",
        }, body: formData
    })
        .then(response => {
            if (response.ok) {

                setSuccessMessage(`Files have been deleted successfully`);
                if (path === "" && objName === "") {
                    successModal._addEventListeners("hide.bs.modal", reloadPage)
                } else {
                    rmBtn.closest(".list-group-item").remove()
                }
                successModal.show();


            } else {
                // TODO: exception handling
                setErrorMessage("Failed to remove");
                errorModal.show();
            }
        })

        .catch(error => {
            console.error(`While uploading the error occurred: ${error}`)
            // setErrorMessage("Failed to rename")
            // errorModal.show()
        })
}


/* Modals, inputs visual effects handling */
function clearMkdirInput() {
    document.getElementById("mkdir-feedback").style.display = "none";
}

function clearRenameInput() {
    document.getElementById("rename-feedback").style.display = "none";
}

// TODO: check if it would be more convenient to call "show" inside these functions
function setSuccessMessage(message) {
    successModalElement.querySelector(".alert-text").textContent = message;
}

function setErrorMessage(message) {
    errorModalElement.querySelector(".alert-text").textContent = message;
}

function showLeftPaneTools() {
    toolsDropzoneDiv.classList.add("d-none")
    uploadBtn.classList.remove("d-none")
    mkDirBtn.classList.remove("d-none")
    if (getCurPath() === "") {
        rmAllBtn.classList.remove("d-none")
    }

}

function showDropzoneTools() {
    toolsDropzoneDiv.classList.remove("d-none")
    uploadBtn.classList.add("d-none")
    mkDirBtn.classList.add("d-none")
    if (getCurPath() === "") {
        rmAllBtn.classList.add("d-none")
    }
}


/* Custom validity settings */
function setSearchInputCustomValidity() {
    let input = document.getElementsByName("query")[0];
    input.addEventListener("invalid", event => {
        if (event.target.validity.valueMissing || event.target.validity.patternMismatch) {
            event.target.setCustomValidity("Search query must contain at least 1 non-whitespacespace character");
        }
    })
    input.addEventListener("change", event => {
        event.target.setCustomValidity("");
    })
}

function addAutoClearValidityErrors() {
    let inputs = document.querySelectorAll(".needs-validation input[type='text']");
    Array.from(inputs).forEach(input => {
        input.addEventListener("change", event => {
            input.closest("form").classList.add("was-validated");
        })
    })
}

function clearDefaultValidity() {
    // Fetch all the forms we want to apply custom Bootstrap validation styles to
    const forms = document.querySelectorAll(".needs-validation")
    // Loop over them and prevent submission
    Array.from(forms).forEach(form => {
        form.addEventListener("submit", event => {
            if (!form.checkValidity()) {
                event.preventDefault();
                event.stopPropagation();
            }

            form.classList.add("was-validated");
        }, false)
    })
}

clearDefaultValidity();
setSearchInputCustomValidity();
addAutoClearValidityErrors();


/* Miscellaneous */
function removeExtension(filename) {
    let lastDotIndex = filename.lastIndexOf(".");
    if (lastDotIndex !== -1) {
        return filename.substring(0, lastDotIndex);
    }
    return filename
}

function getCurPath() {
    let path = document.getElementById("my-dropzone").querySelector("input[name='path']").value
    return typeof (path) === "string" ? path : "";
}

function getCsrfToken() {
    let csrfToken = document.querySelector("input[name='_csrf']").value;

    if (typeof csrfToken !== "string") {
        throw new Error("CSRF token not found");
    }

    return csrfToken;
}

function reloadPage() {
    location.reload();
}


// myDropzone.on("uploadprogress", function (file, progress, bytesSent) {
//     file.previewElement.querySelector('.dz-progress .dz-upload').style.width = progress + "%";
// });

