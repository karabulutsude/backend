import { defineConfig } from 'vite';
import { overrideVaadinConfig } from './vite.generated';

export default defineConfig((env) => {
    const baseConfig = overrideVaadinConfig(env);
    return {
        ...baseConfig,
        // Workbox ve PWA eklentisinin build aşamasında hata vermesini engeller
        build: {
            ...baseConfig.build,
            rollupOptions: {
                ...baseConfig.rollupOptions,
                external: ['workbox-build'],
            },
        },
    };
});