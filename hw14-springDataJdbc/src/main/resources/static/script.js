function saveClient() {
    let formData = new FormData(clientForm);
    let json = Object.fromEntries(formData);
    json.phoneNumbers = json.phoneNumbers.split(",").filter(item => item);
    fetch('/api/client', {
        method: 'POST',
        headers: {
            'Accept': 'application/json',
            'Content-Type': 'application/json'
        },
        body: JSON.stringify(json)
    })
        .then(response => response.json())
        .then(client => {
            document.getElementById('clientDataContainer').innerHTML = 'Добавлен клиент с id: ' + client.id;
            addRowClient(client);
        });
}

function addRowClient(client) {
    const tbody = document.querySelector('#clientsTable tbody');
    const tr = document.createElement('tr');

    ['id', 'name', 'street'].forEach(key => {
        const td = document.createElement('td');
        td.textContent = client[key];
        tr.appendChild(td);
    });

    const tdWithTable = document.createElement('td');
    const innerTable = document.createElement('table');
    const innerTbody = document.createElement('tbody');

    client.phoneNumbers.forEach(item => {
        const innerTr = document.createElement('tr');
        const innerTd = document.createElement('td');
        innerTd.textContent = item;
        innerTr.appendChild(innerTd);
        innerTbody.appendChild(innerTr);
    });

    innerTable.appendChild(innerTbody);
    tdWithTable.appendChild(innerTable);
    tr.appendChild(tdWithTable);

    tbody.appendChild(tr);
}
