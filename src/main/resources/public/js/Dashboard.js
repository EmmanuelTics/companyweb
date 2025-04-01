document.addEventListener("DOMContentLoaded", function () {
    const toggleBtn = document.querySelector('.toggle-btn');
    const sidebar = document.querySelector('#sidebar');
    const submenus = document.querySelectorAll('.sidebar-link');
    const inicioLink = document.getElementById('inicioLink');
    const logoutLink = document.querySelector('.sidebar-footer a[href="Login.html"]');
    const iframe = document.querySelector('iframe');

    let isSidebarExpanded = false;

    // Verificar si los elementos existen antes de usarlos
    if (toggleBtn && sidebar) {
        toggleBtn.addEventListener('click', () => {
            sidebar.classList.toggle('expand');
            isSidebarExpanded = !isSidebarExpanded;

            submenus.forEach(link => {
                link.setAttribute('data-bs-toggle', isSidebarExpanded ? 'collapse' : '');
                link.setAttribute('aria-expanded', 'false');
                const target = link.getAttribute('data-bs-target');
                if (target) {
                    const targetElement = document.querySelector(target);
                    if (targetElement) {
                        targetElement.classList.remove('show');
                    }
                }
            });
        });
    } else {
        console.error("No se encontró '.toggle-btn' o '#sidebar' en el DOM.");
    }

    // Evento para cada enlace de submenú
    submenus.forEach(link => {
        link.addEventListener('click', (e) => {
            // Si el menú lateral no está expandido, lo expandimos
            if (!isSidebarExpanded) {
                sidebar.classList.add('expand');
                isSidebarExpanded = true;
    
                // Aseguramos que todos los enlaces del submenú tengan la configuración de colapso
                submenus.forEach(menu => {
                    menu.setAttribute('data-bs-toggle', 'collapse');
                    menu.setAttribute('aria-expanded', 'false');
                });
    
                // Esperamos un pequeño retraso antes de abrir el submenú
                setTimeout(() => {
                    link.setAttribute('data-bs-toggle', 'collapse');
                    // Opcional: Puedes ajustar el atributo 'aria-expanded' según necesites
                }, 300); // Retraso de 300ms para la transición del sidebar
            }
    
            // Cerramos todos los submenús excepto el actual
            submenus.forEach(otherLink => {
                if (otherLink !== link) {
                    otherLink.setAttribute('aria-expanded', 'false');
                    const target = otherLink.getAttribute('data-bs-target');
                    if (target) {
                        const targetElement = document.querySelector(target);
                        if (targetElement) {
                            targetElement.classList.remove('show');
                        }
                    }
                }
            });
    
            // Ahora mostramos el submenú actual (si es que tiene submenú)
            const target = link.getAttribute('data-bs-target');
            if (target) {
                const targetElement = document.querySelector(target);
                if (targetElement) {
                    targetElement.classList.add('show');
                }
            }
        });
    });
    
    // Evento para el enlace de "Inicio"
    if (inicioLink) {
        inicioLink.addEventListener('click', function (event) {
            event.preventDefault();
            if (window.location.pathname !== 'Dashboard.html') {
                document.body.style.transition = 'transform 0.3s ease-out';
                document.body.style.transform = 'translateX(-100%)';

                setTimeout(() => {
                    window.location.href = 'Dashboard.html';
                }, 300);
            } else {
                console.log("Ya estás en la página de inicio.");
            }
        });
    } else {
        console.warn("No se encontró el enlace de inicio con id 'inicioLink'.");
    }

    // Evento para cerrar sesión
    if (logoutLink) {
        logoutLink.addEventListener('click', function (event) {
            event.preventDefault();
            window.location.href = 'Login.html';
        });
    } else {
        console.warn("No se encontró el enlace de cierre de sesión.");
    }

    // Función para cargar vistas en el iframe
    window.cargarVista = function (event, ruta, nombre) {
        event.preventDefault();
        if (!iframe) {
            console.error("No se encontró el iframe.");
            return;
        }

        if (!ruta) {
            window.location.href = 'error/error.html';
            return;
        }

        fetch(ruta)
            .then(response => {
                if (response.ok) {
                    iframe.src = ruta;
                    actualizarBreadcrumbs(nombre);
                } else {
                    throw new Error('Página no encontrada');
                }
            })
            .catch(() => {
                window.location.href = 'error/error.html';
            });
    };

    // Función para actualizar los breadcrumbs
    function actualizarBreadcrumbs(nombre) {
        const breadcrumbs = document.getElementById('breadcrumbs');
        if (!breadcrumbs) {
            console.error("No se encontró el contenedor de breadcrumbs.");
            return;
        }

        breadcrumbs.innerHTML = `
            <ol class="breadcrumb">
                <li class="breadcrumb-item"><a href="#" onclick="cargarVista(event, 'Home.html', 'Home')">Home</a></li>
                <li class="breadcrumb-item active" aria-current="page">${nombre}</li>
            </ol>`;
    }

    // Manejo de errores en el iframe
    if (iframe) {
        iframe.onerror = function () {
            window.location.href = 'error.html';
        };
    }
});
