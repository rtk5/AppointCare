// ── Slot Selection ───────────────────────────────────────────
function selectSlot(el, scheduleId) {
  document.querySelectorAll('.slot-item').forEach(s => s.classList.remove('selected'));
  el.classList.add('selected');
  const input = document.getElementById('scheduleIdInput');
  if (input) input.value = scheduleId;
}

// ── Payment Method Selection ─────────────────────────────────
document.querySelectorAll('.payment-method').forEach(pm => {
  pm.addEventListener('click', () => {
    document.querySelectorAll('.payment-method').forEach(p => p.classList.remove('selected'));
    pm.classList.add('selected');
    const radio = pm.querySelector('input[type=radio]');
    if (radio) radio.checked = true;
  });
});

// ── Auto-dismiss Alerts ──────────────────────────────────────
document.querySelectorAll('.alert').forEach(alert => {
  setTimeout(() => {
    alert.style.transition = 'opacity 0.5s';
    alert.style.opacity = '0';
    setTimeout(() => alert.remove(), 500);
  }, 4000);
});

// ── Doctor specialization filter ─────────────────────────────
const specFilter = document.getElementById('specFilter');
if (specFilter) {
  specFilter.addEventListener('change', () => {
    const val = specFilter.value.toLowerCase();
    document.querySelectorAll('.doctor-card').forEach(card => {
      const spec = card.dataset.spec ? card.dataset.spec.toLowerCase() : '';
      card.style.display = (!val || spec.includes(val)) ? '' : 'none';
    });
  });
}

// ── Confirm dangerous actions ────────────────────────────────
document.querySelectorAll('[data-confirm]').forEach(btn => {
  btn.addEventListener('click', e => {
    if (!confirm(btn.dataset.confirm)) e.preventDefault();
  });
});

// ── Role-based registration form toggle ──────────────────────
const roleSelect = document.getElementById('roleSelect');
if (roleSelect) {
  function toggleRoleFields() {
    const val = roleSelect.value;
    const doctorFields = document.getElementById('doctorFields');
    const adminFields  = document.getElementById('adminFields');
    if (doctorFields) doctorFields.style.display = val === 'DOCTOR' ? '' : 'none';
    if (adminFields)  adminFields.style.display  = val === 'ADMIN'  ? '' : 'none';
  }
  roleSelect.addEventListener('change', toggleRoleFields);
  toggleRoleFields();
}

// ── Sidebar toggle for mobile ────────────────────────────────
const menuToggle = document.getElementById('menuToggle');
const sidebar    = document.querySelector('.sidebar');
if (menuToggle && sidebar) {
  menuToggle.addEventListener('click', () => sidebar.classList.toggle('open'));
}
