window.addEventListener('DOMContentLoaded', function() {
            const navLinks = document.querySelectorAll('.nav-lista li a');
            const currentPath = window.location.pathname.split('/').pop();  // Captura solo el nombre del archivo

            navLinks.forEach(link => {
                const linkPath = link.getAttribute('href').split('/').pop(); // Captura el nombre del archivo del href
                if (linkPath === currentPath) {
                    link.classList.add('active');
                }
            });
        });