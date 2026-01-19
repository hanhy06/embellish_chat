const labels = [0, 50, 100, 300, 500];

const data50 = [
  { label: "Plain Text", data: [2, 3, 3, 5, 8], borderColor: "#FF6384", backgroundColor: "#FF6384", tension: 0.1 },
  { label: "Styling Only", data: [2, 4, 5, 9, 11], borderColor: "#36A2EB", backgroundColor: "#36A2EB", tension: 0.1 },
  { label: "Mention Only(on)", data: [2, 4, 5, 10, 14], borderColor: "#FFCE56", backgroundColor: "#FFCE56", tension: 0.1 },
  { label: "Mention Only(off)", data: [2, 4, 5, 10, 14], borderColor: "#4BC0C0", backgroundColor: "#4BC0C0", tension: 0.1 },
  { label: "Mixed", data: [2, 4, 5, 11, 14], borderColor: "#9966FF", backgroundColor: "#9966FF", tension: 0.1 }
];

const data200 = [
  { label: "Plain Text", data: [2, 4, 5, 11, 14], borderColor: "#FF6384", backgroundColor: "#FF6384", tension: 0.1 },
  { label: "Styling Only", data: [2, 5, 7, 15, 19], borderColor: "#36A2EB", backgroundColor: "#36A2EB", tension: 0.1 },
  { label: "Mention Only(on)", data: [2, 7, 11, 23, 34], borderColor: "#FFCE56", backgroundColor: "#FFCE56", tension: 0.1 },
  { label: "Mention Only(off)", data: [2, 7, 9, 21, 31], borderColor: "#4BC0C0", backgroundColor: "#4BC0C0", tension: 0.1 },
  { label: "Mixed", data: [2, 6, 9, 18, 24], borderColor: "#9966FF", backgroundColor: "#9966FF", tension: 0.1 }
];

let myChart = null;

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
        // --- 범례(Legend) 설정 추가됨 ---
        legend: {
          labels: {
            generateLabels: function(chart) {
              // 1. 기본 라벨 생성 동작을 가져옵니다.
              const original = Chart.defaults.plugins.legend.labels.generateLabels(chart);

              // 2. 각 라벨을 순회하며 숨겨진 상태인지 확인합니다.
              original.forEach(label => {
                if (label.hidden) {
                  // 3. 숨겨진 상태(취소선 그어진 상태)라면 색상을 회색으로 덮어씁니다.
                  label.fillStyle = '#b0b0b0';
                  label.strokeStyle = '#b0b0b0';
                }
              });
              return original;
            }
          }
        },
        // -----------------------------
        title: {
          display: true,
          text: 'Processing Time (50 Characters)'
        },
        tooltip: {
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

function updateMode(mode) {
  if (!myChart) return;

  if (mode === '50') {
    myChart.data.datasets = data50;
    myChart.options.plugins.title.text = 'Processing Time (50 Characters)';
    myChart.options.scales.y.max = 20;
  } else {
    myChart.data.datasets = data200;
    myChart.options.plugins.title.text = 'Processing Time (200 Characters)';
    myChart.options.scales.y.max = 40;
  }
  myChart.update();
}