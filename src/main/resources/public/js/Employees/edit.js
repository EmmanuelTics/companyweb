function goToTop() {
    
    window.location.href = 'ListEmployee.html';
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
const employeeName = urlParams.get('name');  

function showFilePreview(inputId, previewId, currentInputId) {
    const inputFile = document.getElementById(inputId);
    const previewContainer = document.getElementById(previewId);
    const currentFileBase64 = document.getElementById(currentInputId).value;

    // Verificar si se seleccionó un archivo
    inputFile.addEventListener('change', function(event) {
        const file = event.target.files[0];
        
        if (file) {
          
            const reader = new FileReader();
            reader.onload = function(e) {
                const fileType = file.type.split('/')[0]; 
                previewContainer.innerHTML = ""; 

                if (fileType === "image") {
             
                    const img = document.createElement('img');
                    img.src = e.target.result;
                    previewContainer.appendChild(img);
                } else if (fileType === "application" && file.type === "application/pdf") {
                   
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

    
    if (currentFileBase64) {
        const fileType = currentFileBase64.split(';')[0].split('/')[0]; 
        previewContainer.innerHTML = ""; 

        if (fileType === "image") {
            const img = document.createElement('img');
            img.src = currentFileBase64;
            img.style.maxWidth = "100%";  
            img.style.maxHeight = "100%"; 
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


function fetchEmployeeData(name) {
    fetch(`https://webapicompany.onrender.com/employee/search/${encodeURIComponent(name)}`)
    .then(response => response.json())
    .then(data => {
        console.log('Datos del empleado recuperados:', data); 
      
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

    
        if (data.ine) {
            convertBase64ToPreview(data.ine, 'ine-preview');
        }

      
        if (data.birthCertificate) {
            convertBase64ToPreview(data.birthCertificate, 'birth-certificate-preview');
        }
    })
    .catch(error => console.error('Error al obtener los datos del empleado:', error));
}


document.addEventListener('DOMContentLoaded', function() {
    const employeeName = new URLSearchParams(window.location.search).get('name'); 
    fetchEmployeeData(employeeName);
});


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
       
        const base64String = reader.result.split(',')[1]; 
        console.log('Archivo convertido a base64:', base64String);  
        callback(base64String);
    };
    reader.readAsDataURL(file); 
}


document.getElementById('editForm').addEventListener('submit', function (e) {
    e.preventDefault(); 

    const formData = new FormData(this);

   
    readFile('birth_certificate_input', function (birthCertificateBase64) {
        readFile('ine_input', function (ineBase64) {
           
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
        

          
            fetch(`https://webapicompany.onrender.com/employee/update/${encodeURIComponent(employeeName)}`, {
                method: 'PUT', 
                headers: {
                    'Content-Type': 'application/json'
                },
                body: JSON.stringify(employeeData)
            })
            .then(response => {
                if (response.ok) {
                    console.log('Empleado actualizado correctamente');
                  
                    document.getElementById('successCard').style.display = 'block';

                  
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


document.getElementById('phone').addEventListener('input', function (e) {
    let value = e.target.value.replace(/[^\d]/g, ''); 

    if (value.length > 3 && value.length <= 6) {
      value = value.slice(0, 3) + '-' + value.slice(3);
    } else if (value.length > 6) {
      value = value.slice(0, 3) + '-' + value.slice(3, 6) + '-' + value.slice(6, 10);
    }

    e.target.value = value;
  });

  
  document.getElementById("editForm").addEventListener("submit", function (e) {
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