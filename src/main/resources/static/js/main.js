/**
 * Script principal pour l'application de gestion des stagiaires
 */

// Attendre que le DOM soit chargé
document.addEventListener('DOMContentLoaded', function() {
    // Activer les tooltips Bootstrap
    var tooltipTriggerList = [].slice.call(document.querySelectorAll('[data-bs-toggle="tooltip"]'));
    var tooltipList = tooltipTriggerList.map(function (tooltipTriggerEl) {
        return new bootstrap.Tooltip(tooltipTriggerEl);
    });
    
    // Activer les popovers Bootstrap
    var popoverTriggerList = [].slice.call(document.querySelectorAll('[data-bs-toggle="popover"]'));
    var popoverList = popoverTriggerList.map(function (popoverTriggerEl) {
        return new bootstrap.Popover(popoverTriggerEl);
    });
    
    // Validation des formulaires
    var forms = document.querySelectorAll('.needs-validation');
    Array.prototype.slice.call(forms).forEach(function (form) {
        form.addEventListener('submit', function (event) {
            if (!form.checkValidity()) {
                event.preventDefault();
                event.stopPropagation();
            }
            form.classList.add('was-validated');
        }, false);
    });
    
    // Gestion des alertes avec fermeture automatique
    var autoAlerts = document.querySelectorAll('.alert-auto-dismiss');
    autoAlerts.forEach(function(alert) {
        setTimeout(function() {
            var closeButton = alert.querySelector('.btn-close');
            if (closeButton) {
                closeButton.click();
            } else {
                alert.classList.remove('show');
                setTimeout(function() {
                    alert.remove();
                }, 150);
            }
        }, 5000); // 5 secondes
    });
    
    // Gestionnaire pour le chargement des périodes de stage
    var dateDebutEl = document.getElementById('dateDebut');
    var dateFinEl = document.getElementById('dateFin');
    
    if (dateDebutEl && dateFinEl) {
        dateDebutEl.addEventListener('change', function() {
            if (dateDebutEl.value && !dateFinEl.value) {
                var dateDebut = new Date(dateDebutEl.value);
                // Ajouter 3 mois par défaut
                var dateFin = new Date(dateDebut);
                dateFin.setMonth(dateFin.getMonth() + 3);
                
                // Formater la date pour l'input date
                var formattedDate = dateFin.toISOString().split('T')[0];
                dateFinEl.value = formattedDate;
            }
        });
    }
    
    // Gestionnaire pour la recherche dynamique
    var searchInput = document.querySelector('input[name="keyword"]');
    if (searchInput) {
        searchInput.addEventListener('keypress', function(e) {
            if (e.key === 'Enter') {
                e.preventDefault();
                this.closest('form').submit();
            }
        });
    }
});