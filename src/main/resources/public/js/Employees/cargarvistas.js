document.addEventListener("DOMContentLoaded", () => {
    // Seleccionar todos los enlaces con 'data-ruta'
    const enlaces = document.querySelectorAll('a[data-ruta]');
    const subMenus = document.querySelectorAll('.submenu'); // Asumimos que los submenús tienen la clase 'submenu'

    enlaces.forEach(enlace => {
        enlace.addEventListener('click', cargarVista);
    });

    // Función para cerrar todos los submenús
    function cerrarSubMenus() {
        subMenus.forEach(subMenu => {
            subMenu.style.display = 'none'; // Oculta los submenús
        });
    }

    // Función para abrir un submenú
    function abrirSubMenu(subMenu) {
        subMenu.style.display = 'block'; // Muestra el submenú
    }

    // Añadir un evento para los submenús
    enlaces.forEach(enlace => {
        enlace.addEventListener('click', () => {
            const submenu = enlace.nextElementSibling; // Suponemos que el submenú es el siguiente hermano del enlace
            if (submenu && submenu.classList.contains('submenu')) {
                cerrarSubMenus(); // Cerrar todos los submenús
                if (submenu.style.display === 'none' || submenu.style.display === '') {
                    abrirSubMenu(submenu); // Abrir el submenú si estaba cerrado
                } else {
                    submenu.style.display = 'none'; // Si el submenú ya está abierto, lo cerramos
                }
            } else {
                cerrarSubMenus(); // Si no hay submenú, simplemente cerramos todos
            }
        });
    });
});

async function cargarVista(event) {
    event.preventDefault(); // Evita la recarga de la página
    const ruta = event.target.getAttribute('data-ruta'); // Obtén la ruta desde el atributo 'data-ruta'
    const content = document.getElementById('content'); // Selecciona el contenedor principal

    try {
        const response = await fetch(ruta); // Carga la vista desde la ruta especificada
        if (!response.ok) throw new Error(`Error al cargar la vista desde ${ruta}`);
        const html = await response.text(); // Obtén el contenido como texto
        content.innerHTML = html; // Inserta el contenido en el contenedor
    } catch (error) {
        content.innerHTML = `<p class="text-danger">No se pudo cargar la vista: ${error.message}</p>`;
    }
}
