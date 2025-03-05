function goToTop() {
    // Ocultar la tarjeta de éxito
    document.getElementById('successCard').style.display = 'none';

    // Mostrar la primera sección del formulario
    document.querySelectorAll('.section').forEach(section => section.style.display = 'none');
    document.getElementById('section-personal').style.display = 'block';

    // Desplazar la página hasta el inicio
    window.scrollTo(0, 0);
}


function nextSection(nextId) {
    document.querySelectorAll('.section').forEach(section => section.style.display = 'none');
    document.getElementById(nextId).style.display = 'block';
}

function prevSection(prevId) {
    document.querySelectorAll('.section').forEach(section => section.style.display = 'none');
    document.getElementById(prevId).style.display = 'block';
}



const urlParams = new URLSearchParams(window.location.search);
const employeeName = urlParams.get('name');  // Recuperar el nombre del empleado desde la URL

// Función para convertir base64 a PDF y mostrar la previsualización
// Función para mostrar la previsualización de archivos (INE y Acta de Nacimiento)
function showFilePreview(inputId, previewId, currentInputId) {
    const inputFile = document.getElementById(inputId);
    const previewContainer = document.getElementById(previewId);
    const currentFileBase64 = document.getElementById(currentInputId).value;

    // Verificar si se seleccionó un archivo
    inputFile.addEventListener('change', function(event) {
        const file = event.target.files[0];
        
        if (file) {
            // Crear una URL de objeto para previsualizar el archivo
            const reader = new FileReader();
            reader.onload = function(e) {
                const fileType = file.type.split('/')[0]; // Obtener tipo de archivo (image o application)
                previewContainer.innerHTML = ""; // Limpiar la previsualización actual

                if (fileType === "image") {
                    // Si el archivo es una imagen (PNG, JPG)
                    const img = document.createElement('img');
                    img.src = e.target.result;
                    previewContainer.appendChild(img);
                } else if (fileType === "application" && file.type === "application/pdf") {
                    // Si el archivo es un PDF
                    const embed = document.createElement('embed');
                    embed.src = e.target.result;
                    embed.type = "application/pdf";
                    embed.width = "100%";
                    embed.height = "100%";
                    previewContainer.appendChild(embed);
                }
            };
            reader.readAsDataURL(file);
        }
    });

    // Si no se selecciona un archivo nuevo, mostrar la previsualización actual
    if (currentFileBase64) {
        const fileType = currentFileBase64.split(';')[0].split('/')[0]; // Obtener tipo de archivo
        previewContainer.innerHTML = ""; // Limpiar la previsualización actual

        if (fileType === "image") {
            const img = document.createElement('img');
            img.src = currentFileBase64;
            img.style.maxWidth = "100%";  // Ajustar al contenedor
            img.style.maxHeight = "100%"; // Ajustar al contenedor
            previewContainer.appendChild(img);
        } else if (fileType === "application" && currentFileBase64.includes("pdf")) {
            const embed = document.createElement('embed');
            embed.src = currentFileBase64;
            embed.type = "application/pdf";
            embed.width = "100%";
            embed.height = "500px";
            previewContainer.appendChild(embed);
        }
    }
}

// Función para obtener los datos del empleado y completar el formulario
function fetchEmployeeData(name) {
    fetch(`https://webapicompany.onrender.com//employee/search/${encodeURIComponent(name)}`)
    .then(response => response.json())
    .then(data => {
        console.log('Datos del empleado recuperados:', data); 
        // Completar el formulario con los datos obtenidos
        document.getElementById('name').value = data.name;
        document.getElementById('dob').value = data.birthdate;
        document.getElementById('marital_status').value = data.maritalStatus;
        document.getElementById('address').value = data.address;
        document.getElementById('nationality').value = data.nationality;
        document.getElementById('education_level').value = data.educationLevel;
        document.getElementById('rfc').value = data.rfc;
        document.getElementById('curp').value = data.curp;
        document.getElementById('nss').value = data.nss;
        document.getElementById('phone').value = data.phone;
        document.getElementById('email').value = data.email;
        document.getElementById('bank_account').value = data.bankAccount;
        document.getElementById('bank_name').value = data.bankName;
        document.getElementById('salary').value = data.salary;
        document.getElementById('position').value = data.workstation;

        // Si existe el INE, mostrar la previsualización
        if (data.ine) {
            convertBase64ToPreview(data.ine, 'ine-preview');
        }

        // Si existe el Acta de Nacimiento, mostrar la previsualización
        if (data.birthCertificate) {
            convertBase64ToPreview(data.birthCertificate, 'birth-certificate-preview');
        }
    })
    .catch(error => console.error('Error al obtener los datos del empleado:', error));
}

