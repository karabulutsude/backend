import 'Frontend/generated/jar-resources/flow-component-renderer.js';
import '@vaadin/polymer-legacy-adapter/style-modules.js';
import 'Frontend/generated/jar-resources/vaadin-grid-flow-selection-column.js';
import '@vaadin/grid/theme/lumo/vaadin-grid-column.js';
import '@vaadin/integer-field/theme/lumo/vaadin-integer-field.js';
import '@vaadin/tooltip/theme/lumo/vaadin-tooltip.js';
import '@vaadin/icon/theme/lumo/vaadin-icon.js';
import '@vaadin/context-menu/theme/lumo/vaadin-context-menu.js';
import 'Frontend/generated/jar-resources/contextMenuConnector.js';
import 'Frontend/generated/jar-resources/contextMenuTargetConnector.js';
import '@vaadin/grid/theme/lumo/vaadin-grid.js';
import '@vaadin/grid/theme/lumo/vaadin-grid-sorter.js';
import '@vaadin/checkbox/theme/lumo/vaadin-checkbox.js';
import 'Frontend/generated/jar-resources/gridConnector.ts';
import '@vaadin/button/theme/lumo/vaadin-button.js';
import '@vaadin/number-field/theme/lumo/vaadin-number-field.js';
import '@vaadin/text-field/theme/lumo/vaadin-text-field.js';
import '@vaadin/icons/vaadin-iconset.js';
import '@vaadin/dialog/theme/lumo/vaadin-dialog.js';
import '@vaadin/vertical-layout/theme/lumo/vaadin-vertical-layout.js';
import '@vaadin/horizontal-layout/theme/lumo/vaadin-horizontal-layout.js';
import 'Frontend/generated/jar-resources/disableOnClickFunctions.js';
import '@vaadin/grid/theme/lumo/vaadin-grid-column-group.js';
import 'Frontend/generated/jar-resources/lit-renderer.ts';
import '@vaadin/notification/theme/lumo/vaadin-notification.js';
import '@vaadin/login/theme/lumo/vaadin-login-form.js';
import '@vaadin/side-nav/theme/lumo/vaadin-side-nav.js';
import '@vaadin/app-layout/theme/lumo/vaadin-app-layout.js';
import '@vaadin/side-nav/theme/lumo/vaadin-side-nav-item.js';
import '@vaadin/avatar/theme/lumo/vaadin-avatar.js';
import '@vaadin/scroller/theme/lumo/vaadin-scroller.js';
import '@vaadin/common-frontend/ConnectionIndicator.js';
import '@vaadin/vaadin-lumo-styles/color-global.js';
import '@vaadin/vaadin-lumo-styles/typography-global.js';
import '@vaadin/vaadin-lumo-styles/sizing.js';
import '@vaadin/vaadin-lumo-styles/spacing.js';
import '@vaadin/vaadin-lumo-styles/style.js';
import '@vaadin/vaadin-lumo-styles/vaadin-iconset.js';
import 'Frontend/generated/jar-resources/ReactRouterOutletElement.tsx';

const loadOnDemand = (key) => {
  const pending = [];
  if (key === '92e622b93ac4a4b37904c4a24074ce66c96ff3b6e494f5aa02ffb4fa1e6d68e4') {
    pending.push(import('./chunks/chunk-174186b0799dc722a67640bc003972b901c491640fb04ba237abe6e91690ab4b.js'));
  }
  if (key === 'e97fe1e5382ac105bfacebfa94eaaa56a83d80f38ea72587e94d8071856a0e70') {
    pending.push(import('./chunks/chunk-cf9f7a1cbfa121447c845fd0fd8363e34bf38ec393cf522578840f8dc0610899.js'));
  }
  if (key === '526a4fba6216eb73878ac474b23ead2f01a9cad3a54653a6a1f5ff6806a2cea8') {
    pending.push(import('./chunks/chunk-aa688640de4b2359abad03675254d96030468e33b1e042d6d44192f061d02f6b.js'));
  }
  if (key === '1ee2c91833ff37e8611188c7401f6456b684a6927325594f4fb2a5d8d2d594ed') {
    pending.push(import('./chunks/chunk-59c7c253d6b474447a32154c2639833b12f694513c3bdc869d35137b8bf4cb49.js'));
  }
  return Promise.all(pending);
}

window.Vaadin = window.Vaadin || {};
window.Vaadin.Flow = window.Vaadin.Flow || {};
window.Vaadin.Flow.loadOnDemand = loadOnDemand;
window.Vaadin.Flow.resetFocus = () => {
 let ae=document.activeElement;
 while(ae&&ae.shadowRoot) ae = ae.shadowRoot.activeElement;
 return !ae || ae.blur() || ae.focus() || true;
}