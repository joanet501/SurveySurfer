document.addEventListener('DOMContentLoaded', function () {
    const form = document.querySelector('form');
    if (!form) {
        return;
    }

    form.addEventListener('submit', function () {
        // For each question with checkboxes
        const checkboxInputs = form.querySelectorAll('input[type="checkbox"][data-question-id]');
        const grouped = {};

        checkboxInputs.forEach(function (cb) {
            const qId = cb.getAttribute('data-question-id');
            if (!grouped[qId]) {
                grouped[qId] = [];
            }
            if (cb.checked) {
                grouped[qId].push(cb.value);
            }
        });

        // Write the grouped values into the hidden inputs
        Object.keys(grouped).forEach(function (qId) {
            const hidden = form.querySelector('input[type="hidden"][data-question-hidden="' + qId + '"]');
            if (hidden) {
                // Store as comma-separated, or use JSON.stringify(grouped[qId]) if you prefer
                hidden.value = grouped[qId].join(',');
            }
        });
    });
});