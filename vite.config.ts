import { defineConfig } from 'vite';
import { overrideVaadinConfig } from './vite.generated';

export default defineConfig((env) => {
    const baseConfig = overrideVaadinConfig(env);
    return {
        ...baseConfig,
        optimizeDeps: {
            ...baseConfig.optimizeDeps,
            exclude: [...(baseConfig.optimizeDeps?.exclude || []), 'workbox-build'],
        },
        ssr: {
            ...baseConfig.ssr,
            external: [...(baseConfig.ssr?.external || []), 'workbox-build'],
        },
    };
});