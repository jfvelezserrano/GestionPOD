var generarCSV = function()
{
    alert("Hello!");
};

/*
var el = document.getElementById('#buscador-asignatura');
var elTop = el.getBoundingClientRect().top - document.body.getBoundingClientRect().top;
*/
/*
window.addEventListener('scroll', function(){
    if (document.documentElement.scrollTop > elTop){
        el.style.position = 'fixed';
        el.style.top = '0px';
    }
    else
    {
        el.style.position = 'static';
        el.style.top = 'auto';
    }
});

$(document).ready(function(){
    $(".circle").knob({
        "min":0,
        "max":100,
        "width":250,
        "height":250,
        "fgColor":"#000000",
        "readOnly":true,
        "displayInput":true,
        "release": function(v){alert(v);}
    })
})*/

document.addEventListener("DOMContentLoaded", function () {
    var style = getComputedStyle(document.body);
    const ctx = document.getElementById('horas-por-asignatura');
    const horasPorAsignatura = new Chart(ctx, {
        type: 'bar',
        data: {
            responsive: true,
            labels: ['ED', 'LDM', 'BBDD', 'Lógica', 'Física', 'Multimedia'],
            datasets: [{
            barThickness: 15,
            maxBarThickness: 15,
            minBarLength: 2,
            label: 'Mis horas',
            data: [60, 80, 30, 80, 50, 90],
            backgroundColor: [
                style.getPropertyValue('--libre'),
                style.getPropertyValue('--libre'),
                style.getPropertyValue('--libre'),
                style.getPropertyValue('--libre'),
                style.getPropertyValue('--libre'),
                style.getPropertyValue('--libre')
            ]
            },
            {
            barThickness: 15,
            maxBarThickness: 15,
            minBarLength: 2,
            label: 'Horas totales',
            data: [90, 80, 75, 90, 80, 90],
            backgroundColor: [
                style.getPropertyValue('--gris'),
                style.getPropertyValue('--gris'),
                style.getPropertyValue('--gris'),
                style.getPropertyValue('--gris'),
                style.getPropertyValue('--gris'),
                style.getPropertyValue('--gris'),
            ]
            }
        ]
        },
        options: {
            scales: {
                y: {
                    beginAtZero: true
                },
                x: {
                    ticks: {display: false},
                    grid:{display: false}
                }            
            },

            tooltips: {
                enabled: false,
            },

            plugins: {
                legend: {display: true,
                position:'bottom'}
            }
        }
    });
});

document.addEventListener("DOMContentLoaded", function () {
    var doughnutDocente = document.getElementById("doughnut-docente");
    var myChart = new Chart(doughnutDocente, {
    type: 'doughnut',
    data: {
        responsive: true,
        labels:['ED', 'LDM', 'BBDD', 'Lógica', 'Física', 'Multimedia'],
        datasets: [{
        label: 'Porcentaje Fuerza Docente',
        data: [21, 22, 16, 21, 5, 11],
        backgroundColor: [
            'rgb(255, 99, 132)',
            'rgb(255, 159, 64)',
            'rgb(255, 205, 86)',
            'rgb(75, 192, 192)',
            'rgb(54, 162, 235)',
            'rgba(153, 102, 255, 1)'
        ],
        borderWidth: 1
        }]
    },
    options: {
        plugins: {
        legend: {display: true,
            position:'right'}
        }
    }
    });
});

$(function () {
  $('[data-toggle="tooltip"]').tooltip()
})

document.querySelector(function () {
  document.querySelector('[data-toggle="tooltip"]').tooltip()
})

$(document).ready(function() {
  $("body").tooltip({ selector: '[data-toggle=tooltip]' });
});

angular.module("app", ["chart.js"]).controller("BarCtrl", function($scope) {
    $scope.labels = ['2006', '2007', '2008', '2009', '2010', '2011', '2012'];
    $scope.series = ['Series A', 'Series B'];
  
    $scope.data = [
      [65, 59, 80, 81, 56, 55, 40],
      [28, 48, 40, 19, 86, 27, 90]
    ];
  });