// Espera a que el DOM se cargue completamente antes de llamar a la función
document.addEventListener('DOMContentLoaded', function() {
    const employeeName = new URLSearchParams(window.location.search).get('name');  // Recuperar el nombre del empleado desde la URL
    fetchEmployeeData(employeeName);
});

// Función para convertir base64 a PDF y mostrar la previsualización
function convertBase64ToPreview(base64Data, elementId) {
    const byteCharacters = atob(base64Data);
    const byteArray = new Uint8Array(byteCharacters.length);

    for (let i = 0; i < byteCharacters.length; i++) {
        byteArray[i] = byteCharacters.charCodeAt(i);
    }

    const blob = new Blob([byteArray], { type: 'application/pdf' });
    const url = URL.createObjectURL(blob);

    pdfjsLib.getDocument(url).promise.then(pdf => {
        pdf.getPage(1).then(page => {
            const canvas = document.createElement('canvas');
            const context = canvas.getContext('2d');
            const viewport = page.getViewport({ scale: 0.5 });
            canvas.width = viewport.width;
            canvas.height = viewport.height;

            page.render({
                canvasContext: context,
                viewport: viewport
            }).promise.then(() => {
                const viewer = document.getElementById(elementId);
                viewer.innerHTML = '';
                viewer.appendChild(canvas);
            });
        });
    }).catch(error => {
        console.error('Error al cargar el PDF:', error);
    });
}

// Ejecutar la función para el INE y el Acta de Nacimiento
showFilePreview('ine_input', 'ine-preview', 'current-ine');
showFilePreview('birth_certificate_input', 'birth-certificate-preview', 'current-birth-certificate');



function readFile(inputId, callback) {
    const fileInput = document.getElementById(inputId);
    const file = fileInput.files[0];

    if (!file) {
        return callback(null);
    }

    const reader = new FileReader();
    reader.onloadend = function () {
        // Convertir el archivo a una cadena base64
        const base64String = reader.result.split(',')[1]; // Remover la cabecera de base64
        console.log('Archivo convertido a base64:', base64String);  // Verificar el base64 generado
        callback(base64String);
    };
    reader.readAsDataURL(file); // Convertir el archivo a base64
}


document.getElementById('editForm').addEventListener('submit', function (e) {
    e.preventDefault(); // Evitar el envío tradicional del formulario

    const formData = new FormData(this);

    // Leer archivos y convertirlos a base64
    readFile('birth_certificate_input', function (birthCertificateBase64) {
        readFile('ine_input', function (ineBase64) {
            // Crear el objeto con los datos del formulario
            const employeeData = [{
                name: formData.get('name'),
                address: formData.get('address'),
                birthdate: formData.get('birthdate'),
                nationality: formData.get('nationality'),
                maritalStatus: formData.get('maritalStatus'),
                educationLevel: formData.get('educationLevel'),
                birthCertificate: birthCertificateBase64, // Acta de nacimiento como base64
                rfc: formData.get('rfc'),
                ine: ineBase64, // INE como base64
                curp: formData.get('curp'),
                nss: formData.get('nss'),
                phone: formData.get('phone'),
                email: formData.get('email'),
                bankAccount: formData.get('bankAccount'),
                bankName: formData.get('bankName'),
                salary: formData.get('salary'),
                workstation: formData.get('workstation')
            }];
            console.log('Datos del empleado a enviar al servidor:', employeeData);  // Verificar los datos que se van a enviar

            // Enviar los datos al servidor para actualizar al empleado por nombre
            fetch(`http://localhost:8080/employee/update/${encodeURIComponent(employeeName)}`, {
                method: 'PUT', // Usamos PUT para actualización
                headers: {
                    'Content-Type': 'application/json'
                },
                body: JSON.stringify(employeeData) // Enviar los datos como JSON
            })
            .then(response => {
                if (response.ok) {
                    console.log('Empleado actualizado correctamente');
                    // Mostrar la tarjeta de éxito
                    document.getElementById('successCard').style.display = 'block';

                    // Limpiar el formulario
                    document.getElementById('editForm').reset();

                } else {
                    console.error('Error al actualizar el empleado');
                }
            })
            .catch(error => {
                console.error("Error al actualizar el empleado:", error);
            });

        });
    });
});
