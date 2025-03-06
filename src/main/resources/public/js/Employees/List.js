let employees = []; 
let currentPage = 1;
const rowsPerPage = 10;

function editEmployee(name) {
    window.location.href = `EditEmployee.html?name=${encodeURIComponent(name)}`;
}



function fetchEmployees() {
    fetch('https://webapicompany.onrender.com/employee/get') 
        .then(response => response.json())
        .then(data => {
            employees = data;
            displayEmployees();
        })
        .catch(error => console.error('Error al obtener los empleados:', error));
}


function eliminarEmpleado(name) {

    fetch(`https://webapicompany.onrender.com//employee/delete/${encodeURIComponent(name)}`, { 
        method: 'DELETE',
    })
        .then(response => {
            if (response.ok) {
                alert(`Empleado ${name} eliminado`);
                fetchEmployees(); 
            } else {
                alert('Error al eliminar el empleado');
            }
        })
        .catch(error => console.error('Error al eliminar el empleado:', error));
}


function searchEmployee() {
    const searchQuery = document.getElementById('search').value.toLowerCase();
    const filteredEmployees = employees.filter(empleado => 
        empleado.name && empleado.name.toLowerCase().includes(searchQuery)
    );
    currentPage = 1;
    displayEmployees(filteredEmployees);
}


function displayEmployees(filteredEmployees = employees) {
    const employeeList = document.getElementById('employee-list');
    const pagination = document.getElementById('pagination');
    const start = (currentPage - 1) * rowsPerPage;
    const end = start + rowsPerPage;
    const paginatedEmployees = filteredEmployees.slice(start, end);

    employeeList.innerHTML = ''; // Limpiar la lista de empleados
    paginatedEmployees.forEach(empleado => {
        const row = document.createElement('tr');
        row.innerHTML = `
            <td>${empleado.name}</td>
            <td>${empleado.workstation || 'No disponible'}</td>
            <td>${empleado.salary || 'No disponible'}</td>
            <td>${empleado.email || 'No disponible'}</td>
            <td>${empleado.phone || 'No disponible'}</td>
            <td>${empleado.address || 'No disponible'}</td>
            <td>
                <div class="action-buttons">
                   <button class="edit-button" onclick="editEmployee('${empleado.name}')">Editar</button>
                    <button class="delete-button" onclick="eliminarEmpleado('${empleado.name}')">Eliminar</button>
                </div>
            </td>
        `;
        employeeList.appendChild(row);
    });

 
    const totalPages = Math.ceil(filteredEmployees.length / rowsPerPage);
    pagination.innerHTML = '';
    for (let i = 1; i <= totalPages; i++) {
        const button = document.createElement('button');
        button.textContent = i;
        button.className = i === currentPage ? 'active' : '';
        button.addEventListener('click', () => {
            currentPage = i;
            displayEmployees(filteredEmployees);
        });
        pagination.appendChild(button);
    }
}


fetchEmployees();