import { defineConfig } from 'vite';
import { overrideVaadinConfig } from './vite.generated';

export default defineConfig((env) => {
    const baseConfig = overrideVaadinConfig(env);
    return {
        ...baseConfig,
        build: {
            ...baseConfig.build,
            rollupOptions: {
                ...baseConfig.build?.rollupOptions,
                external: ['workbox-build'],
            },
        },
    };
});