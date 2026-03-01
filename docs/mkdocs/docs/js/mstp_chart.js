const labels = [0, 50, 100, 300, 500];

const chartData = [
  { label: "Plain Text",        data: [0, 0.27, 1.06, 3.77, 5.83],  borderColor: "#FF6384", backgroundColor: "#FF6384", tension: 0.1 },
  { label: "Styling Only",      data: [0, 0.90, 2.16, 6.93, 10.99], borderColor: "#36A2EB", backgroundColor: "#36A2EB", tension: 0.1 },
  { label: "Mention Only (ON)", data: [0, 3.06, 4.86, 9.83, 14.27], borderColor: "#FFCE56", backgroundColor: "#FFCE56", tension: 0.1 },
  { label: "Mention Only (OFF)",data: [0, 1.18, 3.03, 8.69, 13.00], borderColor: "#4BC0C0", backgroundColor: "#4BC0C0", tension: 0.1 },
  { label: "Mixed",             data: [0, 1.28, 2.57, 7.06, 12.00], borderColor: "#9966FF", backgroundColor: "#9966FF", tension: 0.1 }
];

let myChart = null;

document.addEventListener("DOMContentLoaded", function() {
  const ctx = document.getElementById('mstpChart').getContext('2d');

  myChart = new Chart(ctx, {
    type: 'line',
    data: {
      labels: labels,
      datasets: chartData
    },
    options: {
      aspectRatio: window.innerWidth < 768 ? 1.0 : 2.0,
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
          text: 'Message Processing Time by Message Type'
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
          title: { display: true, text: 'Message Count / tick' }
        },
        y: {
          beginAtZero: true,
          max: 16,
          title: { display: true, text: 'Processing Time (ms)' }
        }
      }
    }
  });
});
