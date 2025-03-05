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
        callback(base64String);
    };
    reader.readAsDataURL(file); // Convertir el archivo a base64
}

document.getElementById('employeeForm').addEventListener('submit', function (e) {
    e.preventDefault(); // Evitar el envío tradicional del formulario

    const formData = new FormData(this);

    // Leer archivos y convertirlos a base64
    readFile('birth_certificate', function (birthCertificateBase64) {
        readFile('ine', function (ineBase64) {
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

            // Enviar los datos al servidor
            fetch('http://localhost:8080/employee/add', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json'
                },
                body: JSON.stringify(employeeData) // Enviar los datos como JSON
            })

                .then(response => {
                    if (response.ok) {
                        // Mostrar la tarjeta de éxito
                        document.getElementById('successCard').style.display = 'block';

                        // Limpiar el formulario
                        document.getElementById('employeeForm').reset();

                    } else {
                        console.error('Error al registrar el empleado');
                    }
                })
                .catch(error => {
                    console.error("Error al registrar el empleado:", error);
                });

            // Función para volver al inicio

        });
    });
});
