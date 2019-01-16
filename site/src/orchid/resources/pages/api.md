---
title: API Reference
extraJs:
  - 'assets/swagger-ui-bundle.js'
  - 'assets/swagger-ui-standalone-preset.js'
extraCss:
  - 'assets/swagger-ui.css'
  - 'assets/swagger-ui-customization.css'
components:
  - type: swaggerUi
    openApiSource: '#{$0|baseUrlRoot}/netlify/openApi.json'
    swaggerUiBaseUrl: '#{$0|baseUrlRoot}/assets'
  - type: pageContent
---
