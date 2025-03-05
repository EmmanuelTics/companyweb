const toggleBtn = document.querySelector('.toggle-btn');
const sidebar = document.querySelector('#sidebar');
const submenus = document.querySelectorAll('.sidebar-link');


let isSidebarExpanded = false;


toggleBtn.addEventListener('click', () => {
    sidebar.classList.toggle('expand');
    isSidebarExpanded = !isSidebarExpanded; 

    if (isSidebarExpanded) {
       
        submenus.forEach(link => {
            link.setAttribute('data-bs-toggle', 'collapse'); 
            link.setAttribute('aria-expanded', 'false'); 
            const target = link.getAttribute('data-bs-target');
            if (target) {
                const targetElement = document.querySelector(target);
                if (targetElement) {
                    targetElement.classList.remove('show'); 
                }
            }
        });
    } else {
   
        submenus.forEach(link => {
            link.removeAttribute('data-bs-toggle');
            link.setAttribute('aria-expanded', 'false');

            const target = link.getAttribute('data-bs-target');
            if (target) {
                const targetElement = document.querySelector(target);
                if (targetElement) {
                    targetElement.classList.remove('show'); 
                }
            }
        });
    }
});


submenus.forEach(link => {
    link.addEventListener('click', (e) => {
        if (!isSidebarExpanded) {
         
            sidebar.classList.add('expand');
            isSidebarExpanded = true;

            
            submenus.forEach(menu => {
                menu.setAttribute('data-bs-toggle', 'collapse');
                menu.setAttribute('aria-expanded', 'false'); 
            });

           
            setTimeout(() => {
                link.setAttribute('data-bs-toggle', 'collapse');
            }, 300); 
        }

       
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
    });
});


document.getElementById('inicioLink').addEventListener('click', function (event) {
    event.preventDefault();
  
    if (window.location.pathname !== 'Dashboard.html') {
      
        document.body.style.transition = 'transform 0.3s ease-out'; 
        document.body.style.transform = 'translateX(-100%)'; 

        
        setTimeout(function() {
            window.location.href = 'Dashboard.html'; 
        }, 300); 
    } else {
        
        console.log("Ya estás en la página de inicio.");
    }
});


document.querySelector('.sidebar-footer a[href="Login.html"]').addEventListener('click', function(event) {
    event.preventDefault(); 
    window.location.href = 'Login.html';
});



function cargarVista(event) {
    event.preventDefault();
    const ruta = event.target.getAttribute('data-ruta');
    const nombre = event.target.textContent.trim();
    const iframe = document.querySelector('iframe');

    if (!ruta) {
        window.location.href = 'error/error.html';
        return;
    }

    fetch(ruta)
        .then(response => {
            if (response.ok) {
                iframe.src = ruta;
                actualizarBreadcrumbs(ruta, nombre);
            } else {
                throw new Error('Página no encontrada');
            }
        })
        .catch(() => {
            window.location.href = 'error/error.html';
        });
}


function actualizarBreadcrumbs(ruta, nombre) {
    const breadcrumbs = document.getElementById('breadcrumbs');
    breadcrumbs.innerHTML = `
        <ol class="breadcrumb">
            <li class="breadcrumb-item"><a href="#" onclick="cargarVista(event)" data-ruta="Home.html">Home</a></li>
            <li class="breadcrumb-item active" aria-current="page">${nombre}</li>
        </ol>`;
}


document.querySelector('iframe').onerror = function () {
    window.location.href = 'error.html';
};