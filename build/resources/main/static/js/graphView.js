import { fetchGraphDataFromGitHub,  fetchGraphLocal} from './api.js';

var graphElems
document.getElementById('loadGraphFromGitHub').addEventListener('click', async function() {
    if (cy) {
        cy.destroy();
    }
    showSpinner()
    document.getElementById('loadGraphError').style.display = 'none';
    const projectName = document.getElementById('projectName').value
    const repoName = document.getElementById('repoName').value
    const token = document.getElementById('token').value
    showWaitingDataText()
    graphElems = await fetchGraphDataFromGitHub(projectName,repoName,token)
    console.log(graphElems)
    hideWaitingDataText()
    if(graphElems=="error"){
        document.getElementById('loadGraphError').style.display = 'block';
    }else{
    initCytoscape("cy", graphElems);
    }
    hideSpinner()
});
document.getElementById('loadMatricesLocal').addEventListener('click', async function() {
    if (cy) {
        cy.destroy();
    }
    showSpinner()
    document.getElementById('loadGraphError').style.display = 'none';
    graphElems = await fetchGraphLocal("ressources/matrices")
    console.log(graphElems)
    initCytoscape("cy", graphElems);
    hideSpinner()
});
document.getElementById('loadArgoLocal').addEventListener('click', async function() {
    if (cy) {
        cy.destroy();
    }
    showSpinner()
    document.getElementById('loadGraphError').style.display = 'none';
    graphElems = await fetchGraphLocal("ressources/argo")
    console.log(graphElems)
    initCytoscape("cy", graphElems);
    hideSpinner()
});
var cy
function initCytoscape(containerId,elements) {
  showGeneratingGraphText()
  document.getElementById(containerId).innerHTML = "";
  const typeColor ={
      package_declaration: "green",
      class_declaration: "yellow",
      method_declaration: "purple",
      field_declaration: "orange",
      local_variable_declaration: "brown",
      constructor_declaration: "blue",
      default: "grey"
  }
  // Initialisation de Cytoscape
  var filteredElements = {nodes: [], edges: []}
  filteredElements.nodes = elements.nodes
  filteredElements.edges = elements.edges.filter(el => el.classes === 'contain')

  cy = cytoscape({
    container: document.getElementById(containerId), // Container pour le graphique
    elements: filteredElements,
    style: [
      // Style des nœuds et des liens
      {
        selector: "node",
        style: {
          "background-color": 'white',
          // "label": "data(id)",
          "color": "black",
          "text-valign": "center",
          "text-halign": "center",
          // "shape": "rectangle",
          "font-size": "12px", // Ajustez selon vos besoins
          "text-wrap": "wrap", // Active l'enveloppement du texte
          "text-max-width": "80px", // Ajustez selon vos besoins
          // "width": "label", // Ajuste la largeur du nœud en fonction du label
          // "height": "label", // Ajuste la hauteur du nœud en fonction du label
          "padding": "5px", // Largeur de la bordure des nœuds
          'border-width': 3, // Largeur de la bordure des nœuds
          'border-color': function(node) {
              const type = node.data('type');
              return typeColor[type] || typeColor.default;
            },
        }
      },
      {
        selector: "edge",
        style: {
          width: 3,
          "line-color": "#bbb",
          "target-arrow-shape": "triangle",
          'curve-style': 'bezier',
        },
      },
      {
        selector: 'edge.contain',
        style: {
          width: 3,
          "line-color": "#ccc",
          'line-style': 'dashed'

        },
      },
      {
        selector: 'edge.use',
        style: {
          width: 3,
          "line-color": "lightblue",
          "target-arrow-color": "blue",

        },
      },
    ],
    layout: {
      name: "dagre", // Disposition du graphique breadthfirst
      rankDir: 'TB', // Direction du graphe, TB = de haut en bas
      // nodeDimensionsIncludeLabels: true,
    // align: 'UL',
      nodeSep: 10, // Espacement entre les nœuds adjacents dans la même rangée ou colonne
      edgeSep: 10, // Espacement entre les arêtes adjacentes
      rankSep: 10,// Espacement entre chaque rangée ou colonne
    },
    userZoomingEnabled: false
  });
  window.addEventListener('keydown', function(event) {
    if (event.ctrlKey || event.metaKey) {
      cy.userZoomingEnabled(true);
    }
  });

  window.addEventListener('keyup', function(event) {
    if (!event.ctrlKey && !event.metaKey) {
      cy.userZoomingEnabled(false);
    }
  });

    // Gestionnaires d'événements pour les boutons de zoom
    document.getElementById('zoom-in').addEventListener('click', function() {
      cy.zoom(cy.zoom() * 1.2);
      cy.center();
    });

    document.getElementById('zoom-out').addEventListener('click', function() {
      cy.zoom(cy.zoom() * 0.8);
      cy.center();
    });
  cy.add(elements.edges.filter(el => el.classes === 'use'));

  // Activer le drag des nœuds
  cy.nodes().on("drag", function (event) {
    var node = event.target;
  });


  cy.on('click', 'node', function(event) {
    var node = event.target;
    console.log('Propriétés du nœud :', node.data());
    const type = node.data('type');
    const id = node.data('id');
    console.log(node.style('label'));
    if (!node.style('label')) {
      node.style('label', id);
    }else{
      node.style('label', '');
    }
  });
    hideGeneratingGraphText()
}
document.getElementById('dagreeLayout').addEventListener('click', function() {
  const newLayout = cy.layout({
      name: "dagre", // Disposition du graphique breadthfirst
      rankDir: 'TB', // Direction du graphe, TB = de haut en bas
      // nodeDimensionsIncludeLabels: true,
    // align: 'UL',
      // nodeSep: 10, // Espacement entre les nœuds adjacents dans la même rangée ou colonne
      // edgeSep: 10, // Espacement entre les arêtes adjacentes
      // rankSep: 10,// Espacement entre chaque rangée ou colonne
  });

  newLayout.run();
});
document.getElementById('coseLayout').addEventListener('click', function() {
  cy.layout({
    name: "cose", // Utiliser la mise en page COSE
    // Paramètres spécifiques à COSE
    nodeRepulsion: 400,
    idealEdgeLength: 100,
    componentSpacing: 100,
    nodeOverlap: 10,
  }).run();
});
document.getElementById('dagreeContainLayout').addEventListener('click', function() {
    initCytoscape("cy", graphElems);
});
function showSpinner() {
  document.getElementById('spinner').style.display = 'block';
}
function hideSpinner() {
  document.getElementById('spinner').style.display = 'none';
}
function showWaitingDataText() {
  document.getElementById('waitingDataText').style.display = 'block';
}
function hideWaitingDataText() {
  document.getElementById('waitingDataText').style.display = 'none';
}
function showGeneratingGraphText() {
  document.getElementById('generatingGraphText').style.display = 'block';
}
function hideGeneratingGraphText() {
  document.getElementById('generatingGraphText').style.display = 'none';
}