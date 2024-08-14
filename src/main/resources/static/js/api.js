export function fetchGraphDataFromGitHub(user, project, token, declarationTypes) {
var url = 'graph/github?user=' + user + '&project=' + project + '&token=' + token + '&declarationTypes=package_declaration,' + declarationTypes.join(',');

    return fetch(url)
        .then(response => {
            if (!response.ok) {
                throw new Error('Network response was not ok ' + response);
            }
            return response.json();
        })
        .catch(error => {
           console.error('There has been a problem with your fetch operation:', error);
            return "error";
        });
}
export function fetchGraphLocal(javaFiles,declarationTypes) {
    const wrappedArray = { codeList: javaFiles, declarationTypes };

    return fetch('/graph/local', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json'
        },
        body: JSON.stringify(wrappedArray)
    })
    .then(response => {
        if (!response.ok) {
            throw new Error('Network response was not ok ' + response.statusText);
        }
        return response.json();
    })
    .then(data => {
        console.log(data);
        return data;
    })
    .catch(error => {
        console.error('There has been a problem with your fetch operation:', error);
        return error;
    });
}
