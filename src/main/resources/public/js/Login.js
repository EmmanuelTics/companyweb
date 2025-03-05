const inputs = document.querySelectorAll(".input");


function addcl(){
	let parent = this.parentNode.parentNode;
	parent.classList.add("focus");
}

function remcl(){
	let parent = this.parentNode.parentNode;
	if(this.value == ""){
		parent.classList.remove("focus");
	}
}


inputs.forEach(input => {
	input.addEventListener("focus", addcl);
	input.addEventListener("blur", remcl);
}); 

// Función para manejar el envío del formulario
async function redirectToPage(event) {
    // Prevenir el envío del formulario por defecto
    event.preventDefault();

    // Obtener los valores de los campos de entrada
    const username = document.querySelector('input[name="username"]').value;
    const password = document.querySelector('input[name="password"]').value;

    // Verificar que no estén vacíos
    if (!username || !password) {
        alert("Por favor, ingresa un nombre de usuario y una contraseña.");
        return;
    }

    // Crear el cuerpo de la solicitud
    const loginData = {
        username: username,
        password: password
    };

    try {
        // Realizar la solicitud POST a la API
        const response = await fetch("https://webapicompany.onrender.com/user/login", {
            method: "POST",
            headers: {
                "Content-Type": "application/json"
            },
            body: JSON.stringify(loginData)
        });

        // Manejar la respuesta de la API
        if (response.ok) {
            const message = await response.text();
     
            window.location.href = "Dashboard.html"; 
        } else if (response.status === 401) {
            alert("Usuario o contraseña inválidos.");
        } else {
            alert("Error en el inicio de sesión. Inténtalo de nuevo.");
        }
    } catch (error) {
        console.error("Error al conectar con la API:", error);
        alert("No se pudo conectar con el servidor.");
    }
}