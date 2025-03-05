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


