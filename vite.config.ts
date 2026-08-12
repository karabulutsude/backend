import { defineConfig } from 'vite';
import { overrideVaadinConfig } from './vite.generated';

export default defineConfig((env) => {
    const baseConfig = overrideVaadinConfig(env);
    return {
        ...baseConfig,
        optimizeDeps: {
            exclude: ['workbox-build'],
        },
        build: {
            ...baseConfig.build,
            rollupOptions: {
                ...baseConfig.build?.rollupOptions,
                external: ['workbox-build'],
            },
        },
    };
});