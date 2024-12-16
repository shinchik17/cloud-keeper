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
const storageInfoDiv = document.getElementById("storage-info");
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
errorDiv = document.getElementById("error-modal-trigger");

if (errorDiv != null) {
    errorMessage = errorDiv.getAttribute("data-error-message")
    errorModalElement.addEventListener("hide.bs.modal", reloadPage)
    showErrorMessage(errorMessage)
}

/* Dropzone options, events and additional functions */
Dropzone.options.myDropzone = {
    url: baseUrl + "files",
    renameFile: function (file) {
        return file.fullPath;
    },
    paramName: function () {
        return "files";
    },
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
        this.on("successmultiple", function () {
            showSuccessMessage("Files uploaded successfully");
            this.removeAllFiles(true);
            showLeftPaneTools();
        });

        this.on("maxfilesexceeded", function () {
            showErrorMessage(this.options.dictMaxFilesExceeded);

            for (let file of this.files.slice()) {
                if ((file.status !== Dropzone.QUEUED)) {
                    this.removeFile(file);
                }
            }
        });

        this.on("error", function (file, message, xhr) {
            if (message !== this.options.dictMaxFilesExceeded && message !== this.options.dictUploadCanceled) {
                message = xhr && xhr.response ? JSON.parse(xhr.response).message : message
                showErrorMessage(message);
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
            showErrorMessage(Dropzone.options.myDropzone.dictFileTooBig)
            return;
        }
    }

    dropzoneElement.dropzone.processQueue();
}

function clearDropzone() {
    let dropzoneElement = document.getElementById("my-dropzone")
    dropzoneElement.dropzone.removeAllFiles(true);
    // dropzoneElement.dropzone.reset();
    showLeftPaneTools()
}


/* Business logic */
function mkDir(mkBtn) {

    let dirnameInput = document.getElementById("mk-dir-name")
    if (!dirnameInput.checkValidity()) {
        dirnameInput.closest("form").classList.add("was-validated")
        return
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
                location.reload()
            } else {
                response.text().then(text => {
                    text = JSON.parse(text).message
                    showErrorMessage(text);
                    console.error(text);
                })
            }
        })

        .catch(error => {
            console.error(`While creating new folder an error occurred: ${error}`)
        })

}

function renameObj(renameBtn) {

    let newNameInput = document.getElementById("new-obj-name")
    if (!newNameInput.checkValidity()) {
        newNameInput.closest("form").classList.add("was-validated")
        return
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
                location.reload()
            } else {
                response.text().then(text => {
                    text = JSON.parse(text).message
                    showErrorMessage(text);
                    console.error(text);
                })
            }
        })

        .catch(error => {
            console.error(`While renaming an error occurred: ${error}`)
        })

}

function uploadObj() {
    let url = baseUrl + uploadBtn.getAttribute("data-req-path");
    let formData = new FormData();
    formData.append("path", getCurPath());
    formData.append("_csrf", getCsrfToken())

    let files = fileInput.files;
    if (files.length > MAX_FILES) {
        showErrorMessage(Dropzone.options.myDropzone.dictMaxFilesExceeded)
    }

    let totalSize = 0;
    for (let i = 0; i < Math.min(files.length, MAX_FILES); i++) {
        formData.append(`files`, files[i], files[i].name);
        totalSize += files[i].size

        if (totalSize > MAX_FILE_SIZE * 1024 * 1024) {
            showErrorMessage(Dropzone.options.myDropzone.dictFileTooBig)
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
                showSuccessMessage(`Files have been uploaded successfully`);
            } else {
                response.text().then(text => {
                    text = JSON.parse(text).message
                    errorModalElement.addEventListener("hide.bs.modal", reloadPage)
                    showErrorMessage(text);
                    console.error(text);
                })
            }
        })

        .catch(error => {
            console.error(`While uploading an error occurred: ${error}`)
        })
}

function deleteObj(rmBtn) {
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

                if (path === "" && objName === "") {
                    successModalElement.addEventListener("hide.bs.modal", reloadPage)
                } else {
                    rmBtn.closest(".list-group-item").remove()
                }
                showSuccessMessage("Successfully deleted");
                // successModal.show();

            } else {
                response.text().then(text => {
                    text = JSON.parse(text).message
                    errorModalElement.addEventListener("hide.bs.modal", reloadPage)
                    showErrorMessage(text);
                    console.error(text);
                })
            }
        })

        .catch(error => {
            console.error(`While deleting an error occurred: ${error}`)
        })
}

function downloadObj(downloadBtn) {
    let url = baseUrl + downloadBtn.getAttribute("data-req-path");
    let path = downloadBtn.getAttribute("data-path")
    path = path !== null ? path : ""
    let objName = downloadBtn.getAttribute("data-obj-name")
    url = url + `?path=${path}&objName=${objName}`

    fetch(url, {
        method: "GET",
    })
        .then(response => {
            if (response.ok) {
                let disposition = response.headers.get("Content-Disposition");
                let encodedFilename = disposition.split("; ")[1]
                    .replaceAll("filename*=UTF-8''", "")
                    .replaceAll("\"", "");
                let filename = decodeURIComponent(encodedFilename);

                response.blob().then(blob => {
                    const link = document.createElement('a');
                    link.href = window.URL.createObjectURL(blob);
                    link.download = filename;
                    // Append to the body (required for Firefox)
                    document.body.appendChild(link);
                    // Programmatically click the link to trigger the download
                    link.click();
                    // Clean up and remove the link
                    document.body.removeChild(link);
                    window.URL.revokeObjectURL(link.href); // Free up memory
                })
            } else {
                response.text().then(text => {
                    text = JSON.parse(text).message
                    errorModalElement.addEventListener("hide.bs.modal", reloadPage);
                    showErrorMessage(text);
                    console.error(text);
                })
            }
        })

        .catch(error => {
            console.error(`While downloading an error occurred: ${error}`)
        })
}


/* Modals, inputs visual effects handling */
function clearMkdirInput() {
    document.getElementById("mkdir-form").classList.remove("was-validated");
    document.getElementById("mk-dir-name").value = "";
}

function clearRenameInput() {
    document.getElementById("rename-form").classList.remove("was-validated");
    document.getElementById("new-obj-name").value = "";
}

function showSuccessMessage(message) {
    successModalElement.querySelector(".alert-text").textContent = message;
    successModal.show()
}

function showErrorMessage(message) {
    if (message.startsWith("You have run out of free space")) {
        message = message.replaceAll("subscription", "<a href='https://telegra.ph/Mentorstvo-po-trudoustrojstvu-06-08' target=\"_blank\" rel=\"noopener noreferrer\">subscription</a>")
        errorModalElement.querySelector(".alert-text").innerHTML = message;
    } else {
        errorModalElement.querySelector(".alert-text").textContent = message;
    }
    errorModal.show()
}

function showLeftPaneTools() {
    toolsDropzoneDiv.classList.add("d-none")
    uploadBtn.classList.remove("d-none")
    mkDirBtn.classList.remove("d-none")
    storageInfoDiv.classList.remove("d-none")
    if (getCurPath() === "" && rmAllBtn !== null) {
        rmAllBtn.classList.remove("d-none")
    }

}

function showDropzoneTools() {
    toolsDropzoneDiv.classList.remove("d-none")
    uploadBtn.classList.add("d-none")
    mkDirBtn.classList.add("d-none")
    storageInfoDiv.classList.add("d-none")
    if (getCurPath() === "" && rmAllBtn !== null) {
        rmAllBtn.classList.add("d-none")
    }
}

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


