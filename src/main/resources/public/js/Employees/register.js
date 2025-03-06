function goToTop() {

    document.getElementById('successCard').style.display = 'none';

    document.querySelectorAll('.section').forEach(section => section.style.display = 'none');
    document.getElementById('section-personal').style.display = 'block';


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
       
        const base64String = reader.result.split(',')[1]; 
        callback(base64String);
    };
    reader.readAsDataURL(file); 
}

document.getElementById('employeeForm').addEventListener('submit', function (e) {
    e.preventDefault(); 

    const formData = new FormData(this);


    readFile('birth_certificate', function (birthCertificateBase64) {
        readFile('ine', function (ineBase64) {
          
            const employeeData = [{
                name: formData.get('name'),
                address: formData.get('address'),
                birthdate: formData.get('birthdate'),
                nationality: formData.get('nationality'),
                maritalStatus: formData.get('maritalStatus'),
                educationLevel: formData.get('educationLevel'),
                birthCertificate: birthCertificateBase64, 
                rfc: formData.get('rfc'),
                ine: ineBase64, 
                curp: formData.get('curp'),
                nss: formData.get('nss'),
                phone: formData.get('phone'),
                email: formData.get('email'),
                bankAccount: formData.get('bankAccount'),
                bankName: formData.get('bankName'),
                salary: formData.get('salary'),
                workstation: formData.get('workstation')
            }];

       
            fetch('https://webapicompany.onrender.com/employee/add', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json'
                },
                body: JSON.stringify(employeeData) 
            })

                .then(response => {
                    if (response.ok) {
                       
                        document.getElementById('successCard').style.display = 'block';

                      
                        document.getElementById('employeeForm').reset();

                    } else {
                        console.error('Error al registrar el empleado');
                    }
                })
                .catch(error => {
                    console.error("Error al registrar el empleado:", error);
                });


        });
    });
});

document.getElementById('phone').addEventListener('input', function (e) {
    let value = e.target.value.replace(/[^\d]/g, ''); 

    if (value.length > 3 && value.length <= 6) {
      value = value.slice(0, 3) + '-' + value.slice(3);
    } else if (value.length > 6) {
      value = value.slice(0, 3) + '-' + value.slice(3, 6) + '-' + value.slice(6, 10);
    }

    e.target.value = value;
  });


  document.getElementById("employeeForm").addEventListener("submit", function (e) {
    e.preventDefault(); // Evita que se envíe el formulario si hay errores

    // Variables de error
    let isValid = true;

    // Validar RFC
    const rfc = document.getElementById("rfc").value.trim();
    const rfcPattern = /^[A-ZÑ&]{3,4}[0-9]{6}[A-Z0-9]{3}$/;
    if (!rfcPattern.test(rfc)) {
        document.getElementById("rfc-error").style.display = "block";
        isValid = false;
    } else {
        document.getElementById("rfc-error").style.display = "none";
    }

    // Validar CURP
    const curp = document.getElementById("curp").value.trim();
        const curpPattern = /^[A-Z]{4}[0-9]{6}[HM][A-Z]{5}[0-9A-Z]{2}$/;
    if (!curpPattern.test(curp)) {
        document.getElementById("curp-error").style.display = "block";
        isValid = false;
    } else {
        document.getElementById("curp-error").style.display = "none";
    }

    // Validar NSS
    const nss = document.getElementById("nss").value.trim();
    const nssPattern = /^\d{11}$/;
    if (!nssPattern.test(nss)) {
        document.getElementById("nss-error").style.display = "block";
        isValid = false;
    } else {
        document.getElementById("nss-error").style.display = "none";
    }

    // Validar Teléfono
    const phone = document.getElementById("phone").value.trim();
    const phonePattern = /^\d{3}-\d{3}-\d{4}$/;
    if (!phonePattern.test(phone)) {
        document.getElementById("phone-error").style.display = "block";
        isValid = false;
    } else {
        document.getElementById("phone-error").style.display = "none";
    }

    // Validar selección en Estado Civil y Nivel de Estudios
    const maritalStatus = document.getElementById("marital_status").value;
    const educationLevel = document.getElementById("education_level").value;

    if (!maritalStatus) {
        alert("Por favor, selecciona un Estado Civil.");
        isValid = false;
    }

    if (!educationLevel) {
        alert("Por favor, selecciona un Nivel de Estudios.");
        isValid = false;
    }

    // Validar Número de Cuenta Bancaria
    const bankAccount = document.getElementById("bank_account").value.trim();
    const bankAccountPattern = /^\d{10,18}$/;
    if (!bankAccountPattern.test(bankAccount)) {
        alert("El Número de Cuenta Bancaria debe tener entre 10 y 18 dígitos.");
        isValid = false;
    }

    // Validar campos requeridos de tipo archivo
    const birthCertificate = document.getElementById("birth_certificate").files.length;
    const ine = document.getElementById("ine").files.length;

    if (!birthCertificate) {
        alert("Por favor, sube tu Acta de Nacimiento.");
        isValid = false;
    }

    if (!ine) {
        alert("Por favor, sube tu INE.");
        isValid = false;
    }

    // Si todo es válido, mostrar mensaje de éxito
    if (isValid) {
        document.getElementById("employeeForm").style.display = "none";
        document.getElementById("successCard").style.display = "block";
    }
});



function goToTop() {
    location.reload();
}

function validateSection(sectionId) {
    const section = document.getElementById(sectionId);
    const inputs = section.querySelectorAll("input, select");
    let isValid = true;

    inputs.forEach(input => {
        if (!input.checkValidity()) {
            input.classList.add("invalid"); // Agrega una clase para resaltar los campos inválidos
            isValid = false;
        } else {
            input.classList.remove("invalid"); // Quita la clase si el campo es válido
        }
    });

    return isValid;
}

function nextSection(nextSectionId) {
    const currentSection = document.querySelector(".section:not([style='display: none;'])");

    if (validateSection(currentSection.id)) {
        currentSection.style.display = "none";
        document.getElementById(nextSectionId).style.display = "block";
    } else {
        alert("Por favor, llena todos los campos requeridos correctamente antes de continuar.");
    }
}

function prevSection(prevSectionId) {
    const currentSection = document.querySelector(".section:not([style='display: none;'])");
    currentSection.style.display = "none";
    document.getElementById(prevSectionId).style.display = "block";
}