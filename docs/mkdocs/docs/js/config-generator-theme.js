        (() => {
            const storageKey = 'embellish-chat-theme';
            const root = document.documentElement;
            const systemTheme = window.matchMedia && window.matchMedia('(prefers-color-scheme: dark)').matches ? 'dark' : 'light';

            try {
                const savedTheme = localStorage.getItem(storageKey);
                const resolvedTheme = savedTheme === 'dark' || savedTheme === 'light' ? savedTheme : systemTheme;
                root.classList.toggle('dark', resolvedTheme === 'dark');
                root.dataset.theme = resolvedTheme;
            } catch (error) {
                root.classList.toggle('dark', systemTheme === 'dark');
                root.dataset.theme = systemTheme;
            }
        })();
