export function fetchGraphDataFromGitHub(user, project, token) {
var url = 'http://localhost:8080/graph/github?user=' + user + '&project=' + project + '&token=' + token;
    return fetch(url)
        .then(response => {
            if (!response.ok) {
                throw new Error('Network response was not ok ' + response);
            }
            return response.json();
        })
        .catch(error => {
           // console.error('There has been a problem with your fetch operation:', error);
            return "error";
        });
}
export function fetchGraphLocal(directoryPath) {
var url = 'http://localhost:8080/graph/local?directoryPath='+directoryPath;
    return fetch(url)
        .then(response => {
            if (!response.ok) {
                throw new Error('Network response was not ok ' + response);
            }
            return response.text();
        })
        .then(data => {
            return JSON.parse(data); // Parse JSON after logging
        })
        .then(parsedData => {
            return parsedData;
        })
        .catch(error => {
           // console.error('There has been a problem with your fetch operation:', error);
           console.log(error)
            return error;
        });
}