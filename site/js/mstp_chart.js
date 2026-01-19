document.addEventListener("DOMContentLoaded", function() {
  const ctx = document.getElementById('mstpChart').getContext('2d');

  myChart = new Chart(ctx, {
    type: 'line',
    data: {
      labels: labels,
      datasets: data50
    },
    options: {
      responsive: true,
      interaction: {
        mode: 'index',
        intersect: false,
      },
      plugins: {
        legend: {
          labels: {
            generateLabels: function(chart) {
              const original = Chart.defaults.plugins.legend.labels.generateLabels(chart);

              original.forEach(label => {
                if (label.hidden) {
                  label.fillStyle = '#b0b0b0';
                  label.strokeStyle = '#b0b0b0';
                }
              });
              return original;
            }
          }
        },
        title: {
          display: true,
          text: 'Processing Time (50 Characters)'
        },
        tooltip: {
          itemSort: function(a, b) {
            return b.parsed.y - a.parsed.y;
          },
          callbacks: {
            title: function(context) {
              return context[0].label + ' msg/tick';
            },
            label: function(context) {
              return context.dataset.label + ': ' + context.parsed.y + ' ms';
            }
          }
        }
      },
      scales: {
        x: {
          title: { display: true, text: 'Message Count/tick' }
        },
        y: {
          beginAtZero: true,
          max: 20,
          title: { display: true, text: 'Value' }
        }
      }
    }
  });
});